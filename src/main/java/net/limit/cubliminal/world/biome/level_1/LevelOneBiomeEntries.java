package net.limit.cubliminal.world.biome.level_1;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public enum LevelOneBiomeEntries {
    HABITABLE_ZONE(0, 0.5, 4),
    PARKING_ZONE(1, 4, 3);

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
