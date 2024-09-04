package net.limit.cubliminal.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.limit.cubliminal.util.IEntityDataSaver;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ResetNoClipCooldownC2SPacket {
	public static void receive(MinecraftServer server, ServerPlayerEntity player,
							   ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		NbtCompound nbt = IEntityDataSaver.castAndGet(player);
		if (nbt.getInt("ticksToNc") == 1) nbt.putInt("ticksToNc", 0);
	}
}
