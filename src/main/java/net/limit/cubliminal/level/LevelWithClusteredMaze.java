package net.limit.cubliminal.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class LevelWithClusteredMaze extends LevelWithMaze implements LevelMaze, LevelClusteredMaze {
    public static Codec<LevelWithClusteredMaze> LEVEL_WITH_CLUSTERED_MAZE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("biome_block_offset", 0).forGetter(level -> level.biome_block_offset),
            Codec.INT.optionalFieldOf("world_height", 256).forGetter(level -> level.world_height),
            Codec.INT.optionalFieldOf("min_y", 0).forGetter(level -> level.min_y),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_layer_count", 0).forGetter(level -> level.layer_count),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("layer_height", 16).forGetter(level -> level.layer_height),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("spacing_x", 16).forGetter(level -> level.spacing_x),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("spacing_z", 16).forGetter(level -> level.spacing_z),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("maze_width", 4).forGetter(level -> level.maze_width),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("maze_height", 4).forGetter(level -> level.maze_height),
            Codec.LONG.optionalFieldOf("maze_seed_modifier", 0L).forGetter(level -> level.maze_seed_modifier),
            Codec.intRange(1, 64).optionalFieldOf("cluster_size_x", 4).forGetter(level -> level.cluster_size_x),
            Codec.intRange(1, 64).optionalFieldOf("cluster_size_z", 4).forGetter(level -> level.cluster_size_z)
    ).apply(instance, instance.stable(LevelWithClusteredMaze::new)));

    public final int biome_block_offset;
    public final int cluster_size_x;
    public final int cluster_size_z;

    public LevelWithClusteredMaze(int biome_block_offset, int world_height, int min_y, int max_layer_count, int layer_height,
                                  int spacing_x, int spacing_z, int maze_width, int maze_height, long maze_seed_modifier,
                                  int cluster_size_x, int cluster_size_z) {
        super(world_height, min_y, max_layer_count, layer_height,
                spacing_x, spacing_z, maze_width, maze_height, maze_seed_modifier);
        if (biome_block_offset % spacing_x == 0 && biome_block_offset % spacing_z == 0) {
            this.biome_block_offset = biome_block_offset;
        } else {
            throw new IllegalStateException("Biome block offset must be a multiple of both spacing_x and spacing_z");
        }
        if (cluster_size_x > 0) {
            this.cluster_size_x = cluster_size_x;
        } else {
            throw new IllegalStateException("Cluster size X should always greater than 0");
        }
        if (cluster_size_z > 0) {
            this.cluster_size_z = cluster_size_z;
        } else {
            throw new IllegalStateException("Cluster size Z should always greater than 0");
        }
    }

    @Override
    public int getBiomeBlockOffset() {
        return this.biome_block_offset;
    }

    @Override
    public int getClusterSizeX() {
        return this.cluster_size_x;
    }

    @Override
    public int getClusterSizeZ() {
        return this.cluster_size_z;
    }
}
