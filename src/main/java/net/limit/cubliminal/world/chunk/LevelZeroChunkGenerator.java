package net.limit.cubliminal.world.chunk;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.block.CustomProperties;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.limit.cubliminal.init.CubliminalBlocks;
import net.limit.cubliminal.init.CubliminalRegistrar;
import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.minecraft.block.*;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
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
				.create(Cubliminal.id(CubliminalRegistrar.THE_LOBBY))
				.with("0column", 1, 2)
				.with("0corner", 1, 1)
				.with("0corridor", 1, 1)
				.with("0space", 1, 1)
				.with("0thickcorner", 1, 1)
				.with("0thickwall", 1, 1)
				.with("0tinywall", 1, 2)
				.with("0twowalls", 1, 2)
				.with("0wall", 1, 1)
				.with("manila_gateway", 1, 4)
				.with("manila_room", 1, 1)
				.with("pillars", 1, 1)
				.with("preset", 1, 1)
				.with("r_column", 1, 2)
				.with("r_corner", 1, 1)
				.with("r_corridor", 1, 2)
				.with("r_space", 1, 1)
				.with("r_thickcorner", 1, 1)
				.with("r_thickwall", 1, 1)
				.with("r_twowalls", 1, 3)
				.with("r_wall", 1, 1)
				.with("special", 1, 4)
			.build();
	}
	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	private void decorateLobby(ChunkRegion region, BlockPos pos){
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
				.chooseGroup(random, "0corner", "0wall",
					 "0thickcorner", "0thickwall", "0twowalls"), random), Manipulation.random(random));
		} else if (randomInt < 15888) {
			// 1 : 8
			generateNbt(region, pos, nbtGroup.pick("manila_gateway", random));
		} else {
			generateNbt(region, pos, nbtGroup.pick("0tinywall", random), Manipulation.random(random));
		}
	}

	private void decorateRedRooms(ChunkRegion region, BlockPos pos){
		Random random = Random
				.create(region.getSeed() + LimlibHelper.blockSeed(pos));
		int randomInt = random.nextInt(25);

		if (randomInt < 5) {
			// 1 : 5
			generateNbt(region, pos, nbtGroup.nbtId("r_space", "r_space_1"));
		} else if (randomInt < 9) {
			// 1 : 5
			generateNbt(region, pos, nbtGroup.pick(nbtGroup
					.chooseGroup(random, "r_column", "r_corridor"), random), Manipulation.random(random));
		} else {
			generateNbt(region, pos, nbtGroup.pick(nbtGroup
					.chooseGroup(random, "r_corner", "r_wall",
							"r_thickcorner", "r_thickwall", "r_twowalls"), random), Manipulation.random(random));
		}
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor,
												  ServerWorld world, ChunkGenerator generator,
												  StructureTemplateManager structureTemplateManager,
												  ServerLightingProvider lightingProvider, Function<Chunk,
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter, List<Chunk> chunks, Chunk chunk) {

		BlockPos startPos = chunk.getPos().getStartPos();
		int spacing = 8;
		int cellHeight = 7;

		generateNbt(region, startPos, nbtGroup.nbtId("preset", "preset_1"));
		if (startPos.equals(new BlockPos(0, 0, 0))) generateNbt(region, startPos.add(0, cellHeight * floors + 1, 0),
				nbtGroup.nbtId("manila_room", "manila_room_1"));

		Optional<RegistryKey<Biome>> biomeKey = world.getBiome(startPos).getKey();
		if (biomeKey.isEmpty()) return CompletableFuture.completedFuture(chunk);
		if (biomeKey.get().equals(CubliminalBiomes.THE_LOBBY_BIOME)) {
			Random random = Random
					.create(region.getSeed() + LimlibHelper.blockSeed(startPos));
			if (random.nextInt(1200) == 0) {
				generateNbt(region, startPos.add(0, 1, 0), random.nextBoolean() ? nbtGroup
						.nbtId("special", "special_1") : nbtGroup.nbtId("special", "special_4"));
			} else {
				for (int y = 0; y < floors; y++) {
					if (random.nextInt(1000) == 0) {
						generateNbt(region, startPos.add(0, y * cellHeight + 1, 0),
								nbtGroup.nbtId("special", "special_2"), Manipulation.random(random));
					} else {
						for (int x = 0; x < 2; x++) {
							for (int z = 0; z < 2; z++) {
								decorateLobby(region, startPos.add(spacing * x, y * cellHeight + 1, spacing * z));
							}
						}
					}
				}
			}
		} else if (biomeKey.get().equals(CubliminalBiomes.PILLAR_BIOME)) {
			Random random = Random
					.create(region.getSeed() + LimlibHelper.blockSeed(startPos));
			for (int y = 0; y < floors; y++) {
				if (random.nextInt(160) == 0) {
					generateNbt(region, startPos.add(0, y * cellHeight + 1, 0),
							nbtGroup.nbtId("special", "special_3"), Manipulation.random(random));
				} else {
					generateNbt(region, startPos.add(0, y * cellHeight + 1, 0),
							nbtGroup.nbtId("pillars", "pillars_1"));
				}
			}
		} else if (biomeKey.get().equals(CubliminalBiomes.REDROOMS_BIOME)) {
			for (int y = 0; y < floors; y++) {
				for (int x = 0; x < 2; x++) {
					for (int z = 0; z < 2; z++) {
						decorateRedRooms(region, startPos.add(spacing * x, y * cellHeight + 1, spacing * z));
					}
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
						.with(TrapdoorBlock.FACING, state.get(TrapdoorBlock.FACING))
								.with(CustomProperties.RED, state.get(CustomProperties.RED)),
					Block.FORCE_STATE, 1);
			} else if (randomInt == 1) {
				region.setBlockState(pos, CubliminalBlocks.FLICKERING_FLUORESCENT_LIGHT.getDefaultState()
						.with(TrapdoorBlock.FACING, state.get(TrapdoorBlock.FACING))
								.with(CustomProperties.RED, state.get(CustomProperties.RED)),
					Block.FORCE_STATE, 1);
			}
		} else if (state.isOf(CubliminalBlocks.SOCKET)) {
			if (randomInt != 0) {
				region.setBlockState(pos, Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, 3),
						Block.FORCE_STATE, 1);
			}
		} else if (state.isOf(CubliminalBlocks.DAMAGED_YELLOW_WALLPAPERS)) {
			if (randomInt2 != 0) {
				region.setBlockState(pos, CubliminalBlocks.YELLOW_WALLPAPERS.getDefaultState(),
						Block.FORCE_STATE, 1);
			}
		} else if (state.isOf(CubliminalBlocks.DIRTY_DAMP_CARPET)) {
			if (randomInt2 != 0) {
				region.setBlockState(pos, CubliminalBlocks.DAMP_CARPET.getDefaultState(),
						Block.FORCE_STATE, 1);
			}
		} else if (state.isOf(CubliminalBlocks.SMOKE_DETECTOR)) {
			if (randomInt != 0) {
				region.setBlockState(pos, Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, 3),
						Block.FORCE_STATE, 1);
			}
		} else if (state.isOf(Blocks.BROWN_MUSHROOM)) {
			if (randomInt2 != 0) {
				region.setBlockState(pos, Blocks.AIR.getDefaultState(),
						Block.FORCE_STATE, 1);
			}
		} else if (state.isOf(CubliminalBlocks.MOLD)) {
			if (randomInt != 0) {
				region.setBlockState(pos, Blocks.AIR.getDefaultState(),
						Block.FORCE_STATE, 1);
			}
		}
	}

	@Override
	protected Identifier getContainerLootTable(LootableContainerBlockEntity container) {
		if (container.getLootTableId() != null) {
			container.setLootTableId(LootTables.EMPTY);
			return Cubliminal.id("barrels/the_lobby/0");
		} else return LootTables.EMPTY;
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
