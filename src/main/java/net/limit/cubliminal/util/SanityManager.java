package net.limit.cubliminal.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.limit.cubliminal.access.IEntityDataSaver;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.limit.cubliminal.init.CubliminalEffects;
import net.limit.cubliminal.init.CubliminalPackets;
import net.limit.cubliminal.world.biome.noise.RegistryNoisePreset;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

public class SanityManager {

	public static void run(ServerPlayerEntity player) {
		Random random = player.getRandom();
		int bound = player.isSprinting() ? 20 : 16;

		// days without sleeping and light level
		World world = player.getWorld();
		int daysSinceRest = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)) / 24000;
		if (world.getLightLevel(player.getBlockPos()) < 3 || daysSinceRest > 2) {
			bound -= 3;
		}

		// difficulty
		Difficulty difficulty = world.getDifficulty();
		if (difficulty.equals(Difficulty.HARD)) {
			bound -= 3;
		} else if (difficulty.equals(Difficulty.EASY)) {
			bound += 3;
		}
		bound *= 2;

		// biomes that drop sanity even faster
		RegistryEntry<Biome> biome = world.getBiome(player.getBlockPos());
		RegistryNoisePreset noisePreset = RegistryNoisePreset.getPreset(world.getRegistryKey());
		double globalDecayFactor = noisePreset.globalSettings().decayFactor();
		double decayFactor = noisePreset.noiseParameters(biome).decayFactor();

		// decrease sanity if needed
		double result = random.nextInt(bound) * (1 - decayFactor);
		if (result * result < bound * globalDecayFactor) {
			SanityManager.decrease(player, random);
		}
	}

	public static void decrease(ServerPlayerEntity player, Random random) {
		NbtCompound nbt = IEntityDataSaver.cast(player);
		int sanity = nbt.getInt("sanity");
		int mentalFatigue = nbt.getInt("mentalFatigue") + 1;

		if (mentalFatigue > 10) {
			mentalFatigue = 0;
			sanity = Math.max(sanity - 1, 0);
			nbt.putInt("sanity", sanity);

			if (sanity < 20) {
				if (sanity < 5) {
					player.addStatusEffect(new StatusEffectInstance(
							Registries.STATUS_EFFECT.getEntry(CubliminalEffects.PARANOIA),
							400, 0, true, false, true));
				}
				switch (random.nextInt(5 + sanity)) {
					case 1:
						player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60,
								1, false, false, false));
					case 2:
						player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 200,
								0, false, false, false));
					case 3:
						player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200,
								0, false, false, false));
					default:
						break;
				}
			}
		}

		nbt.putInt("mentalFatigue", mentalFatigue);
	}

	public static void resetTimer(ServerPlayerEntity playerEntity) {
		IEntityDataSaver.cast(playerEntity).putInt("sanity", 100);
		IEntityDataSaver.cast(playerEntity).putInt("mentalFatigue", 0);
	}

	public static void syncSanity(ServerPlayerEntity playerEntity) {
		int sanity = IEntityDataSaver.cast(playerEntity).getInt("sanity");
		ServerPlayNetworking.send(playerEntity, new CubliminalPackets.SanitySyncPayload(sanity));
	}
}
