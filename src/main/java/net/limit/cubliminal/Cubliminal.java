package net.limit.cubliminal;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.limit.cubliminal.config.CubliminalConfig;
import net.limit.cubliminal.entity.custom.BacteriaEntity;
import net.limit.cubliminal.event.ServerTickHandler;
import net.limit.cubliminal.event.command.NoClipCommand;
import net.limit.cubliminal.event.command.SanityCommand;
import net.limit.cubliminal.init.*;
import net.limit.cubliminal.util.SanityData;
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
		ServerTickEvents.START_SERVER_TICK.register(new ServerTickHandler());
		ServerPlayerEvents.AFTER_RESPAWN.register(SanityData::resetAfterDeath);
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(ServerTickHandler::afterWorldChange);
		CommandRegistrationCallback.EVENT.register(NoClipCommand::register);
		CommandRegistrationCallback.EVENT.register(SanityCommand::register);
		ServerLifecycleEvents.SERVER_STARTED.register(server -> LVL_0 = server.getWorld(CubliminalWorlds.THE_LOBBY_KEY));
	}
}