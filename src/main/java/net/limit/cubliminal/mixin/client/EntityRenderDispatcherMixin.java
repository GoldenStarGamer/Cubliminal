package net.limit.cubliminal.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.limit.cubliminal.init.CubliminalRegistrar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Unique
    private static MinecraftClient client = MinecraftClient.getInstance();

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At("HEAD"), cancellable = true)
    private <E extends Entity, S extends EntityRenderState> void cancelRender(E entity, double x, double y, double z,
                                                                              float tickDelta, MatrixStack matrices,
                                                                              VertexConsumerProvider vertexConsumers,
                                                                              int light, EntityRenderer<? super E, S> renderer,
                                                                              CallbackInfo ci) {
        if (client.player == null) return;
        if (entity.isPlayer() && client.player.getWorld().getRegistryKey().equals(
                CubliminalRegistrar.THE_LOBBY_KEY) && !entity.equals(client.player)
                && notInManilaRoom()) ci.cancel();
    }

    @Unique
    private static boolean notInManilaRoom() {
        return client.player.getBlockY() < 8;
    } //TODO 22 for 3 layers
}
