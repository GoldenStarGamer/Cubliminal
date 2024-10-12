package net.limit.cubliminal.init;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.packet.*;
import net.minecraft.util.Identifier;

public class CubliminalPackets {
	public static final Identifier NOCLIP = Cubliminal.id("noclip");
	public static final Identifier RESET_NOCLIP_COOLDOWN = Cubliminal.id("reset_noclip_cooldown");
	public static final Identifier NOCLIP_SYNC = Cubliminal.id("noclip_sync");
	public static final Identifier SANITY_SYNC = Cubliminal.id("sanity_sync");

	public static void registerC2SPackets() {
		ServerPlayNetworking.registerGlobalReceiver(NOCLIP, NoClipC2SPacket::receive);
		ServerPlayNetworking.registerGlobalReceiver(RESET_NOCLIP_COOLDOWN, ResetNoClipCooldownC2SPacket::receive);
	}

	public static void registerS2CPackets() {
		ClientPlayNetworking.registerGlobalReceiver(NOCLIP_SYNC, NoClipS2CPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(SANITY_SYNC, SanitySyncS2CPacket::receive);
	}
}
