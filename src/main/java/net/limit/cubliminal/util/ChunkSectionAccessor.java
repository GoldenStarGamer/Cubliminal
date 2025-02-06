package net.limit.cubliminal.util;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public interface ChunkSectionAccessor {
    void cubliminal$populateBiomes(RegistryEntry<Biome> biome);
}
