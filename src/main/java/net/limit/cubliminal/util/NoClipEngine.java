package net.limit.cubliminal.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.init.CubliminalPackets;
import net.limit.cubliminal.init.CubliminalSounds;
import net.limit.cubliminal.init.CubliminalRegistrar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;

import java.util.HashMap;


public class NoClipEngine {
    public static HashMap<ServerPlayerEntity, RegistryKey<World>> NOCLIP_MAP = new HashMap<>();

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
        RegistryKey<World> registryKey = player.getWorld().getRegistryKey();

        if (registryKey.equals(CubliminalRegistrar.THE_LOBBY_KEY)) {
            NOCLIP_MAP.put(player, CubliminalRegistrar.HABITABLE_ZONE_KEY);
        } else if (registryKey.equals(CubliminalRegistrar.HABITABLE_ZONE_KEY)) {
            NOCLIP_MAP.put(player, RegistryKeys.toWorldKey(DimensionOptions.OVERWORLD));
        }

        NbtCompound nbt = IEntityDataSaver.cast(playerEntity);
        nbt.putInt("ticksToNc", -1);
    }

    public static <T extends PlayerEntity> void noClip(T playerEntity, RegistryKey<World> registryKey) {
        ServerPlayerEntity player = (ServerPlayerEntity) playerEntity;
        CubliminalSounds.clientPlaySoundSingle(player, CubliminalSounds.NOCLIPPING, SoundCategory.MASTER,
                player.getX(), player.getY(), player.getZ(),1f, 1f, 1);

        NbtCompound nbt = IEntityDataSaver.cast(playerEntity);
        nbt.putInt("ticksToNc", -1);

        NOCLIP_MAP.put(player, registryKey);
    }

    public static void noClipDestination(ServerPlayerEntity playerEntity) {
        RegistryKey<World> registryKey = NOCLIP_MAP.remove(playerEntity);
        BlockPos destination;

        if (registryKey == null) registryKey = CubliminalRegistrar.THE_LOBBY_KEY;

        if (registryKey.getValue().getNamespace().equals(Cubliminal.MOD_ID)) {
            destination = new BlockPos(4, 2, 4);
        } else {
            registryKey = RegistryKeys.toWorldKey(DimensionOptions.OVERWORLD);
            destination = playerEntity.getServerWorld().getSpawnPos();
        }

        playerEntity.setSpawnPoint(registryKey, destination, 0f, true, false);

        TeleportTarget teleportTarget = new TeleportTarget(playerEntity.getServer().getWorld(registryKey), destination.toCenterPos()
                .add(0, 2.5, 0), Vec3d.ZERO, playerEntity.getYaw(), playerEntity.getPitch(), NoClipEngine::afterNoCLip);

        playerEntity.teleportTo(teleportTarget);
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
