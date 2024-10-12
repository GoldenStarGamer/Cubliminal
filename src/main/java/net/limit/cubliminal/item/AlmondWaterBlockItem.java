package net.limit.cubliminal.item;

import net.limit.cubliminal.block.custom.AlmondWaterBlock;
import net.limit.cubliminal.init.CubliminalEffects;
import net.limit.cubliminal.util.SanityData;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public class AlmondWaterBlockItem extends BlockItem {

    public AlmondWaterBlockItem(Block block, Settings settings) {
        super(block, settings);
        Validate.isInstanceOf(AlmondWaterBlock.class, block);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!world.isClient) {
                SanityData.resetTimer(serverPlayerEntity);
                user.removeStatusEffect(CubliminalEffects.PARANOIA);
            }
        }
        /*
        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        if (user instanceof PlayerEntity playerEntity && !((PlayerEntity) user).getAbilities().creativeMode) {
            ItemStack itemStack = new ItemStack(Items.GLASS_BOTTLE);
            if (!playerEntity.getInventory().insertStack(itemStack)) {
                playerEntity.dropItem(itemStack, false);
            }
        }
         */
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 40;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public SoundEvent getDrinkSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
