package net.limit.cubliminal.access;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public interface IEntityDataSaver {
	NbtCompound cubliminal$getPersistentData();

	static <T extends PlayerEntity> NbtCompound cast(T player) {
		return ((IEntityDataSaver) player).cubliminal$getPersistentData();
	}
}
