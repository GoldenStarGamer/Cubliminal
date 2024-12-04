package net.limit.cubliminal.block.custom;

import net.limit.cubliminal.block.CustomProperties;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class FusedFluorescentLightBlock extends Block {

	private static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty RED = CustomProperties.RED;
	public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
	private static final VoxelShape VOXEL_SHAPE = Block.createCuboidShape(0, 15,0, 16, 16, 16);

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VOXEL_SHAPE;
	}

	public FusedFluorescentLightBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(FACING, Direction.NORTH).with(RED, false));
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.offset(Direction.DOWN.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.isSideSolidFullSquare(world, blockPos, Direction.DOWN);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.setBlockState(pos, state.with(LIT, false));
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
		if (!state.canPlaceAt(world, pos)) {
			world.breakBlock(pos, false);
		}
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!world.isClient) {
			if (random.nextInt(2) == 0) {
				world.setBlockState(pos, state.with(LIT, true));
				world.scheduleBlockTick(pos, state.getBlock(), 2);
			}
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = this.getDefaultState();
		Direction direction = ctx.getSide();
		boolean needsToBeRed = ctx.getWorld().getBiome(ctx.getBlockPos()).getKey()
				.get().equals(CubliminalBiomes.REDROOMS_BIOME);
		if (!ctx.canReplaceExisting() && direction.getAxis().isHorizontal()) {
			blockState = blockState.with(FACING, direction);
		} else {
			blockState = blockState.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
		}

		return blockState.with(RED, needsToBeRed);
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
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT, FACING, RED);
	}

}
