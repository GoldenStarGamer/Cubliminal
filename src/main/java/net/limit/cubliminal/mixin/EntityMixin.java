package net.limit.cubliminal.mixin;

import net.limit.cubliminal.access.PEAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "readNbt", at = @At("HEAD"))
	protected void fixPlayerData(NbtCompound nbtCompound, CallbackInfo ci) {
		if ((Entity) (Object) this instanceof PlayerEntity player && nbtCompound.contains("cubliminal.cubliminal_data", 10)) {
			NbtCompound persistentData = nbtCompound.getCompound("cubliminal.cubliminal_data");
			((PEAccessor) player).getNoclipEngine().setTicksToNc(persistentData.getInt("ticksToNc"));
			((PEAccessor) player).getSanityManager().setSanity(persistentData.getInt("sanity"));
			nbtCompound.remove("cubliminal.cubliminal_data");
		}
	}

	@Inject(method = "doesRenderOnFire", at = @At("HEAD"), cancellable = true)
	private void renderFire(CallbackInfoReturnable<Boolean> cir) {
		if ((Entity) (Object) this instanceof PlayerEntity player
				&& ((PEAccessor) player).getNoclipEngine().isClipping()) cir.setReturnValue(false);
	}

	@Inject(method = "attemptTickInVoid", at = @At("HEAD"), cancellable = true)
	private void onTryTickInVoid(CallbackInfo ci) {
		if ((Entity) (Object) this instanceof PlayerEntity player
				&& ((PEAccessor) player).getNoclipEngine().isClipping()) ci.cancel();
	}

	@Inject(method = "isFireImmune", at = @At("HEAD"), cancellable = true)
	private void isImmuneToFire(CallbackInfoReturnable<Boolean> cir) {
		if ((Entity) (Object) this instanceof PlayerEntity player
				&& ((PEAccessor) player).getNoclipEngine().isClipping()) cir.setReturnValue(true);
	}
}
