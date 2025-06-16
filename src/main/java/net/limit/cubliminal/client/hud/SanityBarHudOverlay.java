package net.limit.cubliminal.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.access.PEAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SanityBarHudOverlay implements HudRenderCallback {
	private static final Identifier SANITY_BAR_0 = Cubliminal.id("textures/hud/sanity/sanity_bar.png");

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;
		if (player == null || player.isCreative() || player.isSpectator() ||
			!player.getWorld().getRegistryKey().getValue().getNamespace()
					.equals(Cubliminal.MOD_ID) || client.options.hudHidden) return;

		int x = drawContext.getScaledWindowWidth() / 2 + 95;
		int l = drawContext.getScaledWindowHeight() - 34;
		int i = ((PEAccessor) player).getSanityManager().getSanity();
		RenderSystem.enableBlend();
		int color1 = 0xffff8070;
		int color2 = 0xffff3722;
		if (i > 80) {
			color1 = -1;
			color2 = -1;
		} else if (i > 60) {
			color1 = 0xfff7ff86;
			color2 = 0xfff4ff50;
		} else if (i > 30) {
			color1 = 0xffffd777;
			color2 = 0xffffc12b;
		} else if (i > 10) {
			color1 = 0xffffa269;
			color2 = 0xffff7722;
		}

		drawContext.drawTexture(RenderLayer::getGuiTextured, SANITY_BAR_0, x, l, 2, 0, 12, 32, 32, 32, color1);

		if (i > 0) {
			int k = (int) (i * 0.01 * 28);
			drawContext.drawTexture(RenderLayer::getGuiTextured, SANITY_BAR_0, x + 2, l + 30 - k, 20, 30 - k, 8, k, 32, 32, color2);
		}
		int j = i > 90 ? 4 : 3;
		String string = i + "%";
		drawContext.drawText(client.inGameHud.getTextRenderer(), string, x - j, l - 7, 20165255, true);
		RenderSystem.disableBlend();
	}
}
