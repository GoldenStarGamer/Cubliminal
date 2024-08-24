package net.limit.cubliminal.init;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class CubliminalFoodComponents {
    public static final FoodComponent ALMOND_WATER = new FoodComponent.Builder().alwaysEdible().hunger(20).saturationModifier(1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 60, 0), 0.33f).build();
}
