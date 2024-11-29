package net.limit.cubliminal;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.limit.cubliminal.config.CubliminalConfig;
import net.limit.cubliminal.event.ServerTickHandler;
import net.limit.cubliminal.event.command.NoClipCommand;
import net.limit.cubliminal.event.command.SanityCommand;
import net.limit.cubliminal.init.*;
import net.limit.cubliminal.util.IEntityDataSaver;
import net.limit.cubliminal.util.NoClipEngine;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cubliminal implements ModInitializer {
	public static final String MOD_ID = "cubliminal";

	public static Identifier id(String id) {
		return new Identifier(MOD_ID, id);
	}

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ServerWorld LVL_0;

	public static final RegistryKey<DamageType> MENTAL_COLLAPSE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("mental_collapse"));


	@Override
	public void onInitialize() {
		AutoConfig.register(CubliminalConfig.class, GsonConfigSerializer::new);

		CubliminalItemGroups.init();
		CubliminalItems.init();
		CubliminalBlocks.init();
		CubliminalBiomes.init();
		CubliminalSounds.init();
		CubliminalEntities.init();
		CubliminalEffects.init();
		CubliminalBlockEntities.init();

		PayloadTypeRegistry.playC2S().register(CubliminalPackets.NoClipC2SPayload.ID, CubliminalPackets.NoClipC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(CubliminalPackets.NoClipSyncPayload.ID, CubliminalPackets.NoClipSyncPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(CubliminalPackets.SanitySyncPayload.ID, CubliminalPackets.SanitySyncPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(CubliminalPackets.NoClipC2SPayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			NbtCompound nbt = IEntityDataSaver.cast(player);
			if (NoClipEngine.canNoCLip(player)) {
				if (payload.reset()) nbt.putInt("ticksToNc", 0);
				else NoClipEngine.noClip(player);
			}
		});

		ServerTickEvents.START_SERVER_TICK.register(new ServerTickHandler());
		ServerPlayerEvents.AFTER_RESPAWN.register(ServerTickHandler::onAfterDeath);
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(ServerTickHandler::afterWorldChange);
		CommandRegistrationCallback.EVENT.register(NoClipCommand::register);
		CommandRegistrationCallback.EVENT.register(SanityCommand::register);
		ServerLifecycleEvents.SERVER_STARTED.register(server -> LVL_0 = server.getWorld(CubliminalRegistrar.THE_LOBBY_KEY));
	}
}