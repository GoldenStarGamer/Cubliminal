package net.limit.cubliminal.world.biome.level_1;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.stream.Stream;

public class LevelOneBiomeSource extends BiomeSource {
    public static final MapCodec<LevelOneBiomeSource> CODEC = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(RegistryOps.getEntryCodec(CubliminalBiomes.HABITABLE_ZONE_BIOME),
                            RegistryOps.getEntryCodec(CubliminalBiomes.PARKING_ZONE_BIOME))
                    .apply(instance, instance.stable(LevelOneBiomeSource::new)));

    private SimplexNoiseSampler rarity;
    private SimplexNoiseSampler spacing;
    private SimplexNoiseSampler safety;
    private final RegistryEntry.Reference<Biome> baseBiome;
    private final RegistryEntry.Reference<Biome> rareBiome;
    private boolean bl;


    public LevelOneBiomeSource(RegistryEntry.Reference<Biome> baseBiome, RegistryEntry.Reference<Biome> rareBiome) {
        this.baseBiome = baseBiome;
        this.rareBiome = rareBiome;
        LevelOneBiomeEntries.HABITABLE_ZONE.biome = baseBiome;
        LevelOneBiomeEntries.PARKING_ZONE.biome = rareBiome;
    }

    @Override
    public RegistryEntry.Reference<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        if (Math.abs(x) + Math.abs(z) < 200) return baseBiome;
        if (!bl) {
            // initialize noise samplers
            Random random = Random.create(Cubliminal.LVL_0.getSeed());
            this.rarity = new SimplexNoiseSampler(new ChunkRandom(random));
            this.spacing = new SimplexNoiseSampler(new ChunkRandom(random));
            this.safety = new SimplexNoiseSampler(new ChunkRandom(random));
            bl = true;
        }
        // get noise value at the given position (range of 0 - 3)
        double rarityValue = (rarity.sample(x * 0.005,z * 0.005) + 1) * 1.5;
        double spacingValue = (spacing.sample(x * 0.005,z * 0.005) + 1) * 1.5;
        double safetyValue = (safety.sample(x * 0.005,z * 0.005) + 1) * 1.5;

        // return most suitable biome entry by making use of helper enum
        return getBiomeReference(rarityValue, spacingValue, safetyValue);
    }

    private static RegistryEntry.Reference<Biome> getBiomeReference(double rarityValue, double spacingValue, double safetyValue) {
        double smallestDifference = 256;
        RegistryEntry.Reference<Biome> chosenBiome = null;

        for (LevelOneBiomeEntries biomeEntry : LevelOneBiomeEntries.values()) {
            double totalDifference = 0;
            totalDifference += Math.abs(rarityValue - biomeEntry.rarity);
            totalDifference += Math.abs(spacingValue - biomeEntry.spacing);
            totalDifference += Math.abs(safetyValue - biomeEntry.safety);
            if (totalDifference < smallestDifference) {
                smallestDifference = totalDifference;
                chosenBiome = biomeEntry.biome;
            }
        }
        return chosenBiome;
    }

    @Override
    protected Stream<RegistryEntry<Biome>> biomeStream() {
        return Stream.of(this.baseBiome, this.rareBiome);
    }

    @Override
    protected MapCodec<? extends BiomeSource> getCodec() {
        return CODEC;
    }
}
