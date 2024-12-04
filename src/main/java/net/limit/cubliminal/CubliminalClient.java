package net.limit.cubliminal;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.limit.cubliminal.client.hud.NoClippingHudOverlay;
import net.limit.cubliminal.client.hud.SanityBarHudOverlay;
import net.limit.cubliminal.entity.client.SeatRenderer;
import net.limit.cubliminal.event.KeyInputHandler;
import net.limit.cubliminal.init.*;
import net.limit.cubliminal.util.IEntityDataSaver;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class CubliminalClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(CubliminalBlocks.THE_LOBBY_GATEWAY_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(),
				CubliminalBlocks.EMERGENCY_EXIT_DOOR_0,
				CubliminalBlocks.MOLD,
				CubliminalBlocks.JUMBLED_DOCUMENTS);

		KeyInputHandler.register();
		//CubliminalModelRenderers.init();

		EntityRendererRegistry
				.register(CubliminalEntities.SEAT_ENTITY, SeatRenderer::new);


		ClientPlayNetworking.registerGlobalReceiver(CubliminalPackets.NoClipSyncPayload.ID, (payload, context) -> {
			ClientPlayerEntity player = context.player();
			if (player != null) IEntityDataSaver.cast(player).putInt("ticksToNc", payload.ticks());
		});

		ClientPlayNetworking.registerGlobalReceiver(CubliminalPackets.SanitySyncPayload.ID, (payload, context) -> {
			ClientPlayerEntity player = context.player();
			if (player != null) IEntityDataSaver.cast(player).putInt("sanity", payload.sanity());
		});

		HudRenderCallback.EVENT.register(NoClippingHudOverlay.INSTANCE);
		HudRenderCallback.EVENT.register(new SanityBarHudOverlay());
	}
}
