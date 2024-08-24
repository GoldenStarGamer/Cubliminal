package net.limit.cubliminal.mixin;

import net.limit.cubliminal.util.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class CubliminalEntityDataSaverMixin implements IEntityDataSaver {
	@Unique
	private NbtCompound persistentData;

	@Override
	public NbtCompound cubliminal$getPersistentData() {
		if (this.persistentData == null) {
			this.persistentData = new NbtCompound();
		}

		return persistentData;
	}

	@Inject(
		method = "writeNbt",
	at = @At("HEAD"))
	protected void injectWriteMethod(NbtCompound nbtCompound, CallbackInfoReturnable ci) {
		if (persistentData != null) {
			nbtCompound.put("cubliminal.cubliminal_data", persistentData);
		}
	}

	@Inject(
		method = "readNbt",
		at = @At("HEAD"))
	protected void injectReadMethod(NbtCompound nbtCompound, CallbackInfo ci) {
		if (nbtCompound.contains("cubliminal.cubliminal_data", 10)) {
			persistentData = nbtCompound.getCompound("cubliminal.cubliminal_data");
		}
	}
}
