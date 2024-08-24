package net.limit.cubliminal.mixin;

import net.limit.cubliminal.util.NoClipEngine;
import net.limit.cubliminal.util.ParalyzingEntries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "doesRenderOnFire", at = @At("HEAD"), cancellable = true)
	private void renderFire(CallbackInfoReturnable<Boolean> cir) {
		if (NoClipEngine.isNoClipping(this)) cir.setReturnValue(false);
	}

	@Inject(method = "attemptTickInVoid", at = @At("HEAD"), cancellable = true)
	private void onTryTickInVoid(CallbackInfo ci) {
		if (NoClipEngine.isNoClipping(this)) ci.cancel();
	}

	@Inject(method = "isFireImmune", at = @At("HEAD"), cancellable = true)
	private void isImmuneToFire(CallbackInfoReturnable<Boolean> cir) {
		if (NoClipEngine.isNoClipping(this)) cir.setReturnValue(true);
	}

	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	private void suppressMovement(MovementType movementType, Vec3d movement, CallbackInfo ci) {
		if (!ParalyzingEntries.PARALYZING_ENTRIES.isEmpty()) ci.cancel();
	}
}
