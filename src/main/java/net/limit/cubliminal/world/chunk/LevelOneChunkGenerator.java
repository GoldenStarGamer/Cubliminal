package net.limit.cubliminal.world.chunk;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.limit.cubliminal.init.CubliminalRegistrar;
import net.limit.cubliminal.util.ChunkAccessor;
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
import net.minecraft.registry.entry.RegistryEntry;
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
import java.util.concurrent.CompletableFuture;

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

		MazeComponent maze = new ClusteredDepthFirstMaze(width, height, mazePos, random, 0.05f, checkpoints, parkingSpots);
		for (int i = 0; i < connections.size(); ++i) {
			maze.cellState(connections.get(i)).go(Face.values()[i]);
		}

		maze.generateMaze();

		return maze;
	}

	public void decorateCell(ChunkRegion region, BlockPos pos, BlockPos mazePos, MazeComponent maze, CellState state, BlockPos thickness, Random random) {
		Pair<MazePiece, Manipulation> piece = MazePiece.getFromCell(state, random);

		if (piece.getFirst() != MazePiece.E) {
			generateNbt(region, pos, this.nbtGroup.pick(piece.getFirst().getAsLetter(), random), piece.getSecond());
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
		}
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkGenerationContext context,
												  BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk) {
		BlockPos startPos = chunk.getPos().getStartPos();
		RegistryEntry.Reference<Biome> biome = ((LevelOneBiomeSource) this.biomeSource)
				.calcBiome(startPos, chunk.getHeightLimitView().getBottomSectionCoord());

		if (biome.matchesKey(CubliminalBiomes.PARKING_ZONE_BIOME)) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					region.setBlockState(startPos.add(x, this.getMinimumY(), z), Blocks.SHROOMLIGHT.getDefaultState(), 0);
					region.setBlockState(startPos.add(x, this.getMinimumY() + this.layerThickness, z), Blocks.SHROOMLIGHT.getDefaultState(), 0);
				}
			}

		} else {
			this.bottomSectionCoord = chunk.getBottomSectionCoord();
			this.mazeGenerator.generateMaze(startPos, region, this.getWorldHeight(), this::newMaze, this::decorateCell);
		}

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
