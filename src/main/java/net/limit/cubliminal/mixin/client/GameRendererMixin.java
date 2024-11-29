package net.limit.cubliminal.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.config.CubliminalConfig;
import net.limit.cubliminal.init.CubliminalEffects;
import net.limit.cubliminal.util.GameRendererAccessor;
import net.ludocrypt.limlib.impl.shader.PostProcesser;
import net.ludocrypt.limlib.impl.shader.PostProcesserManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements GameRendererAccessor {

    @Shadow
    protected abstract double getFov(Camera camera, float tickDelta, boolean changingFov);

    @Shadow
    protected abstract void tiltViewWhenHurt(MatrixStack matrices, float tickDelta);

    @Shadow
    protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Override
    public double cubliminal$callGetFov(Camera camera, float tickDelta, boolean changingFov) {
        return this.getFov(camera, tickDelta, changingFov);
    }

    @Override
    public void cubliminal$callTiltViewWhenHurt(MatrixStack matrices, float tickDelta) {
        this.tiltViewWhenHurt(matrices, tickDelta);
    }

    @Override
    public void cubliminal$callBobView(MatrixStack matrices, float tickDelta) {
        this.bobView(matrices, tickDelta);
    }

    @Shadow
    @Final
    MinecraftClient client;

    @Unique
    private final Function<Identifier, PostProcesser> memoizedShaders = Util
            .memoize(PostProcesserManager.INSTANCE::find);

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawEntityOutlinesFramebuffer()V", shift = At.Shift.BEFORE))
    private void cubliminal$renderPostEffects(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {

        if (client.player != null && client.world != null && client.player.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(CubliminalEffects.PARANOIA))
                && !CubliminalConfig.get().disableAggressiveGraphics) {
            PostProcesser shader = memoizedShaders.apply(Cubliminal.id("shaders/post/paranoia.json"));
            shader.getShaderEffect().setUniforms("RenderTime", (float) client.world.getTime());
            shader.render(tickDelta);
        }


    }
}
