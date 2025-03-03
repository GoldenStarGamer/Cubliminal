package net.limit.cubliminal.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public record FogSettings(float fogStart, float fogEnd, float fadeTicks) {
    public static Map<RegistryKey<Biome>, FogSettings> FOG_PRESETS = new HashMap<>();
    public static FogSettings DUMMY = new FogSettings(Float.MIN_VALUE, Float.MAX_VALUE, 2048);

    public FogSettings(float fogStart, float fogEnd, float fadeTicks) {
        this.fogStart = fogStart;
        this.fogEnd = fogEnd;
        if (fogStart >= fogEnd) {
            throw new IllegalArgumentException("Fog end should always be greater than fog start");
        }
        this.fadeTicks = fadeTicks;
        if (fadeTicks <= 1) {
            throw new IllegalArgumentException("Fog fade ticks should never be set below 1");
        }
    }

    public static FogSettings create(RegistryKey<Biome> biome, float fogStart, float fogEnd, float fadeTicks) {
        FogSettings fogSettings = new FogSettings(fogStart, fogEnd, fadeTicks);
        FOG_PRESETS.put(biome, fogSettings);
        return fogSettings;
    }


    public static void init() {
        create(CubliminalBiomes.PARKING_ZONE_BIOME, 16f, 48f, 100f);
        create(CubliminalBiomes.HABITABLE_ZONE_BIOME, 32f, 64f, 100f);
    }
}
