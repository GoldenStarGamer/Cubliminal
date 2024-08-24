package net.limit.cubliminal.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.config.CubliminalConfig;
import net.limit.cubliminal.util.IEntityDataSaver;
import net.limit.cubliminal.util.NoClipEngine;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class NoClippingHudOverlay implements HudRenderCallback {
	private static final Identifier GLITCH_OVERLAY_1 = Cubliminal.id(
			"textures/hud/noclip/glitch_overlay_1.png");
	private static final Identifier GLITCH_OVERLAY_2 = Cubliminal.id(
			"textures/hud/noclip/glitch_overlay_2.png");

	@Override
	public void onHudRender(DrawContext drawContext, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.player.isCreative() || client.player.isSpectator()) return;
		if (!CubliminalConfig.get().disableAggressiveGraphics) {
			if (NoClipEngine.isNoClipping(client.player)) {
				int random = new Random().nextInt(11);
				if (random < 2) {
					int width = client.getWindow().getScaledWidth();
					int height = client.getWindow().getScaledHeight();
					RenderSystem.disableDepthTest();
					RenderSystem.depthMask(false);
					drawContext.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
					drawContext.drawTexture(GLITCH_OVERLAY_1, 0, 0, -90, 0.0f, 0.0f, width, height, width, height);
					RenderSystem.depthMask(true);
					RenderSystem.enableDepthTest();
					drawContext.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
				} else if (random == 2) {
					int width = client.getWindow().getScaledWidth();
					int height = client.getWindow().getScaledHeight();
					float f = new Random().nextFloat(1);
					float g = new Random().nextFloat(1);
					float h = new Random().nextFloat(1);
					RenderSystem.disableDepthTest();
					RenderSystem.depthMask(false);
					drawContext.setShaderColor(f, g, h, 1.0f);
					drawContext.drawTexture(GLITCH_OVERLAY_2, 0, 0, -90, 0.0f, 0.0f, width, height, width, height);
					RenderSystem.depthMask(true);
					RenderSystem.enableDepthTest();
					drawContext.setShaderColor(f, g, h, 1.0f);
				}
			} else if (IEntityDataSaver.castAndGet(client.player).getInt("ticksToNc") == 1) {
				for (int i = 0; i < 5; i++) {
					if ((client.player.getWorld().getTime() + i) % 200 == 0) {
						Identifier texture = GLITCH_OVERLAY_1;
						int width = client.getWindow().getScaledWidth();
						int height = client.getWindow().getScaledHeight();
						float f = 1;
						float g = 1;
						float h = 1;
						if (new Random().nextInt(3) == 0) {
							f = new Random().nextFloat(1);
							g = new Random().nextFloat(1);
							h = new Random().nextFloat(1);
							texture = GLITCH_OVERLAY_2;
						}
						RenderSystem.disableDepthTest();
						RenderSystem.depthMask(false);
						drawContext.setShaderColor(f, g, h, 1.0f);
						drawContext.drawTexture(texture, 0, 0, -90, 0.0f, 0.0f, width, height, width, height);
						RenderSystem.depthMask(true);
						RenderSystem.enableDepthTest();
						drawContext.setShaderColor(f, g, h, 1.0f);
						break;
					}
				}
			}
		}
	}
}
