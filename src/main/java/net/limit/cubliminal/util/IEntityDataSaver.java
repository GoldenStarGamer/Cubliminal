package net.limit.cubliminal.util;

import net.minecraft.nbt.NbtCompound;

public interface IEntityDataSaver {
	NbtCompound cubliminal$getPersistentData();

	static NbtCompound castAndGet(Object object) {
		return ((IEntityDataSaver) object).cubliminal$getPersistentData();
	}
}
