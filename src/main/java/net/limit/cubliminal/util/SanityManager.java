package net.limit.cubliminal.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.limit.cubliminal.init.CubliminalEffects;
import net.limit.cubliminal.init.CubliminalPackets;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

public class SanityManager {

	public static void run(ServerPlayerEntity player) {
		NbtCompound nbt = IEntityDataSaver.cast(player);
		Random random = player.getRandom();
		int bound = player.isSprinting() ? 18 : 16;

		// days without sleeping and light level
		int daysSinceRest = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)) / 24000;
		if (player.getWorld().getLightLevel(player.getBlockPos()) < 4 || daysSinceRest > 2) {
			bound -= 3;
		}
		// biomes that drop sanity even faster
		Optional<RegistryKey<Biome>> registryKey = player.getWorld().getBiome(player.getBlockPos()).getKey();
		if (registryKey.isPresent() && registryKey.get().equals(CubliminalBiomes.REDROOMS_BIOME)) {
			bound -= 5;
		}
		// difficulty
		Difficulty difficulty = player.getWorld().getDifficulty();
		if (difficulty.equals(Difficulty.HARD)) {
			bound -= 3;
		} else if (difficulty.equals(Difficulty.EASY)) {
			bound += 3;
		}
		bound *= 2;

		// decrease sanity if needed
		if (random.nextInt(bound) == 0) {
			decrease(player, nbt, random);
		}
	}

	public static void decrease(ServerPlayerEntity player, NbtCompound nbt, Random random) {
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
