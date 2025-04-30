package net.limit.cubliminal.world.biome.source;

import net.limit.cubliminal.level.Level;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public interface LiminalBiomeSource {
    RegistryEntry<Biome> calcBiome(int blockX, int blockY, int blockZ);
    RegistryEntry<Biome> calcBiome(BlockPos pos);
    Level getLevel();
}
