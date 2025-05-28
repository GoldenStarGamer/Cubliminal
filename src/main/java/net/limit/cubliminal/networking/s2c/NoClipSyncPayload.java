package net.limit.cubliminal.networking.s2c;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.access.PEAccessor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record NoClipSyncPayload(int ticks) implements CustomPayload {
    public static final Identifier NOCLIP_SYNC = Cubliminal.id("noclip_sync");

    public static final CustomPayload.Id<NoClipSyncPayload> ID = new CustomPayload.Id<>(NOCLIP_SYNC);

    public static final PacketCodec<RegistryByteBuf, NoClipSyncPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.VAR_INT, NoClipSyncPayload::ticks, NoClipSyncPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(NoClipSyncPayload payload, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        if (player != null) {
            ((PEAccessor) player).getNoclipEngine().setTicksToNc(payload.ticks());
        }
    }
}
