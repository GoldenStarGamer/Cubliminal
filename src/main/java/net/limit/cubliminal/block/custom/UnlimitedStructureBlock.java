package net.limit.cubliminal.block.custom;

import com.mojang.serialization.MapCodec;
import net.limit.cubliminal.block.entity.USBlockBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class UnlimitedStructureBlock extends BlockWithEntity implements OperatorBlock {
    public static final MapCodec<UnlimitedStructureBlock> CODEC = createCodec(UnlimitedStructureBlock::new);
    public static final EnumProperty<StructureBlockMode> MODE;

    public MapCodec<UnlimitedStructureBlock> getCodec() {
        return CODEC;
    }

    public UnlimitedStructureBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(MODE, StructureBlockMode.LOAD));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new USBlockBlockEntity(pos, state);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof USBlockBlockEntity usBlockBlockEntity) {
            return usBlockBlockEntity.openScreen(player) ? ActionResult.SUCCESS : ActionResult.PASS;
        } else {
            return ActionResult.PASS;
        }
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            if (placer != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof USBlockBlockEntity usBlockBlockEntity) {
                    usBlockBlockEntity.setAuthor(placer);
                }
            }

        }
    }

    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MODE);
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world instanceof ServerWorld) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof USBlockBlockEntity usBlockBlockEntity) {
                boolean bl = world.isReceivingRedstonePower(pos);
                boolean bl2 = usBlockBlockEntity.isPowered();
                if (bl && !bl2) {
                    usBlockBlockEntity.setPowered(true);
                    this.doAction((ServerWorld)world, usBlockBlockEntity);
                } else if (!bl && bl2) {
                    usBlockBlockEntity.setPowered(false);
                }

            }
        }
    }

    private void doAction(ServerWorld world, USBlockBlockEntity blockEntity) {
        switch (blockEntity.getMode()) {
            case SAVE:
                blockEntity.saveStructure(false);
                break;
            case LOAD:
                blockEntity.loadAndPlaceStructure(world);
                break;
            case CORNER:
                blockEntity.unloadStructure();
            case DATA:
        }

    }

    static {
        MODE = Properties.STRUCTURE_BLOCK_MODE;
    }
}
