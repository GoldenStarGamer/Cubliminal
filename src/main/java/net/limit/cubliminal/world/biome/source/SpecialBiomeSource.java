package net.limit.cubliminal.world.biome.source;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public interface SpecialBiomeSource {
    RegistryEntry<Biome> calcBiome(BlockPos startPos);
    RegistryEntry<Biome> getBiome(double rarityValue, double spacingValue, double safetyValue);
}
