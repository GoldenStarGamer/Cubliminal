package net.limit.cubliminal.entity.ai.goal;

import net.limit.cubliminal.entity.custom.BacteriaEntity;
import net.limit.cubliminal.init.CubliminalSounds;
import net.limit.cubliminal.util.ParalyzingEntries;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

import static net.limit.cubliminal.init.CubliminalSounds.clientStopSound;


public class BacteriaAttackGoal extends MeleeAttackGoal {
    private final BacteriaEntity entity;
    private int attackDelay = 5;
    private int ticksUntilNextAttack = 10;
    private boolean shouldCountTillNextAttack = false;
	private int ticksUntilKill = 40;
    public BacteriaAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        entity = ((BacteriaEntity) mob);
    }

    @Override
    public void start() {
        super.start();
        attackDelay = 5;
        ticksUntilNextAttack = 10;
    }


    @Override
    protected void attack(LivingEntity pEnemy) {
        if (isEnemyWithinAttackDistance(pEnemy)) {
            shouldCountTillNextAttack = true;

            if (isTimeToStartAttackAnimation() && !pEnemy.isPlayer()) {
				entity.setAttacking(true);
			}

            if (isTimeToAttack()) {
                this.mob.getLookControl().lookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
				if (pEnemy.isPlayer()) {
					clientStopSound(pEnemy.getCommandSource().getWorld()
							.getEntitiesByClass(ServerPlayerEntity.class, new Box(entity.getBlockPos())
								.expand(30), Predicate.not(ServerPlayerEntity::isDisconnected)),
						SoundCategory.HOSTILE, CubliminalSounds.BACTERIA_CHASE.getId());

					pEnemy.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES,
						new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ()));

					if (ticksUntilKill == 35) {
                        if (!entity.getWorld().isClient()) {
                            entity.playSound(CubliminalSounds.BACTERIA_KILL.value(), 1f, 1f);
                        }
					} else if (ticksUntilKill == 5) {
						entity.setAttacking(true);
					} else if (ticksUntilKill == 0) {
						performAttack(pEnemy);
					}
                    if (ParalyzingEntries.PARALYZING_ENTRIES.isEmpty()) {
                        ParalyzingEntries.PARALYZING_ENTRIES.add(entity);
                        entity.setInvulnerable(true);
                    }
				} else {
					performAttack(pEnemy);
				}
            }
        } else {
            resetAttackCooldown();
            shouldCountTillNextAttack = false;
            entity.setAttacking(false);
            entity.attackAnimationTimeout = 0;
        }
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy) {
        return this.entity.distanceTo(pEnemy) <= 2.5f; // TODO
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.getTickCount(attackDelay * 2);
    }

    protected boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;

    }

    protected void performAttack(LivingEntity pEnemy) {
        this.resetAttackCooldown();
        this.mob.swingHand(Hand.MAIN_HAND);
        this.mob.tryAttack(pEnemy);
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldCountTillNextAttack) {
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }
		if (entity.isInvulnerable() && ticksUntilKill > 0) {
			--ticksUntilKill;
		} else if (ticksUntilKill == 0 && !entity.isInvulnerable()) {
			ticksUntilKill = 40;
		}
    }

	@Override
    public void stop() {
        entity.setAttacking(false);
		entity.setInvulnerable(false);
        ParalyzingEntries.PARALYZING_ENTRIES.remove(entity);
        super.stop();
    }
}
