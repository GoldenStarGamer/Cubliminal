package net.limit.cubliminal;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.limit.cubliminal.config.CubliminalConfig;
import net.limit.cubliminal.entity.custom.BacteriaEntity;
import net.limit.cubliminal.event.PlayerTickHandler;
import net.limit.cubliminal.event.command.NoClipCommand;
import net.limit.cubliminal.init.*;
import net.limit.cubliminal.util.NoClipEngine;
import net.limit.cubliminal.util.SanityData;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cubliminal implements ModInitializer {
	public static final String MOD_ID = "cubliminal";

	public static Identifier id(String id) {
		return new Identifier(MOD_ID, id);
	}

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
		CubliminalModelRenderers.init();
		FabricDefaultAttributeRegistry.register(CubliminalEntities.BACTERIA, BacteriaEntity.createBacteriaAttributes());
		CubliminalBlockEntities.init();
		CubliminalPackets.registerC2SPackets();
		ServerTickEvents.START_SERVER_TICK.register(new PlayerTickHandler());
		ServerPlayConnectionEvents.JOIN.register(NoClipEngine::onPlayerJoin);
		ServerPlayerEvents.AFTER_RESPAWN.register(SanityData::resetAfterDeath);
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(PlayerTickHandler::afterWorldChange);
		CommandRegistrationCallback.EVENT.register(NoClipCommand::register);
	}
}