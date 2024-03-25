package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.mixin.accessors.ProjectileAttackGoalAccessor;
import de.dafuqs.spectrum.registries.SpectrumItemTags;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class KindlingEntity extends Horse implements RangedAttackMob, NeutralMob {
	
	protected static final ResourceLocation CLIPPING_LOOT_TABLE = SpectrumCommon.locate("gameplay/kindling_clipping");
	protected static final Ingredient FOOD = Ingredient.of(SpectrumItemTags.KINDLING_FOOD);
	
	private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(30, 59);
	private static final EntityDataAccessor<Integer> ANGER = SynchedEntityData.defineId(KindlingEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> CLIPPED = SynchedEntityData.defineId(KindlingEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> CHILL = SynchedEntityData.defineId(KindlingEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> PLAYING = SynchedEntityData.defineId(KindlingEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> INCITED = SynchedEntityData.defineId(KindlingEntity.class, EntityDataSerializers.BOOLEAN);
	
	protected @Nullable UUID angryAt;
	
	public AnimationState standingAnimationState = new AnimationState();
	public AnimationState walkingAnimationState = new AnimationState();
	public AnimationState standingAngryAnimationState = new AnimationState();
	public AnimationState walkingAngryAnimationState = new AnimationState();
	public AnimationState glidingAnimationState = new AnimationState();
	
	public KindlingEntity(EntityType<? extends KindlingEntity> entityType, Level world) {
		super(entityType, world);
		
		this.setPathfindingMalus(BlockPathTypes.WATER, -0.75F);
		
		this.xpReward = 8;
	}
	
	public static AttributeSupplier.Builder createKindlingAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 100.0D)
				.add(Attributes.ARMOR, 25.0D)
				.add(Attributes.ARMOR_TOUGHNESS, 12.0D)
				.add(AdditionalEntityAttributes.MAGIC_PROTECTION, 6.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.6D)
				.add(Attributes.ATTACK_DAMAGE, 25F)
				.add(Attributes.ATTACK_KNOCKBACK, 1.5F)
				.add(Attributes.JUMP_STRENGTH, 12.0D);
	}
	
	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
		this.setPose(Pose.STANDING);
		return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
	}
	
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.4));
		this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.2F));
		this.goalSelector.addGoal(3, new MeleeChaseGoal(this));
		this.goalSelector.addGoal(4, new CancellableProjectileAttackGoal(this, 1.25, 40, 20.0F));
		this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new PlayRoughGoal(this));
		this.goalSelector.addGoal(7, new TemptGoal(this, 1.25, FOOD, false));
		this.goalSelector.addGoal(8, new FollowParentGoal(this, 1.1D));
		this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
		
		this.targetSelector.addGoal(1, new CoughRevengeGoal(this));
		this.targetSelector.addGoal(2, new FindPlayMateGoal<>(this, 4, 0.25F, Monster.class));
		this.targetSelector.addGoal(3, new FindPlayMateGoal<>(this, 10, 1F, KindlingEntity.class));
		this.targetSelector.addGoal(4, new FindPlayMateGoal<>(this, 40, 4F, Player.class));
		this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ANGER, 0);
		this.entityData.define(CHILL, 40);
		this.entityData.define(CLIPPED, 0);
		this.entityData.define(PLAYING, false);
		this.entityData.define(INCITED, false);
	}
	
	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
		if (DATA_POSE.equals(data)) {
			this.standingAnimationState.stop();
			this.walkingAnimationState.stop();
			this.standingAngryAnimationState.stop();
			this.walkingAngryAnimationState.stop();
			this.glidingAnimationState.stop();
			
			switch (this.getPose()) {
				case STANDING -> this.standingAnimationState.start(this.tickCount);
				case DIGGING -> this.walkingAnimationState.start(this.tickCount);
				case ROARING -> this.standingAngryAnimationState.start(this.tickCount);
				case SNIFFING -> this.walkingAngryAnimationState.start(this.tickCount);
				case FALL_FLYING -> this.glidingAnimationState.start(this.tickCount);
				default -> {
				}
			}
		}
		super.onSyncedDataUpdated(data);
	}
	
	@Override
	public double getPassengersRidingOffset() {
		return this.getBbHeight() - (this.isBaby() ? 0.2 : 0.15);
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		this.addPersistentAngerSaveData(nbt);
		nbt.putInt("chillTime", getChillTime());
		nbt.putBoolean("playing", isPlaying());
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		this.readPersistentAngerSaveData(this.level(), nbt);
		setChillTime(nbt.getInt("chillTime"));
		setPlaying(nbt.getBoolean("playing"));
	}
	
	@Override
	public boolean isFood(ItemStack stack) {
		return FOOD.test(stack);
	}
	
	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
		Horse baby = SpectrumEntityTypes.KINDLING.create(world);
		this.setOffspringAttributes(entity, baby);
		return baby;
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return SpectrumSoundEvents.ENTITY_KINDLING_AMBIENT;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SpectrumSoundEvents.ENTITY_KINDLING_HURT;
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		return SpectrumSoundEvents.ENTITY_KINDLING_DEATH;
	}
	
	@Override
	protected SoundEvent getAngrySound() {
		return SpectrumSoundEvents.ENTITY_KINDLING_ANGRY;
	}
	
	@Override
	protected void playJumpSound() {
		this.playSound(SpectrumSoundEvents.ENTITY_KINDLING_JUMP, 0.4F, 1.0F);
	}
	
	@Override
	public boolean isJumping() {
		return !this.onGround();
	}
	
	@Override
	public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}
	
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.getEntity() instanceof KindlingEntity) {
			amount = 1;
			
			if (random.nextBoolean()) {
				setChillTime(0);
			}
		}
		
		if (amount > 1) {
			setPlaying(false);
		}
		
		thornsFlag = source.is(DamageTypes.THORNS);
		
		return super.hurt(source, amount);
	}
	
	// makes it so Kindlings are not angered by thorns damage
	// since they play fight and may damage their owner
	// that would make them aggro otherwise
	boolean thornsFlag = false;
	
	@Override
	public void setLastHurtByMob(@Nullable LivingEntity attacker) {
		if(!thornsFlag) {
			super.setLastHurtByMob(attacker);
		}
	}
	
	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return 0.6F * dimensions.height;
	}
	
	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		
		if (!this.level().isClientSide()) {
			this.updatePersistentAnger((ServerLevel) this.level(), false);
			this.setClipped(this.getClipTime() - 1);
			this.setChillTime(this.getChillTime() - 1);
		}
		if (this.tickCount % 600 == 0) {
			this.heal(1.0F);
		}
	}
	
	@Override
	public void aiStep() {
		super.aiStep();
		
		Vec3 vec3d = this.getDeltaMovement();
		if (!this.onGround() && vec3d.y < 0.0) {
			this.setDeltaMovement(vec3d.multiply(1.0, 0.6, 1.0));
		}
		if (this.fallDistance < 0.2) {
			boolean isMoving = this.getX() - this.xo != 0 || this.getZ() - this.zo != 0; // pretty ugly, but also triggers when being ridden
			if (getRemainingPersistentAngerTime() > 0) {
				this.setPose(isMoving ? Pose.EMERGING : Pose.ROARING);
			} else {
				this.setPose(isMoving ? Pose.SNIFFING : Pose.STANDING);
			}
		} else {
			this.setPose(Pose.FALL_FLYING);
		}
	}
	
	@Override
	protected boolean isFlapping() {
		return true;
	}
	
	@Override
	protected void onFlap() {
		// TODO - Make the Kindling flap its wings? Maybe while jumping or passively
	}
	
	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		if(player.isSecondaryUseActive()) {
			return super.mobInteract(player, hand);
		}
		
		if (this.getRemainingPersistentAngerTime() > 0) {
			return InteractionResult.sidedSuccess(this.level().isClientSide());
		}
		
		ItemStack handStack = player.getMainHandItem();
		if(!this.isBaby()) {
			if (!this.isClipped() && handStack.is(ConventionalItemTags.SHEARS)) {
				handStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
				
				if (!this.level().isClientSide()) {
					setTarget(player);
					takeRevenge(player.getUUID());
					this.makeMad();
					clipAndDrop();
				}
				
				return InteractionResult.sidedSuccess(this.level().isClientSide());
				
			} else if (handStack.is(SpectrumItemTags.PEACHES) || handStack.is(SpectrumItemTags.EGGPLANTS)) {
				// 🍆 / 🍑 = 💘
				
				if (!this.level().isClientSide()) {
					handStack.shrink(1);
					
					this.setTamed(true);
					if (getOwnerUUID() == null && player instanceof ServerPlayer serverPlayerEntity) {
						this.setOwnerUUID(player.getUUID());
						CriteriaTriggers.TAME_ANIMAL.trigger(serverPlayerEntity, this);
					}
					
					this.setInLove(player);
					
					this.level().broadcastEntityEvent(this, (byte) 7); // heart particles
					this.playSound(SpectrumSoundEvents.ENTITY_KINDLING_LOVE);
					
					clipAndDrop();
				}
				
				return InteractionResult.sidedSuccess(this.level().isClientSide());
			}
		}
		
		return super.mobInteract(player, hand);
	}
	
	@Override
	protected boolean handleEating(Player player, ItemStack item) {
		boolean canEat = false;

		this.setInLove(player);
		
		if (this.getHealth() < this.getMaxHealth()) {
			this.heal(2.0F);
			canEat = true;
		}
		
		if (this.isBaby()) {
			this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
			if (!this.level().isClientSide) {
				this.ageUp(20);
			}
			
			canEat = true;
		}
		
		if ((canEat || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
			canEat = true;
			if (!this.level().isClientSide) {
				this.modifyTemper(3);
			}
		}
		
		if (canEat) {
			//this.playEatingAnimation();
			this.gameEvent(GameEvent.EAT);
		}
		
		return canEat;
	}
	
	@Override
	public void updatePersistentAnger(ServerLevel world, boolean angerPersistent) {
		LivingEntity livingEntity = this.getTarget();
		UUID uUID = this.getPersistentAngerTarget();
		if ((livingEntity == null || livingEntity.isDeadOrDying()) && uUID != null && world.getEntity(uUID) instanceof Mob) {
			this.stopBeingAngry();
		} else {
			if (this.getRemainingPersistentAngerTime() > 0 && (livingEntity == null || livingEntity.getType() != EntityType.PLAYER || !angerPersistent)) {
				this.setRemainingPersistentAngerTime(this.getRemainingPersistentAngerTime() - 1);
				if (this.getRemainingPersistentAngerTime() == 0) {
					this.stopBeingAngry();
				}
			}
			
		}
	}
	
	private void clipAndDrop() {
		setClipped(4800); // 4 minutes
		for (ItemStack clippedStack : getClippedStacks((ServerLevel) this.level())) {
			spawnAtLocation(clippedStack, 0.3F);
		}
	}
	
	public List<ItemStack> getClippedStacks(ServerLevel world) {
		LootTable lootTable = world.getServer().getLootData().getLootTable(CLIPPING_LOOT_TABLE);
		return lootTable.getRandomItems(
				new LootParams.Builder(world)
						.withParameter(LootContextParams.THIS_ENTITY, KindlingEntity.this)
						.create(LootContextParamSets.PIGLIN_BARTER)
		);
	}
	
	protected void coughAt(LivingEntity target) {
		KindlingCoughEntity kindlingCoughEntity = new KindlingCoughEntity(this.level(), this);
		double d = target.getX() - this.getX();
		double e = target.getY(0.33F) - kindlingCoughEntity.getY();
		double f = target.getZ() - this.getZ();
		double g = Math.sqrt(d * d + f * f) * 0.2;
		kindlingCoughEntity.shoot(d, e + g, f, 1.5F, 10.0F);
		
		if (!this.isSilent()) {
			this.playSound(SpectrumSoundEvents.ENTITY_KINDLING_SHOOT, 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
		}
		
		this.level().addFreshEntity(kindlingCoughEntity);
	}
	
	public boolean isClipped() {
		return this.entityData.get(CLIPPED) > 0;
	}
	
	public int getClipTime() {
		return this.entityData.get(CLIPPED);
	}
	
	public void setClipped(int clipTime) {
		this.entityData.set(CLIPPED, clipTime);
	}
	
	public int getChillTime() {
		return this.entityData.get(CHILL);
	}
	
	public void setChillTime(int chillTime) {
		this.entityData.set(CHILL, chillTime);
	}
	
	public void setPlaying(boolean playing) {
		this.entityData.set(PLAYING, playing);
	}
	
	public boolean isPlaying() {
		return this.entityData.get(PLAYING);
	}
	
	public void setIncited(boolean incited) {
		this.entityData.set(INCITED, incited);
	}
	
	public boolean isIncited() {
		return entityData.get(INCITED);
	}
	
	@Override
	public boolean isStanding() {
		return super.isStanding() || this.getRemainingPersistentAngerTime() > 0;
	}
	
	@Override
	public int getRemainingPersistentAngerTime() {
		return this.entityData.get(ANGER);
	}
	
	@Override
	public void setRemainingPersistentAngerTime(int angerTime) {
		this.entityData.set(ANGER, angerTime);
	}
	
	@Override
	public @Nullable UUID getPersistentAngerTarget() {
		return this.angryAt;
	}
	
	@Override
	public void setPersistentAngerTarget(@Nullable UUID angryAt) {
		this.angryAt = angryAt;
	}
	
	public void takeRevenge(UUID target) {
		setPersistentAngerTarget(target);
		setIncited(false);
		setPlaying(false);
		
		startPersistentAngerTimer();
	}
	
	public
	@Override void startPersistentAngerTimer() {
		this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
	}
	
	@Override
	public void performRangedAttack(LivingEntity target, float pullProgress) {
		this.coughAt(target);
	}
	
	@Override
	public boolean canEatGrass() {
		return false;
	}
	
	@Override
	public boolean canMate(Animal other) {
		return other != this && other instanceof KindlingEntity otherKindling && this.canParent() && otherKindling.canParent();
	}
	

	
	protected class CoughRevengeGoal extends HurtByTargetGoal {
		
		public CoughRevengeGoal(KindlingEntity kindling) {
			super(kindling, KindlingEntity.class);
		}
		
		@Override
		public boolean canContinueToUse() {
			return KindlingEntity.this.isAngry() && super.canContinueToUse();
		}
		
		@Override
		public void start() {
			super.start();
			var attacker = getLastHurtByMob();
			if (attacker != null) {
				takeRevenge(getLastHurtByMob().getUUID());
			}
		}
		
		@Override
		protected void alertOther(Mob mob, LivingEntity target) {
			if (mob instanceof Bee && this.mob.hasLineOfSight(target)) {
				mob.setTarget(target);
			}
		}
		
	}
	
	protected class MeleeChaseGoal extends MeleeAttackGoal {
		
		public MeleeChaseGoal(KindlingEntity kindling) {
			super(kindling, 0.5F, true);
		}
		
		@Override
		public boolean canUse() {
			var kindling = KindlingEntity.this;
			var angryAt = kindling.getPersistentAngerTarget();
			if (angryAt == null)
				return false;
			return super.canUse() && kindling.isAngry() && !isPlaying() && KindlingEntity.this.distanceTo(this.mob.getTarget()) < 5F;
		}
		
		@Override
		public boolean canContinueToUse() {
			return super.canContinueToUse() && KindlingEntity.this.distanceTo(this.mob.getTarget()) < 9F;
		}
	}
	
	protected class PlayRoughGoal extends MeleeAttackGoal {
		
		public PlayRoughGoal(PathfinderMob mob) {
			super(mob, 0.4F, true);
		}
		
		@Override
		public boolean canUse() {
			return super.canUse() && !isAngry() && !isVehicle() && isPlaying();
		}
		
		@Override
		public boolean canContinueToUse() {
			if (!super.canContinueToUse())
				return false;
			
			if ((getTarget() instanceof KindlingEntity playMate && playMate.isAngry()) || isVehicle()) {
				setTarget(null);
				setIncited(false);
				return false;
			}
			
			return !isAngry();
		}
		
		@Override
		protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
			double d = this.getAttackReachSqr(target);
			if (squaredDistance <= d && this.getTicksUntilNextAttack() <= 0) {
				this.resetAttackCooldown();
				this.mob.swing(InteractionHand.MAIN_HAND);
				this.mob.doHurtTarget(target);
				if (target instanceof KindlingEntity playMate && !playMate.isAngry() && random.nextBoolean()) {
					playMate.setIncited(true);
				}
				
				if (!(target instanceof Enemy)) {
					target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200));
				}
				if (random.nextBoolean()) {
					stop();
					setIncited(false);
					this.mob.setTarget(null);
					KindlingEntity.this.setChillTime(2400 * (target instanceof Player ? 2 : 1));
				}
			}
		}
	}
	
	protected class FindPlayMateGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
		
		private final float waitModifier;
		
		public FindPlayMateGoal(Mob mob, int reciprocalChance, float waitModifier, Class<T> targetClass) {
			super(mob, targetClass, reciprocalChance, true, true, null);
			this.waitModifier = waitModifier;
		}
		
		@Override
		public boolean canUse() {
			if (isAngry() || isVehicle())
				return false;
			
			if (!isIncited()) {
				var chill = getChillTime();
				
				if (chill > 0)
					return false;
			}
			
			if (isIncited() || (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0)) {
				this.findTarget();
				
				if (this.target != null) {
					setChillTime((int) (1200 * waitModifier));
					return true;
				}
			}
			return false;
		}
		
		@Override
		public void start() {
			super.start();
			setPlaying(true);
		}
	}
	
	protected class CancellableProjectileAttackGoal extends RangedAttackGoal {
		
		public CancellableProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange) {
			super(mob, mobSpeed, intervalTicks, maxShootRange);
		}
		
		@Override
		public boolean canContinueToUse() {
			return KindlingEntity.this.isAngry() && super.canContinueToUse() && distanceTo(getProjectileTarget()) > 5F;
		}
		
		@Override
		public boolean canUse() {
			return super.canUse() && !isPlaying() && distanceTo(getProjectileTarget()) > 6F;
		}
		
		protected LivingEntity getProjectileTarget() {
			return ((ProjectileAttackGoalAccessor) this).getProjectileAttackTarget();
		}
		
	}
}
