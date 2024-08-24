package net.limit.cubliminal.entity.custom;

import net.limit.cubliminal.entity.ai.goal.BacteriaAttackGoal;
import net.limit.cubliminal.init.CubliminalSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

import static net.limit.cubliminal.init.CubliminalSounds.clientStopSound;

public class BacteriaEntity extends HostileEntity {
    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(BacteriaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(
		ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.1f, EntityAttributeModifier.Operation.ADDITION
	);
	private static final UUID PLAYER_DAMAGE_BOOST_ID = UUID.fromString("D12717a5-1157-4f2c-9d47-006aa7a31fbf");
	private static final EntityAttributeModifier PLAYER_DAMAGE_BOOST = new EntityAttributeModifier(
		PLAYER_DAMAGE_BOOST_ID, "Player damage boost", 1000f, EntityAttributeModifier.Operation.ADDITION
	);

	private static final Predicate<Difficulty> DOOR_BREAK_DIFFICULTY_CHECKER = difficulty -> difficulty == Difficulty.HARD;
    public boolean canPlaySound = true;
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;
    public BacteriaEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(3f);
		((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
    }

	public boolean canBreakDoors() {
        return true;
	}

	private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 10;
            attackAnimationState.start(this.age);
        } else {
            --this.attackAnimationTimeout;
        }
        if (!this.isAttacking()) {
            attackAnimationState.stop();
        }
    }


    @Override
    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2f);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getWorld().isClient()) {
            setupAnimationStates();
        } else {
			if (!canPlaySound) {
				clientStopSound(this.getCommandSource().getWorld()
						.getEntitiesByClass(ServerPlayerEntity.class, new Box(this.getBlockPos())
							.expand(20), Predicate.not(ServerPlayerEntity::isDisconnected)),
					SoundCategory.HOSTILE, CubliminalSounds.BACTERIA_IDLE.getId());
				if (this.getTarget() == null) {
					canPlaySound = true;
				}
			}
			/*
			for (BlockEntity blockEntity : BlockPos.stream(new Box(this.getBlockPos()).expand(6))
				.map(this.getWorld()::getBlockEntity).filter(Objects::nonNull).filter(blockEntity ->
					blockEntity.getCachedState().equals(CubliminalBlocks.FLUORESCENT_LIGHT
						.getDefaultState())).toList()) {
				this.getWorld().setBlockState(blockEntity.getPos(),
					blockEntity.getCachedState().with(LIT, false));
			}
			 */
		}
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 1000;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }
	@Override
    protected void initGoals() {
		this.goalSelector.add(0, new BreakDoorGoal(this, DOOR_BREAK_DIFFICULTY_CHECKER));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.goalSelector.add(2, new BacteriaAttackGoal(this, 1D, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 40.0f));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.6, 4));
    }

    public static DefaultAttributeContainer.Builder createBacteriaAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,500)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.33f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,13)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_ARMOR, 50);
    }
	@Override
	public void setTarget(@Nullable LivingEntity target) {
		super.setTarget(target);
		EntityAttributeInstance entityAttributeInstance1 = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		EntityAttributeInstance entityAttributeInstance2 = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        assert entityAttributeInstance1 != null;
		assert entityAttributeInstance2 != null;
        if (target == null) {
            entityAttributeInstance1.removeModifier(ATTACKING_SPEED_BOOST.getId());
			entityAttributeInstance2.removeModifier(PLAYER_DAMAGE_BOOST.getId());
		} else {
            if (!entityAttributeInstance1.hasModifier(ATTACKING_SPEED_BOOST)) {
				entityAttributeInstance1.addTemporaryModifier(ATTACKING_SPEED_BOOST);
			}
			if (target.isPlayer()) {
				if (!entityAttributeInstance2.hasModifier(PLAYER_DAMAGE_BOOST)) {
					entityAttributeInstance2.addTemporaryModifier(PLAYER_DAMAGE_BOOST);
				}
				if (canPlaySound) {
                    this.playSound(CubliminalSounds.BACTERIA_CHASE, 1f, 1f);
                    canPlaySound = false;
				}
			}
		}
	}
	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("CanBreakDoors", this.canBreakDoors());
	}

	@Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        if (effect.getEffectType() == StatusEffects.WITHER || effect.getEffectType() == StatusEffects.POISON
		|| effect.getEffectType() == StatusEffects.INSTANT_DAMAGE || effect.getEffectType() == StatusEffects.SLOWNESS
		|| effect.getEffectType() == StatusEffects.WEAKNESS) {
            return false;
        }
        return super.canHaveStatusEffect(effect);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean isImmuneToExplosion(Explosion explosion) {
        return true;
    }
    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 2.7f;
    }

    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CubliminalSounds.BACTERIA_IDLE;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
		 if (this.getTarget() != null && !this.getWorld().isClient()) {
			 this.playSound(CubliminalSounds.BACTERIA_STEP_SOUND.value(), 0.7f, 1f);
		 }
    }
}
