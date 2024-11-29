package net.limit.cubliminal.effect;

import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.init.CubliminalSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;


public class ParanoiaEffect extends StatusEffect {

    public ParanoiaEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }


    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		if (entity instanceof PlayerEntity player && !player.isSpectator()) {
			if (!player.getWorld().isClient) {
				while (player.getRandom().nextInt(140) == 1) {
					//if (!player.hasStatusEffect(StatusEffects.DARKNESS)) {
						CubliminalSounds.clientPlaySoundSingle((ServerPlayerEntity) player, CubliminalSounds.HEARTBEAT,
								SoundCategory.PLAYERS, player.getX(), player.getY(),
								player.getZ(), 1f, 1f, 1);
					//}
					//player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60,
					//		0, false, false, false));

				}
				while (player.getRandom().nextInt(800) == 1 && !player.isCreative() && !player.isSpectator()) {
					DamageSource damageSource = new DamageSource(player.getWorld().getRegistryManager()
							.get(RegistryKeys.DAMAGE_TYPE).entryOf(Cubliminal.MENTAL_COLLAPSE));
					player.damage(damageSource, 1.5f);
				}
			}
		}
		return super.applyUpdateEffect(entity, amplifier);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

}
