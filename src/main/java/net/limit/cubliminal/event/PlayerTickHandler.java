package net.limit.cubliminal.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.advancements.AdvancementHelper;
import net.limit.cubliminal.util.NoClipEngine;
import net.limit.cubliminal.util.SanityData;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;


public class PlayerTickHandler implements ServerTickEvents.StartTick {
	public static boolean isVulnerable(ServerPlayerEntity player) {
		return !player.isCreative() && !player.isSpectator();
	}

	@Override
	public void onStartTick(MinecraftServer server) {
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			SanityData.syncSanity(player);
			NoClipEngine.syncNoClip(player);
			if (isVulnerable(player)) {
				if (AdvancementHelper.visitedManilaRoom(player) || NoClipEngine.isNoClipping(player)) {
					NoClipEngine.decreaseTimer(player);
				}
				if (player.getWorld().getRegistryKey().getValue().getNamespace().equals(Cubliminal.MOD_ID)
						&& AdvancementHelper.isDone(player, Cubliminal.id("backrooms/backrooms"))
						&& !player.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
					if (player.getWorld().getTime() % 200 == 0) {
						int bound = player.isSprinting() ? 17 : 14;
						SanityData.modifyTimer(player, bound, null);
					}
				}
			}
		}
	}

	public static void afterWorldChange(ServerPlayerEntity player, ServerWorld origin, ServerWorld destination) {
		RegistryKey<World> registryKey = destination.getRegistryKey();
		if (registryKey.getValue().getNamespace().equals(Cubliminal.MOD_ID)) {
			player.setSpawnPoint(registryKey, new BlockPos(0, 2, 0),
				0f, true, false);

			if (!AdvancementHelper.visitedManilaRoom(player)) {
				SanityData.resetTimer(player);
			}
		}
	}
}
