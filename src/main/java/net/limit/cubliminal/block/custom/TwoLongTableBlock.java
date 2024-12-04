package net.limit.cubliminal.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;


public class TwoLongTableBlock extends HorizontalFacingBlock {
	public static final MapCodec<TwoLongTableBlock> CODEC = TwoLongTableBlock.createCodec(TwoLongTableBlock::new);
	public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape WE_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(16, 0, -7, 18, 14, -5),
		Block.createCuboidShape(-2, 0, -7, 0, 14, -5),
		Block.createCuboidShape(-2, 0, 21, 0, 14, 23),
		Block.createCuboidShape(16, 0, 21, 18, 14, 23),
		Block.createCuboidShape(-3, 14, -8, 19, 16, 24)
	);
	protected static final VoxelShape NS_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(21, 0, 16, 23, 14, 18),
		Block.createCuboidShape(21, 0, -2, 23, 14, 0),
		Block.createCuboidShape(-7, 0, -2, -5, 14, 0),
		Block.createCuboidShape(-7, 0, 16, -5, 14, 18),
		Block.createCuboidShape(-8, 14, -3, 24, 16, 19)
	);
	public TwoLongTableBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
	}

	@Override
	protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
		return CODEC;
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case WEST, EAST -> WE_SHAPE;
            default -> NS_SHAPE;
        };
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		BlockState blockState = this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
		return blockState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}
}
