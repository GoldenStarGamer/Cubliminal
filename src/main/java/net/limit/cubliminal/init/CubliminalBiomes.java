package net.limit.cubliminal.init;

import com.mojang.serialization.Codec;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.world.biome.level_0.LevelZeroBiomeSource;
import net.limit.cubliminal.world.chunk.LevelZeroChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CubliminalBiomes {
    public static final RegistryKey<Biome> THE_LOBBY_BIOME = RegistryKey
		.of(RegistryKeys.BIOME, Cubliminal.id(CubliminalRegistrar.THE_LOBBY));

	public static final RegistryKey<Biome> PILLAR_BIOME = RegistryKey
			.of(RegistryKeys.BIOME, Cubliminal.id("pillar_biome"));

	public static final RegistryKey<Biome> REDROOMS_BIOME = RegistryKey
			.of(RegistryKeys.BIOME, Cubliminal.id("redrooms"));

    public static void init() {
		getChunkGenerator("the_lobby_chunk_generator", LevelZeroChunkGenerator.CODEC);
		getBiomeSource("the_lobby_biome_source", LevelZeroBiomeSource.CODEC);
    }

	public static <C extends ChunkGenerator, D extends Codec<C>> D getChunkGenerator(String id, D chunkGeneratorCodec) {
		return Registry.register(Registries.CHUNK_GENERATOR, Cubliminal.id(id), chunkGeneratorCodec);
	}
	public static <C extends BiomeSource, D extends Codec<C>> D getBiomeSource(String id, D biomeSourceCodec) {
		return Registry.register(Registries.BIOME_SOURCE, Cubliminal.id(id), biomeSourceCodec);
	}

}
