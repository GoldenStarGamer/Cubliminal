package net.limit.cubliminal.world.chunk;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.limit.cubliminal.init.CubliminalRegistrar;
import net.limit.cubliminal.world.maze.GrandMazeGenerator;
import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.*;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkGenerationContext;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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

	private GrandMazeGenerator mazeGenerator;
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
		this.mazeGenerator = new GrandMazeGenerator(this.mazeWidth, this.mazeHeight, this.mazeDilation,
				this.mazeSeedModifier);
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

	public MazeComponent newGrandMaze(ChunkRegion region, Vec2i mazePos, int width, int height, Random random) {
		// Find the position of the grandMaze that contains the current maze
		BlockPos grandMazePos = new BlockPos(
				mazePos.getX() - Math
						.floorMod(mazePos.getX(), (mazeGenerator.width * mazeGenerator.width * mazeGenerator.thicknessX)),
				0, mazePos.getY() - Math
				.floorMod(mazePos.getY(), (mazeGenerator.height * mazeGenerator.height * mazeGenerator.thicknessY)));
		// Check if the grandMaze was already generated, if not generate it
		MazeComponent grandMaze;

		if (mazeGenerator.grandMazeMap.containsKey(grandMazePos)) {
			grandMaze = mazeGenerator.grandMazeMap.get(grandMazePos);
		} else {
			grandMaze = new DepthFirstMaze(mazeGenerator.width / mazeGenerator.dilation,
					mazeGenerator.height / mazeGenerator.dilation,
					Random.create(
									LimlibHelper.blockSeed(grandMazePos.getX(), mazeGenerator.seedModifier, grandMazePos.getZ())));
			grandMaze.generateMaze();
			mazeGenerator.grandMazeMap.put(grandMazePos, grandMaze);
		}

		// Get the cell of the grandMaze that corresponds to the current maze
		CellState originCell = grandMaze
				.cellState(
						(((mazePos.getX() - grandMazePos
								.getX()) / mazeGenerator.thicknessX) / mazeGenerator.width) / mazeGenerator.dilation,
						(((mazePos.getY() - grandMazePos.getZ()) / mazeGenerator.thicknessY) / height) / mazeGenerator.dilation);
		Vec2i start = null;
		List<Vec2i> endings = Lists.newArrayList();

		// Check if the origin cell has an opening in the south or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set.
		if (originCell.goesDown() || originCell.getPosition().getX() == 0) {

			if (start == null) {
				start = new Vec2i(0, (mazeGenerator.height / mazeGenerator.dilation) / 2);
			}

		}

		// Check if the origin cell has an opening in the west or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set.
		if (originCell.goesLeft() || originCell.getPosition().getY() == 0) {

			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, 0);
			} else {
				endings.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, 0));
			}

		}

		// Check if the origin cell has an opening in the north or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set. Else add an ending point to the middle of that side.
		if (originCell.goesUp() || originCell.getPosition().getX() == (mazeGenerator.width / mazeGenerator.dilation) - 1) {

			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) - 1,
						(mazeGenerator.height / mazeGenerator.dilation) / 2);
			} else {
				endings
						.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) - 1,
								(mazeGenerator.height / mazeGenerator.dilation) / 2));
			}

		}

		// Check if the origin cell has an opening in the east or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set. Else add an ending point to the middle of that side.
		if (originCell
				.goesRight() || originCell.getPosition().getY() == (mazeGenerator.height / mazeGenerator.dilation) - 1) {

			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2,
						(mazeGenerator.height / mazeGenerator.dilation) - 1);
			} else {
				endings
						.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2,
								(mazeGenerator.height / mazeGenerator.dilation) - 1));
			}

		}

		// If the origin cell is a dead end, add a random ending point in the middle of
		// the maze. This ensures there is always somewhere to go in a dead end.
		if (endings.isEmpty()) {
			endings
					.add(new Vec2i(random.nextInt((mazeGenerator.width / mazeGenerator.dilation) - 2) + 1,
							random.nextInt((mazeGenerator.height / mazeGenerator.dilation) - 2) + 1));
		}

		// Create a new maze.
		MazeComponent mazeToSolve = new DepthFirstMaze(mazeGenerator.width / mazeGenerator.dilation,
				mazeGenerator.height / mazeGenerator.dilation, random);
		mazeToSolve.generateMaze();
		// Create a maze solver and solve the maze using the starting point and ending
		// points.
		MazeComponent solvedMaze = new DepthFirstMazeSolver(mazeToSolve, random, start, endings.toArray(new Vec2i[0]));
		solvedMaze.generateMaze();
		// Create a scaled maze using the dilation.
		MazeComponent dilatedMaze = new DilateMaze(solvedMaze, mazeGenerator.dilation);
		dilatedMaze.generateMaze();
		Vec2i starting = new Vec2i(random.nextInt((dilatedMaze.width / 2) - 2) + 1,
				random.nextInt((dilatedMaze.height / 2) - 2) + 1);
		Vec2i ending = new Vec2i(random.nextInt((dilatedMaze.width / 2) - 2) + 1,
				random.nextInt((dilatedMaze.height / 2) - 2) + 1);
		// Make a new maze
		MazeComponent overlayMaze = new DepthFirstMaze(dilatedMaze.width / 2, dilatedMaze.height / 2, random);
		overlayMaze.generateMaze();
		// Find a path along two random points
		MazeComponent solvedOverlay = new DepthFirstMazeSolver(overlayMaze, random, starting, ending);
		solvedOverlay.generateMaze();
		// Make it bigger
		MazeComponent dilatedOverlay = new DilateMaze(solvedOverlay, 2);
		dilatedOverlay.generateMaze();
		// Combine the two
		CombineMaze combinedMaze = new CombineMaze(dilatedMaze, dilatedOverlay);
		combinedMaze.generateMaze();

		return combinedMaze;
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
		//this.mazeGenerator.generateMaze(new MazeComponent.Vec2i(chunk.getPos().getStartPos()), region, this::newGrandMaze, this::decorateCell);
		BlockPos startPos = chunk.getPos().getStartPos();

		Optional<RegistryKey<Biome>> biomeKey = context.world().getBiome(startPos.add(1, 0, 1)).getKey();

		if (biomeKey.isEmpty()) return CompletableFuture.completedFuture(chunk);
		if (biomeKey.get().equals(CubliminalBiomes.PARKING_ZONE_BIOME)) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					region.setBlockState(startPos.add(x, 0, z), Blocks.SHROOMLIGHT.getDefaultState(), 0);
				}
			}
		}
		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	protected void modifyStructure(ChunkRegion region, BlockPos pos, BlockState state, Optional<NbtCompound> blockEntityNbt) {

		if (state.isAir() || state.isOf(Blocks.LIGHT)) return;
		super.modifyStructure(region, pos, state, blockEntityNbt);
	}

	@Override
	protected RegistryKey<LootTable> getContainerLootTable(LootableContainerBlockEntity container) {
		if (container.getLootTable() != null) {
			container.setLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, Cubliminal.id("barrels/the_lobby/0")));
			return RegistryKey.of(RegistryKeys.LOOT_TABLE, Cubliminal.id("barrels/the_lobby/0"));
		} else return null;
	}

	@Override
	public int getPlacementRadius() {
		return 1;
	}

	@Override
	public int getWorldHeight() {
		return 100;
	}

	@Override
	public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
	}
}
