package net.limit.cubliminal.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.limit.cubliminal.init.CubliminalStructures;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.DimensionPadding;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class LevelLayerBasedStructure extends Structure {

    public static final MapCodec<LevelLayerBasedStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelLayerBasedStructure.configCodecBuilder(instance),
            StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
            Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
            DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(structure -> structure.dimensionPadding),
            StructureLiquidSettings.codec.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings),
            TagKey.codec(RegistryKeys.BLOCK).optionalFieldOf("block_filter").forGetter(structure -> structure.blockFilter)
    ).apply(instance, LevelLayerBasedStructure::new));

    private final RegistryEntry<StructurePool> startPool;
    private final Optional<Identifier> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Type> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final DimensionPadding dimensionPadding;
    private final StructureLiquidSettings liquidSettings;
    private final Optional<TagKey<Block>> blockFilter;

    public LevelLayerBasedStructure(Config config, RegistryEntry<StructurePool> startPool, Optional<Identifier> startJigsawName,
                                    int size, HeightProvider startHeight, Optional<Heightmap.Type> projectStartToHeightmap,
                                    int maxDistanceFromCenter, DimensionPadding dimensionPadding,
                                    StructureLiquidSettings liquidSettings, Optional<TagKey<Block>> blockFilter) {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
        this.blockFilter = blockFilter;
    }

    private Optional<ChunkPos> extraSpawningChecks(Context context) {
        ChunkPos chunkPos = context.chunkPos();
        return Optional.empty();
    }

    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        return Optional.empty();
    }

    @Override
    public StructureType<?> getType() {
        return CubliminalStructures.LEVEL_LAYER_BASED_STRUCTURES;
    }
}
