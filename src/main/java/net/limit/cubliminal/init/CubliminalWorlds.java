package net.limit.cubliminal.init;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.world.biome.level_0.RedroomsBiome;
import net.limit.cubliminal.world.biome.level_0.LevelZeroBiomeSource;
import net.limit.cubliminal.world.biome.level_0.PillarBiome;
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
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionType.MonsterSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;


public class CubliminalWorlds implements LimlibRegistrar {

	private static final List<Pair<RegistryKey<LimlibWorld>, LimlibWorld>> WORLDS = Lists.newArrayList();
	private static final List<Pair<RegistryKey<SoundEffects>, SoundEffects>> SOUND_EFFECTS = Lists.newArrayList();
	private static final List<Pair<RegistryKey<Skybox>, Skybox>> SKYBOXES = Lists.newArrayList();
	private static final List<Pair<RegistryKey<LDimensionEffects>, LDimensionEffects>> DIMENSION_EFFECTS = Lists.newArrayList();
	private static final List<Pair<RegistryKey<PostEffect>, PostEffect>> POST_EFFECTS = Lists.newArrayList();

	public static String THE_LOBBY = "the_lobby";
	public static final RegistryKey<World> THE_LOBBY_KEY = RegistryKey.of(RegistryKeys.WORLD,
		Cubliminal.id(THE_LOBBY));

	@Override
	public void registerHooks() {
		getSoundEffects(THE_LOBBY,
			new SoundEffects(Optional.of(new StaticReverbEffect.Builder().setDecayTime(2.15f).setDensity(0.0725f).build()),
				Optional.empty(), Optional.empty()));

		getDimEffects(THE_LOBBY, new StaticDimensionEffects(Optional.empty(), false, "NONE", false, true, false, 0f));

		getWorld(THE_LOBBY,
			new LimlibWorld(
				() -> new DimensionType(OptionalLong.of(15500), false, false, false, false, 1.0, false, false, 0, 384, 384,
					TagKey.of(RegistryKeys.BLOCK, Cubliminal.id(THE_LOBBY)), Cubliminal.id(THE_LOBBY),
					0f, new MonsterSettings(false, false, ConstantIntProvider.ZERO, 0)),
				(registry) -> new DimensionOptions(
					registry
						.get(RegistryKeys.DIMENSION_TYPE)
						.getOptional(RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Cubliminal.id(THE_LOBBY)))
						.get(),
					new LevelZeroChunkGenerator(
							new FixedBiomeSource(registry.get(RegistryKeys.BIOME).getOptional(CubliminalBiomes.THE_LOBBY_BIOME).get())
							/*
							new LevelZeroBiomeSource(
								registry.get(RegistryKeys.BIOME).getOptional(CubliminalBiomes.THE_LOBBY_BIOME).get(),
								registry.get(RegistryKeys.BIOME).getOptional(CubliminalBiomes.PILLAR_BIOME).get(),
								registry.get(RegistryKeys.BIOME).getOptional(CubliminalBiomes.REDROOMS_BIOME).get())
							 */
						,
						LevelZeroChunkGenerator.createGroup(), 3))));


		WORLDS.forEach((pair) -> LimlibWorld.LIMLIB_WORLD.add(pair.getFirst(), pair.getSecond(), Lifecycle.stable()));


		LimlibRegistryHooks
			.hook(SoundEffects.SOUND_EFFECTS_KEY, (infoLookup, registryKey, registry) -> SOUND_EFFECTS
				.forEach((pair) -> registry.add(pair.getFirst(), pair.getSecond(), Lifecycle.stable())));
		/*
		LimlibRegistryHooks
			.hook(Skybox.SKYBOX_KEY, (infoLookup, registryKey, registry) -> SKYBOXES
				.forEach((pair) -> registry.add(pair.getFirst(), pair.getSecond(), Lifecycle.stable())));

		 */
		LimlibRegistryHooks
			.hook(LDimensionEffects.DIMENSION_EFFECTS_KEY, (infoLookup, registryKey, registry) -> DIMENSION_EFFECTS
				.forEach((pair) -> registry.add(pair.getFirst(), pair.getSecond(), Lifecycle.stable())));
		/*
		LimlibRegistryHooks
			.hook(PostEffect.POST_EFFECT_KEY, (infoLookup, registryKey, registry) -> POST_EFFECTS
				.forEach((pair) -> registry.add(pair.getFirst(), pair.getSecond(), Lifecycle.stable())));

		 */
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

	private static <W extends LimlibWorld> W getWorld(String id, W world) {
		WORLDS.add(Pair.of(RegistryKey.of(LimlibWorld.LIMLIB_WORLD_KEY, Cubliminal.id(id)), world));
		return world;
	}

	private static <E extends SoundEffects> E getSoundEffects(String id, E soundEffects) {
		SOUND_EFFECTS.add(Pair.of(RegistryKey.of(SoundEffects.SOUND_EFFECTS_KEY, Cubliminal.id(id)), soundEffects));
		return soundEffects;
	}

	private static <S extends Skybox> S getSkybox(String id, S skybox) {
		SKYBOXES.add(Pair.of(RegistryKey.of(Skybox.SKYBOX_KEY, Cubliminal.id(id)), skybox));
		return skybox;
	}

	private static <D extends LDimensionEffects> D getDimEffects(String id, D dimensionEffects) {
		DIMENSION_EFFECTS
			.add(Pair.of(RegistryKey.of(LDimensionEffects.DIMENSION_EFFECTS_KEY, Cubliminal.id(id)), dimensionEffects));
		return dimensionEffects;
	}

	private static <P extends PostEffect> P getPostEffects(String id, P postEffect) {
		POST_EFFECTS.add(Pair.of(RegistryKey.of(PostEffect.POST_EFFECT_KEY, Cubliminal.id(id)), postEffect));
		return postEffect;
	}
}
