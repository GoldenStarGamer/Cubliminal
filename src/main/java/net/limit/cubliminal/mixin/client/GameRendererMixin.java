package net.limit.cubliminal.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.config.CubliminalConfig;
import net.limit.cubliminal.init.CubliminalEffects;
import net.ludocrypt.limlib.impl.shader.PostProcesserManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Pool;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private Pool pool;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawEntityOutlinesFramebuffer()V", shift = At.Shift.AFTER))
    private void cubliminal$renderPostEffects(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (client.player != null && client.world != null && client.player.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(CubliminalEffects.PARANOIA))
                && !CubliminalConfig.get().disableAggressiveGraphics) {

            PostProcesserManager.INSTANCE.find(Cubliminal.id("paranoia")).render(client.getFramebuffer(), this.pool);
        }
    }
}
