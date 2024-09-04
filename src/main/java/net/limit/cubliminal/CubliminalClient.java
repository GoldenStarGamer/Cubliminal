package net.limit.cubliminal;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.limit.cubliminal.client.hud.NoClippingHudOverlay;
import net.limit.cubliminal.client.hud.SanityBarHudOverlay;
import net.limit.cubliminal.entity.client.BacteriaModel;
import net.limit.cubliminal.entity.client.BacteriaRenderer;
import net.limit.cubliminal.entity.client.SeatRenderer;
import net.limit.cubliminal.event.KeyInputHandler;
import net.limit.cubliminal.init.*;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class CubliminalClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(CubliminalBlocks.THE_LOBBY_GATEWAY_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), CubliminalBlocks.EMERGENCY_EXIT_DOOR_0, CubliminalBlocks.MOLD);

		KeyInputHandler.register();
		CubliminalModelRenderers.init();

		EntityRendererRegistry
				.register(CubliminalEntities.BACTERIA, BacteriaRenderer::new);
		EntityModelLayerRegistry
				.registerModelLayer(CubliminalModelLayers.BACTERIA, BacteriaModel::getTexturedModelData);
		EntityRendererRegistry
				.register(CubliminalEntities.SEAT_ENTITY, SeatRenderer::new);

		CubliminalPackets.registerS2CPackets();
		HudRenderCallback.EVENT.register(new NoClippingHudOverlay());
		HudRenderCallback.EVENT.register(new SanityBarHudOverlay());
	}
}
