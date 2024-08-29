package net.limit.cubliminal.world.biome.level_0;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public enum LevelZeroBiomeEntries {
    THE_LOBBY(1.5, 1.5, 1.5),
    PILLARS_BIOME(2.6, 3.3, 1.5),
    THE_REDROOMS(-0.2, 1, 0.5);

    RegistryEntry.Reference<Biome> biome;
    final double rarity;
    final double spacing;
    final double safety;

    LevelZeroBiomeEntries(double rarity, double spacing, double safety) {
        this.rarity = rarity;
        this.spacing = spacing;
        this.safety = safety;
    }
}
