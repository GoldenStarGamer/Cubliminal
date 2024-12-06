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
import net.limit.cubliminal.client.render.ManilaGatewayRenderer;
import net.limit.cubliminal.entity.client.SeatRenderer;
import net.limit.cubliminal.event.KeyInputHandler;
import net.limit.cubliminal.init.*;
import net.limit.cubliminal.util.IEntityDataSaver;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class CubliminalClient implements ClientModInitializer {

	public static final ShaderProgramKey RENDERTYPE_CUBLIMINAL_MANILA_SKYBOX = ShaderProgramKeys.register("rendertype_cubliminal_manila_skybox", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);

	public static final RenderPhase.ShaderProgram MANILA_PROGRAM = new RenderPhase.ShaderProgram(RENDERTYPE_CUBLIMINAL_MANILA_SKYBOX);

	public static final RenderLayer MANILA = RenderLayer.of("manila", VertexFormats.POSITION,
			VertexFormat.DrawMode.QUADS, 1536, false, false,
			RenderLayer.MultiPhaseParameters.builder().program(MANILA_PROGRAM).texture(
					RenderPhase.Textures.create()
							.add(Cubliminal.id("textures/sky/manila_" + 0 + ".png"), false, false)
							.add(Cubliminal.id("textures/sky/manila_" + 1 + ".png"), false, false)
							.add(Cubliminal.id("textures/sky/manila_" + 2 + ".png"), false, false)
							.add(Cubliminal.id("textures/sky/manila_" + 3 + ".png"), false, false)
							.add(Cubliminal.id("textures/sky/manila_" + 4 + ".png"), false, false)
							.add(Cubliminal.id("textures/sky/manila_" + 5 + ".png"), false, false)
							.build()).build(false));

	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(CubliminalBlocks.THE_LOBBY_GATEWAY_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(),
				CubliminalBlocks.THE_LOBBY_GATEWAY_BLOCK,
				CubliminalBlocks.EMERGENCY_EXIT_DOOR_0,
				CubliminalBlocks.MOLD,
				CubliminalBlocks.JUMBLED_DOCUMENTS,
				CubliminalBlocks.EXIT_SIGN,
				CubliminalBlocks.SMOKE_DETECTOR);

		BlockEntityRendererFactories.register(CubliminalBlockEntities.THE_LOBBY_GATEWAY_BLOCK_ENTITY, ManilaGatewayRenderer::new);

		KeyInputHandler.register();

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
