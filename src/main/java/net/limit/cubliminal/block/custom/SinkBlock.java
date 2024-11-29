package net.limit.cubliminal.block.custom;

import com.mojang.serialization.MapCodec;
import net.limit.cubliminal.block.entity.SinkBlockEntity;
import net.limit.cubliminal.init.CubliminalBlockEntities;
import net.limit.cubliminal.init.CubliminalSounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SinkBlock extends BlockWithEntity implements BlockEntityProvider {
	public static final MapCodec<SinkBlock> CODEC = SinkBlock.createCodec(SinkBlock::new);
	private static final BooleanProperty ENABLED = Properties.ENABLED;
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	protected static final VoxelShape EAST_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(0, 10, 0, 14, 11, 16),
		Block.createCuboidShape(13, 11, 0, 14, 16, 16),
		Block.createCuboidShape(0, 11, 0, 3, 16, 16),
		Block.createCuboidShape(3, 11, 0, 13, 16, 1),
		Block.createCuboidShape(3, 11, 15, 13, 16, 16),
		Block.createCuboidShape(0, 16, 7, 2, 19, 9),
		Block.createCuboidShape(7, 7, 7, 9, 9, 9),
		Block.createCuboidShape(6, 9, 6, 10, 10, 10),
		Block.createCuboidShape(0, 4, 6, 2, 8, 10),
		Block.createCuboidShape(2, 5, 7, 9, 7, 9),
		Block.createCuboidShape(0, 19, 7, 6, 20, 9));
	protected static final VoxelShape WEST_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(2, 10, 0, 16, 11, 16),
		Block.createCuboidShape(2, 11, 0, 3, 16, 16),
		Block.createCuboidShape(13, 11, 0, 16, 16, 16),
		Block.createCuboidShape(3, 11, 15, 13, 16, 16),
		Block.createCuboidShape(3, 11, 0, 13, 16, 1),
		Block.createCuboidShape(14, 16, 7, 16, 19, 9),
		Block.createCuboidShape(7, 7, 7, 9, 9, 9),
		Block.createCuboidShape(6, 9, 6, 10, 10, 10),
		Block.createCuboidShape(14, 4, 6, 16, 8, 10),
		Block.createCuboidShape(7, 5, 7, 14, 7, 9),
		Block.createCuboidShape(10, 19, 7, 16, 20, 9));
	protected static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(0, 10, 2, 16, 11, 16),
		Block.createCuboidShape(0, 11, 2, 16, 16, 3),
		Block.createCuboidShape(0, 11, 13, 16, 16, 16),
		Block.createCuboidShape(0, 11, 3, 1, 16, 13),
		Block.createCuboidShape(15, 11, 3, 16, 16, 13),
		Block.createCuboidShape(7, 16, 14, 9, 19, 16),
		Block.createCuboidShape(7, 7, 7, 9, 9, 9),
		Block.createCuboidShape(6, 9, 6, 10, 10, 10),
		Block.createCuboidShape(6, 4, 14, 10, 8, 16),
		Block.createCuboidShape(7, 5, 7, 9, 7, 14),
		Block.createCuboidShape(7, 19, 10, 9, 20, 16));
	protected static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(0, 10, 0, 16, 11, 14),
		Block.createCuboidShape(0, 11, 13, 16, 16, 14),
		Block.createCuboidShape(0, 11, 0, 16, 16, 3),
		Block.createCuboidShape(15, 11, 3, 16, 16, 13),
		Block.createCuboidShape(0, 11, 3, 1, 16, 13),
		Block.createCuboidShape(7, 16, 0, 9, 19, 2),
		Block.createCuboidShape(7, 7, 7, 9, 9, 9),
		Block.createCuboidShape(6, 9, 6, 10, 10, 10),
		Block.createCuboidShape(6, 4, 0, 10, 8, 2),
		Block.createCuboidShape(7, 5, 2, 9, 7, 9),
		Block.createCuboidShape(7, 19, 0, 9, 20, 6));

	public SinkBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(ENABLED, false));
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return CODEC;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(10) == 0) {
			switch (state.get(FACING)) {
				case NORTH:
					world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.68, 0, 0, 0);
					break;
				case SOUTH:
					world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.32, 0, 0, 0);
					break;
				case WEST:
					world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.68, pos.getY() + 1.05, pos.getZ() + 0.5, 0, 0, 0);
					break;
				case EAST:
					world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.32, pos.getY() + 1.05, pos.getZ() + 0.5, 0, 0, 0);
					break;
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(ENABLED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return switch (state.get(FACING)) {
            case SOUTH -> SOUTH_SHAPE;
			case WEST -> WEST_SHAPE;
			case EAST -> EAST_SHAPE;
            default -> NORTH_SHAPE;
        };
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
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : state;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = this.getDefaultState();
		Direction[] directions = ctx.getPlacementDirections();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				blockState = blockState.with(FACING, direction.getOpposite());
				return blockState;
			}
		}

		return null;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		boolean bl = !state.get(Properties.ENABLED);
		world.setBlockState(pos, state.with(ENABLED, bl));
		CubliminalSounds.blockPlaySound(world, pos, CubliminalSounds.OPEN_SINK.value());
		return ActionResult.success(world.isClient);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker(type, CubliminalBlockEntities.SINK_BLOCK_ENTITY, SinkBlockEntity::tick);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SinkBlockEntity(pos, state);
	}
}
