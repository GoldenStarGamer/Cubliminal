package net.limit.cubliminal.init;

import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.entity.custom.SeatEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CubliminalEntities {

    public static final EntityType<SeatEntity> SEAT_ENTITY = Registry.register(Registries.ENTITY_TYPE, Cubliminal.id("seat_entity"),
            EntityType.Builder.create(SeatEntity::new, SpawnGroup.MISC).dimensions(0f, 0f).build());

    public static void init() {
    }
}

