package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.blocks.gravity.FloatBlock;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.recipe.anvil_crushing.AnvilCrusher;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Predicate;

/**
 * An entity that resembles a block.
 */
public class FloatBlockEntity extends Entity {
	
	private static final float MAX_DAMAGE = 8.0F;
	private static final float DAMAGE_PER_FALLEN_BLOCK = 0.5F;
	
	private static final EntityDataAccessor<BlockPos> ORIGIN = SynchedEntityData.defineId(FloatBlockEntity.class, EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<Long> LAUNCH_TIME = SynchedEntityData.defineId(FloatBlockEntity.class, EntityDataSerializers.LONG);
	private static final EntityDataAccessor<Float> GRAVITY_MODIFIER = SynchedEntityData.defineId(FloatBlockEntity.class, EntityDataSerializers.FLOAT);
	
	public int moveTime;
	public boolean dropItem = true;
	protected CompoundTag blockEntityData;
	protected BlockState blockState = Blocks.STONE.defaultBlockState();
	protected boolean canSetBlock = true;
	protected boolean collides;
	
	public FloatBlockEntity(EntityType<? extends FloatBlockEntity> entityType, Level world) {
		super(entityType, world);
		this.moveTime = 0;
	}
	
	public FloatBlockEntity(EntityType<? extends FloatBlockEntity> entityType, Level world, double x, double y, double z, BlockState blockState) {
		this(entityType, world);
		this.blockState = blockState;
		this.blocksBuilding = true;
		this.setPos(x, y, z);
		this.setDeltaMovement(Vec3.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setOrigin(BlockPos.containing(this.position()));
		this.setLaunchTime(level().getGameTime());
		
		if (blockState.getBlock() instanceof FloatBlock floatBlock) {
			setGravity(floatBlock.getGravityMod());
		}
	}
	
	public FloatBlockEntity(Level world, BlockPos pos, BlockState blockState) {
		this(SpectrumEntityTypes.FLOAT_BLOCK, world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, blockState);
	}
	
	/**
	 * Calculates the bounding box based on the blockstate's collision shape.
	 * If the blockstate doesn't have collision, this method turns collision
	 * off for this entity and sets the bounding box to the outline shape instead.
	 * Note: Complex bounding boxes are not supported. These are all rectangular prisms.
	 *
	 * @return The bounding box of this entity
	 */
	@Override
	protected AABB makeBoundingBox() {
		if (this.entityData == null || this.blockState == null) {
			return super.makeBoundingBox();
		}
		BlockPos origin = this.entityData.get(ORIGIN);
		VoxelShape shape = this.blockState.getCollisionShape(level(), origin);
		if (shape.isEmpty()) {
			this.collides = false;
			shape = this.blockState.getShape(level(), origin);
			if (shape.isEmpty()) {
				return super.makeBoundingBox();
			}
		} else {
			this.collides = true;
		}
		AABB box = shape.bounds();
		return box.move(position().subtract(new Vec3(0.5, 0, 0.5)));
	}
	
	
	@Override
	public void tick() {
		if (this.getBlockState().isAir()) {
			this.discard();
			return;
		}
		
		// Destroy the block in the world that this is spawned from
		// If no block exists, remove this entity
		if (this.moveTime++ == 0) {
			BlockPos blockPos = this.blockPosition();
			Block block = this.blockState.getBlock();
			if (this.level().getBlockState(blockPos).is(block)) {
				this.level().removeBlock(blockPos, false);
			}
		}
		
		if (!this.isNoGravity()) {
			this.moveDist = (float) this.position().y() - this.getOrigin().getY();
			long launchTime = level().getGameTime() - getLaunchTime();
			double additionalYVelocity = launchTime > 100 ? this.getGravity() / 10 : Math.min(Math.sin((Math.PI * launchTime) / 100D), 1) * (this.getGravity() / 10);
			this.push(0.0D, additionalYVelocity, 0.0D);
			this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
			
			// recalculate fall damage
			if (!level().isClientSide) {
				this.dealDamage();
			}
		}
		
		this.move(MoverType.SELF, this.getDeltaMovement());
		this.moveEntities();
		
		if (!this.level().isClientSide) {
			if (!this.verticalCollision || this.onGround()) {
				if (this.tickCount > 100 && this.level().isOutsideBuildHeight(this.blockPosition())) {
					this.breakApart();
					this.discard();
				}
			} else {
				trySetBlock();
			}
		}
	}
	
	@Override
	public void move(MoverType movementType, Vec3 movement) {
		super.move(movementType, movement);
		
		if (movementType != MoverType.SELF) {
			this.setDeltaMovement(movement);
		}
	}
	
	@Override
	public boolean isPickable() {
		return !this.isRemoved();
	}
	
	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		if (player.isShiftKeyDown()) {
			if (this.level().isClientSide()) {
				return InteractionResult.SUCCESS;
			} else {
				Item item = this.blockState.getBlock().asItem();
				if (item != null) {
					player.getInventory().placeItemBackInInventory(item.getDefaultInstance());
				}
				this.discard();
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public ItemStack getPickResult() {
		return this.blockState.getBlock().asItem().getDefaultInstance();
	}
	
	/**
	 * Take actions on entities on "collision".
	 * By default, it replicates the blockstate's behavior on collision.
	 */
	public void onEntityCollision(Entity entity) {
		if (!(entity instanceof FloatBlockEntity)) {
			this.blockState.entityInside(level(), this.blockPosition(), entity);
		}
	}
	
	public void dealDamage() {
		int traveledDistance = Mth.ceil(this.fallDistance - 1.0F);
		if (traveledDistance > 0) {
			int damage = (int) Math.min(Mth.floor(traveledDistance * DAMAGE_PER_FALLEN_BLOCK), MAX_DAMAGE);
			if (damage > 0) {
				// since the players position is tracked at its head and item entities are laying directly on the ground we have to use a relatively big bounding box here
				Predicate<Entity> predicate = EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(entity -> entity.isAlive() && (entity instanceof LivingEntity || entity instanceof ItemEntity));
				this.level().getEntities(this, this.getBoundingBox(), predicate).forEach((entity) -> {
					if (entity instanceof ItemEntity itemEntity) {
						AnvilCrusher.crush(itemEntity, damage);
					} else {
						entity.hurt(SpectrumDamageTypes.floatblock(entity.level()), damage);
					}
				});
			}
		}
	}
	
	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.put("BlockState", NbtUtils.writeBlockState(this.blockState));
		compound.putInt("Time", this.moveTime);
		compound.putBoolean("DropItem", this.dropItem);
		if (this.blockEntityData != null) {
			compound.put("BlockEntityData", this.blockEntityData);
		}
		compound.putFloat("GravityModifier", getGravity());
	}
	
	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		this.blockState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compound.getCompound("BlockState"));
		this.moveTime = compound.getInt("Time");
		if (compound.contains("DropItem", 99)) this.dropItem = compound.getBoolean("DropItem");
		if (compound.contains("BlockEntityData", 10)) this.blockEntityData = compound.getCompound("BlockEntityData");
		if (this.blockState.isAir()) this.blockState = Blocks.STONE.defaultBlockState();
		if (compound.contains("GravityModifier", Tag.TAG_FLOAT))
			setGravity(compound.getFloat("GravityModifier"));
	}
	
	@Override
	public boolean displayFireAnimation() {
		return false;
	}
	
	@Override
	public void fillCrashReportCategory(CrashReportCategory section) {
		super.fillCrashReportCategory(section);
		section.setDetail("Imitating BlockState", this.blockState.toString());
	}
	
	public BlockState getBlockState() {
		return this.blockState;
	}
	
	public void trySetBlock() {
		if (!this.dropItem) {
			boolean canBeReplaced = this.blockState.canBeReplaced(new DirectionalPlaceContext(this.level(), this.blockPosition(), Direction.UP, ItemStack.EMPTY, Direction.DOWN));
			
			BlockState upperState = this.level().getBlockState(this.blockPosition().above());
			boolean isAboveFree = upperState.isAir() || upperState.is(BlockTags.FIRE) || upperState.liquid() || upperState.canBeReplaced();
			
			boolean canBlockSurvive = this.getBlockState().canSurvive(this.level(), this.blockPosition()) && !isAboveFree;
			if (canBeReplaced && canBlockSurvive) {
				if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level().getFluidState(this.blockPosition()).getType() == Fluids.WATER) {
					this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED, true);
				}
				
				if (this.level().setBlock(this.blockPosition(), this.blockState, Block.UPDATE_ALL)) {
					this.discard();
					if (this.blockEntityData != null && this.blockState.hasBlockEntity()) {
						BlockEntity blockEntity = this.level().getBlockEntity(this.blockPosition());
						if (blockEntity != null) {
							CompoundTag compoundTag = blockEntity.saveWithoutMetadata();
							for (String keyName : this.blockEntityData.getAllKeys()) {
								Tag tag = this.blockEntityData.get(keyName);
								if (tag != null && !"x".equals(keyName) && !"y".equals(keyName) && !"z".equals(keyName)) {
									compoundTag.put(keyName, tag.copy());
								}
							}
							
							blockEntity.load(compoundTag);
							blockEntity.setChanged();
						}
					}
					// Stop entities from clipping through the block when it's set
					this.moveEntities();
					
				} else if (this.blockState.requiresCorrectToolForDrops() && this.dropItem && this.level().getGameRules().getRule(GameRules.RULE_DOENTITYDROPS).get()) {
					this.breakApart();
				}
			} else {
				this.breakApart();
			}
			
			this.moveEntities();
		}
		
		BlockPos blockPos = this.blockPosition();
		BlockState blockState = this.level().getBlockState(blockPos);
		boolean canReplace = blockState.canBeReplaced(new DirectionalPlaceContext(this.level(), blockPos, Direction.UP, ItemStack.EMPTY, Direction.DOWN));
		boolean canPlace = this.blockState.canSurvive(this.level(), blockPos);
		if (!this.canSetBlock || !canPlace || !canReplace) {
			return;
		}
		
		if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level().getFluidState(blockPos).getType() == Fluids.WATER) {
			this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED, true);
		}
		
		if (this.level().setBlock(blockPos, this.blockState, Block.UPDATE_ALL)) {
			this.discard();
			if (this.blockEntityData != null && this.blockState.hasBlockEntity()) {
				BlockEntity blockEntity = this.level().getBlockEntity(blockPos);
				if (blockEntity != null) {
					CompoundTag compoundTag = blockEntity.saveWithoutMetadata();
					for (String keyName : this.blockEntityData.getAllKeys()) {
						Tag tag = this.blockEntityData.get(keyName);
						if (tag != null && !"x".equals(keyName) && !"y".equals(keyName) && !"z".equals(keyName)) {
							compoundTag.put(keyName, tag.copy());
						}
					}
					
					blockEntity.load(compoundTag);
					blockEntity.setChanged();
				}
			}
		}
	}
	
	public void moveEntities() {
		if (FallingBlock.isFree(this.blockState)) {
			return;
		}
		
		Level world = this.level();
		for (Entity entity : world.getEntities(this, getBoundingBox().move(0, 1.0, 0).move(this.xo - this.getX(), this.yo - this.getY(), this.zo - this.getZ()).inflate(0.1))) {
			if (entity instanceof FloatBlockEntity other && isPaltaeriaStratineCollision(other)) {
				world.explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, Level.ExplosionInteraction.NONE);
				
				ItemStack collisionStack = SpectrumBlocks.HOVER_BLOCK.asItem().getDefaultInstance();
				ItemEntity itemEntity = new ItemEntity(world, this.getX(), this.getY(), this.getZ(), collisionStack);
				itemEntity.push(0.1 - world.random.nextFloat() * 0.2, 0.1 - world.random.nextFloat() * 0.2, 0.1 - world.random.nextFloat() * 0.2);
				world.addFreshEntity(itemEntity);
				
				this.discard();
				other.discard();
			} else if (entity.isPushable() && entity.getPistonPushReaction() != PushReaction.IGNORE) {
				if(entity.position().y() < this.position().y() + 1) {
					entity.setPosRaw(entity.position().x(), this.position().y() + 1, entity.position().z());
				}
				if (entity.getDeltaMovement().lengthSqr() < this.getDeltaMovement().lengthSqr()) {
					entity.setDeltaMovement(this.getDeltaMovement());
				}
				entity.move(MoverType.SHULKER_BOX, this.getDeltaMovement());
				
				entity.setOnGround(true);
				entity.fallDistance = 0F;
				
				this.onEntityCollision(entity);
			}
			
		}
	}
	
	public boolean isPaltaeriaStratineCollision(FloatBlockEntity other) {
		Block thisBlock = this.blockState.getBlock();
		Block otherBlock = other.getBlockState().getBlock();
		return thisBlock == SpectrumBlocks.PALTAERIA_FRAGMENT_BLOCK && otherBlock == SpectrumBlocks.STRATINE_FRAGMENT_BLOCK
				|| thisBlock == SpectrumBlocks.STRATINE_FRAGMENT_BLOCK && otherBlock == SpectrumBlocks.PALTAERIA_FRAGMENT_BLOCK;
	}
	
	/**
	 * Break the block, spawn break particles, and drop stacks if it can.
	 */
	public void breakApart() {
		if (this.isRemoved()) return;
		
		this.discard();
		if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			Block.dropResources(this.blockState, this.level(), this.blockPosition());
		}
		// spawn break particles
		level().levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, this.blockPosition(), Block.getId(blockState));
	}
	
	@Override
	public boolean onlyOpCanSetNbt() {
		return true;
	}
	
	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this, Block.getId(this.getBlockState()));
	}
	
	@Override
	public void recreateFromPacket(ClientboundAddEntityPacket packet) {
		super.recreateFromPacket(packet);
		this.blockState = Block.stateById(packet.getData());
		this.blocksBuilding = true;
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		this.setPos(d, e + (double) ((1.0F - this.getBbHeight()) / 2.0F), f);
		this.setOrigin(this.blockPosition());
	}
	
	@Override
	public boolean isAttackable() {
		return false;
	}
	
	public BlockPos getOrigin() {
		return this.entityData.get(ORIGIN);
	}
	
	public void setOrigin(BlockPos origin) {
		this.entityData.set(ORIGIN, origin);
		this.setPos(getX(), getY(), getZ());
	}
	
	public Long getLaunchTime() {
		return this.entityData.get(LAUNCH_TIME);
	}
	
	public void setLaunchTime(long spawnTime) {
		this.entityData.set(LAUNCH_TIME, spawnTime);
	}
	
	@Override
	protected void defineSynchedData() {
		this.entityData.define(ORIGIN, BlockPos.ZERO);
		this.entityData.define(GRAVITY_MODIFIER, 0.0F);
		this.entityData.define(LAUNCH_TIME, 0L);
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return collides;
	}
	
	@Override
	public boolean canCollideWith(Entity other) {
		return other.canBeCollidedWith() && !this.isPassengerOfSameVehicle(other);
	}
	
	public float getGravity() {
		return this.entityData.get(GRAVITY_MODIFIER);
	}
	
	protected void setGravity(float modifier) {
		this.entityData.set(GRAVITY_MODIFIER, modifier);
	}
	
	@Override
	public boolean isNoGravity() {
		return this.getGravity() == 0.0 || super.isNoGravity();
	}
	
}
