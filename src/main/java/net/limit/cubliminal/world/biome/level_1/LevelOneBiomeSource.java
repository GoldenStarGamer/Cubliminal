package net.limit.cubliminal.world.biome.level_1;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
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
    private SimplexNoiseSampler rarity2;
    private SimplexNoiseSampler spacing2;
    private SimplexNoiseSampler safety2;
    private final RegistryEntry.Reference<Biome> baseBiome;
    private final RegistryEntry.Reference<Biome> rareBiome;
    private boolean bl;
    private boolean bl2;


    public LevelOneBiomeSource(RegistryEntry.Reference<Biome> baseBiome, RegistryEntry.Reference<Biome> rareBiome) {
        this.baseBiome = baseBiome;
        this.rareBiome = rareBiome;
        LevelOneBiomeEntries.HABITABLE_ZONE.biome = baseBiome;
        LevelOneBiomeEntries.PARKING_ZONE.biome = rareBiome;
    }

    @Override
    public RegistryEntry.Reference<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        //Cubliminal.LOGGER.info("Noise pos: {}, {}, {}", x, y, z);

        if (!bl) {
            // initialize noise samplers
            Random random = Random.create(Cubliminal.LVL_0.getSeed());

            this.rarity = new SimplexNoiseSampler(new ChunkRandom(random));
            this.spacing = new SimplexNoiseSampler(new ChunkRandom(random));
            this.safety = new SimplexNoiseSampler(new ChunkRandom(random));
            bl = true;
        }

        BlockPos grandPos = new BlockPos(x - Math.floorMod(x, 16), y, z - Math.floorMod(z, 16));
        // get noise value at the given position (range of 0 - 2)
        double rarityValue = Math.pow((rarity.sample(grandPos.getX() * 6.9, grandPos.getZ() * 6.9) + 1) / 1.5, 3.5);
        double spacingValue = Math.pow((spacing.sample(grandPos.getX() * 6.9, grandPos.getZ() * 6.9) + 1) / 1.5, 3.5);
        double safetyValue = Math.pow((safety.sample(grandPos.getX() * 6.9, grandPos.getZ() * 6.9) + 1) / 1.5, 3.5);

        // return most suitable biome entry by making use of helper enum
        return getBiomeReference(rarityValue, spacingValue, safetyValue);
    }

    public RegistryEntry.Reference<Biome> calcBiome(BlockPos startPos, int bottomSectionCoord) {
        //int x = BiomeCoords.fromBlock(startPos.getX() + 32);
        int x = BiomeCoords.fromBlock(startPos.getX());
        int y = BiomeCoords.fromChunk(bottomSectionCoord);
        //int z = BiomeCoords.fromBlock(startPos.getZ() + 32);
        int z = BiomeCoords.fromBlock(startPos.getZ());

        if (!bl2) {
            Random random = Random.create(Cubliminal.LVL_0.getSeed());

            this.rarity2 = new SimplexNoiseSampler(new ChunkRandom(random));
            this.spacing2 = new SimplexNoiseSampler(new ChunkRandom(random));
            this.safety2 = new SimplexNoiseSampler(new ChunkRandom(random));
            bl2 = true;
        }

        BlockPos grandPos = new BlockPos(x - Math.floorMod(x, 16), y, z - Math.floorMod(z, 16));

        double rarityValue = Math.pow((rarity2.sample(grandPos.getX() * 6.9, grandPos.getZ() * 6.9) + 1) / 1.5, 3.5);
        double spacingValue = Math.pow((spacing2.sample(grandPos.getX() * 6.9, grandPos.getZ() * 6.9) + 1) / 1.5, 3.5);
        double safetyValue = Math.pow((safety2.sample(grandPos.getX() * 6.9, grandPos.getZ() * 6.9) + 1) / 1.5, 3.5);

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
