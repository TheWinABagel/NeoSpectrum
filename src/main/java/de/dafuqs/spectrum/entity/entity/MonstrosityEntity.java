package de.dafuqs.spectrum.entity.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.ai.EmptyBodyControl;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.particle.VectorPattern;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import de.dafuqs.spectrum.sound.MonstrositySoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class MonstrosityEntity extends SpectrumBossEntity implements RangedAttackMob {
	
	public static final UUID BONUS_DAMAGE_UUID = UUID.fromString("4425979b-f987-4937-875a-1e26d727c67f");

	public static @Nullable MonstrosityEntity theOneAndOnly = null;

	public static final ResourceLocation KILLED_MONSTROSITY_ADVANCEMENT_IDENTIFIER = SpectrumCommon.locate("lategame/killed_monstrosity");
	public static final Predicate<LivingEntity> ENTITY_TARGETS = (entity) -> {
		if (entity instanceof Player player) {
			if (player.isSpectator() || player.isCreative()) {
				return false;
			}
			return !AdvancementHelper.hasAdvancement(player, KILLED_MONSTROSITY_ADVANCEMENT_IDENTIFIER);
		}
		return false;
	};
	private final TargetingConditions TARGET_PREDICATE = TargetingConditions.forCombat().selector(ENTITY_TARGETS);
	
	private static final float MAX_LIFE_LOST_PER_TICK = 20F;
	private static final int GROW_STRONGER_EVERY_X_TICKS = 400;
	
	private Vec3 targetPosition = Vec3.ZERO;
	private MovementType movementType = MovementType.SWOOPING_TO_POSITION;
	
	private float previousHealth;
	private int timesGottenStronger = 0;
	private int ticksWithoutTarget = 0;

	public MonstrosityEntity(EntityType<? extends MonstrosityEntity> entityType, Level world) {
		super(entityType, world);
		this.moveControl = new MonstrosityMoveControl(this);
		this.xpReward = 500;
		this.noPhysics = true;
		this.noCulling = true;
		this.previousHealth = getHealth();

		if (!world.isClientSide && (MonstrosityEntity.theOneAndOnly == null || MonstrosityEntity.theOneAndOnly.isRemoved() || !MonstrosityEntity.theOneAndOnly.isAlive())) {
			MonstrosityEntity.theOneAndOnly = this;
		} else {
			this.remove(RemovalReason.DISCARDED);
		}
	}

	@Override
	public void spawnAnim() {
		super.spawnAnim();
	}
	
	@Override
	public void onClientRemoval() {
		if (theOneAndOnly == this) {
			theOneAndOnly = null;
		}
		super.onClientRemoval();
	}
	
	@Override
	protected BodyRotationControl createBodyControl() {
		return new EmptyBodyControl(this);
	}
	
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new StartSwoopAttackGoal());
		this.goalSelector.addGoal(2, new SwoopMovementGoal());
		this.goalSelector.addGoal(3, new RetreatAndAttackGoal(40));
		this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.0, 40, 28.0F));
		this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0));
		
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, ENTITY_TARGETS));
		this.targetSelector.addGoal(2, new FindTargetGoal());
	}
	
	@Override
	protected void customServerAiStep() {
		float currentHealth = this.getHealth();
		if (currentHealth < this.previousHealth - MAX_LIFE_LOST_PER_TICK) {
			this.setHealth(this.previousHealth - MAX_LIFE_LOST_PER_TICK);
		}
		this.previousHealth = currentHealth;
		this.tickInvincibility();
		
		if (!this.level().isClientSide() && this.tickCount % GROW_STRONGER_EVERY_X_TICKS == 0) {
			this.growStronger(1);
		}
		
		//destroyBlocks(this.getBoundingBox());
		
		super.customServerAiStep();
		
		if (this.tickCount % 10 == 0) {
			this.heal(1.0F);
		}
	}
	
	@Override
	protected void checkFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
	}
	
	@Override
	public void travel(Vec3 movementInput) {
		if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
			float f = 0.91F;
			float g = 0.16277137F / (f * f * f);
			
			this.moveRelative(this.onGround() ? 0.1F * g : 0.02F, movementInput);
			this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(f));
		}
		
		this.calculateEntityAnimation(false);
	}
	
	@Override
	public boolean onClimbable() {
		return false;
	}
	
	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide()) {
			if (this.tickCount == 0) {
				MonstrositySoundInstance.startSoundInstance(this);
			}
		} else {
			checkDespawn();
		}

		if (this.hasInvincibilityTicks()) {
			for (int j = 0; j < 3; ++j) {
				this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + this.random.nextGaussian(), this.getY() + (double) (this.random.nextFloat() * 3.3F), this.getZ() + this.random.nextGaussian(), 0.7, 0.7, 0.7);
			}
		}
	}
	
	@Override
	public void checkDespawn() {
		super.checkDespawn();
		
		if (hasValidTarget()) {
			ticksWithoutTarget = 0;
		} else {
			this.ticksWithoutTarget++;
			if (ticksWithoutTarget > 600) {
				this.playAmbientSound();
				this.discard();
			}
		}
	}

	public boolean hasValidTarget() {
		LivingEntity target = getTarget();
		return target != null && canAttack(target, TARGET_PREDICATE);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
		if (spawnReason == MobSpawnType.NATURAL && theOneAndOnly != null && theOneAndOnly != this) {
			discard();
		}

		this.targetPosition = position();
		return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
	}
	
	@Override
	protected PathNavigation createNavigation(Level world) {
		FlyingPathNavigation birdNavigation = new FlyingPathNavigation(this, world);
		birdNavigation.setCanOpenDoors(true);
		birdNavigation.setCanFloat(true);
		birdNavigation.setCanPassDoors(true);
		return birdNavigation;
	}
	
	public void growStronger(int amount) {
		this.timesGottenStronger += amount;

		Multimap<Attribute, AttributeModifier> map = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);
		AttributeModifier jeopardantModifier = new AttributeModifier(BONUS_DAMAGE_UUID, "spectrum:monstrosity_bonus", 1.0 + timesGottenStronger * 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL);
		map.put(Attributes.ATTACK_DAMAGE, jeopardantModifier);
		this.getAttributes().addTransientAttributeModifiers(map);

		playSound(SpectrumSoundEvents.ENTITY_MONSTROSITY_GROWL, 1.0F, 1.0F);
		for (float i = 0; i <= 1.0; i += 0.2) {
			SpectrumS2CPacketSender.playParticleWithPatternAndVelocity(null, (ServerLevel) this.level(), new Vec3(getX(), getY(i), getZ()), SpectrumParticleTypes.WHITE_SPARKLE_RISING, VectorPattern.SIXTEEN, 0.05F);
		}
	}
	
	public static AttributeSupplier createMonstrosityAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 600.0)
				.add(Attributes.ATTACK_DAMAGE, 24.0)
				.add(Attributes.FOLLOW_RANGE, 48.0)
				.add(Attributes.ARMOR, 18.0)
				.add(Attributes.ARMOR_TOUGHNESS, 4.0)
				.add(Attributes.ATTACK_KNOCKBACK, 2.0)
				.add(AdditionalEntityAttributes.MAGIC_PROTECTION, 4.0)
				.build();
	}
	
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!this.level().isClientSide() && isNonVanillaKillCommandDamage(source, amount)) {
			// na, we do not feel like dying rn, we ballin
			this.setHealth(this.getHealth() + this.getMaxHealth() / 2);
			this.growStronger(8);
			this.playSound(getHurtSound(source), 2.0F, 1.5F);
			return false;
		}
		return super.hurt(source, amount);
	}

	@Override
	public boolean hasLineOfSight(Entity entity) {
		if (entity.level() != this.level()) {
			return false;
		}
		return entity.position().distanceTo(this.position()) < 128;
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}
	
	@Override
	protected Component getTypeName() {
		return Component.literal("§kLivingNightmare");
	}
	
	@Override
	public void performRangedAttack(LivingEntity target, float pullProgress) {
		var world = target.level();
		if (world.random.nextBoolean()) {
			LightShardBaseEntity.summonBarrageInternal(world, this, () -> {
				LightSpearEntity entity = new LightSpearEntity(world, MonstrosityEntity.this, Optional.of(target), 6.0F, 800);
				entity.setTargetPredicate(ENTITY_TARGETS);
				return entity;
			}, this.getEyePosition(), UniformInt.of(5, 7));
		} else {
			LightShardBaseEntity.summonBarrageInternal(world, this, () -> {
				LightMineEntity entity = new LightMineEntity(world, MonstrosityEntity.this, Optional.empty(), 4, 8.0F, 800);
				entity.setEffects(List.of(getRandomMineStatusEffect(random)));
				entity.setTargetPredicate(ENTITY_TARGETS);
				return entity;
			}, this.getEyePosition(), UniformInt.of(7, 11));
		}

		this.playSound(SpectrumSoundEvents.ENTITY_MONSTROSITY_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
	}

	protected MobEffectInstance getRandomMineStatusEffect(net.minecraft.util.RandomSource random) {
		int i = random.nextInt();
		switch (i) {
			case 0 -> {
				return new MobEffectInstance(SpectrumStatusEffects.SCARRED, 200, 0);
			}
			case 1 -> {
				return new MobEffectInstance(SpectrumStatusEffects.STIFFNESS, 200, 1);
			}
			case 2 -> {
				return new MobEffectInstance(SpectrumStatusEffects.DENSITY, 200, 2);
			}
			case 3 -> {
				return new MobEffectInstance(SpectrumStatusEffects.VULNERABILITY, 200, 1);
			}
			default -> {
				return new MobEffectInstance(SpectrumStatusEffects.LIFE_DRAIN, 200, 0);
			}
		}
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);

		nbt.putFloat("previous_health", this.previousHealth);
		nbt.putInt("times_gotten_stronger", this.timesGottenStronger);
		nbt.putInt("ticks_without_target", this.ticksWithoutTarget);
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);

		if (nbt.contains("previous_health", Tag.TAG_FLOAT)) {
			this.previousHealth = nbt.getFloat("previous_health");
		}
		if (nbt.contains("times_gotten_stronger", Tag.TAG_ANY_NUMERIC)) {
			this.timesGottenStronger = nbt.getInt("times_gotten_stronger");
		}
		if (nbt.contains("ticks_without_target", Tag.TAG_ANY_NUMERIC)) {
			this.ticksWithoutTarget = nbt.getInt("ticks_without_target");
		}
	}
	
	private enum MovementType {
		SWOOPING_TO_POSITION, // position based movement
		START_SWOOPING, // swoop to player and try hitting them
		RETREATING // pissing off far, far away
	}
	
	private class MonstrosityMoveControl extends MoveControl {
		
		private float targetSpeed = 0.1F;
		
		public MonstrosityMoveControl(Mob owner) {
			super(owner);
		}
		
		@Override
		public void tick() {
			if (MonstrosityEntity.this.horizontalCollision) {
				MonstrosityEntity.this.setYRot(MonstrosityEntity.this.getYRot() + 180.0F);
				this.targetSpeed = 0.1F;
			}
			
			double d = MonstrosityEntity.this.targetPosition.x - MonstrosityEntity.this.getX();
			double e = MonstrosityEntity.this.targetPosition.y - MonstrosityEntity.this.getY();
			double f = MonstrosityEntity.this.targetPosition.z - MonstrosityEntity.this.getZ();
			double g = Math.sqrt(d * d + f * f);
			if (Math.abs(g) > 10.0E-6) {
				double h = 1.0 - Math.abs(e * 0.7) / g;
				d *= h;
				f *= h;
				g = Math.sqrt(d * d + f * f);
				double i = Math.sqrt(d * d + f * f + e * e);
				float j = MonstrosityEntity.this.getYRot();
				float k = (float) Mth.atan2(f, d);
				float l = Mth.wrapDegrees(MonstrosityEntity.this.getYRot() + 90.0F);
				float m = Mth.wrapDegrees(k * 57.295776F);
				MonstrosityEntity.this.setYRot(Mth.approachDegrees(l, m, 4.0F) - 90.0F);
				MonstrosityEntity.this.yBodyRot = MonstrosityEntity.this.getYRot();
				if (Mth.degreesDifferenceAbs(j, MonstrosityEntity.this.getYRot()) < 3.0F) {
					this.targetSpeed = Mth.approach(this.targetSpeed, 1.8F, 0.005F * (1.8F / this.targetSpeed));
				} else {
					this.targetSpeed = Mth.approach(this.targetSpeed, 0.2F, 0.025F);
				}
				
				float n = (float) (-(Mth.atan2(-e, g) * 57.2957763671875));
				MonstrosityEntity.this.setXRot(n);
				float o = MonstrosityEntity.this.getYRot() + 90.0F;
				double p = (double) (this.targetSpeed * Mth.cos(o * 0.017453292F)) * Math.abs(d / i);
				double q = (double) (this.targetSpeed * Mth.sin(o * 0.017453292F)) * Math.abs(f / i);
				double r = (double) (this.targetSpeed * Mth.sin(n * 0.017453292F)) * Math.abs(e / i);
				Vec3 vec3d = MonstrosityEntity.this.getDeltaMovement();
				MonstrosityEntity.this.setDeltaMovement(vec3d.add((new Vec3(p, r, q)).subtract(vec3d).scale(0.2)));
			}
		}
	}
	
	private class StartSwoopAttackGoal extends Goal {
		private int cooldown;
		
		@Override
		public boolean canUse() {
			LivingEntity target = MonstrosityEntity.this.getTarget();
			return target != null && MonstrosityEntity.this.canAttack(target, TARGET_PREDICATE);
		}
		
		@Override
		public void start() {
			this.cooldown = this.adjustedTickDelay(10);
			MonstrosityEntity.this.movementType = MovementType.SWOOPING_TO_POSITION;
			this.aimAtTarget();
		}
		
		@Override
		public void tick() {
			if (MonstrosityEntity.this.movementType == MovementType.SWOOPING_TO_POSITION) {
				--this.cooldown;
				if (this.cooldown <= 0) {
					MonstrosityEntity.this.movementType = MovementType.START_SWOOPING;
					this.aimAtTarget();
					this.cooldown = this.adjustedTickDelay((8 + MonstrosityEntity.this.random.nextInt(4)) * 20);
					MonstrosityEntity.this.playSound(SpectrumSoundEvents.ENTITY_MONSTROSITY_SWOOP, 10.0F, 0.95F + MonstrosityEntity.this.random.nextFloat() * 0.1F);
				}
			}
		}
		
		private void aimAtTarget() {
			MonstrosityEntity.this.targetPosition = MonstrosityEntity.this.getTarget().position();
		}
	}
	
	private class SwoopMovementGoal extends Goal {
		
		SwoopMovementGoal() {
			super();
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}
		
		@Override
		public boolean canUse() {
			return MonstrosityEntity.this.getTarget() != null && MonstrosityEntity.this.movementType == MovementType.START_SWOOPING;
		}
		
		@Override
		public boolean canContinueToUse() {
			LivingEntity livingEntity = MonstrosityEntity.this.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isAlive()) {
				return false;
			} else {
				if (livingEntity instanceof Player playerEntity) {
					if (livingEntity.isSpectator() || playerEntity.isCreative()) {
						return false;
					}
				}
				return this.canUse();
			}
		}
		
		@Override
		public void stop() {
			MonstrosityEntity.this.movementType = MovementType.SWOOPING_TO_POSITION;
		}
		
		@Override
		public void tick() {
			LivingEntity livingEntity = MonstrosityEntity.this.getTarget();
			if (livingEntity != null) {
				MonstrosityEntity.this.targetPosition = new Vec3(livingEntity.getX(), livingEntity.getY(0.5), livingEntity.getZ());
				if (MonstrosityEntity.this.getBoundingBox().inflate(0.2).intersects(livingEntity.getBoundingBox())) {
					// the monstrosity hit the entity
					MonstrosityEntity.this.doHurtTarget(livingEntity);
					MonstrosityEntity.this.movementType = MovementType.SWOOPING_TO_POSITION;
					if (!MonstrosityEntity.this.isSilent()) {
						MonstrosityEntity.this.level().levelEvent(LevelEvent.SOUND_PHANTOM_BITE, MonstrosityEntity.this.blockPosition(), 0);
					}
				} else if (MonstrosityEntity.this.horizontalCollision || MonstrosityEntity.this.hurtTime > 0) {
					// the player hit monstrosity
					MonstrosityEntity.this.movementType = MovementType.SWOOPING_TO_POSITION;
				}
			}
		}
	}
	
	private class FindTargetGoal extends Goal {
		
		private int delay = reducedTickDelay(20);
		
		FindTargetGoal() {
		}
		
		@Override
		public boolean canUse() {
			if (this.delay > 0) {
				--this.delay;
				return false;
			}
			
			this.delay = reducedTickDelay(60);
			Player newTarget = MonstrosityEntity.this.level().getNearestPlayer(TARGET_PREDICATE, MonstrosityEntity.this);
			if (newTarget == null) {
				return false;
			}

			MonstrosityEntity.this.setTarget(newTarget);
			return true;
		}
		
		@Override
		public boolean canContinueToUse() {
			LivingEntity target = MonstrosityEntity.this.getTarget();
			return target != null && MonstrosityEntity.this.canAttack(target, TARGET_PREDICATE);
		}
	}
	
	private class RetreatAndAttackGoal extends Goal {

		protected final float retreatDistance;
		
		RetreatAndAttackGoal(float retreatDistance) {
			super();
			this.retreatDistance = retreatDistance;
		}
		
		@Override
		public boolean canUse() {
			return MonstrosityEntity.this.movementType == MovementType.START_SWOOPING
					&& MonstrosityEntity.this.getTarget() != null
					&& MonstrosityEntity.this.level().random.nextBoolean() && MonstrosityEntity.this.distanceTo(MonstrosityEntity.this.getTarget()) < retreatDistance - 4;
		}
		
		@Override
		public boolean canContinueToUse() {
			return MonstrosityEntity.this.getTarget() != null
					&& MonstrosityEntity.this.canAttack(MonstrosityEntity.this.getTarget(), TARGET_PREDICATE)
					&& MonstrosityEntity.this.distanceTo(MonstrosityEntity.this.getTarget()) < retreatDistance;
		}
		
		@Override
		public void start() {
			super.start();
			Vec3 differenceToTarget = MonstrosityEntity.this.position().subtract(MonstrosityEntity.this.getTarget().position());
			Vec3 multipliedDifference = differenceToTarget.multiply(1, 0, 1).normalize().scale(retreatDistance);
			MonstrosityEntity.this.targetPosition = MonstrosityEntity.this.position().add(multipliedDifference);
			MonstrosityEntity.this.movementType = MovementType.RETREATING;
		}
		
		@Override
		public void stop() {
			LivingEntity target = MonstrosityEntity.this.getTarget();
			if (target != null && MonstrosityEntity.this.canAttack(target, TARGET_PREDICATE)) {
				LightShardEntity.summonBarrage(MonstrosityEntity.this.level(), MonstrosityEntity.this, target);
			}
			MonstrosityEntity.this.movementType = MovementType.START_SWOOPING;
			super.stop();
		}
		
	}
	
}
