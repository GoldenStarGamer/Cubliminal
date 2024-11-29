package net.limit.cubliminal.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.limit.cubliminal.init.CubliminalEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MilkBucketItem.class)
public abstract class MilkBucketItemMixin extends Item {
	public MilkBucketItemMixin(Settings settings) {
		super(settings);
	}

	@WrapOperation(method = "finishUsing",
	at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;clearStatusEffects()Z"))
	private boolean onClearStatusEffects(LivingEntity instance, Operation<Boolean> original) {
		if (instance.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(CubliminalEffects.PARANOIA))) {
			StatusEffectInstance effect = instance.getStatusEffect(Registries.STATUS_EFFECT
					.getEntry(CubliminalEffects.PARANOIA));
			instance.clearStatusEffects();
			instance.addStatusEffect(effect);
		} else instance.clearStatusEffects();
		return true;
	}
}
