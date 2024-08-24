package net.limit.cubliminal.util;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.init.CubliminalPackets;
import net.limit.cubliminal.init.CubliminalWorlds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;

public class NoClipData {
	public static boolean isNoClipping(Object object) {
		return ((IEntityDataSaver) object).cubliminal$getPersistentData().getInt("tickslefttonc") < 0;
	}

	public static int runTimer(ServerPlayerEntity playerEntity) {
		NbtCompound nbt = IEntityDataSaver.castAndGet(playerEntity);
		int i = nbt.getInt("tickslefttonc");
		PacketByteBuf buf = PacketByteBufs.create();

		if (i > 1) {
			if (playerEntity.isSprinting()) nbt.putInt("tickslefttonc", i - 1);
		} else if (i < 0 && i > -80) {
			nbt.putInt("tickslefttonc", i - 1);
		} else if (i != 1) {
			if (i <= -80 && playerEntity.isAlive() && !playerEntity.isDisconnected()) {
				noClipDestination(playerEntity);
				playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 50,
					0, false, false, false));
			}
			int random = playerEntity.getRandom().nextInt(200);
			nbt.putInt("tickslefttonc", random);
			buf.writeInt(random);
			ServerPlayNetworking.send(playerEntity, CubliminalPackets.NOCLIP_SYNC, buf);
		}

		int x = nbt.getInt("tickslefttonc");
		if (i != x && (x == 1 || x == -2)) {
			buf.writeInt(x);
			ServerPlayNetworking.send(playerEntity, CubliminalPackets.NOCLIP_SYNC, buf);
		}
		return x;
	}

	public static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		int i = IEntityDataSaver.castAndGet(handler.player).getInt("tickslefttonc");
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(i);
		ServerPlayNetworking.send(handler.player, CubliminalPackets.NOCLIP_SYNC, buf);
	}

	public static void noClipDestination(ServerPlayerEntity player) {
		RegistryKey<World> registryKey = player.getWorld().getRegistryKey();
		Vec3d destination = player.getServerWorld().getSpawnPos().toCenterPos();

		if (registryKey.getValue().getNamespace().equals(Cubliminal.MOD_ID)) {
			registryKey = RegistryKeys.toWorldKey(DimensionOptions.OVERWORLD);
		} else {
			registryKey = CubliminalWorlds.THE_LOBBY_KEY;
		}

		FabricDimensions.teleport(player, player.getServer().getWorld(registryKey),
				new TeleportTarget(destination, new Vec3d(0, 0, 0),
						player.getYaw(), player.getPitch()));
	}
}
