package net.limit.cubliminal.init;

import net.limit.cubliminal.Cubliminal;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class CubliminalPackets {
	public static final Identifier NOCLIP = Cubliminal.id("noclip");
	public static final Identifier NOCLIP_SYNC = Cubliminal.id("noclip_sync");
	public static final Identifier SANITY_SYNC = Cubliminal.id("sanity_sync");


	public record NoClipSyncPayload(int ticks) implements CustomPayload {
		public static final CustomPayload.Id<NoClipSyncPayload> ID = new CustomPayload.Id<>(NOCLIP_SYNC);

		public static final PacketCodec<RegistryByteBuf, NoClipSyncPayload> CODEC =
				PacketCodec.tuple(PacketCodecs.VAR_INT, NoClipSyncPayload::ticks, NoClipSyncPayload::new);


		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	public record SanitySyncPayload(int sanity) implements CustomPayload {
		public static final CustomPayload.Id<SanitySyncPayload> ID = new CustomPayload.Id<>(SANITY_SYNC);

		public static final PacketCodec<RegistryByteBuf, SanitySyncPayload> CODEC =
				PacketCodec.tuple(PacketCodecs.VAR_INT, SanitySyncPayload::sanity, SanitySyncPayload::new);


		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	public record NoClipC2SPayload(boolean reset) implements CustomPayload {
		public static final CustomPayload.Id<NoClipC2SPayload> ID = new CustomPayload.Id<>(NOCLIP);

		public static final PacketCodec<RegistryByteBuf, NoClipC2SPayload> CODEC =
				PacketCodec.tuple(PacketCodecs.BOOL, NoClipC2SPayload::reset, NoClipC2SPayload::new);


		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
}
