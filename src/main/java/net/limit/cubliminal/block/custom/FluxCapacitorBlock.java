package net.limit.cubliminal.block.custom;

import com.mojang.serialization.MapCodec;
import net.limit.cubliminal.block.entity.FluxCapacitorBlockEntity;
import net.limit.cubliminal.init.CubliminalBlockEntities;
import net.limit.cubliminal.init.CubliminalSounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class FluxCapacitorBlock extends BlockWithEntity implements BlockEntityProvider {
	public static final MapCodec<FluxCapacitorBlock> CODEC = FluxCapacitorBlock.createCodec(FluxCapacitorBlock::new);
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
	public FluxCapacitorBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false).with(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return CODEC;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new FluxCapacitorBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker(type, CubliminalBlockEntities.FLUX_CAPACITOR_BLOCK_ENTITY, FluxCapacitorBlockEntity::tick);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWERED, FACING);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
				.with(POWERED, shouldBePowered(ctx.getWorld(), ctx.getBlockPos()));
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
		if (shouldBePowered(world, pos)) {
			world.setBlockState(pos, state.with(POWERED, true));
		}
	}

	protected boolean shouldBePowered(World world, BlockPos pos) {
		return world.isReceivingRedstonePower(pos) && world.getBlockState(pos.up())
				.equals(Blocks.LIGHTNING_ROD.getDefaultState().with(LightningRodBlock.POWERED, true));
	}

	@Override
	public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
		if (!world.isClient()) {
			CubliminalSounds.clientStopSound(world.getServer().getOverworld().getPlayers(),
					SoundCategory.BLOCKS, CubliminalSounds.FLUX_CAPACITOR.value().id());
		}
	}
}
