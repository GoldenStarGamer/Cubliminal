package net.limit.cubliminal.world.chunk;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.block.custom.template.RotatableLightBlock;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.limit.cubliminal.init.CubliminalBlocks;
import net.limit.cubliminal.init.CubliminalRegistrar;
import net.limit.cubliminal.access.ChunkAccessor;
import net.limit.cubliminal.level.Level;
import net.limit.cubliminal.level.LevelWithClusteredMaze;
import net.limit.cubliminal.util.MazeUtil;
import net.limit.cubliminal.world.biome.source.LevelOneBiomeSource;
import net.limit.cubliminal.world.maze.ClusteredDepthFirstMaze;
import net.limit.cubliminal.world.maze.MultiFloorMazeGenerator;
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
import net.minecraft.util.math.Direction;
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
import java.util.function.BiFunction;

public class LevelOneChunkGenerator extends AbstractNbtChunkGenerator implements BackroomsLevel {
	public static final MapCodec<LevelOneChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			LevelOneBiomeSource.CODEC.fieldOf("biome_source").stable().forGetter(chunkGenerator -> chunkGenerator.biomeSource),
			NbtGroup.CODEC.fieldOf("group").stable().forGetter(chunkGenerator -> chunkGenerator.nbtGroup),
			LevelWithClusteredMaze.LEVEL_WITH_CLUSTERED_MAZE_CODEC.fieldOf("level").stable().forGetter(chunkGenerator -> chunkGenerator.level)
	).apply(instance, instance.stable(LevelOneChunkGenerator::new)));

	private final LevelOneBiomeSource biomeSource;
	private final MultiFloorMazeGenerator<MazeComponent> mazeGenerator;
	private final LevelWithClusteredMaze level;
	private final int thicknessX;
	private final int thicknessZ;
	private final int layerHeight;
	private final int clusterSizeX;
	private final int clusterSizeZ;
	private final int cellOverheadX;
	private final int cellOverheadZ;

	public LevelOneChunkGenerator(LevelOneBiomeSource biomeSource, NbtGroup group, LevelWithClusteredMaze level) {
		super(biomeSource, group);
		this.biomeSource = biomeSource;
		this.level = level;
		this.thicknessX = level.spacing_x;
		this.thicknessZ = level.spacing_z;
		this.layerHeight = level.layer_height;
		this.clusterSizeX = level.cluster_size_x;
		this.clusterSizeZ = level.cluster_size_z;
		this.cellOverheadX = level.biome_block_offset / this.thicknessX;
		this.cellOverheadZ = level.biome_block_offset / this.thicknessZ;
		this.mazeGenerator = new MultiFloorMazeGenerator<>(level.maze_width, level.maze_height, this.thicknessX, this.layerHeight, this.thicknessZ, level.maze_seed_modifier);
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
			.build();
	}

	@Override
	protected MapCodec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	public MazeComponent optimizedNewMaze(ChunkRegion region, BlockPos mazePos, int width, int height, Random random) {

		Random randomUp = Random.create(LimlibHelper.blockSeed(mazePos.add(height * thicknessZ - 1, 0, 0)));
		Random randomDown = Random.create(LimlibHelper.blockSeed(mazePos.add(-1, 0, 0)));
		Random randomLeft = Random.create(LimlibHelper.blockSeed(mazePos));
		Random randomRight = Random.create(LimlibHelper.blockSeed(mazePos.add(0, 0, width * thicknessX)));

		// Include 4 additional connections to other mazes
		// Note that connections are saved for later use
		List<Vec2i> connections = new ArrayList<>();
		connections.add(new Vec2i(width - 1, randomUp.nextInt(height)));
		connections.add(new Vec2i(0, randomDown.nextInt(height)));
		connections.add(new Vec2i(randomLeft.nextInt(width), 0));
		connections.add(new Vec2i(randomRight.nextInt(width), height - 1));

		List<Vec2i> checkpoints = new ArrayList<>(connections);

		// Check cell cluster biomes as well if no lists have been generated
		Vec2i mazeVec = new Vec2i(mazePos.getX(), mazePos.getZ());
		// All the parking cells within the maze
		boolean areSpotsRegistered = this.mazeGenerator.isIn(mazeVec);
		List<Vec2i> parkingSpots = areSpotsRegistered ? this.mazeGenerator.getParkingSpots(mazeVec) : new ArrayList<>();
		Map<Vec2i, List<Vec2i>> parkingClusters = new HashMap<>();

		for (int x = -this.cellOverheadX; x < width + this.cellOverheadX; x+= this.clusterSizeX) {
			for (int y = -this.cellOverheadZ; y < height + this.cellOverheadZ; y+= this.clusterSizeZ) {

				Vec2i currentCell = new Vec2i(x, y);
				boolean isParking = parkingSpots.contains(currentCell);
				if (!isParking) {
					// Retrieve the biome at that cluster if not already computed
					RegistryEntry<Biome> biome = biomeSource.calcBiome(mazePos.add(x * thicknessX, 0, y * thicknessZ));
					isParking = biome.matchesKey(CubliminalBiomes.PARKING_ZONE_BIOME);
				}

				if (isParking) {
					// Add all the cells from the cluster to the list if they're inside the maze
					List<Vec2i> clusterCells = new ArrayList<>();
					for (int dx = 0; dx < this.clusterSizeX; dx++) {
						for (int dy = 0; dy < this.clusterSizeZ; dy++) {
							Vec2i component = currentCell.add(dx, dy);
							if (MazeUtil.fits(component, width, height)) {
								clusterCells.add(component);
							}
						}
					}
					if (!areSpotsRegistered) {
						parkingSpots.addAll(clusterCells);
					}
					parkingClusters.putIfAbsent(currentCell, clusterCells);
				}
			}
		}

		// Perform dfs for each parking zone to choose a few checkpoints
		while (!parkingClusters.isEmpty()) {
			Vec2i start = parkingClusters.keySet().stream().toList().getFirst();
			Stack<Vec2i> stack = new Stack<>();
			stack.push(start);
			// Parking zone cells
			List<Vec2i> parkingZone = new ArrayList<>(parkingClusters.remove(start));
			Vec2i current;
			while (!parkingClusters.isEmpty()) {
				current = stack.peek();
				List<Vec2i> neighbours = new ArrayList<>();
				for (Face face : Face.values()) {
					Direction.Axis adjAxis = MazeUtil.getDirection(face).getAxis();
					Vec2i neighbour = current.go(face, adjAxis == Direction.Axis.X ? this.clusterSizeX : this.clusterSizeZ);
					// If it's a parking cluster include as a valid neighbour
					if (parkingClusters.containsKey(neighbour)) {
						neighbours.add(neighbour);
					}
				}

				if (!neighbours.isEmpty()) {
					// Choose a random neighbour
					Vec2i randomNeighbour = neighbours.get(random.nextInt(neighbours.size()));
					parkingZone.addAll(parkingClusters.remove(randomNeighbour));
					stack.push(randomNeighbour);
				} else {
					stack.pop();
					// Break if we're back in the start and have no neighbours
					if (stack.isEmpty()) {
						break;
					}
				}
			}
			// Choose a random checkpoint to connect this parking zone
			if (!parkingZone.isEmpty()) {
				checkpoints.add(parkingZone.get(random.nextInt(parkingZone.size())));
			}
		}

		if (!areSpotsRegistered) {
			this.mazeGenerator.setParkingSpots(mazeVec, parkingSpots);
		}

		MazeComponent maze = new ClusteredDepthFirstMaze(width, height, mazePos, random, 0.2f, checkpoints, parkingSpots);
		for (int i = 0; i < connections.size(); ++i) {
			maze.cellState(connections.get(i)).go(Face.values()[i]);
		}

		maze.generateMaze();

		return maze;
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
		this.mazeGenerator.generateMaze(startPos, region, this.getWorldHeight(), this::optimizedNewMaze, this::decorateCell);

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public CompletableFuture<Chunk> populateBiomes(NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		return CompletableFuture.supplyAsync(() -> {
			((ChunkAccessor) chunk).cubliminal$levelOne(this.biomeSource);
			return chunk;
		}, Util.getMainWorkerExecutor().named("init_biomes"));
	}

	@Override
	protected void modifyStructure(ChunkRegion region, BlockPos pos, BlockState state, Optional<NbtCompound> blockEntityNbt, int update) {
		if (state.isAir()) {
			return;
		}

		super.modifyStructure(region, pos, state, blockEntityNbt, update);

		BiFunction<ChunkRegion, BlockPos, Random> random = (region1, pos1) -> Random.create(region1.getSeed() + LimlibHelper.blockSeed(pos1));

		if (state.isOf(Blocks.TUFF_BRICKS)) {
			if (random.apply(region, pos).nextFloat() > 0.8) {
				region.setBlockState(pos, Blocks.POLISHED_TUFF.getDefaultState(), 0);
			}
		} else if (state.isOf(Blocks.YELLOW_CONCRETE)) {
			if (random.apply(region, pos).nextFloat() < 0.8) {
				region.setBlockState(pos, Blocks.STONE.getDefaultState(), 0);
			} else {
				region.setBlockState(pos, Blocks.ANDESITE.getDefaultState(), 0);
			}
		} else if (state.isOf(CubliminalBlocks.WET_GRAY_ASPHALT)) {
			if (random.apply(region, pos).nextFloat() > 0.4) {
				region.setBlockState(pos, CubliminalBlocks.GRAY_ASPHALT.getDefaultState(), 0);
			}
		} else if (state.getBlock() instanceof RotatableLightBlock) {
			if (random.apply(region, pos).nextFloat() > 0.9) {
				region.setBlockState(pos, state.with(Properties.LIT, false), 0);
			}
		} else if (state.isOf(CubliminalBlocks.SMOKE_DETECTOR)) {
			if (random.apply(region, pos).nextFloat() > 0.1) {
				region.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
			}
		} else if (state.isOf(Blocks.ANDESITE_STAIRS)) {
			if (random.apply(region, pos).nextFloat() > 0.5) {
				region.setBlockState(pos, Blocks.ANDESITE.getDefaultState(), 0);
			}
		} else if (state.isOf(Blocks.STONE_STAIRS)) {
			if (random.apply(region, pos).nextFloat() > 0.5) {
				region.setBlockState(pos, Blocks.STONE.getDefaultState(), 0);
			}
		} else if (state.isOf(Blocks.BLUE_CONCRETE)) {
			if (random.apply(region, pos).nextFloat() > 0.03) {
				region.setBlockState(pos, Blocks.STONE.getDefaultState(), 0);
			} else {
				region.setBlockState(pos, Blocks.WATER.getDefaultState(), 0);
				region.scheduleFluidTick(pos, Fluids.WATER, 0);
			}
		} else if (state.isOf(CubliminalBlocks.WOODEN_CRATE)) {
			if (random.apply(region, pos).nextFloat() > 0.7) {
				region.setBlockState(pos, Blocks.DARK_OAK_PLANKS.getDefaultState(), 0);
			}
		}
	}

	@Override
	public int getPlacementRadius() {
		return 1;
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
