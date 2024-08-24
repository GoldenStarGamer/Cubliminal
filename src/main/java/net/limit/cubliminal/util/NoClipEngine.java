package net.limit.cubliminal.util;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.init.CubliminalPackets;
import net.limit.cubliminal.init.CubliminalSounds;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;

import java.util.HashSet;
import java.util.Set;

import static net.limit.cubliminal.init.CubliminalSounds.clientPlaySoundSingle;


public class NoClipEngine {
    public static Set<Object> TO_OVER = new HashSet<>();
    public static Set<Object> TO_LEVEL_0 = new HashSet<>();

    public static boolean isNoClipping(Object object) {
        return ((IEntityDataSaver) object).cubliminal$getPersistentData().getInt("ticksToNc") < 0;
    }

    public static int decreaseTimer(ServerPlayerEntity playerEntity) {
        NbtCompound nbt = IEntityDataSaver.castAndGet(playerEntity);
        int i = nbt.getInt("ticksToNc");

        if (i > 1) {
            if (playerEntity.isSprinting()) i--;
        } else if (i < 0 && i > -80) {
            i--;
        } else if (i != 1) {
            if (i <= -80 && playerEntity.isAlive() && !playerEntity.isDisconnected()) {
                noClipDestination(playerEntity);
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 50,
                        0, false, false, false));
            }
            i = playerEntity.getRandom().nextInt(200);
        }

        nbt.putInt("ticksToNc", i);
        return i;
    }

    public static void noClip(Object object) {
        ServerPlayerEntity player = (ServerPlayerEntity) object;
        player.changeGameMode(GameMode.SURVIVAL);
        clientPlaySoundSingle(player, CubliminalSounds.NOCLIPPING, SoundCategory.MASTER,
                player.getX(), player.getY(), player.getZ(),1f, 1f, 1);
        NbtCompound nbt = IEntityDataSaver.castAndGet(object);
        nbt.putInt("ticksToNc", -1);
    }

    public static void noClip(Object object, RegistryKey<World> registryKey) {
        ServerPlayerEntity player = (ServerPlayerEntity) object;
        player.changeGameMode(GameMode.SURVIVAL);
        clientPlaySoundSingle(player, CubliminalSounds.NOCLIPPING, SoundCategory.MASTER,
                player.getX(), player.getY(), player.getZ(),1f, 1f, 1);

        NbtCompound nbt = IEntityDataSaver.castAndGet(object);
        nbt.putInt("ticksToNc", -1);

        if (registryKey.getValue().getNamespace().equals(Cubliminal.MOD_ID)) {
            if (registryKey.equals(CubliminalWorlds.THE_LOBBY_KEY)) {
                TO_LEVEL_0.add(player);
            }
        } else {
            TO_OVER.add(player);
        }
    }

    public static void noClipDestination(ServerPlayerEntity playerEntity) {
        RegistryKey<World> registryKey = playerEntity.getWorld().getRegistryKey();
        Vec3d destination;

        if (TO_OVER.contains(playerEntity)) {
            registryKey = RegistryKeys.toWorldKey(DimensionOptions.OVERWORLD);
            destination = playerEntity.getServerWorld().getSpawnPos().toCenterPos();
            TO_OVER.remove(playerEntity);
        } else if (TO_LEVEL_0.contains(playerEntity)) {
            registryKey = CubliminalWorlds.THE_LOBBY_KEY;
            destination = new Vec3d(2.5, 2, 2.5);
            TO_LEVEL_0.remove(playerEntity);
        } else if (registryKey.getValue().getNamespace().equals(Cubliminal.MOD_ID)) {
            registryKey = RegistryKeys.toWorldKey(DimensionOptions.OVERWORLD);
            destination = playerEntity.getServerWorld().getSpawnPos().toCenterPos();
        } else {
            registryKey = CubliminalWorlds.THE_LOBBY_KEY;
            destination = new Vec3d(2.5, 2, 2.5);
        }

        FabricDimensions.teleport(playerEntity, playerEntity.getServer().getWorld(registryKey),
                new TeleportTarget(destination.add(0, 2.5, 0), new Vec3d(0, 0, 0),
                        playerEntity.getYaw(), playerEntity.getPitch()));
    }

    public static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        int i = IEntityDataSaver.castAndGet(handler.player).getInt("ticksToNc");
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(i);
        ServerPlayNetworking.send(handler.player, CubliminalPackets.NOCLIP_SYNC, buf);
    }

    public static void syncNoClip(ServerPlayerEntity playerEntity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(IEntityDataSaver.castAndGet(playerEntity).getInt("ticksToNc"));
        ServerPlayNetworking.send(playerEntity, CubliminalPackets.NOCLIP_SYNC, buf);
    }
}
