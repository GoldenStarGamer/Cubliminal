package net.limit.cubliminal.world.chunk;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jdiemke.triangulation.*;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.block.custom.template.RotatableLightBlock;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.limit.cubliminal.init.CubliminalBlocks;
import net.limit.cubliminal.init.CubliminalRegistrar;
import net.limit.cubliminal.access.ChunkAccessor;
import net.limit.cubliminal.level.Level;
import net.limit.cubliminal.level.LevelWithMaze;
import net.limit.cubliminal.util.MazeUtil;
import net.limit.cubliminal.world.biome.source.LevelOneBiomeSource;
import net.limit.cubliminal.world.maze.*;
import net.limit.cubliminal.world.placement.MSTree;
import net.limit.cubliminal.world.placement.PoissonDiskSampler;
import net.limit.cubliminal.world.room.Room;
import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.*;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.*;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkGenerationContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class LevelOneChunkGenerator extends AbstractNbtChunkGenerator implements BackroomsLevel {
	public static final MapCodec<LevelOneChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			LevelOneBiomeSource.CODEC.fieldOf("biome_source").stable().forGetter(chunkGenerator -> chunkGenerator.biomeSource),
			NbtGroup.CODEC.fieldOf("group").stable().forGetter(chunkGenerator -> chunkGenerator.nbtGroup),
			LevelWithMaze.LEVEL_WITH_MAZE_CODEC.fieldOf("level").stable().forGetter(chunkGenerator -> chunkGenerator.level)
	).apply(instance, instance.stable(LevelOneChunkGenerator::new)));

	private final LevelOneBiomeSource biomeSource;
	private final MultiFloorMazeGenerator<MazeComponent> mazeGenerator;
	private final PoissonDiskSampler poissonDiskSampler;
	private final LevelWithMaze level;
	private final int thicknessX;
	private final int thicknessZ;
	private final int layerHeight;

	public LevelOneChunkGenerator(LevelOneBiomeSource biomeSource, NbtGroup group, LevelWithMaze level) {
		super(biomeSource, group);
		this.biomeSource = biomeSource;
		this.level = level;
		this.thicknessX = level.spacing_x;
		this.thicknessZ = level.spacing_z;
		this.layerHeight = level.layer_height;
		this.mazeGenerator = new MultiFloorMazeGenerator<>(level.maze_width, level.maze_height, this.thicknessX, this.layerHeight, this.thicknessZ, level.maze_seed_modifier);
		this.poissonDiskSampler = new PoissonDiskSampler(level.maze_width, level.maze_height, 10, biome -> biome.isIn(CubliminalBiomes.DEEP_LEVEL_ONE));
	}

	public static NbtGroup createGroup() {
		return NbtGroup.Builder
				.create(Cubliminal.id(CubliminalRegistrar.HABITABLE_ZONE))
				.with("f", 1, 1)
				.with("i", 1, 1)
				.with("l", 1, 1)
				.with("n", 1, 1)
				.with("t", 1, 1)
				.with("e", 1, 1)
				.with("parking", 1, 10)
				.with("ramp",
						"n_1", "n_2", "n_3",
						"s_1", "s_2", "s_3",
						"w_1", "w_2", "w_3",
						"e_1", "e_2", "e_3")
				.with("entrance")
				.with("rooms", "room_0_0", "room_0_1", "room_0_2", "room_1_0", "room_2_0", "room_2_1", "room_3_0", "room_3_1", "room_a", "small", "medium")
			.build();
	}

	@Override
	protected MapCodec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@SuppressWarnings("unchecked")
	public MazeComponent generateMaze(ChunkRegion region, BlockPos mazePos, int width, int height, Random random) {
		// Cache per-cell biome
		RegistryEntry<Biome>[][] biomeGrid = new RegistryEntry[width][height];
		for (int x = 0; x < width; x++) {
			for (int z = 0; z < height; z++) {
				biomeGrid[x][z] = this.biomeSource.calcBiome(x * this.thicknessX + mazePos.getX(), mazePos.getY(), z * this.thicknessZ + mazePos.getZ());
			}
		}

		// Run poisson disk sampler to find a position for each room
		List<Room.Instance> roomInstances = new ArrayList<>();
		boolean[][] grid = new boolean[width][height];
		List<Vec2i> roomPositions = this.poissonDiskSampler.generate(roomInstances, grid, biomeGrid, random);
		Set<Vector2D> nodes = new HashSet<>();
		SetMultimap<Vec2i, Room.DoorInstance> doors = HashMultimap.create();
		LevelOneMaze maze = new LevelOneMaze(width, height, grid, 0.2f, random);

		// Mark room origin cells for generation and add their doors' positions as nodes
		if (roomInstances.size() == roomPositions.size()) {
			for (int i = 0; i < roomInstances.size(); i++) {
				Room.Instance room = roomInstances.get(i);
				Vec2i roomPos = roomPositions.get(i);
				room.place(maze, roomPos.getX(), roomPos.getY())
						.forEach(door -> {
							Room.DoorInstance instance = Room.DoorInstance.of(roomPos, door.facing());
							Vec2i vec = instance.entry(
									door.relativePos().x() + roomPos.getX(),
									door.relativePos().y() + roomPos.getY());
							Vector2D entryPos = new Vector2D(vec.getX(), vec.getY());
							doors.put(vec, instance);
							nodes.add(entryPos);
						});
			}
		}

		// Add connections to other mazes in form of doors
		List<Vector2D> connections = this.addConnections(mazePos, width, height);
		for (int i = 0; i < connections.size(); ++i) {
			Face face = Face.values()[i];
			Vector2D vec = connections.get(i);
			Vec2i entryPos = new Vec2i((int) vec.x, (int) vec.y);
			doors.put(entryPos, new Room.DoorInstance(entryPos, face.mirror()));
            nodes.add(vec);
		}

		// Triangulate room positions to create a graph-based layout
		try {
			DelaunayTriangulator triangulator = new DelaunayTriangulator(nodes.stream().toList());
			triangulator.triangulate();
			// Collect all the unique edges
			List<Edge2D> mst = MSTree.buildCorridors(nodes, doors, triangulator.getTriangles(), random);
			maze.setMst(mst);
			maze.setDoors(doors);
			maze.generateMaze();
		} catch (NotEnoughPointsException e) {
			Cubliminal.LOGGER.error(e.getMessage());
		}

		return maze;
	}

	public List<Vector2D> addConnections(BlockPos mazePos, int width, int height) {
		Random randomUp = Random.create(LimlibHelper.blockSeed(mazePos.add(height * this.thicknessZ - 1, 0, 0)));
		Random randomDown = Random.create(LimlibHelper.blockSeed(mazePos.add(-1, 0, 0)));
		Random randomLeft = Random.create(LimlibHelper.blockSeed(mazePos));
		Random randomRight = Random.create(LimlibHelper.blockSeed(mazePos.add(0, 0, width * this.thicknessX)));
		List<Vector2D> connections = new ArrayList<>();
		// East
		connections.add(new Vector2D(width - 1, randomUp.nextInt(height)));
		// West
		connections.add(new Vector2D(0, randomDown.nextInt(height)));
		// North
		connections.add(new Vector2D(randomLeft.nextInt(width), 0));
		// South
		connections.add(new Vector2D(randomRight.nextInt(width), height - 1));
		return connections;
	}

	public void decorateMaze(ChunkRegion region, BlockPos pos, BlockPos mazePos, MazeComponent maze, CellState state, BlockPos thickness, Random random) {
		if (state instanceof SpecialCellState special) {
			special.decorate(manipulation -> generateNbt(region, pos, special.nbtId(nbtGroup, random), manipulation));
		} else {
			Pair<MazePiece, Manipulation> piece = MazePiece.getFromCell(state, random);
			if (piece.getFirst() != MazePiece.E) {
				generateNbt(region, pos.up(), this.nbtGroup.pick(piece.getFirst().getAsLetter(), random), piece.getSecond());
			}
		}
	}

	public void decorateCell(ChunkRegion region, BlockPos pos, BlockPos mazePos, MazeComponent maze, CellState state, BlockPos thickness, Random random) {
		Pair<MazePiece, Manipulation> piece = MazePiece.getFromCell(state, random);
		RegistryEntry<Biome> biome = this.biomeSource.calcBiome(pos);

		if (biome.matchesKey(CubliminalBiomes.PARKING_ZONE_BIOME)) {
			if (state.getExtra().containsKey("ramp")) {
				if (pos.getY() == this.getMinimumY()) {

					byte[] bytes = state.getExtra().get("ramp").getByteArray("ramp");
					generateNbt(region, pos, nbtGroup.nbtId("ramp", MazeUtil.rotString(Face.values()[bytes[1]]).concat("_" + bytes[0])));
				}
			} else {
				generateNbt(region, pos, nbtGroup.pick("parking", random));
			}
		} else {
			if (piece.getFirst() != MazePiece.E) {
				generateNbt(region, pos.up(2), this.nbtGroup.pick(piece.getFirst().getAsLetter(), random), piece.getSecond());
				if (state.getExtra().containsKey("elevatorHall")) {
					Face face = Face.values()[state.getExtra().get("elevatorHall").getByte("elevatorHall")];
					BlockState bState = Blocks.BLUE_CONCRETE.getDefaultState();
					switch (face) {
						case UP -> region.setBlockState(pos.add(15, 0, 8), bState, 0);
						case DOWN -> region.setBlockState(pos.add(0, 0, 8), bState, 0);
						case LEFT -> region.setBlockState(pos.add(8, 0, 0), bState, 0);
						case RIGHT -> region.setBlockState(pos.add(8, 0, 15), bState, 0);
					}
				}
			} else if (state.getExtra().containsKey("elevator")) {
				Face face = Face.values()[state.getExtra().get("elevator").getByte("elevator")];
				BlockState bState = Blocks.RED_CONCRETE.getDefaultState();
				switch (face) {
					case UP -> region.setBlockState(pos.add(15, 0, 8), bState, 0);
					case DOWN -> region.setBlockState(pos.add(0, 0, 8), bState, 0);
					case LEFT -> region.setBlockState(pos.add(8, 0, 0), bState, 0);
					case RIGHT -> region.setBlockState(pos.add(8, 0, 15), bState, 0);
				}
			} else {
				for (int x = 0; x < thicknessX; x++) {
					for (int y = 0; y < layerHeight; y++) {
						for (int z = 0; z < thicknessZ; z++) {

							BlockState state1 = Blocks.STONE.getDefaultState();
							if (y == 0 || y == layerHeight - 1) {
								state1 = CubliminalBlocks.GABBRO.getDefaultState();
							}

							region.setBlockState(pos.add(x, y, z), state1, 0);
						}
					}
				}
			}

			for (Face face : Face.values()) {
				Vec2i adjCell = state.getPosition().go(face);
				boolean parking = ((ClusteredDepthFirstMaze) maze).getParkingSpots().contains(adjCell);
				if (parking) {
					String key = state.goes(face) ? "entrance" : "e";
					generateNbt(region, pos, nbtGroup.pick(key, random), MazeUtil.get(face));
				}
			}
		}
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkGenerationContext context,
												  BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk) {
		BlockPos startPos = chunk.getPos().getStartPos();
		this.mazeGenerator.generateMaze(startPos, region, this.getWorldHeight(), this::generateMaze, this::decorateMaze);

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public CompletableFuture<Chunk> populateBiomes(NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		return CompletableFuture.supplyAsync(() -> {
			((ChunkAccessor) chunk).cubliminal$populateBiomes(this.biomeSource);
			return chunk;
		}, Util.getMainWorkerExecutor().named("init_biomes"));
	}

	@Override
	protected void modifyStructure(ChunkRegion region, BlockPos pos, BlockState state, Optional<NbtCompound> blockEntityNbt, int update) {
		if (state.isAir()) {
			return;
		}

		super.modifyStructure(region, pos, state, blockEntityNbt, update);

		Supplier<Random> random = () -> Random.create(region.getSeed() + LimlibHelper.blockSeed(pos));

		if (state.isOf(Blocks.TUFF_BRICKS)) {
			if (random.get().nextFloat() > 0.8) {
				region.setBlockState(pos, Blocks.POLISHED_TUFF.getDefaultState(), 0);
			}
		} else if (state.isOf(Blocks.YELLOW_CONCRETE)) {
			if (random.get().nextFloat() < 0.8) {
				region.setBlockState(pos, Blocks.STONE.getDefaultState(), 0);
			} else {
				region.setBlockState(pos, Blocks.ANDESITE.getDefaultState(), 0);
			}
		} else if (state.isOf(CubliminalBlocks.WET_GRAY_ASPHALT)) {
			if (random.get().nextFloat() > 0.4) {
				region.setBlockState(pos, CubliminalBlocks.GRAY_ASPHALT.getDefaultState(), 0);
			}
		} else if (state.getBlock() instanceof RotatableLightBlock) {
			if (random.get().nextFloat() > 0.9) {
				region.setBlockState(pos, state.with(Properties.LIT, false), 0);
			}
		} else if (state.isOf(CubliminalBlocks.SMOKE_DETECTOR)) {
			if (random.get().nextFloat() > 0.1) {
				region.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
			}
		} else if (state.isOf(Blocks.ANDESITE_STAIRS)) {
			if (random.get().nextFloat() > 0.5) {
				region.setBlockState(pos, Blocks.ANDESITE.getDefaultState(), 0);
			}
		} else if (state.isOf(Blocks.STONE_STAIRS)) {
			if (random.get().nextFloat() > 0.5) {
				region.setBlockState(pos, Blocks.STONE.getDefaultState(), 0);
			}
		} else if (state.isOf(Blocks.BLUE_CONCRETE)) {
			if (random.get().nextFloat() > 0.03) {
				region.setBlockState(pos, Blocks.STONE.getDefaultState(), 0);
			} else {
				region.setBlockState(pos, Blocks.WATER.getDefaultState(), 0);
				region.scheduleFluidTick(pos, Fluids.WATER, 0);
			}
		} else if (state.isOf(CubliminalBlocks.WOODEN_CRATE)) {
			if (random.get().nextFloat() > 0.7) {
				region.setBlockState(pos, Blocks.DARK_OAK_PLANKS.getDefaultState(), 0);
			}
		}
	}

	@Override
	public int getPlacementRadius() {
		return 8;
	}

	@Override
	public int getMinimumY() {
		return this.level.min_y;
	}

	@Override
	public int getWorldHeight() {
		return this.level.world_height;
	}

	@Override
	public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
	}

	@Override
	public Level getLevel() {
		return this.level;
	}
}
