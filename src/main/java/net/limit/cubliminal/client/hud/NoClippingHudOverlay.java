package net.limit.cubliminal.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.access.PEAccessor;
import net.limit.cubliminal.client.NoClippingSoundInstance;
import net.limit.cubliminal.config.CubliminalConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class NoClippingHudOverlay implements HudRenderCallback {

	public static NoClippingHudOverlay INSTANCE = new NoClippingHudOverlay();
	private boolean clippingIntoWall = false;
	private boolean aux_renderOverlay = false;

	private static final Identifier GLITCH_OVERLAY_1 = Cubliminal.id("textures/hud/noclip/glitch_overlay_1.png");
	private static final Identifier GLITCH_OVERLAY_2 = Cubliminal.id("textures/hud/noclip/glitch_overlay_2.png");

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;
		if (player != null && !CubliminalConfig.get().disableAggressiveGraphics) {
			if (((PEAccessor) player).getNoclipEngine().isClipping()) {
				for (int i = 0; i < 5; i++) {
					if ((player.getWorld().getTime() + i) % 8 == 0) {
						Random random = player.getRandom();
						float a = 1.0f;

						switch (random.nextInt(3)) {
							case 0:
							case 1:
								this.renderOverlay(drawContext, GLITCH_OVERLAY_1, a);
								break;
							case 2:
								this.renderOverlay(drawContext, GLITCH_OVERLAY_2, a);
								break;
						}
						break;
					}
				}
			} else if (this.clippingIntoWall || this.aux_renderOverlay) {
				for (int i = 0; i < 2; i++) {
					if ((player.getWorld().getTime() + i) % 6 == 0) {
						Random random = player.getRandom();
						float a = 1.0f;
						Identifier texture = GLITCH_OVERLAY_1;

						if (random.nextInt(3) == 0) {
							texture = GLITCH_OVERLAY_2;
						}

						if (!client.getSoundManager().isPlaying(NoClippingSoundInstance.CREATE)) {
							client.getSoundManager().play(NoClippingSoundInstance.CREATE);
						}

						this.renderOverlay(drawContext, texture, a);
						break;
					}
				}
			}
		}
	}

	private void renderOverlay(DrawContext drawContext, Identifier texture, float opacity) {
		int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
		int height = MinecraftClient.getInstance().getWindow().getScaledHeight();

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opacity);
		drawContext.drawTexture(RenderLayer::getGuiTextured, texture, 0, 0, 0.0f, 0.0f, width, height, width, height);
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
	}

    public boolean isClippingIntoWall() {
        return clippingIntoWall;
    }

    public void setClippingIntoWall(boolean clippingIntoWall) {
        this.clippingIntoWall = clippingIntoWall;
    }

    public void setAux_renderOverlay(boolean aux_renderOverlay) {
        this.aux_renderOverlay = aux_renderOverlay;
    }
}
