package net.limit.cubliminal.init;

import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.effect.ParanoiaEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CubliminalEffects {
    public static StatusEffect PARANOIA;
    public static StatusEffect registerStatusEffect(String id) {
        return Registry.register(Registries.STATUS_EFFECT, Cubliminal.id(id),
                new ParanoiaEffect(StatusEffectCategory.HARMFUL, 24828));
    }

    public static void init() {
        PARANOIA = registerStatusEffect("paranoia");
    }
}
