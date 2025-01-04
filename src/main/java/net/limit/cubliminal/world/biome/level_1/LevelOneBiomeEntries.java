package net.limit.cubliminal.world.biome.level_1;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public enum LevelOneBiomeEntries {
    HABITABLE_ZONE(1.5, 1.5, 0.8),
    PARKING_ZONE(2.2, 2.2, 1.2);

    RegistryEntry.Reference<Biome> biome;
    final double rarity;
    final double spacing;
    final double safety;

    LevelOneBiomeEntries(double rarity, double spacing, double safety) {
        this.rarity = rarity;
        this.spacing = spacing;
        this.safety = safety;
    }
}
