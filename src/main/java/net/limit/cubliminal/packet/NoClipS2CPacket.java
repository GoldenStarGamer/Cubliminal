package net.limit.cubliminal.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.limit.cubliminal.util.IEntityDataSaver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class NoClipS2CPacket {
	public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
							   PacketByteBuf buf, PacketSender responseSender) {
		if (client.player != null) IEntityDataSaver.castAndGet(client.player).putInt("ticksToNc", buf.readInt());
	}
}
