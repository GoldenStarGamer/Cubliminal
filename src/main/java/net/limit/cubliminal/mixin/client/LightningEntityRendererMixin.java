package net.limit.cubliminal.mixin.client;

import net.minecraft.client.render.entity.LightningEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightningEntityRenderer.class)
public class LightningEntityRendererMixin {

    @ModifyVariable(method = "drawBranch", at = @At("HEAD"), ordinal = 4, argsOnly = true)
    private static float red(float value) {
        return 0.435f;
    }

    @ModifyVariable(method = "drawBranch", at = @At("HEAD"), ordinal = 5, argsOnly = true)
    private static float green(float value) {
        return 0.012f;
    }

    @ModifyVariable(method = "drawBranch", at = @At("HEAD"), ordinal = 6, argsOnly = true)
    private static float blue(float value) {
        return 0.988f;
    }

}
