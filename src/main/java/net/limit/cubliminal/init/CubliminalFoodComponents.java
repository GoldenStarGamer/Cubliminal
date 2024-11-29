package net.limit.cubliminal.init;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class CubliminalFoodComponents {
    public static final FoodComponent ALMOND_WATER = new FoodComponent.Builder().alwaysEdible().nutrition(11).saturationModifier(1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 60, 0), 0.33f).build();
}
