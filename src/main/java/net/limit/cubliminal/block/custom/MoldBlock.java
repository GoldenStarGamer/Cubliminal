package net.limit.cubliminal.block.custom;

import com.mojang.serialization.MapCodec;
import net.limit.cubliminal.init.CubliminalBlocks;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class MoldBlock extends MultifaceGrowthBlock implements Waterloggable {
    public static final MapCodec<MoldBlock> CODEC = MoldBlock.createCodec(MoldBlock::new);
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public MoldBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends MultifaceGrowthBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return context.getStack().isOf(CubliminalBlocks.MOLD.asItem()) || super.canReplace(state, context);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public LichenGrower getGrower() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }
}
