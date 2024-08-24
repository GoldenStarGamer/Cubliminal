package net.limit.cubliminal.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.limit.cubliminal.init.CubliminalEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.MilkBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MilkBucketItem.class)
public abstract class MilkBucketItemMixin extends Item {
	public MilkBucketItemMixin(Settings settings) {
		super(settings);
	}

	@WrapOperation(method = "finishUsing",
	at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;clearStatusEffects()Z")
	)
	private boolean onClearStatusEffects(LivingEntity instance, Operation<Boolean> original) {
		if (instance.hasStatusEffect(CubliminalEffects.PARANOIA)) {
			int duration = instance.getStatusEffect(CubliminalEffects.PARANOIA).getDuration();
			instance.clearStatusEffects();
			instance.addStatusEffect(new StatusEffectInstance(CubliminalEffects.PARANOIA, duration,
				0, false, false, false));
		} else instance.clearStatusEffects();
		return true;
	}
}
