package net.limit.cubliminal.world.biome.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public record NoisePresetHolder(RegistryKey<World> world, NoiseParameters globalSettings, Map<RegistryKey<Biome>, NoiseParameters> biomes) {
    public static final Codec<NoisePresetHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            World.CODEC.fieldOf("dimension").forGetter(NoisePresetHolder::world),
            NoiseParameters.CODEC.fieldOf("global_settings").forGetter(NoisePresetHolder::globalSettings),
            Codec.unboundedMap(RegistryKey.createCodec(RegistryKeys.BIOME), NoiseParameters.CODEC)
                    .fieldOf("biomes").forGetter(NoisePresetHolder::biomes)
    ).apply(instance, instance.stable(NoisePresetHolder::new)));

    public static class Builder {
        private final Map<RegistryKey<Biome>, NoiseParameters> biomes = new HashMap<>();
        private final RegistryKey<World> world;
        private final NoiseParameters globalSettings;

        public Builder(RegistryKey<World> world, NoiseParameters globalSettings) {
            this.world = world;
            this.globalSettings = globalSettings;
        }

        public void with(RegistryKey<Biome> biome, NoiseParameters parameters) {
            this.biomes.putIfAbsent(biome, parameters);
        }

        public void with(Map<RegistryKey<Biome>, NoiseParameters> biomes) {
            biomes.forEach(this::with);
        }

        public NoisePresetHolder build() {
            return new NoisePresetHolder(this.world, this.globalSettings, this.biomes);
        }
    }
}
