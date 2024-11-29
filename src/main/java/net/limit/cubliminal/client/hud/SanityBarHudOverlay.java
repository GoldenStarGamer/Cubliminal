package net.limit.cubliminal.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.util.IEntityDataSaver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SanityBarHudOverlay implements HudRenderCallback {
	private static final Identifier SANITY_BAR_0 = Cubliminal.id("textures/hud/sanity/sanity_bar_0.png");
	private static final Identifier SANITY_BAR_1 = Cubliminal.id("textures/hud/sanity/sanity_bar_1.png");
	private static final Identifier SANITY_BAR_2 = Cubliminal.id("textures/hud/sanity/sanity_bar_2.png");
	private static final Identifier SANITY_BAR_3 = Cubliminal.id("textures/hud/sanity/sanity_bar_3.png");
	private static final Identifier SANITY_BAR_4 = Cubliminal.id("textures/hud/sanity/sanity_bar_4.png");

	MinecraftClient client = MinecraftClient.getInstance();

	@Override
	public void onHudRender(DrawContext drawContext, float tickDelta) {

		ClientPlayerEntity player = client.player;
		if (player == null || player.isCreative() || player.isSpectator() ||
			!player.getWorld().getRegistryKey().getValue().getNamespace()
					.equals(Cubliminal.MOD_ID) || client.options.hudHidden) return;

		int x = drawContext.getScaledWindowWidth() / 2 + 95;
		int l = drawContext.getScaledWindowHeight() - 34;
		int i = IEntityDataSaver.cast(player).getInt("sanity");
		Identifier texture;
		RenderSystem.enableBlend();

		if (i > 8) {
			texture = SANITY_BAR_0;
		} else if (i > 6) {
			texture = SANITY_BAR_1;
		} else if (i > 3) {
			texture = SANITY_BAR_2;
		} else if (i > 1) {
			texture = SANITY_BAR_3;
		} else {
			texture = SANITY_BAR_4;
		}

		drawContext.drawTexture(texture, x, l, 2, 0, 12, 32, 32, 32);
		if (i > 0) {
			int k = (int) (i * 0.1 * 28);
			drawContext.drawTexture(texture, x + 2, l + 30 - k, 20, 30 - k, 8, k, 32, 32);
		}
		int j = 3;
		if (i > 9) j = 4;
		String string = i * 10 + "%";
		drawContext.drawText(client.inGameHud.getTextRenderer(), string, x - j, l - 7, 20165255, true);
		RenderSystem.disableBlend();
	}
}
