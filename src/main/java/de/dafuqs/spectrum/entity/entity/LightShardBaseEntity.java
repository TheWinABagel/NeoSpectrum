package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class LightShardBaseEntity extends Projectile {
	
	protected static final Predicate<LivingEntity> EVERYTHING_TARGET = livingEntity -> true;
	protected static final Predicate<LivingEntity> MONSTER_TARGET = livingEntity -> livingEntity instanceof Enemy;

	protected static final IntProvider DEFAULT_COUNT_PROVIDER = UniformInt.of(7, 13);
	private static final EntityDataAccessor<Integer> MAX_AGE = SynchedEntityData.defineId(LightShardBaseEntity.class, EntityDataSerializers.INT);
	
	public static final int DECELERATION_PHASE_LENGTH = 25;
	public static final float DEFAULT_ACCELERATION = 0.03F;

	protected float scaleOffset, damage, detectionRange;
	protected Optional<UUID> target = Optional.empty();
	protected Optional<LivingEntity> targetEntity = Optional.empty();
	protected Vec3 initialVelocity = Vec3.ZERO;
	protected Predicate<LivingEntity> targetPredicate;

	public LightShardBaseEntity(EntityType<? extends Projectile> entityType, Level world) {
		super(entityType, world);
		this.scaleOffset = world.random.nextFloat() + 0.15F;
	}
	
	public LightShardBaseEntity(EntityType<? extends Projectile> entityType, Level world, LivingEntity owner, Optional<LivingEntity> target, float detectionRange, float damage, float lifeSpanTicks) {
		super(entityType, world);
		
		this.setOwner(owner);
		this.detectionRange = detectionRange;
		this.damage = damage;

		if (target.isPresent()) {
			setTarget(target.get());
			this.targetPredicate = EVERYTHING_TARGET;
		} else {
			this.targetPredicate = MONSTER_TARGET;
		}
		setMaxAge((int) ((lifeSpanTicks + Mth.normal(world.getRandom(), 10, 7))));
	}
	
	@Override
	protected void defineSynchedData() {
		this.entityData.define(MAX_AGE, 20);
	}
	
	public int getMaxAge() {
		return this.entityData.get(MAX_AGE);
	}
	
	public void setMaxAge(int maxAge) {
		this.entityData.set(MAX_AGE, maxAge);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		tickCount++;
		if (this.level().isClientSide() && tickCount > DECELERATION_PHASE_LENGTH - 1 && getDeltaMovement().length() > 0.075) {
			if (getDeltaMovement().length() > 0.2 || this.level().getGameTime() % 2 == 0)
				this.level().addParticle(SpectrumParticleTypes.LIGHT_TRAIL, true, xo, yo, zo, 0, 0, 0);
		}
		
		if (tickCount > getMaxAge()) {
			playSound(SpectrumSoundEvents.SOFT_HUM, random.nextFloat() + 0.25F, 1F + random.nextFloat());
			this.remove(RemovalReason.DISCARDED);
		}
		
		var velocity = getDeltaMovement();
		absMoveTo(getX() + velocity.x(), getY() + velocity.y(), getZ() + velocity.z());
		
		if (tickCount < DECELERATION_PHASE_LENGTH) {
			var deceleration = Math.max((float) tickCount / DECELERATION_PHASE_LENGTH, 0.5);
			setDeltaMovement(
					Mth.lerp(deceleration, initialVelocity.x, 0),
					Mth.lerp(deceleration, initialVelocity.y, 0),
					Mth.lerp(deceleration, initialVelocity.z, 0)
			);
			hasImpulse = true;
			markHurt();
			return;
		}
		
		var hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
		onHit(hitResult);
		
		if (detectionRange > 0 && (this.targetEntity.isEmpty() || !isValidTarget(targetEntity.get()))) {
			Level world = this.level();
			if (world.isClientSide)
				return;
			
			if (random.nextFloat() > 0.25)
				return;
			
			findSuitableTargets((ServerLevel) this.level());
		}
		
		if (this.targetEntity.isPresent() && isValidTarget(targetEntity.get())) {
			var entity = targetEntity.get();
			
			var transformVector = entity
					.position()
					.add(0, entity.getBbHeight() / 2, 0)
					.subtract(position())
					.normalize();
			
			var accelerationVector = transformVector.scale(DEFAULT_ACCELERATION);
			push(accelerationVector.x, accelerationVector.y, accelerationVector.z);
		}
	}
	
	protected void setTargetPredicate(@NotNull Predicate<LivingEntity> targetPredicate) {
		this.targetPredicate = targetPredicate;
	}

	protected void findSuitableTargets(ServerLevel serverWorld) {
		List<LivingEntity> potentialTargets = serverWorld.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(position(), detectionRange, detectionRange, detectionRange), this.targetPredicate);

		Collections.shuffle(potentialTargets);

		for (LivingEntity potentialTarget : potentialTargets) {
			if (this.canSee(potentialTarget) && isValidTarget(potentialTarget)) {
				setTarget(potentialTarget);
				return;
			}
		}
	}

	public boolean canSee(Entity entity) {
		if (entity.level() != this.level()) {
			return false;
		} else {
			if (entity.position().distanceTo(this.position()) > 128.0) {
				return false;
			} else {
				return this.level().clip(new ClipContext(this.position(), entity.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == net.minecraft.world.phys.HitResult.Type.MISS;
			}
		}
	}

	protected boolean isValidTarget(LivingEntity entity) {
		Entity owner = getOwner();
		if (entity == owner) {
			return false;
		}
		if (owner != null && entity.isAlliedTo(owner)) {
			return false;
		}
		if (!this.targetPredicate.test(entity)) {
			return false;
		}
		if (entity instanceof OwnableEntity pet) {
			Entity petOwner = pet.getOwner();
			if (petOwner instanceof LivingEntity livingEntity) {
				if (this.targetPredicate.test(livingEntity)) {
					return false;
				}
			}
		}
		return !entity.isRemoved() && entity.isAlive() && !entity.isInvisible() && !entity.isInvulnerable();
	}

	protected void setInitialVelocity(Vec3 vector) {
		initialVelocity = vector;
		setDeltaMovement(vector);
	}
	
	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		if (!this.level().isClientSide()) {
			var hitEntity = entityHitResult.getEntity();

			if (!(hitEntity instanceof LivingEntity livingEntity)) {
				return;
			}
			if (!isValidTarget(livingEntity)) {
				return;
			}

			onHitEntity(livingEntity);
		}

		this.remove(RemovalReason.DISCARDED);
		super.onHitEntity(entityHitResult);
	}
	
	protected void onHitEntity(LivingEntity attacked) {
		float finalDamage = damage * (random.nextFloat() + 0.5F) * (1 - getVanishingProgress(tickCount));
		attacked.invulnerableTime = 0;
		attacked.hurt(SpectrumDamageTypes.irradiance(this.level(), getOwner() instanceof LivingEntity owner ? owner : null), finalDamage);
		
		attacked.playSound(SpectrumSoundEvents.SOFT_HUM, 1.334F, 0.9F + random.nextFloat());
		attacked.playSound(SpectrumSoundEvents.CRYSTAL_STRIKE, random.nextFloat() * 0.4F + 0.2F, 0.8F + random.nextFloat());
	}
	
	@Override
	public void onClientRemoval() {
	}

	@Override
	public void remove(RemovalReason reason) {
		super.remove(reason);
		var bound = random.nextInt(11);
		if(reason.shouldDestroy()) {
			for (int i = 0; i < bound + 5; i++) {
				if (random.nextFloat() < 0.665) {
					this.level().addAlwaysVisibleParticle(SpectrumParticleTypes.WHITE_SPARKLE_RISING, true, getX(), getY(), getZ(),
							random.nextFloat() * 0.25F - 0.125F,
							random.nextFloat() * 0.25F - 0.125F,
							random.nextFloat() * 0.25F - 0.125F
					);
				} else {
					this.level().addAlwaysVisibleParticle(SpectrumParticleTypes.SHOOTING_STAR, true, getX(), getY(), getZ(),
							random.nextFloat() * 0.5F - 0.25F,
							random.nextFloat() * 0.5F - 0.25F,
							random.nextFloat() * 0.5F - 0.25F
					);
				}
			}
		}
	}
	
	public static void summonBarrageInternal(Level world, @Nullable LivingEntity user, Supplier<LightShardBaseEntity> supplier, Vec3 pos, IntProvider count) {
		var random = world.getRandom();
		var projectiles = count.sample(random);
		
		world.playSound(null, BlockPos.containing(pos), SpectrumSoundEvents.GLASS_SHIMMER, SoundSource.AMBIENT, 1F, 0.9F + random.nextFloat() * 0.5F);
		
		for (int i = 0; i < projectiles; i++) {
			// spawn the shard
			LightShardBaseEntity shard = supplier.get();
			shard.setPos(pos);
			var velocityY = 0.0;
			if (user != null && user.onGround()) {
				velocityY = random.nextFloat() * 0.75;
				shard.setInitialVelocity(new Vec3(random.nextFloat() * 2 - 1, velocityY, random.nextFloat() * 2 - 1).add(user.getDeltaMovement()));
			} else {
				velocityY = random.nextFloat() - 0.5;
				shard.setInitialVelocity(new Vec3(random.nextFloat() * 2 - 1, velocityY, random.nextFloat() * 2 - 1));
			}

			world.addFreshEntity(shard);
			
			// spawn particles
			for (int j = 0; j < 3; j++) {
				world.addParticle(SpectrumParticleTypes.SHOOTING_STAR, pos.x, pos.y, pos.z,
						random.nextFloat() * 0.8F - 0.4F,
						velocityY * 2,
						random.nextFloat() * 0.8F - 0.4F
				);
			}
		}
	}
	
	public float getScaleOffset() {
		return scaleOffset;
	}
	
	public float getVanishingProgress(int age) {
		return 1 - (float) Math.min(getMaxAge() - age, getVanishingLength()) / getVanishingLength();
	}
	
	public int getVanishingLength() {
		return Math.round(getMaxAge() / 4F);
	}
	
	public void setTarget(@NotNull LivingEntity target) {
		this.target = Optional.ofNullable(target.getUUID());
		this.targetEntity = Optional.of(target);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		target.ifPresent(uuid -> nbt.putUUID("target", uuid));
		nbt.putDouble("initX", initialVelocity.x);
		nbt.putDouble("initY", initialVelocity.y);
		nbt.putDouble("initZ", initialVelocity.z);
		
		nbt.putFloat("damage", damage);
		nbt.putFloat("scale", scaleOffset);
		nbt.putInt("maxAge", getMaxAge());
	}
	
	@Override
	protected void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		if (nbt.contains("target")) {
			target = Optional.ofNullable(nbt.getUUID("target"));
		}
		
		initialVelocity = new Vec3(
				nbt.getDouble("initX"),
				nbt.getDouble("initY"),
				nbt.getDouble("initZ")
		);
		
		damage = nbt.getFloat("damage");
		scaleOffset = nbt.getFloat("scale");
		setMaxAge(nbt.getInt("maxAge"));
	}
	
	public abstract ResourceLocation getTexture();
	
}
