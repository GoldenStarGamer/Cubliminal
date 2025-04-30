package net.limit.cubliminal.init;

import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.world.structure.LevelLayerBasedStructure;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.structure.StructureType;

public class CubliminalStructures {
    public static StructureType<LevelLayerBasedStructure> LEVEL_LAYER_BASED_STRUCTURES;

    public static void init() {
        LEVEL_LAYER_BASED_STRUCTURES = Registry.register(Registries.STRUCTURE_TYPE, Cubliminal.id("level_layer_based_structures"), () -> LevelLayerBasedStructure.CODEC);
    }
}
