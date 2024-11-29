package net.limit.cubliminal.block.custom;

import com.mojang.serialization.MapCodec;
import net.limit.cubliminal.block.entity.ShowerBlockEntity;
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
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class ShowerBlock extends BlockWithEntity implements BlockEntityProvider {
	public static final MapCodec<ShowerBlock> CODEC = ShowerBlock.createCodec(ShowerBlock::new);
	private static final BooleanProperty ENABLED = Properties.ENABLED;
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	protected static final VoxelShape EAST_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(1.6, 0, 3, 3.6, 3, 6),
		Block.createCuboidShape(0, 0, 7, 2, 31, 9),
		Block.createCuboidShape(4, 29, 6, 8, 31, 10),
		Block.createCuboidShape(3, 28, 5, 9, 29, 11),
		Block.createCuboidShape(2, 29, 7.1, 4, 31, 8.9),
		Block.createCuboidShape(0.6, 1, 4, 1.6, 2, 12),
		Block.createCuboidShape(1.6, 0, 10, 3.6, 3, 13));
	protected static final VoxelShape WEST_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(12.4, 0, 10, 14.4, 3, 13),
		Block.createCuboidShape(14, 0, 7, 16, 31, 9),
		Block.createCuboidShape(8, 29, 6, 12, 31, 10),
		Block.createCuboidShape(7, 28, 5, 13, 29, 11),
		Block.createCuboidShape(12, 29, 7.1, 14, 31, 8.9),
		Block.createCuboidShape(14.4, 1, 4, 15.4, 2, 12),
		Block.createCuboidShape(12.4, 0, 3, 14.4, 3, 6));
	protected static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(3, 0, 12.4, 6, 3, 14.4),
		Block.createCuboidShape(7, 0, 14, 9, 31, 16),
		Block.createCuboidShape(6, 29, 8, 10, 31, 12),
		Block.createCuboidShape(5, 28, 7, 11, 29, 13),
		Block.createCuboidShape(7.1, 29, 12, 8.9, 31, 14),
		Block.createCuboidShape(4, 1, 14.4, 12, 2, 15.4),
		Block.createCuboidShape(10, 0, 12.4, 13, 3, 14.4));
	protected static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(10, 0, 1.6, 13, 3, 3.6),
		Block.createCuboidShape(7, 0, 0, 9, 31, 2),
		Block.createCuboidShape(6, 29, 4, 10, 31, 8),
		Block.createCuboidShape(5, 28, 3, 11, 29, 9),
		Block.createCuboidShape(7.1, 29, 2, 8.9, 31, 4),
		Block.createCuboidShape(4, 1, 0.6, 12, 2, 1.6),
		Block.createCuboidShape(3, 0, 1.6, 6, 3, 3.6));

	public ShowerBlock(Settings settings) {
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
			float offsetX = 0;
			float offsetZ = switch (state.get(ShowerBlock.FACING)) {
                case NORTH -> {
                    offsetX = 0.375f;
                    yield 0.5f;
                }
                case SOUTH -> {
                    offsetX = 0.375f;
                    yield 0.25f;
                }
                case WEST -> {
                    offsetX = 0.5f;
                    yield 0.375f;
                }
                case EAST -> {
                    offsetX = 0.25f;
                    yield 0.375f;
                }
                default -> 0;
            };
            world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.125f + offsetX,
				pos.getY() + 1.65, pos.getZ() + 0.125f + offsetZ, 0, 0, 0);
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
		WorldView worldView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
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
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
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
		return validateTicker(type, CubliminalBlockEntities.SHOWER_BLOCK_ENTITY, ShowerBlockEntity::tick);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ShowerBlockEntity(pos, state);
	}
}
