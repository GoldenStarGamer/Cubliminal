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

	private MultiFloorMazeGenerator<MazeComponent> mazeGenerator;
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

		// Check 4x4 cell cluster biomes as well, include into 'must visit' list to avoid inescapable parking zones
		for (int x = 0; x < width; x += 4) {
			for (int y = 0; y < height; y += 4) {

				RegistryEntry.Reference<Biome> biome = ((LevelOneBiomeSource) biomeSource)
						.calcBiome(mazePos.add(x * thicknessX, 0, y * thicknessZ), bottomSectionCoord);

				if (biome.matchesKey(CubliminalBiomes.PARKING_ZONE_BIOME)) {
					Random randomX = Random.create(LimlibHelper.blockSeed(mazePos.add(x, 0, 0)));
					Random randomY = Random.create(LimlibHelper.blockSeed(mazePos.add(0, 0, y)));
					Vec2i randomCell = new Vec2i(x + randomX.nextInt(4), y + randomY.nextInt(4));

					// Add a random cell from the 4x4 cluster
					if (!checkpoints.contains(randomCell)) {
						checkpoints.add(randomCell);
					}
				}
			}
		}

		//Cubliminal.LOGGER.info("Maze: {}; Checkpoints: {}", mazePos, checkpoints);

		MazeComponent maze = new ClusteredDepthFirstMaze(width, height, random, 0.05f, checkpoints);
		for (int i = 0; i < connections.size(); ++i) {
			maze.cellState(connections.get(i)).go(Face.values()[i]);
		}
		maze.generateMaze();

		return maze;
	}

	public MazeComponent $newMaze2D(ChunkRegion region, Vec2i mazePos, int width, int height, Random random) {
		List<Vec2i> checkpoints = Lists.newArrayList();

		Random randomUp = Random.create(LimlibHelper.blockSeed(mazePos.up(height * thicknessZ - 1).toBlock()));
		Random randomDown = Random.create(LimlibHelper.blockSeed(mazePos.down().toBlock()));
		Random randomLeft = Random.create(LimlibHelper.blockSeed(mazePos.toBlock()));
		Random randomRight = Random.create(LimlibHelper.blockSeed(mazePos.right(width * thicknessX).toBlock()));

		// Include 4 additional connections to other mazes
		List<Vec2i> connections = Lists.newArrayList();

		connections.add(new Vec2i(width - 1, randomUp.nextInt(height)));
		connections.add(new Vec2i(0, randomDown.nextInt(height)));
		connections.add(new Vec2i(randomLeft.nextInt(width), 0));
		connections.add(new Vec2i(randomRight.nextInt(width), height - 1));

		checkpoints.addAll(connections);

		// Check 4x4 cell cluster biomes as well, include into 'must visit' list to avoid inescapable parking zones
		for (int x = 0; x < width; x += 4) {
			for (int y = 0; y < height; y += 4) {

				RegistryEntry.Reference<Biome> biome = ((LevelOneBiomeSource) biomeSource)
						.calcBiome(mazePos.add(x * thicknessX, y * thicknessZ).toBlock(), bottomSectionCoord);

				if (biome.matchesKey(CubliminalBiomes.PARKING_ZONE_BIOME)) {
					Random randomX = Random.create(LimlibHelper.blockSeed(mazePos.add(x, 0).toBlock()));
					Random randomY = Random.create(LimlibHelper.blockSeed(mazePos.add(0, y).toBlock()));
					Vec2i randomCell = new Vec2i(x + randomX.nextInt(4), y + randomY.nextInt(4));

					// Add a random cell from the 4x4 cluster
					if (!checkpoints.contains(randomCell)) {
						checkpoints.add(randomCell);
					}
				}
			}
		}

        //Cubliminal.LOGGER.info("Maze: {}; Checkpoints: {}", mazePos, checkpoints);

		MazeComponent maze = new ClusteredDepthFirstMaze(width, height, random, 0.05f, checkpoints);
		for (int i = 0; i < connections.size(); ++i) {
			maze.cellState(connections.get(i)).go(Face.values()[i]);
		}
		maze.generateMaze();

		return maze;
	}

	public void $decorateCell2D(ChunkRegion region, Vec2i pos, Vec2i mazePos, MazeComponent maze, CellState state, Vec2i thickness, Random random) {
		Pair<MazePiece, Manipulation> piece = MazePiece.getFromCell(state, random);

		if (piece.getFirst() != MazePiece.E) {
			generateNbt(region, pos.toBlock(), this.nbtGroup.pick(piece.getFirst().getAsLetter(), random), piece.getSecond());
		}
	}

	public void decorateCell(ChunkRegion region, BlockPos pos, BlockPos mazePos, MazeComponent maze, CellState state, BlockPos thickness, Random random) {
		Pair<MazePiece, Manipulation> piece = MazePiece.getFromCell(state, random);

		if (piece.getFirst() != MazePiece.E) {
			generateNbt(region, pos, this.nbtGroup.pick(piece.getFirst().getAsLetter(), random), piece.getSecond());
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
