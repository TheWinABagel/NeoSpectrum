package de.dafuqs.spectrum.entity.entity;

import com.mojang.logging.LogUtils;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.entity.PlayerEntityAccessor;
import de.dafuqs.spectrum.blocks.fluid.SpectrumFluidBlock;
import de.dafuqs.spectrum.data_loaders.EntityFishingDataLoader;
import de.dafuqs.spectrum.data_loaders.EntityFishingDataLoader.EntityFishingEntity;
import de.dafuqs.spectrum.enchantments.ExuberanceEnchantment;
import de.dafuqs.spectrum.enchantments.FoundryEnchantment;
import de.dafuqs.spectrum.items.tools.SpectrumFishingRodItem;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// yeah, this pretty much is a full reimplementation. Sadge
// I wanted to use more of FishingBobberEntity for mod compat,
// but most of FishingRod's methods are either private or are tricky to extend
public abstract class SpectrumFishingBobberEntity extends Projectile {
	
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final EntityDataAccessor<Integer> HOOK_ENTITY_ID = SynchedEntityData.defineId(SpectrumFishingBobberEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> CAUGHT_FISH = SynchedEntityData.defineId(SpectrumFishingBobberEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ABLAZE = SynchedEntityData.defineId(SpectrumFishingBobberEntity.class, EntityDataSerializers.BOOLEAN); // needs to be synced to the client, so it can render on fire
	
	private final RandomSource velocityRandom;
	private boolean caughtFish;
	private int outOfOpenFluidTicks;
	private int removalTimer;
	private int hookCountdown;
	private int waitCountdown;
	private int fishTravelCountdown;
	private float fishAngle;
	private boolean inTheOpen;
	private @Nullable Entity hookedEntity;
	private SpectrumFishingBobberEntity.State state;
	protected final int luckOfTheSeaLevel;
	protected final int lureLevel;
	protected final int exuberanceLevel;
	protected final int bigCatchLevel;
	protected final boolean inventoryInsertion;
	
	public static final ResourceLocation LOOT_IDENTIFIER = SpectrumCommon.locate("gameplay/universal_fishing");
	
	public SpectrumFishingBobberEntity(EntityType<? extends SpectrumFishingBobberEntity> type, Level world, int luckOfTheSeaLevel, int lureLevel, int exuberanceLevel, int bigCatchLevel, boolean inventoryInsertion, boolean ablaze) {
		super(type, world);
		this.velocityRandom = RandomSource.create();
		this.inTheOpen = true;
		this.state = SpectrumFishingBobberEntity.State.FLYING;
		this.noCulling = true;
		this.luckOfTheSeaLevel = Math.max(0, luckOfTheSeaLevel);
		this.lureLevel = Math.max(0, lureLevel);
		this.exuberanceLevel = Math.max(0, exuberanceLevel);
		this.bigCatchLevel = Math.max(0, bigCatchLevel);
		this.inventoryInsertion = inventoryInsertion;
		this.getEntityData().set(ABLAZE, ablaze);
	}
	
	public SpectrumFishingBobberEntity(EntityType<? extends SpectrumFishingBobberEntity> entityType, Level world) {
		this(entityType, world, 0, 0, 0, 0, false, false);
	}
	
	public SpectrumFishingBobberEntity(EntityType<? extends SpectrumFishingBobberEntity> entityType, Player thrower, Level world, int luckOfTheSeaLevel, int lureLevel, int exuberanceLevel, int bigCatchLevel, boolean inventoryInsertion, boolean ablaze) {
		this(entityType, world, luckOfTheSeaLevel, lureLevel, exuberanceLevel, bigCatchLevel, inventoryInsertion, ablaze);
		this.setOwner(thrower);
		float f = thrower.getXRot();
		float g = thrower.getYRot();
		float h = Mth.cos(-g * 0.017453292F - 3.1415927F);
		float i = Mth.sin(-g * 0.017453292F - 3.1415927F);
		float j = -Mth.cos(-f * 0.017453292F);
		float k = Mth.sin(-f * 0.017453292F);
		double d = thrower.getX() - (double) i * 0.3D;
		double e = thrower.getEyeY();
		double l = thrower.getZ() - (double) h * 0.3D;
		this.moveTo(d, e, l, g, f);
		Vec3 vec3d = new Vec3((-i), Mth.clamp(-(k / j), -5.0F, 5.0F), (-h));
		double m = vec3d.length();
		vec3d = vec3d.multiply(0.6D / m + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / m + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / m + 0.5D + this.random.nextGaussian() * 0.0045D);
		this.setDeltaMovement(vec3d);
		this.setYRot((float) (Mth.atan2(vec3d.x, vec3d.z) * 57.2957763671875D));
		this.setXRot((float) (Mth.atan2(vec3d.y, vec3d.horizontalDistance()) * 57.2957763671875D));
		this.yRotO = this.getYRot();
		this.xRotO = this.getXRot();
	}
	
	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return distance < 4096.0;
	}
	
	@Override
	public void lerpTo(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
	}
	
	public boolean isAblaze() {
		return this.getEntityData().get(ABLAZE);
	}
	
	@Override
	public boolean displayFireAnimation() {
		return isAblaze();
	}
	
	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(HOOK_ENTITY_ID, 0);
		this.getEntityData().define(CAUGHT_FISH, false);
		this.getEntityData().define(ABLAZE, false);
	}
	
	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
		if (HOOK_ENTITY_ID.equals(data)) {
			int i = this.getEntityData().get(HOOK_ENTITY_ID);
			this.hookedEntity = i > 0 ? this.level().getEntity(i - 1) : null;
		}
		
		if (CAUGHT_FISH.equals(data)) {
			this.caughtFish = this.getEntityData().get(CAUGHT_FISH);
			if (this.caughtFish) {
				this.setDeltaMovement(this.getDeltaMovement().x, (-0.4F * Mth.nextFloat(this.velocityRandom, 0.6F, 1.0F)), this.getDeltaMovement().z);
			}
		}
		
		super.onSyncedDataUpdated(data);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		this.velocityRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level().getGameTime());
		Player playerEntity = this.getPlayerOwner();
		if (playerEntity == null) {
			this.discard();
		} else if (this.level().isClientSide() || !this.removeIfInvalid(playerEntity)) {
			if (this.onGround()) {
				++this.removalTimer;
				if (this.removalTimer >= 1200) {
					this.discard();
					return;
				}
			} else {
				this.removalTimer = 0;
			}
			
			float f = 0.0F;
			BlockPos blockPos = this.blockPosition();
			FluidState fluidState = this.level().getFluidState(blockPos);
			ItemStack rodStack = getFishingRod(playerEntity);
			boolean canFishInFluid = false;
			if (rodStack.getItem() instanceof SpectrumFishingRodItem spectrumFishingRodItem && spectrumFishingRodItem.canFishIn(fluidState)) {
				canFishInFluid = true;
				f = fluidState.getHeight(this.level(), blockPos);
			}
			
			boolean bl = f > 0.0F;
			if (this.state == State.FLYING) {
				if (this.hookedEntity != null) {
					this.setDeltaMovement(Vec3.ZERO);
					this.state = State.HOOKED_IN_ENTITY;
					onHookedEntity(hookedEntity);
					return;
				}
				
				if (bl) {
					this.setDeltaMovement(this.getDeltaMovement().multiply(0.3D, 0.2D, 0.3D));
					this.state = State.BOBBING;
					return;
				}
				
				this.onHit(ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity));
			} else {
				if (this.state == State.HOOKED_IN_ENTITY) {
					if (this.hookedEntity != null) {
						if (!this.hookedEntity.isRemoved() && this.hookedEntity.level().dimension() == this.level().dimension()) {
							this.setPos(this.hookedEntity.getX(), this.hookedEntity.getY(0.8D), this.hookedEntity.getZ());
							hookedEntityTick(this.hookedEntity);
						} else {
							this.updateHookedEntityId(null);
							this.state = State.FLYING;
						}
					}
					
					return;
				}
				
				if (this.state == State.BOBBING) {
					Vec3 vec3d = this.getDeltaMovement();
					double d = this.getY() + vec3d.y - (double) blockPos.getY() - (double) f;
					if (Math.abs(d) < 0.01D) {
						d += Math.signum(d) * 0.1D;
					}
					
					this.setDeltaMovement(vec3d.x * 0.9D, vec3d.y - d * (double) this.random.nextFloat() * 0.2D, vec3d.z * 0.9D);
					if (this.hookCountdown <= 0 && this.fishTravelCountdown <= 0) {
						this.inTheOpen = true;
					} else {
						this.inTheOpen = this.inTheOpen && this.outOfOpenFluidTicks < 10 && this.isInTheOpen(blockPos);
					}
					
					if (bl) {
						this.outOfOpenFluidTicks = Math.max(0, this.outOfOpenFluidTicks - 1);
						if (this.caughtFish) {
							this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.1D * (double) this.velocityRandom.nextFloat() * (double) this.velocityRandom.nextFloat(), 0.0D));
						}
						
						if (!this.level().isClientSide()) {
							this.tickFishingLogic(blockPos);
						}
					} else {
						this.outOfOpenFluidTicks = Math.min(10, this.outOfOpenFluidTicks + 1);
					}
				}
			}
			
			if (!canFishInFluid) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
			}
			
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.updateRotation();
			if (this.state == State.FLYING && (this.onGround() || this.horizontalCollision)) {
				this.setDeltaMovement(Vec3.ZERO);
			}
			
			
			this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
			this.reapplyPosition();
		}
	}
	
	protected void onHookedEntity(Entity hookedEntity) {
	}
	
	protected void hookedEntityTick(Entity hookedEntity) {
	}
	
	@Override
	protected Entity.MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}
	
	@Override
	public boolean canChangeDimensions() {
		return false;
	}
	
	public ItemStack getFishingRod(Player player) {
		ItemStack itemStack = player.getMainHandItem();
		if (itemStack.getItem() instanceof SpectrumFishingRodItem) {
			return itemStack;
		}
		
		itemStack = player.getOffhandItem();
		if (itemStack.getItem() instanceof SpectrumFishingRodItem) {
			return itemStack;
		}
		return ItemStack.EMPTY;
	}
	
	public boolean removeIfInvalid(Player player) {
		ItemStack rodStack = getFishingRod(player);
		if (!player.isRemoved() && player.isAlive() && !rodStack.isEmpty() && !(this.distanceToSqr(player) > 1024.0D)) {
			return false;
		} else {
			this.discard();
			return true;
		}
	}
	
	@Override
	public boolean canHitEntity(Entity entity) {
		return super.canHitEntity(entity) || entity.isAlive() && entity instanceof ItemEntity;
	}
	
	@Override
	public void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
		if (!this.level().isClientSide()) {
			Entity hookedEntity = entityHitResult.getEntity();
			this.updateHookedEntityId(hookedEntity);
		}
	}
	
	@Override
	public void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		this.setDeltaMovement(this.getDeltaMovement().normalize().scale(blockHitResult.distanceTo(this)));
	}
	
	public void updateHookedEntityId(@Nullable Entity entity) {
		this.hookedEntity = entity;
		this.getEntityData().set(HOOK_ENTITY_ID, entity == null ? 0 : entity.getId() + 1);
	}
	
	public void tickFishingLogic(BlockPos pos) {
		ServerLevel serverWorld = (ServerLevel) this.level();
		int i = 1;
		BlockPos blockPos = pos.above();
		if (this.random.nextFloat() < 0.25F && this.level().isRainingAt(blockPos)) {
			++i;
		}
		
		if (this.random.nextFloat() < 0.5F && !this.level().canSeeSky(blockPos)) {
			--i;
		}
		
		if (this.hookCountdown > 0) {
			--this.hookCountdown;
			if (this.hookCountdown <= 0) {
				this.waitCountdown = 0;
				this.fishTravelCountdown = 0;
				this.getEntityData().set(CAUGHT_FISH, false);
			}
		} else {
			float f;
			float g;
			float h;
			double d;
			double e;
			double j;
			BlockState blockState;
			if (this.fishTravelCountdown > 0) {
				this.fishTravelCountdown -= i;
				
				this.fishAngle += (float) (this.random.nextGaussian() * 4.0D);
				f = this.fishAngle * 0.017453292F;
				g = Mth.sin(f);
				h = Mth.cos(f);
				d = this.getX() + (double) (g * (float) this.fishTravelCountdown * 0.1F);
				e = ((float) Mth.floor(this.getY()) + 1.0F);
				j = this.getZ() + (double) (h * (float) this.fishTravelCountdown * 0.1F);
				blockState = serverWorld.getBlockState(BlockPos.containing(d, e - 1.0D, j));
				
				Tuple<SimpleParticleType, SimpleParticleType> particles = getFluidParticles(blockState);
				if (this.fishTravelCountdown > 0) {
					float k = g * 0.04F;
					float l = h * 0.04F;
					if (particles != null) {
						if (this.random.nextFloat() < 0.15F) {
							serverWorld.sendParticles(particles.getA(), d, e - 0.10000000149011612D, j, 1, g, 0.1D, h, 0.0D);
						}
						serverWorld.sendParticles(particles.getB(), d, e, j, 0, l, 0.01D, (-k), 1.0D);
						serverWorld.sendParticles(particles.getB(), d, e, j, 0, (-l), 0.01D, k, 1.0D);
					}
				} else {
					this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
					if (particles != null) {
						double m = this.getY() + 0.5D;
						serverWorld.sendParticles(particles.getA(), this.getX(), m, this.getZ(), (int) (1.0F + this.getBbWidth() * 20.0F), this.getBbWidth(), 0.0D, this.getBbWidth(), 0.20000000298023224D);
						serverWorld.sendParticles(particles.getB(), this.getX(), m, this.getZ(), (int) (1.0F + this.getBbWidth() * 20.0F), this.getBbWidth(), 0.0D, this.getBbWidth(), 0.20000000298023224D);
					}
					this.hookCountdown = Mth.nextInt(this.random, 20, 40);
					this.getEntityData().set(CAUGHT_FISH, true);
				}
			} else if (this.waitCountdown > 0) {
				this.waitCountdown -= i;
				f = 0.15F;
				if (this.waitCountdown < 20) {
					f += (float) (20 - this.waitCountdown) * 0.05F;
				} else if (this.waitCountdown < 40) {
					f += (float) (40 - this.waitCountdown) * 0.02F;
				} else if (this.waitCountdown < 60) {
					f += (float) (60 - this.waitCountdown) * 0.01F;
				}
				
				if (this.random.nextFloat() < f) {
					g = Mth.nextFloat(this.random, 0.0F, 360.0F) * 0.017453292F;
					h = Mth.nextFloat(this.random, 25.0F, 60.0F);
					d = this.getX() + (double) (Mth.sin(g) * h) * 0.1D;
					e = ((float) Mth.floor(this.getY()) + 1.0F);
					j = this.getZ() + (double) (Mth.cos(g) * h) * 0.1D;
					blockState = serverWorld.getBlockState(BlockPos.containing(d, e - 1.0D, j));
					
					Tuple<SimpleParticleType, SimpleParticleType> particles = getFluidParticles(blockState);
					if (particles != null) {
						serverWorld.sendParticles(particles.getA(), d, e, j, 2 + this.random.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
					}
				}
				
				if (this.waitCountdown <= 0) {
					this.fishAngle = Mth.nextFloat(this.random, 0.0F, 360.0F);
					this.fishTravelCountdown = Mth.nextInt(this.random, 20, 80);
				}
			} else {
				this.waitCountdown = Mth.nextInt(this.random, 100, 600);
				this.waitCountdown -= this.lureLevel * 20 * 5;
				this.waitCountdown = Math.max(1, this.waitCountdown);
			}
		}
	}
	
	@Nullable
	private Tuple<SimpleParticleType, SimpleParticleType> getFluidParticles(BlockState blockState) {
		Tuple<SimpleParticleType, SimpleParticleType> particles = null;
		if (this.level().getBlockState(blockPosition()).getBlock() instanceof SpectrumFluidBlock spectrumFluidBlock) {
			particles = spectrumFluidBlock.getFishingParticles();
		} else if (blockState.is(Blocks.LAVA)) {
			particles = new Tuple<>(ParticleTypes.FLAME, SpectrumParticleTypes.LAVA_FISHING);
		} else if (blockState.is(Blocks.WATER)) {
			particles = new Tuple<>(ParticleTypes.BUBBLE, ParticleTypes.FISHING);
		}
		return particles;
	}
	
	public boolean isInTheOpen(BlockPos pos) {
		PositionType positionType = PositionType.INVALID;
		
		for (int i = -1; i <= 2; ++i) {
			PositionType positionType2 = this.getPositionType(pos.offset(-2, i, -2), pos.offset(2, i, 2));
			switch (positionType2) {
				case INVALID -> {
					return false;
				}
				case ABOVE_FLUID -> {
					if (positionType == PositionType.INVALID) {
						return false;
					}
				}
				case INSIDE_FLUID -> {
					if (positionType == PositionType.ABOVE_FLUID) {
						return false;
					}
				}
			}
			
			positionType = positionType2;
		}
		
		return true;
	}
	
	public PositionType getPositionType(BlockPos start, BlockPos end) {
		return BlockPos.betweenClosedStream(start, end).map(this::getPositionType).reduce((positionType, positionType2) -> positionType == positionType2 ? positionType : PositionType.INVALID).orElse(PositionType.INVALID);
	}
	
	public PositionType getPositionType(BlockPos pos) {
		BlockState blockState = this.level().getBlockState(pos);
		if (!blockState.isAir() && !blockState.is(Blocks.LILY_PAD)) {
			FluidState fluidState = blockState.getFluidState();
			return !fluidState.isEmpty() && fluidState.isSource() && blockState.getCollisionShape(this.level(), pos).isEmpty() ? PositionType.INSIDE_FLUID : PositionType.INVALID;
		} else {
			return PositionType.ABOVE_FLUID;
		}
	}
	
	public boolean isInTheOpen() {
		return this.inTheOpen;
	}
	
	public int use(ItemStack usedItem) {
		Player playerEntity = this.getPlayerOwner();
		if (!this.level().isClientSide() && playerEntity != null && !this.removeIfInvalid(playerEntity)) {
			int i = 0;
			if (this.hookedEntity != null) {
				this.pullHookedEntity(this.hookedEntity);
				SpectrumAdvancementCriteria.FISHING_ROD_HOOKED.trigger((ServerPlayer) playerEntity, usedItem, this, null, Collections.emptyList());
				this.level().broadcastEntityEvent(this, (byte) 31);
				i = this.hookedEntity instanceof ItemEntity ? 3 : 5;
			} else if (this.hookCountdown > 0) {
				if (!tryCatchEntity(usedItem, playerEntity, (ServerLevel) this.level(), this.blockPosition())) {
					catchLoot(usedItem, playerEntity);
				}
				
				i = 1;
			}
			
			if (this.onGround()) {
				i = 2;
			}
			
			this.discard();
			return i;
		} else {
			return 0;
		}
	}
	
	private boolean tryCatchEntity(ItemStack usedItem, Player playerEntity, ServerLevel world, BlockPos blockPos) {
		Optional<EntityFishingEntity> caughtEntityType = EntityFishingDataLoader.tryCatchEntity(world, blockPos, this.bigCatchLevel);
		if (caughtEntityType.isPresent()) {
			EntityType<?> entityType = caughtEntityType.get().entityType();
			Optional<CompoundTag> nbt = caughtEntityType.get().nbt();
			
			CompoundTag entityNbt = null;
			if (nbt.isPresent()) {
				entityNbt = new CompoundTag();
				entityNbt.put("EntityTag", nbt.get());
			}
			
			Entity entity = entityType.spawn(world, entityNbt, null, blockPos, MobSpawnType.TRIGGERED, false, false);
			if (entity != null) {
				double xDif = playerEntity.getX() - this.getX();
				double yDif = playerEntity.getY() - this.getY();
				double zDif = playerEntity.getZ() - this.getZ();
				double velocityMod = 0.15D;
				entity.push(xDif * velocityMod, yDif * velocityMod + Math.sqrt(Math.sqrt(xDif * xDif + yDif * yDif + zDif * zDif)) * 0.08D, zDif * velocityMod);
				
				if (isAblaze()) {
					entity.setSecondsOnFire(4);
				}
				
				if (entity instanceof Mob mobEntity) {
					mobEntity.playAmbientSound();
					mobEntity.spawnAnim();
				}
				SpectrumAdvancementCriteria.FISHING_ROD_HOOKED.trigger((ServerPlayer) playerEntity, usedItem, this, entity, List.of());
				
				return true;
			}
		}
		
		return false;
	}
	
	protected void catchLoot(ItemStack usedItem, Player playerEntity) {
		LootParams lootContextParameterSet = new LootParams.Builder((ServerLevel) playerEntity.level())
				.withParameter(LootContextParams.ORIGIN, this.position())
				.withParameter(LootContextParams.TOOL, usedItem)
				.withParameter(LootContextParams.THIS_ENTITY, this)
				.withLuck((float) this.luckOfTheSeaLevel + playerEntity.getLuck())
				.create(LootContextParamSets.FISHING);
		
		LootTable lootTable = this.level().getServer().getLootData().getLootTable(LOOT_IDENTIFIER);
		List<ItemStack> list = lootTable.getRandomItems(lootContextParameterSet);
		SpectrumAdvancementCriteria.FISHING_ROD_HOOKED.trigger((ServerPlayer) playerEntity, usedItem, this, null, list);
		
		for (ItemStack itemStack : list) {
			if (itemStack.is(ItemTags.FISHES)) {
				playerEntity.awardStat(Stats.FISH_CAUGHT, 1);
			}
		}
		
		if (isAblaze()) {
			list = FoundryEnchantment.applyFoundry(this.level(), list);
		}
		
		float exuberanceMod = ExuberanceEnchantment.getExuberanceMod(this.exuberanceLevel);
		for (ItemStack itemStack : list) {
			int experienceAmount = this.random.nextInt((int) (6 * exuberanceMod) + 1);
			
			if (this.inventoryInsertion) {
				playerEntity.getInventory().placeItemBackInInventory(itemStack);
				playerEntity.giveExperiencePoints(experienceAmount);
				
				playerEntity.level().playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
						SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS,
						0.2F, ((playerEntity.getRandom().nextFloat() - playerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
			} else {
				// fireproof item, so it does not burn when fishing in lava
				ItemEntity itemEntity = new FireproofItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemStack);
				double d = playerEntity.getX() - this.getX();
				double e = playerEntity.getY() - this.getY();
				double f = playerEntity.getZ() - this.getZ();
				double g = 0.1D;
				itemEntity.setDeltaMovement(d * g, e * g + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08D, f * g);
				this.level().addFreshEntity(itemEntity);
				
				// experience
				if (experienceAmount > 0) {
					playerEntity.level().addFreshEntity(new ExperienceOrb(playerEntity.level(), playerEntity.getX(), playerEntity.getY() + 0.5D, playerEntity.getZ() + 0.5D, experienceAmount));
				}
			}
		}
	}
	
	@Override
	public void handleEntityEvent(byte status) {
		if (status == 31 && this.level().isClientSide() && this.hookedEntity instanceof Player player && player.isLocalPlayer()) {
			this.pullHookedEntity(player);
		}
		super.handleEntityEvent(status);
	}
	
	public void pullHookedEntity(Entity entity) {
		Entity owner = this.getOwner();
		if (owner != null) {
			Vec3 vec3d = (new Vec3(owner.getX() - this.getX(), owner.getY() - this.getY(), owner.getZ() - this.getZ())).scale(0.1D);
			entity.setDeltaMovement(entity.getDeltaMovement().add(vec3d));
		}
	}
	
	@Override
	public void remove(RemovalReason reason) {
		this.setPlayerFishHook(null);
		super.remove(reason);
	}
	
	@Override
	public void onClientRemoval() {
		this.setPlayerFishHook(null);
	}
	
	@Override
	public void setOwner(@Nullable Entity entity) {
		super.setOwner(entity);
		this.setPlayerFishHook(this);
	}
	
	public void setPlayerFishHook(@Nullable SpectrumFishingBobberEntity fishingBobber) {
		Player playerEntity = this.getPlayerOwner();
		if (playerEntity != null) {
			((PlayerEntityAccessor) playerEntity).setSpectrumBobber(fishingBobber);
		}
	}
	
	@Nullable
	public Player getPlayerOwner() {
		Entity entity = this.getOwner();
		return entity instanceof Player player ? player : null;
	}
	
	@Nullable
	public Entity getHookedEntity() {
		return this.hookedEntity;
	}
	
	@Override
	public void recreateFromPacket(ClientboundAddEntityPacket packet) {
		super.recreateFromPacket(packet);
		if (this.getPlayerOwner() == null) {
			int entityData = packet.getData();
			LOGGER.error("Failed to recreate fishing hook on client. {} (id: {}) is not a valid owner.", this.level().getEntity(entityData), entityData);
			this.kill();
		}
	}
	
	protected enum State {
		FLYING,
		HOOKED_IN_ENTITY,
		BOBBING
	}
	
	protected enum PositionType {
		ABOVE_FLUID,
		INSIDE_FLUID,
		INVALID
	}
	
}
