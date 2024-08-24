package net.limit.cubliminal.advancements;

import net.limit.cubliminal.Cubliminal;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AdvancementHelper {
	public static void grantAdvancement(PlayerEntity player, Identifier id) {
		if (player instanceof ServerPlayerEntity serverPlayerEntity) {
			AdvancementEntry advancementEntry = serverPlayerEntity.server.getAdvancementLoader().get(id);
			AdvancementProgress progress = serverPlayerEntity.getAdvancementTracker().getProgress(advancementEntry);

			if (!progress.isDone()) {
				progress
					.getUnobtainedCriteria()
					.forEach((criteria) -> serverPlayerEntity.getAdvancementTracker().grantCriterion(advancementEntry, criteria));
			}

		}
	}

	public static boolean isDone(PlayerEntity player, Identifier id) {
		if (player instanceof  ServerPlayerEntity serverPlayerEntity) {
			AdvancementEntry advancementEntry = serverPlayerEntity.server.getAdvancementLoader().get(id);
			AdvancementProgress progress = serverPlayerEntity.getAdvancementTracker().getProgress(advancementEntry);
			return progress.isDone();
		} else return false;
	}

	public static boolean visitedManilaRoom(PlayerEntity player) {
		return isDone(player, Cubliminal.id("backrooms/manila_room"));
	}
}
