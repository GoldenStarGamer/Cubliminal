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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

public class SanityData {
	public static void run(ServerPlayerEntity playerEntity) {
		NbtCompound nbt = IEntityDataSaver.cast(playerEntity);
		Random random = playerEntity.getRandom();
		int sanity = nbt.getInt("sanity");
		int bound = playerEntity.isSprinting() ? 18 : 16;
		// days without sleeping
		int days = MathHelper.clamp(playerEntity.getStatHandler().getStat(Stats.CUSTOM
				.getOrCreateStat(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
		if (playerEntity.getWorld().getLightLevel(playerEntity.getBlockPos()) < 3 || random.nextInt(days) < 72000) {
			bound -= 3;
		}
		// biomes that drop sanity even faster
		Optional<RegistryKey<Biome>> registryKey = playerEntity.getWorld().getBiome(playerEntity.getBlockPos()).getKey();
		if (registryKey.isPresent() && registryKey.get().equals(CubliminalBiomes.REDROOMS_BIOME)) {
			bound -= 2;
		}

		Difficulty difficulty = playerEntity.getWorld().getDifficulty();
		if (difficulty.equals(Difficulty.HARD)) {
			bound -= 2;
		} else if (difficulty.equals(Difficulty.EASY)) {
			bound += 2;
		}
		// decrease sanity
		if (random.nextInt(bound) == 0) --sanity;
		sanity = Math.clamp(sanity, 0, 10);

		// status effects
		if (sanity < 2) {
			if (sanity == 0) {
				playerEntity.addStatusEffect(new StatusEffectInstance(
						Registries.STATUS_EFFECT.getEntry(CubliminalEffects.PARANOIA),
						400, 0, false, false, false));
			}

			switch (random.nextInt(5 + 2 * sanity)) {
				case 1:
					playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60,
						1, false, false, false));
				case 2:
					playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 200,
						0, false, false, false));
				case 3:
					playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200,
						0, false, false, false));
				default:
					break;
			}
		}

		nbt.putInt("sanity", sanity);
	}

	public static void resetTimer(ServerPlayerEntity playerEntity) {
		IEntityDataSaver.cast(playerEntity).putInt("sanity", 10);
	}

	public static void syncSanity(ServerPlayerEntity playerEntity) {
		int sanity = IEntityDataSaver.cast(playerEntity).getInt("sanity");
		ServerPlayNetworking.send(playerEntity, new CubliminalPackets.SanitySyncPayload(sanity));
	}
}
