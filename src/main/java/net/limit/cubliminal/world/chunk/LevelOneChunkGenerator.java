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
import net.limit.cubliminal.world.maze.StraightDepthFirstMaze;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.*;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.*;
import net.minecraft.block.*;
import net.minecraft.nbt.NbtCompound;
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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class LevelOneChunkGenerator extends AbstractNbtChunkGenerator {
	public static final MapCodec<LevelOneChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
			(instance) -> instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter(
					(chunkGenerator) -> chunkGenerator.biomeSource), NbtGroup.CODEC.fieldOf("group").stable().forGetter(
							(chunkGenerator) -> chunkGenerator.nbtGroup), Codec.INT.fieldOf("maze_width").stable().forGetter(
									(chunkGenerator) -> chunkGenerator.mazeWidth), Codec.INT.fieldOf("maze_height").stable().forGetter(
											(chunkGenerator) -> chunkGenerator.mazeHeight), Codec.INT.fieldOf("maze_dilation").stable().forGetter(
													(chunkGenerator) -> chunkGenerator.mazeDilation), Codec.LONG.fieldOf("seed_modifier").stable().forGetter(
															(chunkGenerator) -> chunkGenerator.mazeSeedModifier))
					.apply(instance, instance.stable(LevelOneChunkGenerator::new)));

	private MazeGenerator<MazeComponent> mazeGenerator;
	private int mazeWidth;
	private int mazeHeight;
	private int mazeDilation;
	private long mazeSeedModifier;

	public LevelOneChunkGenerator(BiomeSource biomeSource, NbtGroup group, int mazeWidth, int mazeHeight,
								  int mazeDilation, long mazeSeedModifier) {
		super(biomeSource, group);
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		this.mazeDilation = mazeDilation;
		this.mazeSeedModifier = mazeSeedModifier;
		this.mazeGenerator = new MazeGenerator<>(mazeWidth, mazeHeight, 8, 8, 0);
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

	public MazeComponent newMaze(ChunkRegion region, Vec2i mazePos, int width, int height, Random random) {
		StraightDepthFirstMaze maze = new StraightDepthFirstMaze(width, height, random, 0.45);
		maze.generateMaze();
		return maze;
	}

	public void decorateCell(ChunkRegion region, Vec2i pos, Vec2i mazePos, MazeComponent maze, CellState state, Vec2i thickness, Random random) {
		Pair<MazePiece, Manipulation> piece = MazePiece.getFromCell(state, random);

		if (piece.getFirst() != MazePiece.E) {
			generateNbt(region, pos.toBlock(), this.nbtGroup.pick(piece.getFirst().getAsLetter(), random), piece.getSecond());
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
					region.setBlockState(startPos.add(x, 0, z), Blocks.SHROOMLIGHT.getDefaultState(), 0);
				}
			}

			/*
			for (Direction direction : Direction.values()) {
				if (direction.getAxis() != Direction.Axis.Y) {

					BlockPos adjPos = startPos.add(direction.getVector().multiply(16)).add(8, 0, 8);
					// Some adjacent chunks might be still empty, so we have to make use of the biome source
					//ChunkSectionPos sectionPos = ChunkSectionPos.from(adjPos);
					RegistryEntry.Reference<Biome> adjBiome = ((LevelOneBiomeSource) this.biomeSource)
							.calcBiome(adjPos, chunk.getHeightLimitView().getBottomSectionCoord());

					if (adjBiome.matchesKey(CubliminalBiomes.HABITABLE_ZONE_BIOME)) {
                        Cubliminal.LOGGER.info("Pos: {}, chunkOrigin: {}, ref: {}", startPos.add(8, 1, 8).add(direction.getVector().multiply(6)), startPos, adjPos);
						region.setBlockState(startPos.add(8, 1, 8).add(direction.getVector().multiply(6)),
								Blocks.DIAMOND_BLOCK.getDefaultState(), 0);
					}
				}
			}
			 */


		} else {
			this.mazeGenerator.generateMaze(new MazeComponent.Vec2i(startPos), region, this::newMaze, this::decorateCell);
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
	protected void modifyStructure(ChunkRegion region, BlockPos pos, BlockState state, Optional<NbtCompound> blockEntityNbt) {

		if (state.isAir() || state.isOf(Blocks.LIGHT)) return;
		super.modifyStructure(region, pos, state, blockEntityNbt);
	}

	@Override
	public int getPlacementRadius() {
		return 1;
	}

	@Override
	public int getWorldHeight() {
		return 64;
	}

	@Override
	public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
	}
}
