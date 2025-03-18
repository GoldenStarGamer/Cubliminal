package net.limit.cubliminal.world.biome.level_0;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LevelZeroBiomeSource extends BiomeSource {
    public static final MapCodec<LevelZeroBiomeSource> CODEC = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(RegistryOps.getEntryCodec(CubliminalBiomes.THE_LOBBY_BIOME),
                            RegistryOps.getEntryCodec(CubliminalBiomes.PILLAR_BIOME),
                            RegistryOps.getEntryCodec(CubliminalBiomes.REDROOMS_BIOME))
                    .apply(instance, instance.stable(LevelZeroBiomeSource::new)));

    private SimplexNoiseSampler rarity;
    private SimplexNoiseSampler spacing;
    private SimplexNoiseSampler safety;
    private final RegistryEntry.Reference<Biome> baseBiome;
    private final RegistryEntry.Reference<Biome> rareBiome;
    private final RegistryEntry.Reference<Biome> alternateBiome;
    private boolean bl;


    public LevelZeroBiomeSource(RegistryEntry.Reference<Biome> baseBiome, RegistryEntry.Reference<Biome> rareBiome, RegistryEntry.Reference<Biome> alternateBiome) {
        this.baseBiome = baseBiome;
        this.rareBiome = rareBiome;
        this.alternateBiome = alternateBiome;
        LevelZeroBiomeEntries.THE_LOBBY.biome = baseBiome;
        LevelZeroBiomeEntries.PILLARS_BIOME.biome = rareBiome;
        LevelZeroBiomeEntries.THE_REDROOMS.biome = alternateBiome;
    }

    @Override
    public RegistryEntry.Reference<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        if (!bl) {
            // Initialize noise samplers
            Random random = Random.create(Cubliminal.SERVER.getSeed());
            this.rarity = new SimplexNoiseSampler(new ChunkRandom(random));
            this.spacing = new SimplexNoiseSampler(new ChunkRandom(random));
            this.safety = new SimplexNoiseSampler(new ChunkRandom(random));
            bl = true;
        }
        // Get noise value at the given position (range of 0 - 3)
        double rarityValue = (rarity.sample(x * 0.005,z * 0.005) + 1) * 1.5;
        double spacingValue = (spacing.sample(x * 0.005,z * 0.005) + 1) * 1.5;
        double safetyValue = (safety.sample(x * 0.005,z * 0.005) + 1) * 1.5;

        // Return most suitable biome entry by making use of helper enum
        return getBiomeReference(rarityValue, spacingValue, safetyValue);
    }

    public RegistryEntry.Reference<Biome> calcBiome(BlockPos startPos) {
        int x = BiomeCoords.fromBlock(startPos.getX());
        int z = BiomeCoords.fromBlock(startPos.getZ());

        if (!bl) {
            Random random = Random.create(Cubliminal.SERVER.getSeed());
            this.rarity = new SimplexNoiseSampler(new ChunkRandom(random));
            this.spacing = new SimplexNoiseSampler(new ChunkRandom(random));
            this.safety = new SimplexNoiseSampler(new ChunkRandom(random));
            bl = true;
        }

        double rarityValue = (rarity.sample(x * 0.005,z * 0.005) + 1) * 1.5;
        double spacingValue = (spacing.sample(x * 0.005,z * 0.005) + 1) * 1.5;
        double safetyValue = (safety.sample(x * 0.005,z * 0.005) + 1) * 1.5;

        return getBiomeReference(rarityValue, spacingValue, safetyValue);
    }

    private static RegistryEntry.Reference<Biome> getBiomeReference(double rarityValue, double spacingValue, double safetyValue) {
        double smallestDifference = 256;
        RegistryEntry.Reference<Biome> chosenBiome = null;

        for (LevelZeroBiomeEntries biomeEntry : LevelZeroBiomeEntries.values()) {
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
    /*
    @Override
    public @Nullable Pair<BlockPos, RegistryEntry<Biome>> locateBiome(BlockPos origin, int radius, int horizontalBlockCheckInterval, int verticalBlockCheckInterval, Predicate<RegistryEntry<Biome>> predicate, MultiNoiseUtil.MultiNoiseSampler noiseSampler, WorldView world) {
        Cubliminal.LOGGER.info("Sorpresa");
        Set<RegistryEntry<Biome>> set = (Set)this.getBiomes().stream().filter(predicate).collect(Collectors.toUnmodifiableSet());
        if (set.isEmpty()) {
            Cubliminal.LOGGER.info("Nahh empty set");
            return null;
        } else {
            Cubliminal.LOGGER.info("Si funca si");
            int i = Math.floorDiv(radius, horizontalBlockCheckInterval);
            int[] is = MathHelper.stream(origin.getY(), world.getBottomY() + 1, world.getTopYInclusive() + 1, verticalBlockCheckInterval).toArray();
            Iterator var11 = BlockPos.iterateInSquare(BlockPos.ORIGIN, i, Direction.EAST, Direction.SOUTH).iterator();
            Cubliminal.LOGGER.info("Iterator: " + var11 + "; " + Arrays.toString(is));
            while(var11.hasNext()) {

                BlockPos.Mutable mutable = (BlockPos.Mutable)var11.next();Cubliminal.LOGGER.info("Buclando: " + mutable);
                int j = origin.getX() + mutable.getX() * horizontalBlockCheckInterval;
                int k = origin.getZ() + mutable.getZ() * horizontalBlockCheckInterval;
                int l = BiomeCoords.fromBlock(j);
                int m = BiomeCoords.fromBlock(k);
                int[] var17 = is;
                int var18 = is.length;
                Cubliminal.LOGGER.info("Length: " + var18);

                for(int var19 = 0; var19 < var18; ++var19) {
                    Cubliminal.LOGGER.info("Buclando 2");
                    int n = var17[var19];
                    int o = BiomeCoords.fromBlock(n);
                    RegistryEntry<Biome> registryEntry = this.getBiome(l, o, m, noiseSampler);
                    if (set.contains(registryEntry)) {
                        Cubliminal.LOGGER.info("YESSS: " + registryEntry);
                        return Pair.of(new BlockPos(j, n, k), registryEntry);
                    } else Cubliminal.LOGGER.info("Uh uh: " + registryEntry);
                }
            }

            return null;
        }
    }

     */

    @Override
    protected Stream<RegistryEntry<Biome>> biomeStream() {
        return Stream.of(this.baseBiome, this.rareBiome, this.alternateBiome);
    }

    @Override
    protected MapCodec<? extends BiomeSource> getCodec() {
        return CODEC;
    }
}
