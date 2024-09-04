package net.limit.cubliminal.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.Arrays;
import java.util.Optional;

public class SpreadableBlock extends Block {
    private Block deletableBlock;
    private Block[] blocks;
    public SpreadableBlock(Settings settings) {
        super(settings);
    }
    public SpreadableBlock(Optional<Block> deletableBlock, Settings settings, Block... blocks) {
        super(settings);
        deletableBlock.ifPresent(block -> this.deletableBlock = block);
        this.blocks = blocks;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(4) != 0) {
            return;
        }

        BlockPos pos2 = pos.add(random.nextInt(3) - 1,
                random.nextInt(3) - 1, random.nextInt(3) - 1);
        if (deletableBlock != null && world.getBlockState(pos2).isOf(deletableBlock)) {
            world.setBlockState(pos2, Blocks.AIR.getDefaultState());
        } else if (Arrays.stream(blocks).toList().contains(world.getBlockState(pos2).getBlock())) {
            world.setBlockState(pos2, state);
        }
    }
}
