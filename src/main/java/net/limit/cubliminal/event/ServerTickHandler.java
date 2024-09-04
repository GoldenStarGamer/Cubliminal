package net.limit.cubliminal.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.advancements.AdvancementHelper;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.limit.cubliminal.util.NoClipEngine;
import net.limit.cubliminal.util.SanityData;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Optional;


public class ServerTickHandler implements ServerTickEvents.StartTick {
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
						Optional<RegistryKey<Biome>> registryKey = player.getWorld().getBiome(player.getBlockPos()).getKey();
						if (registryKey.isPresent() && registryKey.get().equals(CubliminalBiomes.REDROOMS_BIOME)) bound-= 4;
						SanityData.modifyTimer(player, bound, null);
					}
				}
			}
		}
	}

	public static void afterWorldChange(ServerPlayerEntity player, ServerWorld origin, ServerWorld destination) {
		RegistryKey<World> registryKey = destination.getRegistryKey();
		if (registryKey.getValue().getNamespace().equals(Cubliminal.MOD_ID)) {
			if (!AdvancementHelper.visitedManilaRoom(player)) {
				SanityData.resetTimer(player);
			}
		}
	}
}
