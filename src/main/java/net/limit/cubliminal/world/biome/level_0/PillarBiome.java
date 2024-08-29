package net.limit.cubliminal.world.biome.level_0;

import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

public class PillarBiome {
	public static Biome create(RegistryEntryLookup<PlacedFeature> features, RegistryEntryLookup<ConfiguredCarver<?>> carvers) {
		Biome.Builder biome = new Biome.Builder();

		SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
		GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
		BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder();

		biomeEffects.skyColor(19384);
		biomeEffects.fogColor(16252805);
		biomeEffects.waterColor(6694896);
		biomeEffects.waterFogColor(6694896);
		BiomeEffects effects = biomeEffects.build();

		biome.spawnSettings(spawnSettings.build());
		biome.generationSettings(generationSettings.build());
		biome.temperature(0.8f);
		biome.downfall(0.4f);
		biome.precipitation(false);
		biome.effects(effects);

        return biome.build();
    }
}
