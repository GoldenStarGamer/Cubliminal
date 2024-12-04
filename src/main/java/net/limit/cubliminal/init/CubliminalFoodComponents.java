package net.limit.cubliminal.init;

import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.RemoveEffectsConsumeEffect;
import net.minecraft.sound.SoundEvents;

public class CubliminalFoodComponents {
    public static final FoodComponent ALMOND_WATER = new FoodComponent.Builder().alwaysEdible().nutrition(11).saturationModifier(1.0f).build();

    public static final ConsumableComponent ALMOND_WATER_COMPONENT = ConsumableComponents.drink().consumeSeconds(2.0F)
            .sound(SoundEvents.ITEM_HONEY_BOTTLE_DRINK).consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 60, 0), 0.33f)).build();
}
