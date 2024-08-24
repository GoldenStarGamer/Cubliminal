package net.limit.cubliminal.util;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.limit.cubliminal.init.CubliminalEffects;
import net.limit.cubliminal.init.CubliminalPackets;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.Nullable;

public class SanityData {
	public static int modifyTimer(ServerPlayerEntity playerEntity, int bound, @Nullable Integer amount) {
		NbtCompound nbt = IEntityDataSaver.castAndGet(playerEntity);
		int i = nbt.getInt("sanity");
		bound += 5;

		Difficulty difficulty = playerEntity.getWorld().getDifficulty();
		if (difficulty.equals(Difficulty.HARD)) {
			bound -= 3;
		} else if (difficulty.equals(Difficulty.EASY)) {
			bound += 3;
		}

		i = amount != null ? i + amount : i;
		if (playerEntity.getRandom().nextInt(bound) == 0) --i;
		if (i < 0) {
			i = 0;
		} else if (i > 10) {
			i = 10;
		}
		if (i < 2) {
			if (i == 0) {
				playerEntity.addStatusEffect(new StatusEffectInstance(CubliminalEffects.PARANOIA, 400,
					0, false, false, false));
			}

			int random = playerEntity.getRandom().nextInt(5 + 2 * i);
			switch (random) {
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

		nbt.putInt("sanity", i);
		return i;
	}

	public static void resetTimer(ServerPlayerEntity playerEntity) {
		IEntityDataSaver.castAndGet(playerEntity).putInt("sanity", 10);
	}

	public static void resetAfterDeath(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
		SanityData.resetTimer(newPlayer);
	}

	public static void syncSanity(ServerPlayerEntity playerEntity) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(IEntityDataSaver.castAndGet(playerEntity).getInt("sanity"));
		ServerPlayNetworking.send(playerEntity, CubliminalPackets.SANITY_SYNC, buf);
	}
}
