package net.limit.cubliminal.init;

import com.mojang.serialization.Lifecycle;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.world.biome.level_0.PillarBiome;
import net.limit.cubliminal.world.biome.level_0.RedroomsBiome;
import net.limit.cubliminal.world.biome.level_0.TheLobbyBiome;
import net.limit.cubliminal.world.chunk.LevelZeroChunkGenerator;
import net.ludocrypt.limlib.api.LimlibRegistrar;
import net.ludocrypt.limlib.api.LimlibRegistryHooks;
import net.ludocrypt.limlib.api.LimlibWorld;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.sky.LDimensionEffects;
import net.ludocrypt.limlib.api.effects.sky.StaticDimensionEffects;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.api.effects.sound.reverb.StaticReverbEffect;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.Optional;
import java.util.OptionalLong;

public class CubliminalRegistrar implements LimlibRegistrar {
    public static final Identifier THE_LOBBY_ID = Cubliminal.id("the_lobby");

    public static final SoundEffects THE_LOBBY_SOUND_EFFECTS = new SoundEffects(
            Optional.of(new StaticReverbEffect.Builder().setDecayTime(2.15f).setDensity(0.0725f).build()),
            Optional.empty(), Optional.empty());

    public static final LDimensionEffects THE_LOBBY_EFFECTS = new StaticDimensionEffects(
            Optional.empty(), false, "NONE", false,
            true, false, 0f);

    public static final LimlibWorld THE_LOBBY =
            new LimlibWorld(
                    () -> new DimensionType(OptionalLong.of(15500), false, false, false, false, 1.0, false, false, 0, 384, 384,
                            TagKey.of(RegistryKeys.BLOCK, THE_LOBBY_ID), THE_LOBBY_ID,
                            0f, new DimensionType.MonsterSettings(false, false, ConstantIntProvider.ZERO, 0)),
                    (registry) ->
                            new DimensionOptions(
                                    registry.get(RegistryKeys.DIMENSION_TYPE)
                                            .getOptional(RegistryKey.of(RegistryKeys.DIMENSION_TYPE, THE_LOBBY_ID)).get(),
                                    new LevelZeroChunkGenerator(
                                            new FixedBiomeSource(registry.get(RegistryKeys.BIOME)
                                                    .getOptional(CubliminalBiomes.THE_LOBBY_BIOME).get()),
                                            LevelZeroChunkGenerator.createGroup(), 3
                                    )));

    public static final RegistryKey<World> THE_LOBBY_KEY = RegistryKey.of(RegistryKeys.WORLD, THE_LOBBY_ID);


    @Override
    public void registerHooks() {
        LimlibWorld.LIMLIB_WORLD.add(
                RegistryKey.of(LimlibWorld.LIMLIB_WORLD_KEY, THE_LOBBY_ID), THE_LOBBY, Lifecycle.stable()
        );

        LimlibRegistryHooks.hook(SoundEffects.SOUND_EFFECTS_KEY,
                (infoLookup, registryKey, registry) -> registry.add(
                        RegistryKey.of(SoundEffects.SOUND_EFFECTS_KEY, Cubliminal.id("the_lobby_sound_effects")),
                        THE_LOBBY_SOUND_EFFECTS,
                        Lifecycle.stable()));

        LimlibRegistryHooks.hook(LDimensionEffects.DIMENSION_EFFECTS_KEY,
                (infoLookup, registryKey, registry) -> registry.add(
                        RegistryKey.of(LDimensionEffects.DIMENSION_EFFECTS_KEY, Cubliminal.id("the_lobby_effects")),
                        THE_LOBBY_EFFECTS,
                        Lifecycle.stable()));


        LimlibRegistryHooks.hook(RegistryKeys.BIOME, (infoLookup, registryKey, registry) -> {
            RegistryEntryLookup<PlacedFeature> features = infoLookup.getRegistryInfo(RegistryKeys.PLACED_FEATURE).get().entryLookup();
            RegistryEntryLookup<ConfiguredCarver<?>> carvers = infoLookup.getRegistryInfo(RegistryKeys.CONFIGURED_CARVER).get().entryLookup();

            registry.add(CubliminalBiomes.THE_LOBBY_BIOME, TheLobbyBiome.create(features, carvers),
                    Lifecycle.stable());

            registry.add(CubliminalBiomes.PILLAR_BIOME, PillarBiome.create(features, carvers),
                    Lifecycle.stable());

            registry.add(CubliminalBiomes.REDROOMS_BIOME, RedroomsBiome.create(features, carvers),
                    Lifecycle.stable());
        });
    }
}
