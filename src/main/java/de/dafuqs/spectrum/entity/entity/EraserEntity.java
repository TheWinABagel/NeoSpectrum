package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.api.entity.PackEntity;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EraserEntity extends Spider implements PackEntity<EraserEntity>, Bucketable {
	
	private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EraserEntity.class, EntityDataSerializers.BOOLEAN);
	
	private @Nullable MobEffectInstance effectOnHit;
	
	private @Nullable EraserEntity leader;
	private int groupSize = 1;
	
	public EraserEntity(EntityType<? extends EraserEntity> entityType, Level world) {
		super(entityType, world);
	}
	
	public static AttributeSupplier.Builder createEraserAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 20.0)
				.add(Attributes.MOVEMENT_SPEED, 0.3)
				.add(Attributes.FOLLOW_RANGE, 28.0);
	}
	
	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LizardEntity.class, 6.0F, 1.0, 1.2));
		this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4F));
		this.goalSelector.addGoal(3, new OcelotAttackGoal(this));
		this.goalSelector.addGoal(4, new FollowClanLeaderGoal<>(this));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
	}
	
	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		return super.canBeAffected(effect) && effect.getEffect() != SpectrumStatusEffects.DEADLY_POISON;
	}
	
	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
		RandomSource random = world.getRandom();
		
		this.effectOnHit = getRandomOnHitEffect();
		
		if (entityData == null) {
			entityData = new SwarmingSpiderData();
			((SwarmingSpiderData) entityData).setRandomEffect(random);
		}
		
		if (entityData instanceof SwarmingSpiderData swarmingSpiderData) {
			MobEffect statusEffect = swarmingSpiderData.effect;
			if (statusEffect != null) {
				this.addEffect(swarmingSpiderData.getEffectInstance());
			}
		}
		
		return entityData;
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(FROM_BUCKET, false);
	}
	
	@Override
	public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}
	
	@Override
	public boolean doHurtTarget(Entity target) {
		if (super.doHurtTarget(target)) {
			if (this.effectOnHit != null && target instanceof LivingEntity livingTarget) {
				livingTarget.addEffect(this.effectOnHit, this);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean hasOthersInGroup() {
		return this.groupSize > 1;
	}
	
	@Override
	public @Nullable EraserEntity getLeader() {
		return this.leader;
	}
	
	@Override
	public boolean isCloseEnoughToLeader() {
		return this.distanceToSqr(this.leader) <= 121.0;
	}
	
	@Override
	public void leaveGroup() {
		if (this.leader != null) {
			this.leader.decreaseGroupSize();
			this.leader = null;
		}
	}
	
	@Override
	public void moveTowardLeader() {
		if (this.hasLeader()) {
			this.getNavigation().moveTo(this.leader, 1.0);
		}
	}
	
	@Override
	public int getMaxGroupSize() {
		return super.getMaxSpawnClusterSize();
	}
	
	@Override
	public void joinGroupOf(EraserEntity groupLeader) {
		this.leader = groupLeader;
		groupLeader.increaseGroupSize();
	}
	
	@Override
	public int getGroupSize() {
		return this.groupSize;
	}
	
	protected void increaseGroupSize() {
		++this.groupSize;
	}
	
	protected void decreaseGroupSize() {
		--this.groupSize;
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		
		nbt.putBoolean("FromBucket", this.fromBucket());
		putEffectOnHit(nbt);
	}
	
	private void putEffectOnHit(CompoundTag nbt) {
		if (this.effectOnHit != null) {
			CompoundTag effectNbt = new CompoundTag();
			this.effectOnHit.save(effectNbt);
			nbt.put("EffectOnHit", effectNbt);
		}
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		
		this.setFromBucket(nbt.getBoolean("FromBucket"));
		readEffectOnHit(nbt);
	}
	
	private void readEffectOnHit(CompoundTag nbt) {
		if (nbt.contains("EffectOnHit", Tag.TAG_COMPOUND)) {
			this.effectOnHit = MobEffectInstance.load(nbt.getCompound("EffectOnHit"));
		}
	}
	
	public MobEffectInstance getRandomOnHitEffect() {
		Level world = this.level();
		Difficulty difficulty = this.level().getDifficulty();
		
		MobEffect statusEffect;
		int amplifier = 0;
		switch (world.random.nextInt(8)) {
			case 1 -> {
				statusEffect = SpectrumStatusEffects.STIFFNESS;
				amplifier = random.nextInt(2);
			}
			case 2 -> {
				statusEffect = SpectrumStatusEffects.FRENZY;
				amplifier = random.nextInt(2);
			}
			case 3 -> statusEffect = SpectrumStatusEffects.SCARRED;
			case 4 -> {
				statusEffect = SpectrumStatusEffects.VULNERABILITY;
				amplifier = random.nextInt(2);
			}
			default -> {
				statusEffect = SpectrumStatusEffects.DEADLY_POISON;
				amplifier = random.nextInt(2);
			}
		}
		
		int duration = 120 * difficulty.getId();
		return new MobEffectInstance(statusEffect, duration, amplifier);
	}
	
	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return 0.2F;
	}
	
	// Bucketable
	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		return bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
	}
	
	// Bucketable.tryBucket() has a dedicated check for WATER_BUCKET in there
	// since we are bucketing with Fluids.EMPTY we have to use a custom implementation
	static <T extends LivingEntity & Bucketable> Optional<InteractionResult> bucketMobPickup(Player player, InteractionHand hand, T entity) {
		ItemStack handStack = player.getItemInHand(hand);
		if (handStack.getItem() == Items.BUCKET && entity.isAlive()) {
			entity.playSound(entity.getPickupSound(), 1.0F, 1.0F);
			ItemStack bucketedStack = entity.getBucketItemStack();
			entity.saveToBucketTag(bucketedStack);
			ItemStack exchangedStack = ItemUtils.createFilledResult(handStack, player, bucketedStack, false);
			player.setItemInHand(hand, exchangedStack);
			Level world = entity.level();
			if (!world.isClientSide) {
				CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, bucketedStack);
			}
			
			entity.discard();
			return Optional.of(InteractionResult.sidedSuccess(world.isClientSide));
		} else {
			return Optional.empty();
		}
	}
	
	@Override
	public boolean requiresCustomPersistence() {
		return super.requiresCustomPersistence() || this.fromBucket();
	}
	
	@Override
	public boolean removeWhenFarAway(double distanceSquared) {
		return !this.fromBucket() && !this.hasCustomName();
	}
	
	@Override
	public boolean fromBucket() {
		return this.entityData.get(FROM_BUCKET);
	}
	
	@Override
	public void setFromBucket(boolean fromBucket) {
		this.entityData.set(FROM_BUCKET, fromBucket);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void saveToBucketTag(ItemStack stack) {
		Bucketable.saveDefaultDataToBucketTag(this, stack);
		CompoundTag nbtCompound = stack.getOrCreateTag();
		putEffectOnHit(nbtCompound);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void loadFromBucketTag(CompoundTag nbt) {
		Bucketable.loadDefaultDataFromBucketTag(this, nbt);
		readEffectOnHit(nbt);
	}
	
	@Override
	public ItemStack getBucketItemStack() {
		return new ItemStack(SpectrumItems.BUCKET_OF_ERASER);
	}
	
	@Override
	public SoundEvent getPickupSound() {
		return SoundEvents.BUCKET_FILL;
	}
	
	public static class SwarmingSpiderData extends SpiderEffectsGroupData {
		public MobEffect effect;
		public int amplifier = 0;
		
		public SwarmingSpiderData() {
		}
		
		@Override
		public void setRandomEffect(RandomSource random) {
			switch (random.nextInt(5)) {
				case 0 -> {
					this.effect = MobEffects.MOVEMENT_SPEED;
					this.amplifier = random.nextInt(2);
				}
				case 1 -> {
					this.effect = MobEffects.DAMAGE_BOOST;
					this.amplifier = random.nextInt(2);
				}
				case 2 -> {
					this.effect = MobEffects.REGENERATION;
					this.amplifier = random.nextInt(2);
				}
				case 3 -> this.effect = MobEffects.INVISIBILITY;
				default -> {
					this.effect = SpectrumStatusEffects.MAGIC_ANNULATION;
					this.amplifier = 5;
				}
			}
		}
		
		public MobEffectInstance getEffectInstance() {
			return new MobEffectInstance(this.effect, Integer.MAX_VALUE, this.amplifier);
		}
	}
	
}
