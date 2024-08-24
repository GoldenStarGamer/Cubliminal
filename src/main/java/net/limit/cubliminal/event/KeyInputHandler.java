package net.limit.cubliminal.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.limit.cubliminal.config.CubliminalConfig;
import net.limit.cubliminal.init.CubliminalPackets;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
	public static final String KEY_CATEGORY_CUBLIMINAL = "key.category.cubliminal";
	public static final String KEY_NOCLIP = "key.cubliminal.noclip";

	public static KeyBinding noClipKey;


	public static void registerKeyInputs() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;
			if (CubliminalConfig.get().allowNoClip && noClipKey.wasPressed()) {
				if (CubliminalConfig.get().crouchingNoClippingResetsCooldown && client.player.isSneaking()) {
					ClientPlayNetworking.send(CubliminalPackets.RESET_NOCLIP_COOLDOWN, PacketByteBufs.create());
				} else ClientPlayNetworking.send(CubliminalPackets.NOCLIP, PacketByteBufs.create());
			}
		});
	}

	public static void register() {
		noClipKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			KEY_NOCLIP, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, KEY_CATEGORY_CUBLIMINAL
		));
		registerKeyInputs();
	}
}
