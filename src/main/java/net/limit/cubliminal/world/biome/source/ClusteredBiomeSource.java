package net.limit.cubliminal.world.biome.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.world.biome.noise.NoiseParameters;
import net.limit.cubliminal.world.biome.noise.RegistryNoisePreset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.Set;
import java.util.stream.Stream;

public class ClusteredBiomeSource extends BiomeSource implements SpecialBiomeSource {
    public static final MapCodec<ClusteredBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            World.CODEC.fieldOf("dimension").forGetter(biomeSource -> biomeSource.world),
            Codec.FLOAT.optionalFieldOf("scale", 0.1f).forGetter(biomeSource -> biomeSource.scale),
            Codec.INT.optionalFieldOf("cluster_size", 1).forGetter(biomeSource -> biomeSource.clusterSize)
    ).apply(instance, instance.stable(ClusteredBiomeSource::new)));

    private final RegistryKey<World> world;
    private final RegistryNoisePreset noisePreset;
    private final Set<RegistryEntry<Biome>> biomeList;
    private final float scale;
    private final int clusterSize;
    private boolean initialized;
    private SimplexNoiseSampler raritySampler;
    private final double rarityScale;
    private SimplexNoiseSampler spacingSampler;
    private final double maxSpacing;
    private SimplexNoiseSampler safetySampler;
    private final double levelSafety;

    public ClusteredBiomeSource(RegistryKey<World> world, float scale, int clusterSize) {
        this.world = world;
        this.noisePreset = RegistryNoisePreset.getPreset(world);
        this.biomeList = this.noisePreset.biomes().keySet();
        this.scale = scale;
        this.clusterSize = clusterSize;
        this.rarityScale = this.noisePreset.globalSettings().rarity();
        this.maxSpacing = this.noisePreset.globalSettings().spacing();
        this.levelSafety = this.noisePreset.globalSettings().safety();
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        if (!initialized) {
            // Initialize noise samplers
            Random random = Random.create(Cubliminal.SERVER.getSeed());
            this.raritySampler = new SimplexNoiseSampler(new ChunkRandom(random).split());
            this.spacingSampler = new SimplexNoiseSampler(new ChunkRandom(random));
            this.safetySampler = new SimplexNoiseSampler(new ChunkRandom(random));
            initialized = true;
        }
        BlockPos grandPos = new BlockPos(x - Math.floorMod(x, this.clusterSize * 4), 0, z - Math.floorMod(z, this.clusterSize * 4));
        double dx = grandPos.getX() * this.scale;
        double dz = grandPos.getZ() * this.scale;

        double rarityValue = this.sampleRarity(dx, dz);
        double spacingValue = this.sampleSpacing(grandPos.getX(), grandPos.getZ(), dx, dz);
        double safetyValue = this.sampleSafety(dx, dz);
        Cubliminal.LOGGER.info("Noise: " + rarityValue + "; " + spacingValue + "; " + safetyValue);

        return getBiome(rarityValue, spacingValue, safetyValue);
    }

    @Override
    public RegistryEntry<Biome> calcBiome(BlockPos startPos) {
        if (!initialized) {
            Random random = Random.create(Cubliminal.SERVER.getSeed());
            this.raritySampler = new SimplexNoiseSampler(new ChunkRandom(random).split());
            this.spacingSampler = new SimplexNoiseSampler(new ChunkRandom(random));
            this.safetySampler = new SimplexNoiseSampler(new ChunkRandom(random));
            initialized = true;
        }
        int x = BiomeCoords.fromBlock(startPos.getX());
        int z = BiomeCoords.fromBlock(startPos.getZ());
        BlockPos grandPos = new BlockPos(x - Math.floorMod(x, this.clusterSize * 4), 0, z - Math.floorMod(z, this.clusterSize * 4));
        double dx = grandPos.getX() * this.scale;
        double dz = grandPos.getZ() * this.scale;

        double rarityValue = this.sampleRarity(dx, dz);
        double spacingValue = this.sampleSpacing(grandPos.getX(), grandPos.getZ(), dx, dz);
        double safetyValue = this.sampleSafety(dx, dz);

        return getBiome(rarityValue, spacingValue, safetyValue);
    }

    private double sampleRarity(double dx, double dz) {
        double rarityValue = this.raritySampler.sample(dx * this.rarityScale, dz * this.rarityScale) + 1;
        return 8 * (rarityValue - 1);
    }

    private double sampleSpacing(double x, double z, double dx, double dz) {
        double spacingValue = (this.spacingSampler.sample(dx, dz) + 1) / 2;
        double mx = (maxLoc(x / (4 * this.clusterSize)) + maxLoc(z / (4 * this.clusterSize))) / 2;
        return spacingValue * mx;
    }

    private double sampleSafety(double dx, double dz) {
        return this.levelSafety - Math.pow(this.safetySampler.sample(dx, dz), 3);
    }

    private double maxLoc(double d) {
        return this.maxSpacing * Math.pow(Math.cos((Math.PI / this.maxSpacing) * d), 5);
    }

    @Override
    public RegistryEntry<Biome> getBiome(double rarityValue, double spacingValue, double safetyValue) {
        double smallestDifference = Double.MAX_VALUE;
        RegistryEntry<Biome> chosenBiome = null;

        for (RegistryEntry<Biome> biome : this.biomeList) {
            NoiseParameters parameters = this.noisePreset.noiseParameters(biome);
            double totalDifference = 0;
            totalDifference += (Math.abs(rarityValue - parameters.rarity())) * 1.5;
            totalDifference += (Math.abs(spacingValue - parameters.spacing()) / this.maxSpacing) * 5;
            totalDifference += Math.abs(safetyValue - parameters.safety()) * 3;
            if (totalDifference < smallestDifference) {
                smallestDifference = totalDifference;
                chosenBiome = biome;
            }
        }
        return chosenBiome;
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
