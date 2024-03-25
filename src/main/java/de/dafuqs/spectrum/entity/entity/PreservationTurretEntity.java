package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.entity.ai.FixedBodyControl;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class PreservationTurretEntity extends AbstractGolem implements Enemy, VibrationSystem {

	protected static final int DETECTION_RANGE = 16;
	protected static final float DAMAGE = 4.0F;

	protected static final UUID COVERED_ARMOR_BONUS_ID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
	protected static final UUID COVERED_TOUGHNESS_BONUS_ID = UUID.fromString("8ED24DFF-221F-4ADB-9DD2-7EA92574628C");
	protected static final AttributeModifier COVERED_ARMOR_BONUS = new AttributeModifier(COVERED_ARMOR_BONUS_ID, "Covered armor bonus", 20.0, AttributeModifier.Operation.ADDITION);
	protected static final AttributeModifier COVERED_TOUGHNESS_BONUS = new AttributeModifier(COVERED_TOUGHNESS_BONUS_ID, "Covered toughness bonus", 6.0, AttributeModifier.Operation.ADDITION);
	
	protected static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.defineId(PreservationTurretEntity.class, EntityDataSerializers.DIRECTION);
	protected static final EntityDataAccessor<Byte> PEEK_AMOUNT = SynchedEntityData.defineId(PreservationTurretEntity.class, EntityDataSerializers.BYTE);

	protected static final Vector3f SOUTH_VECTOR = Util.make(() -> {
		Vec3i vec3i = Direction.SOUTH.getNormal();
		return new Vector3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
	});

	protected final TargetingConditions TARGET_PREDICATE = TargetingConditions.forCombat();

	protected final DynamicGameEventListener<VibrationSystem.Listener> gameEventHandler = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
	protected final VibrationSystem.User vibrationCallback = new VibrationsCallback(this);
	protected VibrationSystem.Data vibrationListenerData = new VibrationSystem.Data();

	protected float prevOpenProgress;
	protected float openProgress;
	protected @Nullable BlockPos prevAttachedBlock;
	
	public PreservationTurretEntity(EntityType<? extends PreservationTurretEntity> entityType, Level world) {
		super(entityType, world);
		this.xpReward = 12;
		this.lookControl = new TurretLookControl(this);
	}
	
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new RatatatataGoal());
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 16.0F, 0.04F, true));
	}
	
	@Override
	protected Entity.MovementEmission getMovementEmission() {
		return Entity.MovementEmission.NONE;
	}
	
	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return SpectrumSoundEvents.ENTITY_PRESERVATION_TURRET_AMBIENT;
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		return SpectrumSoundEvents.ENTITY_PRESERVATION_TURRET_DEATH;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.isClosed() ? SpectrumSoundEvents.ENTITY_PRESERVATION_TURRET_HURT_CLOSED : SpectrumSoundEvents.ENTITY_PRESERVATION_TURRET_HURT;
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ATTACHED_FACE, Direction.DOWN);
		this.entityData.define(PEEK_AMOUNT, (byte) 0);
	}
	
	public static AttributeSupplier.Builder createGuardianTurretAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 60.0);
	}
	
	@Override
	protected BodyRotationControl createBodyControl() {
		return new FixedBodyControl(this);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putByte("AttachFace", (byte) this.getAttachedFace().get3DDataValue());
		nbt.putByte("Peek", this.entityData.get(PEEK_AMOUNT));
		
		//DataResult<NbtElement> dataResult = Vibrations.ListenerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVibrationListenerData());
		//dataResult.result().ifPresent((nbtElement) -> nbt.put("listener", nbtElement));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		this.setAttachedFace(Direction.from3DDataValue(nbt.getByte("AttachFace")));
		this.entityData.set(PEEK_AMOUNT, nbt.getByte("Peek"));

		/* If that is preserved the turrets do not target entities when loaded through structures (game event listener does not update chunk section?)
		if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
			DataResult<ListenerData> result = ListenerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")));
			result.result().ifPresent(listenerData -> PreservationTurretEntity.this.vibrationListenerData = listenerData);
		}*/
	}

	@Override
	public VibrationSystem.Data getVibrationData() {
		return this.vibrationListenerData;
	}

	@Override
	public VibrationSystem.User getVibrationUser() {
		return this.vibrationCallback;
	}

	@Override
	public void playAmbientSound() {
		if (this.getPeekAmount() > 0) {
			super.playAmbientSound();
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level() instanceof ServerLevel serverWorld) {
			VibrationSystem.Ticker.tick(serverWorld, this.getVibrationData(), this.getVibrationUser());
		}

		if (!this.level().isClientSide() && !this.isPassenger() && !this.canStay(this.blockPosition(), this.getAttachedFace())) {
			Direction direction = this.findAttachSide(this.blockPosition());
			if (direction != null) {
				this.setAttachedFace(direction);
			}
		}
		
		this.tickOpenProgress();
	}
	
	@Override
	public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> callback) {
		if (this.level() instanceof ServerLevel serverWorld) {
			callback.accept(this.gameEventHandler, serverWorld);
		}
	}
	
	private void tickOpenProgress() {
		this.prevOpenProgress = this.openProgress;
		float peekAmount = (float) this.getPeekAmount() * 0.01F;
		if (this.openProgress != peekAmount) {
			if (this.openProgress > peekAmount) {
				this.openProgress = Mth.clamp(this.openProgress - 0.05F, peekAmount, 1.0F);
			} else {
				this.openProgress = Mth.clamp(this.openProgress + 0.05F, 0.0F, peekAmount);
			}
		}
	}
	
	@Override
	public double getMyRidingOffset() {
		EntityType<?> vehicleType = this.getVehicle().getType();
		return vehicleType != EntityType.BOAT && vehicleType != EntityType.MINECART ? super.getMyRidingOffset() : 0.1875 - this.getVehicle().getPassengersRidingOffset();
	}
	
	@Override
	public boolean startRiding(Entity entity, boolean force) {
		if (this.level().isClientSide()) {
			this.prevAttachedBlock = null;
		}
		
		this.setAttachedFace(Direction.DOWN);
		return super.startRiding(entity, force);
	}
	
	@Override
	public void stopRiding() {
		super.stopRiding();
		if (this.level().isClientSide()) {
			this.prevAttachedBlock = this.blockPosition();
		}
		
		this.yBodyRotO = 0.0F;
		this.yBodyRot = 0.0F;
	}
	
	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
		this.setYRot(0.0F);
		this.yHeadRot = this.getYRot();
		this.setOldPosAndRot();
		return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	public void setPos(double x, double y, double z) {
		BlockPos blockPos = this.blockPosition();
		if (this.isPassenger()) {
			super.setPos(x, y, z);
		} else {
			super.setPos(Mth.floor(x) + 0.5, Mth.floor(y + 0.5), Mth.floor(z) + 0.5);
		}
		
		if (this.tickCount != 0) {
			BlockPos blockPos2 = this.blockPosition();
			if (!blockPos2.equals(blockPos)) {
				this.entityData.set(PEEK_AMOUNT, (byte) 0);
				this.hasImpulse = true;
				if (this.level().isClientSide() && !this.isPassenger() && !blockPos2.equals(this.prevAttachedBlock)) {
					this.prevAttachedBlock = blockPos;
					this.xOld = this.getX();
					this.yOld = this.getY();
					this.zOld = this.getZ();
				}
			}
			
		}
	}
	
	@Nullable
	protected Direction findAttachSide(BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (this.canStay(pos, direction)) {
				return direction;
			}
		}
		return null;
	}
	
	boolean canStay(BlockPos pos, Direction direction) {
		if (this.isInvalidPosition(pos)) {
			return false;
		} else {
			Direction direction2 = direction.getOpposite();
			if (!this.level().loadedAndEntityCanStandOnFace(pos.relative(direction), this, direction2)) {
				return false;
			} else {
				return this.level().noCollision(this, getBoundingBox());
			}
		}
	}
	
	private boolean isInvalidPosition(BlockPos pos) {
		BlockState blockState = this.level().getBlockState(pos);
		if (blockState.isAir()) {
			return false;
		} else {
			boolean bl = blockState.is(Blocks.MOVING_PISTON) && pos.equals(this.blockPosition());
			return !bl;
		}
	}
	
	@Override
	public void lerpTo(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.lerpSteps = 0;
		this.setPos(x, y, z);
		this.setRot(yaw, pitch);
	}
	
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.isClosed() && source.getDirectEntity() instanceof AbstractArrow) {
			return false;
		}
		return super.hurt(source, amount);
	}
	
	private boolean isClosed() {
		return this.getPeekAmount() == 0;
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return this.isAlive();
	}
	
	public Direction getAttachedFace() {
		return this.entityData.get(ATTACHED_FACE);
	}
	
	private void setAttachedFace(Direction face) {
		this.entityData.set(ATTACHED_FACE, face);
	}
	
	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
		if (ATTACHED_FACE.equals(data)) {
			this.setBoundingBox(this.makeBoundingBox());
		}
		
		super.onSyncedDataUpdated(data);
	}
	
	private int getPeekAmount() {
		return this.entityData.get(PEEK_AMOUNT);
	}
	
	void setPeekAmount(int peekAmount) {
		if (!this.level().isClientSide()) {
			this.getAttribute(Attributes.ARMOR).removeModifier(COVERED_ARMOR_BONUS);
			this.getAttribute(Attributes.ARMOR_TOUGHNESS).removeModifier(COVERED_TOUGHNESS_BONUS);
			if (peekAmount == 0) {
				this.getAttribute(Attributes.ARMOR).addPermanentModifier(COVERED_ARMOR_BONUS);
				this.getAttribute(Attributes.ARMOR_TOUGHNESS).addPermanentModifier(COVERED_TOUGHNESS_BONUS);
				this.playSound(SpectrumSoundEvents.ENTITY_PRESERVATION_TURRET_CLOSE, 1.0F, 1.0F);
				this.gameEvent(GameEvent.CONTAINER_CLOSE);
			} else {
				this.playSound(SpectrumSoundEvents.ENTITY_PRESERVATION_TURRET_OPEN, 1.0F, 1.0F);
				this.gameEvent(GameEvent.CONTAINER_OPEN);
			}
		}
		
		this.entityData.set(PEEK_AMOUNT, (byte) peekAmount);
	}
	
	public float getOpenProgress(float delta) {
		return Mth.lerp(delta, this.prevOpenProgress, this.openProgress);
	}

	@Override
	public void recreateFromPacket(ClientboundAddEntityPacket packet) {
		super.recreateFromPacket(packet);
		this.yBodyRot = 0.0F;
		this.yBodyRotO = 0.0F;
	}
	
	@Override
	public int getMaxHeadXRot() {
		return 180;
	}
	
	@Override
	public int getMaxHeadYRot() {
		return 180;
	}
	
	@Override
	public void push(Entity entity) {
	}
	
	@Override
	public boolean hasLineOfSight(Entity entity) {
		if (entity.level() != this.level()) {
			return false;
		}

		Vec3 thisEyePos = this.getEyePosition();
		Vec3 entityEyePos = entity.getEyePosition();
		double distance = entityEyePos.distanceTo(thisEyePos);

		// they only have a very limited vertical field of sight
		// a valid strategy would be to sneak to their top / bottom, since they can't shoot there
		return distance < 26
				&& (Math.abs(this.getEyeY() - entity.getEyeY()) < distance / 2 || Math.abs(this.getEyeY() - entity.getY()) < distance / 2)
				&& this.level().clip(new ClipContext(thisEyePos, entityEyePos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == net.minecraft.world.phys.HitResult.Type.MISS;
	}

	@Override
	public float getPickRadius() {
		return 0.0F;
	}

	@Contract("null->false")
	public boolean isValidTarget(@Nullable Entity entity) {
		return entity instanceof LivingEntity livingEntity
				&& this.level() == entity.level()
				&& EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity)
				&& !this.isAlliedTo(entity)
				&& livingEntity.getType() != EntityType.ARMOR_STAND
				&& livingEntity.getType() != SpectrumEntityTypes.PRESERVATION_TURRET
				&& !livingEntity.isInvulnerable()
				&& !livingEntity.isDeadOrDying()
				&& this.level().getWorldBorder().isWithinBounds(livingEntity.getBoundingBox());
	}

	private class TurretLookControl extends LookControl {
		public TurretLookControl(Mob entity) {
			super(entity);
		}
		
		@Override
		protected void clampHeadRotationToBody() {
		}
		
		@Override
		protected Optional<Float> getYRotD() {
			Direction attachedDirection = PreservationTurretEntity.this.getAttachedFace().getOpposite();
			Vector3f southVectorCopy = new Vector3f(PreservationTurretEntity.SOUTH_VECTOR);
			southVectorCopy.rotate(attachedDirection.getRotation());
			Vec3i vec3i = attachedDirection.getNormal();
			Vector3f vec3f2 = new Vector3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
			vec3f2.cross(southVectorCopy);
			double xOffset = this.wantedX - this.mob.getX();
			double yOffset = this.wantedY - this.mob.getEyeY();
			double zOffset = this.wantedZ - this.mob.getZ();
			Vector3f vec3f3 = new Vector3f((float) xOffset, (float) yOffset, (float) zOffset);
			float g = vec3f2.dot(vec3f3);
			float h = southVectorCopy.dot(vec3f3);
			return !(Math.abs(g) > 1.0E-5F) && !(Math.abs(h) > 1.0E-5F) ? Optional.empty() : Optional.of((float) (Mth.atan2((-g), h) * 57.2957763671875));
		}
		
		@Override
		protected Optional<Float> getXRotD() {
			return Optional.of(0.0F);
		}
	}
	
	/**
	 * This does the ouch
	 */
	private class RatatatataGoal extends Goal {
		
		public RatatatataGoal() {
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
		}
		
		@Override
		public boolean canUse() {
			LivingEntity target = PreservationTurretEntity.this.getTarget();

			if (target != null) {
				PreservationTurretEntity.this.getLookControl().setLookAt(target, 2.0F, 2.0F);

				if (!PreservationTurretEntity.this.hasLineOfSight(target)) {
					PreservationTurretEntity.this.setTarget(null);
					return false;
				}
			}

			return target != null
					&& target.isAlive()
					&& PreservationTurretEntity.this.openProgress == 1.0 && TARGET_PREDICATE.test(PreservationTurretEntity.this, PreservationTurretEntity.this.getTarget());
		}

		@Override
		public boolean canContinueToUse() {
			return super.canContinueToUse() && TARGET_PREDICATE.test(PreservationTurretEntity.this, PreservationTurretEntity.this.getTarget());
		}
		
		@Override
		public void stop() {
			PreservationTurretEntity.this.setPeekAmount(0);
		}
		
		@Override
		public void tick() {
			if (PreservationTurretEntity.this.level().getDifficulty() != Difficulty.PEACEFUL) {
				LivingEntity target = PreservationTurretEntity.this.getTarget();
				if (target == null) {
					return;
				}

				if (!PreservationTurretEntity.this.hasLineOfSight(target)) {
					return;
				}

				target.hurt(level().damageSources().mobAttack(PreservationTurretEntity.this), DAMAGE);
				PreservationTurretEntity.this.playSound(SpectrumSoundEvents.ENTITY_PRESERVATION_TURRET_SHOOT, 2.0F, 1.0F + 0.2F * (PreservationTurretEntity.this.random.nextFloat() - PreservationTurretEntity.this.random.nextFloat()));
				target.playSound(SpectrumSoundEvents.ENTITY_PRESERVATION_TURRET_SHOOT, 1.0F, 0.5F + 0.2F * (PreservationTurretEntity.this.random.nextFloat() - PreservationTurretEntity.this.random.nextFloat()));

				super.tick();
			}
		}
	}

	private class VibrationsCallback implements VibrationSystem.User {

		private final EntityPositionSource positionSource;

		VibrationsCallback(PreservationTurretEntity turretEntity) {
			this.positionSource = new EntityPositionSource(turretEntity, turretEntity.getEyeHeight());
		}

		@Override
		public int getListenerRadius() {
			return DETECTION_RANGE;
		}

		@Override
		public PositionSource getPositionSource() {
			return positionSource;
		}

		@Override
		public boolean canReceiveVibration(ServerLevel world, BlockPos pos, GameEvent event, GameEvent.Context emitter) {
			return !PreservationTurretEntity.this.isRemoved()
				&& !PreservationTurretEntity.this.isDeadOrDying()
				&& !PreservationTurretEntity.this.isNoAi()
				&& world.getWorldBorder().isWithinBounds(pos)
				&& PreservationTurretEntity.this.level() == world
				&& emitter.sourceEntity() instanceof LivingEntity livingEntity
				&& PreservationTurretEntity.this.isValidTarget(livingEntity);
		}

		@Override
		public void onReceiveVibration(ServerLevel world, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity, @Nullable Entity target, float distance) {
			if (!PreservationTurretEntity.this.isDeadOrDying()
				&& sourceEntity instanceof LivingEntity livingEntity
				&& TARGET_PREDICATE.test(PreservationTurretEntity.this, livingEntity)) {

				PreservationTurretEntity.this.setTarget(livingEntity);
				PreservationTurretEntity.this.setPeekAmount(100);
			}
		}
	}

}
