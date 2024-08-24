package net.limit.cubliminal.init;

import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.entity.custom.BacteriaEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CubliminalEntities {
    public static final EntityType<BacteriaEntity> BACTERIA = Registry.register(Registries.ENTITY_TYPE,
            Cubliminal.id("bacteria"),
		EntityType.Builder.create(BacteriaEntity::new, SpawnGroup.MONSTER)
			.setDimensions(0.8f,2.8f).build());

    public static void init() {
    }
}

