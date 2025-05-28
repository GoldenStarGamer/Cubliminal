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

public record SanitySyncPayload(int sanity) implements CustomPayload {
    public static final Identifier SANITY_SYNC = Cubliminal.id("sanity_sync");

    public static final CustomPayload.Id<SanitySyncPayload> ID = new CustomPayload.Id<>(SANITY_SYNC);

    public static final PacketCodec<RegistryByteBuf, SanitySyncPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.VAR_INT, SanitySyncPayload::sanity, SanitySyncPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SanitySyncPayload payload, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        if (player != null) {
            ((PEAccessor) player).getSanityManager().setSanity(payload.sanity());
        }
    }
}
