package net.limit.cubliminal.world.chunk;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.limit.cubliminal.init.CubliminalBlocks;
import net.limit.cubliminal.init.CubliminalRegistrar;
import net.limit.cubliminal.access.ChunkAccessor;
import net.limit.cubliminal.util.Manip;
import net.limit.cubliminal.world.biome.level_1.LevelOneBiomeSource;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkGenerationContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class LevelOneChunkGenerator extends AbstractNbtChunkGenerator {
	public static final MapCodec<LevelOneChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
			(instance) -> instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter(
					(chunkGenerator) -> chunkGenerator.biomeSource), NbtGroup.CODEC.fieldOf("group").stable().forGetter(
							(chunkGenerator) -> chunkGenerator.nbtGroup), Codec.INT.fieldOf("maze_width").stable().forGetter(
									(chunkGenerator) -> chunkGenerator.mazeWidth), Codec.INT.fieldOf("maze_height").stable().forGetter(
											(chunkGenerator) -> chunkGenerator.mazeHeight), Codec.LONG.fieldOf("seed_modifier").stable().forGetter(
															(chunkGenerator) -> chunkGenerator.mazeSeedModifier))
					.apply(instance, instance.stable(LevelOneChunkGenerator::new)));

	private final MultiFloorMazeGenerator<MazeComponent> mazeGenerator;
	private final int mazeWidth;
	private final int mazeHeight;
	private final int thicknessX;
	private final int layerThickness;
	private final int thicknessZ;
	private final long mazeSeedModifier;
	private int bottomSectionCoord;

	public LevelOneChunkGenerator(BiomeSource biomeSource, NbtGroup group, int mazeWidth, int mazeHeight, long mazeSeedModifier) {
		super(biomeSource, group);
		this.thicknessX = 16;
		this.layerThickness = this.getWorldHeight() / 2;
		this.thicknessZ = 16;
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		this.mazeSeedModifier = mazeSeedModifier;
		this.mazeGenerator = new MultiFloorMazeGenerator<>(mazeWidth, mazeHeight, this.thicknessX, this.layerThickness, this.thicknessZ, 0);
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
			.build();
	}

	@Override
	protected MapCodec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	public MazeComponent newMaze(ChunkRegion region, BlockPos mazePos, int width, int height, Random random) {
		List<Vec2i> checkpoints = Lists.newArrayList();

		Random randomUp = Random.create(LimlibHelper.blockSeed(mazePos.add(height * thicknessZ - 1, 0, 0)));
		Random randomDown = Random.create(LimlibHelper.blockSeed(mazePos.add(-1, 0, 0)));
		Random randomLeft = Random.create(LimlibHelper.blockSeed(mazePos));
		Random randomRight = Random.create(LimlibHelper.blockSeed(mazePos.add(0, 0, width * thicknessX)));

		// Include 4 additional connections to other mazes
		List<Vec2i> connections = Lists.newArrayList();

		connections.add(new Vec2i(width - 1, randomUp.nextInt(height)));
		connections.add(new Vec2i(0, randomDown.nextInt(height)));
		connections.add(new Vec2i(randomLeft.nextInt(width), 0));
		connections.add(new Vec2i(randomRight.nextInt(width), height - 1));

		checkpoints.addAll(connections);

		// Check 4x4 cell cluster biomes as well if no lists have been generated
		Vec2i mazeVec = new Vec2i(mazePos.getX(), mazePos.getZ());
		boolean areSpotsRegistered = this.mazeGenerator.isIn(mazeVec);
		// All the parking cells within the maze
		List<Vec2i> parkingSpots = areSpotsRegistered ? this.mazeGenerator.getParkingSpots(mazeVec) : Lists.newArrayList();

		for (int x = 0; x < width; x += 4) {
			for (int y = 0; y < height; y += 4) {
				if (!areSpotsRegistered) {

					RegistryEntry.Reference<Biome> biome = ((LevelOneBiomeSource) biomeSource)
							.calcBiome(mazePos.add(x * thicknessX, 0, y * thicknessZ), bottomSectionCoord);

					if (biome.matchesKey(CubliminalBiomes.PARKING_ZONE_BIOME)) {
						List<Vec2i> clusterSpots = Lists.newArrayList();
						// Add all the cells from the cluster to a temp list to later choose a random one
						for (int dx = 0; dx < 4; dx++) {
							for (int dy = 0; dy < 4; dy++) {
								clusterSpots.add(new Vec2i(x + dx, y + dy));
							}
						}
						parkingSpots.addAll(clusterSpots);

						// Add a random cell from the 4x4 cluster
						Vec2i randomCell = clusterSpots.get(random.nextInt(clusterSpots.size()));
						if (!checkpoints.contains(randomCell)) {
							checkpoints.add(randomCell);
						}
					}
				} else if (parkingSpots.contains(new Vec2i(x, y))) {

					List<Vec2i> clusterSpots = Lists.newArrayList();
					for (int dx = 0; dx < 4; dx++) {
						for (int dy = 0; dy < 4; dy++) {
							clusterSpots.add(new Vec2i(x + dx, y + dy));
						}
					}

					// Add a random cell from the 4x4 cluster
					Vec2i randomCell = clusterSpots.get(random.nextInt(clusterSpots.size()));
					if (!checkpoints.contains(randomCell)) {
						checkpoints.add(randomCell);
					}
				}
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
		RegistryEntry.Reference<Biome> biome = ((LevelOneBiomeSource) this.biomeSource)
				.calcBiome(pos, this.bottomSectionCoord);

		if (biome.matchesKey(CubliminalBiomes.PARKING_ZONE_BIOME)) {
			if (state.getExtra().containsKey("ramp")) {
				if (pos.getY() == this.getMinimumY()) {

					byte[] bytes = state.getExtra().get("ramp").getByteArray("ramp");
					generateNbt(region, pos, nbtGroup.nbtId("ramp", rotate(Face.values()[bytes[1]]).concat("_" + bytes[0])));
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
					for (int y = 0; y < layerThickness; y++) {
						for (int z = 0; z < thicknessZ; z++) {

							BlockState state1 = Blocks.STONE.getDefaultState();
							if (y == 0 || y == layerThickness - 1) {
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

				if (!parking) {
					BlockPos adjPos = mazePos.add(thicknessX * adjCell.getX(), 0, thicknessZ * adjCell.getY());
					parking = ((LevelOneBiomeSource) biomeSource).calcBiome(adjPos, bottomSectionCoord).matchesKey(CubliminalBiomes.PARKING_ZONE_BIOME);
				}
				if (parking) {
					if (state.goes(face)) {
						region.setBlockState(pos.add(8, 8, 8), Blocks.SHROOMLIGHT.getDefaultState(), 0);
					} else {
						generateNbt(region, pos, nbtGroup.pick("e", random), Manip.get(face));
					}
				}
			}
		}
	}

	private static String rotate(Face face) {
		return switch (face) {
            case UP -> "e";
            case DOWN -> "w";
            case LEFT -> "n";
            case RIGHT -> "s";
        };
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkGenerationContext context,
												  BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk) {
		BlockPos startPos = chunk.getPos().getStartPos();

		this.bottomSectionCoord = chunk.getBottomSectionCoord();
		this.mazeGenerator.generateMaze(startPos, region, this.getWorldHeight(), this::newMaze, this::decorateCell);

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public CompletableFuture<Chunk> populateBiomes(NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		return CompletableFuture.supplyAsync(() -> {
			((ChunkAccessor) chunk).cubliminal$populateBiomes(this.biomeSource, noiseConfig.getMultiNoiseSampler());
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
		} else if (state.isOf(CubliminalBlocks.VERTICAL_LIGHT_TUBE)) {
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
	public int getWorldHeight() {
		return 32;
	}

	@Override
	public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
	}
}
