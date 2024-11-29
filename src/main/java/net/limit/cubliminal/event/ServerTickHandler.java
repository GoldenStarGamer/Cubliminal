package net.limit.cubliminal.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.advancements.AdvancementHelper;
import net.limit.cubliminal.util.IEntityDataSaver;
import net.limit.cubliminal.util.NoClipEngine;
import net.limit.cubliminal.util.SanityData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;



public class ServerTickHandler implements ServerTickEvents.StartTick {
	public static boolean inBackrooms(RegistryKey<World> key) {
		return key.getValue().getNamespace().equals(Cubliminal.MOD_ID);
	}

	@Override
	public void onStartTick(MinecraftServer server) {

		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			boolean isVulnerable = !player.isCreative() && !player.isSpectator();
			// sync both sanity and no-clip cooldown
			SanityData.syncSanity(player);
			NoClipEngine.syncNoClip(player);
			// run no-clip cooldown
			if (NoClipEngine.isNoClipping(player) || (isVulnerable && AdvancementHelper.visitedManilaRoom(player))) {
				NoClipEngine.run(player);
			}
			// run sanity stuff
			if (isVulnerable && inBackrooms(player.getWorld().getRegistryKey())
					&& !player.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
				if (player.getWorld().getTime() % 200 == 0) {
					SanityData.run(player);
				}
			}
		}


	}

	public static void afterWorldChange(ServerPlayerEntity player, ServerWorld origin, ServerWorld destination) {
		RegistryKey<World> key = destination.getRegistryKey();
		if (inBackrooms(key) && !AdvancementHelper.visitedManilaRoom(player)) {
			// reset sanity as it was uninitialized
			SanityData.resetTimer(player);
		}
	}

	public static void onAfterDeath(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
		NbtCompound oldNbt = IEntityDataSaver.cast(oldPlayer);
		NbtCompound newNbt = IEntityDataSaver.cast(newPlayer);
		newNbt.putInt("ticksToNc", oldNbt.getInt("ticksToNc"));
		SanityData.resetTimer(newPlayer);
	}
}
