package net.limit.cubliminal.networking.c2s;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.access.IEntityDataSaver;
import net.limit.cubliminal.event.noclip.NoClipEngine;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record NoClipC2SPayload(boolean reset) implements CustomPayload {
    public static final Identifier NOCLIP = Cubliminal.id("noclip");

    public static final CustomPayload.Id<NoClipC2SPayload> ID = new CustomPayload.Id<>(NOCLIP);

    public static final PacketCodec<RegistryByteBuf, NoClipC2SPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.BOOL, NoClipC2SPayload::reset, NoClipC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(NoClipC2SPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if (NoClipEngine.canNoCLip(player)) {
            if (payload.reset()) {
                NbtCompound nbt = IEntityDataSaver.cast(player);
                nbt.putInt("ticksToNc", 0);
            } else {
                NoClipEngine.noClip(player);
            }
        }
    }
}
