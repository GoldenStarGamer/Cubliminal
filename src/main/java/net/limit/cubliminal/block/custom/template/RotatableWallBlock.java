package net.limit.cubliminal.block.custom.template;

import com.mojang.serialization.MapCodec;
import net.limit.cubliminal.Cubliminal;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import org.apache.commons.io.function.IOQuadFunction;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class RotatableWallBlock extends HorizontalFacingBlock implements Waterloggable {
    public static MapCodec<RotatableWallBlock> CODEC = RotatableWallBlock.createCodec(RotatableWallBlock::new);
    private IOQuadFunction<BlockState, ServerWorld, BlockPos, Random, Void> randomTick = (state, world, pos, random) -> null;
    private IOQuadFunction<BlockState, World, BlockPos, Random, Void> randomDisplayTick = (state, world, pos, random) -> null;
    private IOQuadFunction<BlockState, ServerWorld, BlockPos, Random, Void> scheduledTick = (state, world, pos, random) -> null;
    private boolean solid = true;
    private boolean collidable = true;
    private boolean needsAttachment = true;
    private VoxelShape WEST_SHAPE = VoxelShapes.fullCube();
    private VoxelShape EAST_SHAPE = VoxelShapes.fullCube();
    private VoxelShape SOUTH_SHAPE = VoxelShapes.fullCube();
    private VoxelShape NORTH_SHAPE = VoxelShapes.fullCube();
    private static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    public RotatableWallBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    public RotatableWallBlock onRandomTick(IOQuadFunction<BlockState, ServerWorld, BlockPos, Random, Void> randomTick) {
        this.randomTick = randomTick;
        return this;
    }

    public RotatableWallBlock onRandomDisplayTick(IOQuadFunction<BlockState, World, BlockPos, Random, Void> randomDisplayTick) {
        this.randomDisplayTick = randomDisplayTick;
        return this;
    }

    public RotatableWallBlock onScheduledTick(IOQuadFunction<BlockState, ServerWorld, BlockPos, Random, Void> scheduledTick) {
        this.scheduledTick = scheduledTick;
        return this;
    }

    public RotatableWallBlock setSolidBoundingBox(boolean solid) {
        this.solid = solid;
        return this;
    }

    public RotatableWallBlock setCollidable(boolean collidable) {
        this.collidable = collidable;
        return this;
    }

    public RotatableWallBlock needsAttachment(boolean needs) {
        this.needsAttachment = needs;
        return this;
    }

    public RotatableWallBlock voxelShapes(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.WEST_SHAPE = Block.createCuboidShape(16 - maxZ, minY, minX, 16 - minZ, maxY, maxX);
        this.EAST_SHAPE = Block.createCuboidShape(minZ, minY, 16 - maxX, maxZ, maxY, 16 - minX);
        this.SOUTH_SHAPE = Block.createCuboidShape(minX, minY, minZ, maxX, maxY, maxZ);
        this.NORTH_SHAPE = Block.createCuboidShape(16 - maxX, minY, 16 - maxZ, 16 - minX, maxY, 16 - minZ);
        return this;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (this.solid) {
            return switch (state.get(FACING)) {
                case SOUTH -> SOUTH_SHAPE;
                case WEST -> WEST_SHAPE;
                case EAST -> EAST_SHAPE;
                default -> NORTH_SHAPE;
            };
        } else return VoxelShapes.empty();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (this.collidable) {
            return switch (state.get(FACING)) {
                case SOUTH -> SOUTH_SHAPE;
                case WEST -> WEST_SHAPE;
                case EAST -> EAST_SHAPE;
                default -> NORTH_SHAPE;
            };
        } else return VoxelShapes.empty();
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (!this.needsAttachment) {
            return true;
        } else {
            Direction direction = state.get(FACING);
            BlockPos blockPos = pos.offset(direction.getOpposite());
            BlockState blockState = world.getBlockState(blockPos);
            return blockState.isSideSolidFullSquare(world, blockPos, direction);
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = this.getDefaultState();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        WorldView worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction[] directions = ctx.getPlacementDirections();

        for (Direction direction : directions) {
            if (direction.getAxis().isHorizontal()) {
                blockState = blockState.with(FACING, direction.getOpposite());
                if (blockState.canPlaceAt(worldView, blockPos)) {
                    return blockState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
                }
            }
        }

        return null;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, false);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        try {
            this.randomTick.apply(state, world, pos, random);
        } catch (IOException e) {
            Cubliminal.LOGGER.error("Couldn't perform random tick at pos: {}", pos);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        try {
            this.randomDisplayTick.apply(state, world, pos, random);
        } catch (IOException e) {
            Cubliminal.LOGGER.error("Couldn't perform random display tick at pos: {}", pos);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        try {
            this.scheduledTick.apply(state, world, pos, random);
        } catch (IOException e) {
            Cubliminal.LOGGER.error("Couldn't perform scheduled tick at pos: {}", pos);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }
}
