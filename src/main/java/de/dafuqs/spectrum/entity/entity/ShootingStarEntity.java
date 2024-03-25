package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.blocks.shooting_star.ShootingStar;
import de.dafuqs.spectrum.blocks.shooting_star.ShootingStarItem;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.effect.DynamicParticleEffectAlwaysShow;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.BlockUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ShootingStarEntity extends Entity {
	
	private static final EntityDataAccessor<Integer> SHOOTING_STAR_TYPE = SynchedEntityData.defineId(ShootingStarEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> PLAYER_PLACED = SynchedEntityData.defineId(ShootingStarEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> HARDENED = SynchedEntityData.defineId(ShootingStarEntity.class, EntityDataSerializers.BOOLEAN);
	
	protected final float hoverHeight;
	protected int tickCount;
	protected int availableHits;
	protected int lastCollisionCount;
	
	public ShootingStarEntity(EntityType<? extends ShootingStarEntity> entityType, Level world) {
		super(entityType, world);
		this.hoverHeight = (float) (Math.random() * Math.PI * 2.0D);
		this.availableHits = 5 + world.random.nextInt(3);
		this.lastCollisionCount = 0;
	}
	
	public ShootingStarEntity(Level world, double x, double y, double z) {
		this(SpectrumEntityTypes.SHOOTING_STAR, world);
		this.setPos(x, y, z);
		this.setYRot(this.random.nextFloat() * 360.0F);
		this.setShootingStarType(ShootingStar.Type.COLORFUL, false, false);
		this.lastCollisionCount = 0;
	}
	
	public ShootingStarEntity(Level world) {
		this(world, 0, 0, 0);
	}
	
	@Environment(EnvType.CLIENT)
	private ShootingStarEntity(@NotNull ShootingStarEntity entity) {
		super(entity.getType(), entity.level());
		this.setShootingStarType(entity.getShootingStarType(), false, false);
		this.copyPosition(entity);
		this.availableHits = entity.availableHits;
		this.tickCount = entity.tickCount;
		this.hoverHeight = entity.hoverHeight;
		this.lastCollisionCount = entity.lastCollisionCount;
	}
	
	public static boolean canCollide(Entity entity, @NotNull Entity other) {
		return (other.canBeCollidedWith() || other.isPushable()) && !entity.isPassengerOfSameVehicle(other);
	}
	
	public static void playHitParticles(Level world, double x, double y, double z, ShootingStar.Type type, int amount) {
		RandomSource random = world.random;
		// Everything in this lambda is running on the render thread
		
		for (int i = 0; i < amount; i++) {
			float randomScale = 0.5F + random.nextFloat();
			int randomLifetime = 10 + random.nextInt(20);
			
			ParticleOptions particleEffect = new DynamicParticleEffectAlwaysShow(0.98F, type.getRandomParticleColor(random), randomScale, randomLifetime, false, true);
			world.addParticle(particleEffect, x, y, z, 0.35 - random.nextFloat() * 0.7, random.nextFloat() * 0.7, 0.35 - random.nextFloat() * 0.7);
		}
	}
	
	@Override
	public boolean canCollideWith(Entity other) {
		return canCollide(this, other);
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	@Override
	public boolean isPickable() {
		return !this.isRemoved();
	}
	
	@Override
	public boolean isPushable() {
		return true;
	}
	
	@Override
	protected Vec3 getRelativePortalPosition(Direction.Axis portalAxis, BlockUtil.FoundRectangle portalRect) {
		return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(portalAxis, portalRect));
	}
	
	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(SHOOTING_STAR_TYPE, ShootingStar.Type.COLORFUL.ordinal());
		this.getEntityData().define(PLAYER_PLACED, false);
		this.getEntityData().define(HARDENED, false);
	}
	
	@Override
	public void tick() {
		super.tick();
		this.handleNetherPortal();
		
		boolean wasOnGround = this.onGround();
		double previousXVelocity = this.getDeltaMovement().x();
		double previousYVelocity = this.getDeltaMovement().y();
		double previousZVelocity = this.getDeltaMovement().z();
		
		Level world = this.level();
		if (world.isClientSide) {
			this.noPhysics = false;
		} else {
			this.noPhysics = !this.level().noCollision(this, this.getBoundingBox().deflate(1.0E-7D));
			if (this.noPhysics) {
				this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
			}
		}
		
		if (!this.isNoGravity()) {
			double d = this.isInWater() ? -0.005D : -0.04D;
			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, d, 0.0D));
			if (!this.onGround()) {
				this.setDeltaMovement(this.getDeltaMovement().scale(0.95D));
			}
		}
		
		this.move(MoverType.SELF, this.getDeltaMovement());
		
		var collidingEntities = this.level().getEntities(this, getBoundingBox().inflate(0.25, 0.334, 0.25));
		collidingEntities = collidingEntities.stream().filter(entity -> !(entity instanceof ShootingStarEntity) && (entity.isPushable())).collect(Collectors.toList());
		
		// make it bounce back
		boolean spawnLoot = false;
		boolean playerPlaced = this.entityData.get(PLAYER_PLACED);
		boolean hardened = this.entityData.get(HARDENED);
		if (this.onGround() && !wasOnGround) {
			this.push(0, -previousYVelocity * 0.9, 0);
			collidingEntities.forEach(entity -> entity.move(MoverType.SHULKER_BOX, this.getDeltaMovement().multiply(0, 1, 0)));
		}
		if (Math.signum(this.getDeltaMovement().x) != Math.signum(previousXVelocity)) {
			this.push(-previousXVelocity * 0.6, 0, 0);
			if (!hardened && Math.abs(previousXVelocity) > 0.5) {
				spawnLoot = true;
			}
		}
		if (Math.signum(this.getDeltaMovement().z) != Math.signum(previousZVelocity)) {
			this.push(0, 0, -previousZVelocity * 0.6);
			if (!hardened && !spawnLoot && Math.abs(previousZVelocity) > 0.5) {
				spawnLoot = true;
			}
		}
		
		collidingEntities.forEach(entity -> {
			if (entity.getY() >= this.getBoundingBox().maxY) {
				entity.fallDistance = 0F;
				if (this.isPickable()) {
					entity.setPos(entity.position().x, this.getBoundingBox().maxY, entity.position().z);
				}
				entity.move(MoverType.SHULKER_BOX, this.getDeltaMovement());
				entity.setOnGround(true);
			}
		});
		
		if (world.isClientSide) {
			if (!playerPlaced && !hardened) {
				if (this.onGround()) {
					if (world.random.nextInt(10) == 0) {
						playGroundParticles();
					}
				} else {
					if (world.random.nextBoolean()) {
						playFallingParticles();
					}
				}
			}
		} else {
			// despawning
			this.tickCount++;
			if (this.tickCount > 6000 && !playerPlaced && !hardened) {
				this.discard();
			}
			
			this.checkInsideBlocks();
			
			if (spawnLoot) {
				this.lastCollisionCount++;
				if (this.lastCollisionCount > 8) {
					// if the block did collide a lot (maybe bugged or jammed?): break it and drop as item
					this.availableHits--;
					if (this.availableHits > 0) {
						ItemStack shootingStarStack = ShootingStarItem.getWithRemainingHits((ShootingStarItem) this.asItem(), this.availableHits, this.entityData.get(HARDENED));
						ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), shootingStarStack);
						this.level().addFreshEntity(itemEntity);
					} else {
						ItemStack starFragmentStack = SpectrumItems.STAR_FRAGMENT.getDefaultInstance();
						ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), starFragmentStack);
						this.level().addFreshEntity(itemEntity);
					}
					this.discard();
				} else {
					// spawn loot
					List<ItemStack> loot = getLoot((ServerLevel) this.level(), ShootingStar.Type.BOUNCE_LOOT_TABLE);
					for (ItemStack itemStack : loot) {
						ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemStack);
						this.level().addFreshEntity(itemEntity);
					}
					
					// do effects
					SpectrumS2CPacketSender.sendPlayShootingStarParticles(this);
					this.level().playSound(null, this.blockPosition(), SpectrumSoundEvents.SHOOTING_STAR_CRACKER, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
			}
			
			if (!hardened && !wasOnGround && this.onGround() && previousYVelocity < -0.5) { // hitting the ground after a long fall
				SpectrumS2CPacketSender.playParticleWithExactVelocity((ServerLevel) this.level(), position(), ParticleTypes.EXPLOSION, 1, Vec3.ZERO);
				if (!spawnLoot) {
					SpectrumS2CPacketSender.sendPlayShootingStarParticles(this);
					this.level().playSound(null, this.blockPosition(), SpectrumSoundEvents.SHOOTING_STAR_CRACKER, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
			}
			
			// push other entities away
			List<Entity> otherEntities = this.level().getEntities(this, this.getBoundingBox().inflate(0.2D, -0.01D, 0.2D), EntitySelector.pushableBy(this));
			if (!otherEntities.isEmpty()) {
				for (Entity d : otherEntities) {
					this.push(d);
				}
			}
			
			this.updateInWaterStateAndDoFluidPushing();
		}
	}
	
	@Override
	public void playerTouch(Player player) {
		// if the shooting star is still falling from the sky, and it hits a player:
		// give the player the star, some damage and grant an advancement
		if (!this.level().isClientSide() && !this.entityData.get(HARDENED) && !this.onGround() && this.getDeltaMovement().y() < -0.5) {
			this.level().playSound(null, this.blockPosition().getX(), this.blockPosition().getY(), this.blockPosition().getZ(), SpectrumSoundEvents.SHOOTING_STAR_CRACKER, SoundSource.PLAYERS, 1.5F + random.nextFloat() * 0.4F, 0.8F + random.nextFloat() * 0.4F);
			SpectrumS2CPacketSender.sendPlayShootingStarParticles(this);
			player.hurt(SpectrumDamageTypes.shootingStar(this.level()), 18);
			
			ItemStack itemStack = this.getShootingStarType().getBlock().asItem().getDefaultInstance();
			int i = itemStack.getCount();
			player.getInventory().placeItemBackInInventory(itemStack);
			
			Support.grantAdvancementCriterion((ServerPlayer) player, "catch_shooting_star", "catch");
			player.awardStat(Stats.ITEM_PICKED_UP.get(itemStack.getItem()), i);
			
			this.discard();
		}
	}
	
	@Override
	public void push(Entity entity) {
		if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
			super.push(entity);
		}
	}
	
	public Item asItem() {
		return this.getShootingStarType().getBlock().asItem();
	}
	
	public void playGroundParticles() {
		float randomScale = 0.5F + random.nextFloat();
		int randomLifetime = 30 + random.nextInt(20);
		
		ParticleOptions particleEffect = new DynamicParticleEffectAlwaysShow(0.05F, getShootingStarType().getRandomParticleColor(random), randomScale, randomLifetime, false, true);
		this.level().addParticle(particleEffect, this.getX(), this.getEyeY(), this.getZ(), 0.1 - random.nextFloat() * 0.2, 0.4 + random.nextFloat() * 0.2, 0.1 - random.nextFloat() * 0.2);
	}
	
	public void playFallingParticles() {
		float randomScale = this.random.nextFloat() * 0.4F + 0.7F;
		ParticleOptions particleEffect = new DynamicParticleEffectAlwaysShow((float) ((random.nextDouble() - 0.5F) * 0.05F - 0.125F), getShootingStarType().getRandomParticleColor(random), randomScale, 120, false, true);
		this.level().addParticle(particleEffect, this.getX(), this.getEyeY(), this.getZ(), 0.2 - random.nextFloat() * 0.4, 0.1, 0.2 - random.nextFloat() * 0.4);
	}
	
	public void playHitParticles() {
		playHitParticles(this.level(), this.getX(), this.getEyeY(), this.getZ(), this.getShootingStarType(), 25);
	}
	
	public void doPlayerHitEffectsAndLoot(ServerLevel serverWorld, ServerPlayer serverPlayerEntity) {
		// Spawn loot
		ResourceLocation lootTableId = ShootingStar.Type.getLootTableIdentifier(entityData.get(SHOOTING_STAR_TYPE));
		List<ItemStack> loot = getLoot(serverWorld, serverPlayerEntity, lootTableId);
		
		for (ItemStack itemStack : loot) {
			ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemStack);
			this.level().addFreshEntity(itemEntity);
		}
		
		// spawn particles
		SpectrumS2CPacketSender.sendPlayShootingStarParticles(this);
		this.level().playSound(null, this.blockPosition().getX(), this.blockPosition().getY(), this.blockPosition().getZ(), SpectrumSoundEvents.SHOOTING_STAR_CRACKER, SoundSource.PLAYERS, 1.5F + random.nextFloat() * 0.4F, 0.8F + random.nextFloat() * 0.4F);
	}
	
	public List<ItemStack> getLoot(ServerLevel serverWorld, ServerPlayer serverPlayerEntity, ResourceLocation lootTableId) {
		LootTable lootTable = serverWorld.getServer().getLootData().getLootTable(lootTableId);
		return lootTable.getRandomItems(new LootParams.Builder(serverWorld)
				.withParameter(LootContextParams.THIS_ENTITY, this)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.blockPosition()))
				.withParameter(LootContextParams.DAMAGE_SOURCE, serverPlayerEntity.level().damageSources().playerAttack(serverPlayerEntity))
				.withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, serverPlayerEntity)
				.create(LootContextParamSets.ENTITY));
	}
	
	public List<ItemStack> getLoot(ServerLevel serverWorld, ResourceLocation lootTableId) {
		LootTable lootTable = serverWorld.getServer().getLootData().getLootTable(lootTableId);
		return lootTable.getRandomItems(new LootParams.Builder(serverWorld)
				.withParameter(LootContextParams.THIS_ENTITY, this)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.blockPosition()))
				.withParameter(LootContextParams.DAMAGE_SOURCE, serverWorld.damageSources().generic())
				.create(LootContextParamSets.ENTITY));
	}
	
	@Override
	public Component getName() {
		Component text = this.getCustomName();
		return (text != null ? text : asItem().getDescription());
	}
	
	@Override
	public boolean skipAttackInteraction(Entity attacker) {
		if (!this.isRemoved()) {
			if (!this.level().isClientSide()) {
				if (!this.entityData.get(HARDENED)) {
					this.tickCount = 1; // prevent it from despawning, once interacted
					
					this.availableHits--;
					if (this.level() instanceof ServerLevel serverWorld && attacker instanceof ServerPlayer serverPlayerEntity) {
						doPlayerHitEffectsAndLoot(serverWorld, serverPlayerEntity);
						this.lastCollisionCount = 0;
					}
					
					if (this.availableHits <= 0) {
                        SpectrumS2CPacketSender.playParticleWithExactVelocity((ServerLevel) this.level(), this.position(), ParticleTypes.EXPLOSION, 1, Vec3.ZERO);
						
						ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), SpectrumItems.STAR_FRAGMENT.getDefaultInstance());
						itemEntity.push(0, 0.15, 0);
						this.level().addFreshEntity(itemEntity);
						this.discard();
						
						return true;
					}
				}
				this.gameEvent(GameEvent.ENTITY_DAMAGE, attacker);
			}
			
			double attackerOffsetX = this.getX() - attacker.getX();
			double attackerOffsetZ = this.getZ() - attacker.getZ();
			double mod = Math.max(attackerOffsetX, attackerOffsetZ);
			this.push((attackerOffsetX / mod) * 0.75, 0.25, (attackerOffsetZ / mod) * 0.75);
			
			var collidingEntities = this.level().getEntities(this, getBoundingBox().inflate(0.25, 0.334, 0.25));
			collidingEntities = collidingEntities.stream().filter(entity -> !(entity instanceof ShootingStarEntity)).collect(Collectors.toList());
			collidingEntities.forEach(entity -> {
				if (entity.getY() >= this.getBoundingBox().maxY) {
					entity.fallDistance = 0F;
					if (this.isPickable()) {
						entity.setPos(entity.position().x, this.getBoundingBox().maxY, entity.position().z);
					}
					entity.move(MoverType.SHULKER_BOX, this.getDeltaMovement());
					entity.setOnGround(true);
				}
			});
			
			this.markHurt();
		}
		
		return false;
	}
	
	public void setAvailableHits(int availableHits) {
		this.availableHits = availableHits;
	}
	
	@Override
	public boolean isInvulnerableTo(@NotNull DamageSource damageSource) {
		if (damageSource.is(DamageTypes.FALLING_ANVIL) || damageSource.is(SpectrumDamageTypes.FLOATBLOCK)) {
			return false;
		} else {
			return damageSource.is(DamageTypeTags.IS_FIRE) || super.isInvulnerableTo(damageSource);
		}
	}
	
	@Override
	public boolean hurt(DamageSource damageSource, float amount) {
		if (amount > 5 && (damageSource.is(DamageTypes.FALLING_ANVIL) || damageSource.is(SpectrumDamageTypes.FLOATBLOCK))) {
			this.playHitParticles();
			
			ItemStack starFragmentStack = SpectrumItems.STAR_FRAGMENT.getDefaultInstance();
			starFragmentStack.setCount(2);
			ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), starFragmentStack);
			this.level().addFreshEntity(itemEntity);
			this.discard();
			return true;
		}
		this.markHurt();
		return false;
	}
	
	public ShootingStar.Type getShootingStarType() {
		return ShootingStar.Type.getType(this.getEntityData().get(SHOOTING_STAR_TYPE));
	}
	
	public void setShootingStarType(@NotNull ShootingStar.Type type, boolean playerPlaced, boolean hardened) {
		this.getEntityData().set(SHOOTING_STAR_TYPE, type.ordinal());
		this.getEntityData().set(PLAYER_PLACED, playerPlaced);
		this.getEntityData().set(HARDENED, hardened);
	}
	
	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag tag) {
		tag.putShort("Age", (short) this.tickCount);
		tag.putString("Type", this.getShootingStarType().getName());
		tag.putInt("LastCollisionCount", this.lastCollisionCount);
		tag.putBoolean("PlayerPlaced", this.entityData.get(PLAYER_PLACED));
		tag.putBoolean("Hardened", this.entityData.get(HARDENED));
	}
	
	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag tag) {
		this.tickCount = tag.getShort("Age");
		if (tag.contains("LastCollisionCount", Tag.TAG_ANY_NUMERIC)) {
			this.lastCollisionCount = tag.getInt("LastCollisionCount");
		}
		
		boolean playerPlaced = false;
		if (tag.contains("PlayerPlaced")) {
			playerPlaced = tag.getBoolean("PlayerPlaced");
		}
		
		boolean hardened = false;
		if (tag.contains("Hardened")) {
			hardened = tag.getBoolean("Hardened");
		}
		
		if (tag.contains("Type", 8)) {
			this.setShootingStarType(ShootingStar.Type.getType(tag.getString("Type")), playerPlaced, hardened);
		} else {
			this.discard();
		}
	}
	
	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		if (!this.level().isClientSide() && player.isShiftKeyDown()) {
			this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
			player.getInventory().placeItemBackInInventory(ShootingStarItem.getWithRemainingHits((ShootingStarItem) this.asItem(), this.availableHits, this.entityData.get(HARDENED)));
			this.discard();
			return InteractionResult.CONSUME;
		} else {
			return InteractionResult.PASS;
		}
	}
	
	@Override
	public ItemStack getPickResult() {
		return ShootingStarItem.getWithRemainingHits((ShootingStarItem) this.asItem(), this.availableHits, this.entityData.get(HARDENED));
	}
	
	@Environment(EnvType.CLIENT)
	public int getAge() {
		return this.tickCount;
	}
	
	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
	
	@Override
	public SoundSource getSoundSource() {
		return SoundSource.AMBIENT;
	}
	
}
