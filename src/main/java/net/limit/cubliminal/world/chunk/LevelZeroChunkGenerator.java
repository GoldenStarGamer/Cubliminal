package net.limit.cubliminal.world.chunk;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.init.CubliminalBlocks;
import net.limit.cubliminal.init.CubliminalWorlds;
import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.minecraft.block.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class LevelZeroChunkGenerator extends AbstractNbtChunkGenerator {
	public static final Codec<LevelZeroChunkGenerator> CODEC = RecordCodecBuilder.create(
			(instance) -> instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter(
					(chunkGenerator) -> chunkGenerator.biomeSource), NbtGroup.CODEC.fieldOf("group").stable().forGetter(
							(chunkGenerator) -> chunkGenerator.nbtGroup), Codec.INT.fieldOf("floors").stable().forGetter(
									(chunkGenerator) -> chunkGenerator.floors)).apply(instance, instance.stable(LevelZeroChunkGenerator::new)));

	public final int floors;

	public LevelZeroChunkGenerator(BiomeSource biomeSource, NbtGroup group, int floors) {
		super(biomeSource, group);
		this.floors = floors;
	}

	public static NbtGroup createGroup() {
		return NbtGroup.Builder
			.create(Cubliminal.id(CubliminalWorlds.THE_LOBBY))
			.with("0space", 1, 1)
			.with("0column", 1, 2)
			.with("0corridor", 1, 2)
			.with("0midwall", 1, 1)
			.with("0wall", 1, 1)
			.with("0corner", 1, 1)
			.with("0thickcorner", 1, 1)
			.with("0thickwall", 1, 1)
			.with("0tinywall", 1, 2)
			.with("0twowalls", 1, 2)
			.with("preset", 1, 1)
			.with("manila_room", 1, 1)
			.with("manila_gateway", 1, 1)
			.build();
	}
	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	public void decorateRoom(ChunkRegion region, BlockPos pos, ServerWorld world){
		Random random = Random
			.create(region.getSeed() + LimlibHelper.blockSeed(pos));
		int randomInt = random.nextInt(15999);

		if (pos.equals(new BlockPos(0, pos.getY(), 0)) || randomInt < 3200 ) {
			// 1 : 5
			generateNbt(region, pos, nbtGroup.nbtId("0space", "0space_1"));
		} else if (randomInt < 5760) {
			// 1 : 5
			generateNbt(region, pos, nbtGroup.pick(nbtGroup
				.chooseGroup(random, "0column", "0corridor"), random), Manipulation.random(random));
		} else if (randomInt < 15872) {
			// 79 : 80
			generateNbt(region, pos, nbtGroup.pick(nbtGroup
				.chooseGroup(random, "0corner", "0wall", "0midwall"
					, "0thickcorner", "0thickwall", "0twowalls"), random), Manipulation.random(random));
		} else if (randomInt < 15888) {
			// 1 : 8
			generateNbt(region, pos, nbtGroup.pick("manila_gateway", random));
		} else {
			generateNbt(region, pos, nbtGroup.pick("0tinywall", random), Manipulation.random(random));
		}
	}
	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor,
												  ServerWorld world, ChunkGenerator generator,
												  StructureTemplateManager structureTemplateManager,
												  ServerLightingProvider lightingProvider, Function<Chunk,
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter, List<Chunk> chunks, Chunk chunk) {

		BlockPos startPos = chunk.getPos().getStartPos();
		ChunkPos chunkPos = new ChunkPos(startPos);
		int originX = startPos.getX();
		int originZ = startPos.getZ();
		int spacing = 6;
		int cellHeight = 6;
		BlockPos startCoords;

		if (originX >= 0 && originZ >= 0) {
			startCoords = chunkPos.getBlockPos(0, 0, 0);
		} else if (originX < 0 && originZ >= 0) {
			startCoords = chunkPos.getBlockPos(15, 0, 0);
		} else if (originX < 0) {
			startCoords = chunkPos.getBlockPos(15, 0, 15);
		} else {
			startCoords = chunkPos.getBlockPos(0, 0, 15);
		}

		int offsetX = spacing - Math.abs(startCoords.getX()) % spacing;
		int offsetZ = spacing - Math.abs(startCoords.getZ()) % spacing;
		if (offsetX == spacing) offsetX = 0;
		if (offsetZ == spacing) offsetZ = 0;
		int timesX = MathHelper.ceil((16 - offsetX) / (float) spacing);
		int timesZ = MathHelper.ceil((16 - offsetZ) / (float) spacing);

		generateNbt(region, startPos, nbtGroup.nbtId("preset", "preset_1"));
		generateNbt(region, startPos.add(0, cellHeight * floors + 1, 0), nbtGroup.nbtId("preset", "preset_1"));
		if (startPos.equals(new BlockPos(0, 0, 0))) generateNbt(region, startPos.add(0, cellHeight * floors + 2, 0),
			nbtGroup.nbtId("manila_room", "manila_room_1"));

		for (int y = 0; y < floors; y++) {
			for (int x = 0; x < timesX; x++) {
				for (int z = 0; z < timesZ; z++) {
					BlockPos offsetPos;
					if (originX >= 0 && originZ >= 0) {
						offsetPos = startPos.add(x * spacing + offsetX, y * cellHeight + 1, z * spacing + offsetZ);
					} else if (originX < 0 && originZ >= 0) {
						offsetPos = startPos.add(15 - (x * spacing + offsetX), y * cellHeight + 1, z * spacing + offsetZ);
					} else if (originX < 0) {
						offsetPos = startPos.add(15 - (x * spacing + offsetX), y * cellHeight + 1, 15 - (z * spacing + offsetZ));
					} else {
						offsetPos = startPos.add(x * spacing + offsetX, y * cellHeight + 1, 15 - (z * spacing + offsetZ));
					}
					decorateRoom(region, offsetPos, world);
					//region.setBlockState(offsetPos, Blocks.SHROOMLIGHT.getDefaultState(), Block.FORCE_STATE);
				}
			}
		}
		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	protected void modifyStructure(ChunkRegion region, BlockPos pos, BlockState state, Optional<NbtCompound> blockEntityNbt) {
		super.modifyStructure(region, pos, state, blockEntityNbt);

		Random random = Random
			.create(region.getSeed() + LimlibHelper.blockSeed(pos));
		int randomInt = random.nextInt(10);
		int randomInt2 = random.nextInt(20);

		if (state.isOf(CubliminalBlocks.FLUORESCENT_LIGHT)) {
			if (randomInt == 0 || BlockPos.stream(new Box(pos).expand(1)).map(region::getBlockState)
				.filter(blockState -> blockState.isOf(CubliminalBlocks.FUSED_FLUORESCENT_LIGHT)).toArray().length > 0) {
				region.setBlockState(pos, CubliminalBlocks.FUSED_FLUORESCENT_LIGHT.getDefaultState()
						.with(TrapdoorBlock.FACING, state.get(TrapdoorBlock.FACING)),
					Block.NOTIFY_ALL, 1);
			} else if (randomInt == 1) {
				region.setBlockState(pos, CubliminalBlocks.FLICKERING_FLUORESCENT_LIGHT.getDefaultState()
						.with(TrapdoorBlock.FACING, state.get(TrapdoorBlock.FACING)),
					Block.NOTIFY_ALL, 1);
			}
		} else if (state.isOf(CubliminalBlocks.ELECTRICAL_PLUG) && randomInt != 2) {
			region.setBlockState(pos, Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, 3), Block.NOTIFY_ALL, 1);
		} else if (state.isOf(CubliminalBlocks.DAMAGED_YELLOW_WALLPAPERS) && randomInt2 != 0) {
			region.setBlockState(pos, CubliminalBlocks.YELLOW_WALLPAPERS.getDefaultState(),
				Block.NOTIFY_ALL, 1);
		} else if (state.isOf(CubliminalBlocks.DIRTY_DAMP_CARPET) && randomInt2 != 1) {
			region.setBlockState(pos, CubliminalBlocks.DAMP_CARPET.getDefaultState(), Block.NOTIFY_ALL, 1);
		} else if (state.isOf(CubliminalBlocks.SMOKE_DETECTOR) && randomInt != 3) {
			region.setBlockState(pos, Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, 3), Block.NOTIFY_ALL, 1);
		}
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
	public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {

	}
}
