package net.limit.cubliminal.init;

import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.block.entity.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CubliminalBlockEntities {

	public static final BlockEntityType<TheLobbyGatewayBlockEntity> THE_LOBBY_GATEWAY_BLOCK_ENTITY =
		Registry.register(Registries.BLOCK_ENTITY_TYPE, Cubliminal.id("the_lobby_gateway_block"),
				BlockEntityType.Builder.create(TheLobbyGatewayBlockEntity::new, CubliminalBlocks.THE_LOBBY_GATEWAY_BLOCK).build());

	public static final BlockEntityType<SinkBlockEntity> SINK_BLOCK_ENTITY =
		Registry.register(Registries.BLOCK_ENTITY_TYPE, Cubliminal.id("sink"),
				BlockEntityType.Builder.create(SinkBlockEntity::new, CubliminalBlocks.SINK).build());

	public static final BlockEntityType<ShowerBlockEntity> SHOWER_BLOCK_ENTITY =
		Registry.register(Registries.BLOCK_ENTITY_TYPE, Cubliminal.id("shower"),
				BlockEntityType.Builder.create(ShowerBlockEntity::new, CubliminalBlocks.SHOWER).build());


    public static void init() {
    }
}
