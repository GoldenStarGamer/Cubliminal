package net.limit.cubliminal.world.biome.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.util.JsonUtil;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.*;

/**
 * A record that maps a dimension's noise settings. It is used by the biome source to choose the most appropriate biome.
 * @param world The registry key of the dimension used to retrieve a noise preset from the registry
 * @param globalSettings The ideal dimension's noise value point  in form of {@link NoiseParameters}. Components:
 *                       <ul><li>{@code Rarity}: how much a change in location affects rarity's noise values</li>
 *                       <li>{@code Spacing}: the maximum calculated spacing between zones</li>
 *                       <li>{@code Safety}: 5 - Level's survival difficulty</li>
 *                       <li>{@code DecayFactor}: Level's average likelihood of players' mental fatigue to increase each tick</li>
 *                       </ul>
 * @param biomes A map holding each biome entry with its corresponding noise settings
 */
public record RegistryNoisePreset(RegistryKey<World> world, NoiseParameters globalSettings, Map<RegistryEntry<Biome>, NoiseParameters> biomes) {
    public static final Codec<RegistryNoisePreset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            World.CODEC.fieldOf("dimension").forGetter(RegistryNoisePreset::world),
            NoiseParameters.CODEC.fieldOf("global_settings").forGetter(RegistryNoisePreset::globalSettings),
            Codec.unboundedMap(RegistryElementCodec.of(RegistryKeys.BIOME, Biome.CODEC), NoiseParameters.CODEC)
                    .fieldOf("biomes").forGetter(RegistryNoisePreset::biomes)
    ).apply(instance, instance.stable(RegistryNoisePreset::new)));

    private static final Map<RegistryKey<World>, RegistryNoisePreset> REGISTRIES = new HashMap<>();

    public static void register(RegistryNoisePreset preset) {
        REGISTRIES.putIfAbsent(preset.world(), preset);
    }

    private static void clear() {
        REGISTRIES.clear();
    }

    public static RegistryNoisePreset getPreset(RegistryKey<World> world) {
        RegistryNoisePreset preset = REGISTRIES.get(world);
        if (preset == null) {
            throw new MatchException("Couldn't fetch noise preset: " + world.toString(), new Throwable());
        }
        return preset;
    }

    public NoiseParameters noiseParameters(RegistryEntry<Biome> biome) {
        return this.biomes().getOrDefault(biome, NoiseParameters.DEFAULT);
    }

    public static class Builder {
        private final Map<RegistryEntry<Biome>, NoiseParameters> biomes = new HashMap<>();
        private final RegistryKey<World> world;
        private final NoiseParameters globalSettings;

        public Builder(RegistryKey<World> world, NoiseParameters globalSettings) {
            this.world = world;
            this.globalSettings = globalSettings;
        }

        public void with(RegistryEntry<Biome> biome, NoiseParameters parameters) {
            this.biomes.putIfAbsent(biome, parameters);
        }

        public void with(Map<RegistryEntry<Biome>, NoiseParameters> biomes) {
            biomes.forEach(this::with);
        }

        public RegistryNoisePreset build() {
            return new RegistryNoisePreset(this.world, this.globalSettings, this.biomes);
        }
    }

    public static void initNoisePresets(MutableRegistry<Biome> mutableRegistry) {
        List<NoisePresetHolder> decodedResults = JsonUtil.deserializeDataJsonArray(JsonOps.INSTANCE, NoisePresetHolder.CODEC, Cubliminal.id("noise_preset"));
        if (!decodedResults.isEmpty()) {
            RegistryNoisePreset.clear();
            for (NoisePresetHolder decoded : decodedResults) {
                RegistryNoisePreset.Builder builder = new RegistryNoisePreset.Builder(decoded.world(), decoded.globalSettings());
                decoded.biomes().forEach((biomeKey, noiseParameters) -> builder.with(mutableRegistry.getOptional(biomeKey).get(), noiseParameters));
                RegistryNoisePreset.register(builder.build());
            }
        }
    }

}
