package net.limit.cubliminal.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.block.entity.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CubliminalBlockEntities {

	public static <T extends BlockEntityType<?>> T register(String id, T blockEntityType) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, Cubliminal.id(id), blockEntityType);
	}

	public static final BlockEntityType<TheLobbyGatewayBlockEntity> THE_LOBBY_GATEWAY_BLOCK_ENTITY =
			register("the_lobby_gateway_block", FabricBlockEntityTypeBuilder.create(TheLobbyGatewayBlockEntity::new, CubliminalBlocks.THE_LOBBY_GATEWAY_BLOCK).build());

	public static final BlockEntityType<SinkBlockEntity> SINK_BLOCK_ENTITY =
			register("sink", FabricBlockEntityTypeBuilder.create(SinkBlockEntity::new, CubliminalBlocks.SINK).build());

	public static final BlockEntityType<ShowerBlockEntity> SHOWER_BLOCK_ENTITY =
			register("shower", FabricBlockEntityTypeBuilder.create(ShowerBlockEntity::new, CubliminalBlocks.SHOWER).build());


    public static void init() {
    }
}
