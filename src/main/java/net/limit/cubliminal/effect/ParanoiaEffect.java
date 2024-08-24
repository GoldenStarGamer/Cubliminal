package net.limit.cubliminal.effect;

import net.limit.cubliminal.init.CubliminalSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

import static net.limit.cubliminal.init.CubliminalSounds.clientPlaySoundSingle;

public class ParanoiaEffect extends StatusEffect {

    public ParanoiaEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		if (entity.isPlayer() && !entity.getWorld().isClient && !entity.isSpectator()) {
			while (entity.getRandom().nextInt(140) == 1) {
				if (!entity.hasStatusEffect(StatusEffects.DARKNESS)) {
					clientPlaySoundSingle((ServerPlayerEntity) entity, CubliminalSounds.HEARTBEAT,
						SoundCategory.PLAYERS, entity.getX(), entity.getY(),
						entity.getZ(), 1f, 1f, 1);
				}

				entity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60,
					0, false, false, false));
			}
			while (entity.getRandom().nextInt(800) == 1) {
				if (!((ServerPlayerEntity) entity).isCreative()) {
					entity.damage(entity.getDamageSources().genericKill(), 1);
				}
			}
		}
        super.applyUpdateEffect(entity, amplifier);
    }

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

}
