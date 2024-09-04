package net.limit.cubliminal.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.limit.cubliminal.init.CubliminalWorlds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;
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

    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void cancelRenderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                           Entity entity, float opacity, float tickDelta, WorldView world,
                                           float radius, CallbackInfo ci) {
        if (client.player == null) return;
        if (entity.isPlayer() && client.player.getWorld().getRegistryKey().equals(
                CubliminalWorlds.THE_LOBBY_KEY) && !entity.equals(client.player)
                && notInManilaRoom()) ci.cancel();
    }

    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void cancelRenderHitbox(MatrixStack matrices, VertexConsumer vertices,
                                           Entity entity, float tickDelta, CallbackInfo ci) {
        if (client.player == null) return;
        if (entity.isPlayer() && client.player.getWorld().getRegistryKey().equals(
                CubliminalWorlds.THE_LOBBY_KEY) && !entity.equals(client.player)
                && notInManilaRoom()) ci.cancel();
    }

    @Unique
    private static boolean notInManilaRoom() {
        return client.player.getBlockY() < 22;
    }
}
