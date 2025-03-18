package net.limit.cubliminal.util;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.access.IEntityDataSaver;
import net.limit.cubliminal.init.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;


public class NoClipEngine {
    private static final Map<ServerPlayerEntity, Pair<RegistryKey<World>, Pair<BlockPos, Vec3d>>> NOCLIP_MAP = new HashMap<>();

    public static <T extends PlayerEntity> boolean isNoClipping(T player) {
        return ((IEntityDataSaver) player).cubliminal$getPersistentData().getInt("ticksToNc") < 0;
    }

    public static <T extends PlayerEntity> boolean canNoCLip(T player) {
        return ((IEntityDataSaver) player).cubliminal$getPersistentData()
                .getInt("ticksToNc") == 1 && player.isOnGround();
    }


    public static void run(ServerPlayerEntity playerEntity) {
        NbtCompound nbt = IEntityDataSaver.cast(playerEntity);
        int ticksToNc = nbt.getInt("ticksToNc");

        if (ticksToNc > 1) {
            if (playerEntity.isSprinting()) {
                --ticksToNc;
            }
        } else if (ticksToNc < 0 && ticksToNc > -80) {
            --ticksToNc;
        } else if (ticksToNc != 1) {
            if (ticksToNc < -79 && playerEntity.isAlive() && !playerEntity.isDisconnected()) {
                noClipDestination(playerEntity);
            }

            ticksToNc = playerEntity.getRandom().nextInt(6000) + 12000;
        }

        nbt.putInt("ticksToNc", ticksToNc);
    }

    public static <T extends PlayerEntity> void noClip(T playerEntity) {
        ServerPlayerEntity player = (ServerPlayerEntity) playerEntity;
        CubliminalSounds.clientPlaySoundSingle(player, CubliminalSounds.NOCLIPPING, SoundCategory.MASTER,
                player.getX(), player.getY(), player.getZ(),1f, 1f, 1);

        NOCLIP_MAP.put(player, NoclipDestination.getPos(player));

        NbtCompound nbt = IEntityDataSaver.cast(playerEntity);
        nbt.putInt("ticksToNc", -1);
    }

    public static <T extends PlayerEntity> void noClip(T playerEntity, RegistryKey<World> registryKey) {
        ServerPlayerEntity player = (ServerPlayerEntity) playerEntity;
        CubliminalSounds.clientPlaySoundSingle(player, CubliminalSounds.NOCLIPPING, SoundCategory.MASTER,
                player.getX(), player.getY(), player.getZ(),1f, 1f, 1);

        NbtCompound nbt = IEntityDataSaver.cast(playerEntity);
        nbt.putInt("ticksToNc", -1);

        NOCLIP_MAP.put(player, NoclipDestination.fromDestination(registryKey, player));
    }

    private static void noClipDestination(ServerPlayerEntity player) {
        Pair<RegistryKey<World>, Pair<BlockPos, Vec3d>> pair = NOCLIP_MAP.remove(player);
        RegistryKey<World> registryKey = pair.getFirst();

        player.setSpawnPoint(registryKey, pair.getSecond().getFirst(), 0f, true, false);

        TeleportTarget teleportTarget = new TeleportTarget(player.getServer().getWorld(registryKey),
                pair.getSecond().getSecond(), Vec3d.ZERO, player.getYaw(), player.getPitch(), NoClipEngine::afterNoCLip);

        player.teleportTo(teleportTarget);
        Cubliminal.LOGGER.info("Teleporting to: " + teleportTarget.position());
    }

    public static <T extends PlayerEntity> void setTimer(T playerEntity, int amount) {
        IEntityDataSaver.cast(playerEntity).putInt("ticksToNc", amount);
    }

    public static void syncNoClip(ServerPlayerEntity playerEntity) {
        int ticks = IEntityDataSaver.cast(playerEntity).getInt("ticksToNc");
        ServerPlayNetworking.send(playerEntity, new CubliminalPackets.NoClipSyncPayload(ticks));
    }

    public static void afterNoCLip(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 0, true, false, true));
        }
    }

}
