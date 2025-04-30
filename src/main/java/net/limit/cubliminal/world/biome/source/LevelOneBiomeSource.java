package net.limit.cubliminal.world.biome.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.init.CubliminalRegistrar;
import net.limit.cubliminal.level.Level;
import net.limit.cubliminal.level.Levels;
import net.limit.cubliminal.world.biome.noise.RegistryNoisePreset;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.Set;
import java.util.stream.Stream;

public class LevelOneBiomeSource extends BiomeSource implements LiminalBiomeSource {
    public static final MapCodec<LevelOneBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("scale", 0.1f).forGetter(biomeSource -> biomeSource.scale)
    ).apply(instance, instance.stable(LevelOneBiomeSource::new)));

    private final Level level;
    private final float scale;
    private final RegistryNoisePreset noisePreset;
    private final Set<RegistryEntry<Biome>> biomeList;
    private SimplexNoiseSampler raritySampler;
    private SimplexNoiseSampler spacingSampler;
    private SimplexNoiseSampler safetySampler;
    private boolean initialized;
    public LevelOneBiomeSource(float scale) {
        this.level = Levels.LEVEL_1.getLevel();
        this.scale = scale;
        this.noisePreset = RegistryNoisePreset.getPreset(CubliminalRegistrar.HABITABLE_ZONE_KEY);
        this.biomeList = this.noisePreset.biomes().keySet();
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        return this.calcBiome(BiomeCoords.toBlock(x), BiomeCoords.toBlock(y), BiomeCoords.toBlock(z));
    }

    @Override
    public RegistryEntry<Biome> calcBiome(BlockPos pos) {
        return this.calcBiome(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public RegistryEntry<Biome> calcBiome(int blockX, int blockY, int blockZ) {
        return null;
    }

    @Override
    public RegistryEntry<Biome> getBiome(double rarityValue, double spacingValue, double safetyValue) {
        return null;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    protected MapCodec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    protected Stream<RegistryEntry<Biome>> biomeStream() {
        return this.biomeList.stream();
    }
}
