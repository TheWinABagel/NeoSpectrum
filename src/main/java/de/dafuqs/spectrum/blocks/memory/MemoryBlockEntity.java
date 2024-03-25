package de.dafuqs.spectrum.blocks.memory;

import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.helpers.EntityHelper;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class MemoryBlockEntity extends BlockEntity implements PlayerOwned {
	
	protected ItemStack memoryItemStack = ItemStack.EMPTY; // zero or negative values: never hatch
	protected UUID ownerUUID;
	
	//  color rendering cache
	private int tint1 = -1;
	private int tint2 = -1;
	
	public MemoryBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.MEMORY, pos, state);
	}
	
	@Contract("_ -> new")
	public static @NotNull Tuple<Integer, Integer> getEggColorsForEntity(EntityType<?> entityType) {
		SpawnEggItem spawnEggItem = SpawnEggItem.byId(entityType);
		if (spawnEggItem != null) {
			return new Tuple<>(spawnEggItem.getColor(0), spawnEggItem.getColor(1));
		}
		return new Tuple<>(0x222222, 0xDDDDDD);
	}
	
	public static int getManifestAdvanceSteps(@NotNull Level world, @NotNull BlockPos blockPos) {
		BlockState belowBlockState = world.getBlockState(blockPos.below());
		if (belowBlockState.is(SpectrumBlockTags.MEMORY_NEVER_MANIFESTERS)) {
			return 0;
		} else if (belowBlockState.is(SpectrumBlockTags.MEMORY_VERY_FAST_MANIFESTERS)) {
			return 8;
		} else if (belowBlockState.is(SpectrumBlockTags.MEMORY_FAST_MANIFESTERS)) {
			return 3;
		} else {
			return 1;
		}
	}
	
	public void setData(LivingEntity livingEntity, @NotNull ItemStack creatureSpawnItemStack) {
		if (livingEntity instanceof Player playerEntity) {
			setOwner(playerEntity);
		}
		if (creatureSpawnItemStack.getItem() instanceof MemoryItem) {
			this.memoryItemStack = creatureSpawnItemStack.copy();
			this.memoryItemStack.setCount(1);
		}
		if (!livingEntity.level().isClientSide()) {
			this.updateInClientWorld();
		}
		this.setChanged();
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
		if (nbt.contains("MemoryItem", Tag.TAG_COMPOUND)) {
			this.memoryItemStack = ItemStack.of(nbt.getCompound("MemoryItem"));
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
		if (this.memoryItemStack != null) {
			CompoundTag creatureSpawnCompound = new CompoundTag();
			memoryItemStack.save(creatureSpawnCompound);
			nbt.put("MemoryItem", creatureSpawnCompound);
		}
	}
	
	public void advanceManifesting(ServerLevel world, BlockPos blockPos) {
		int ticksToManifest = MemoryItem.getTicksToManifest(this.memoryItemStack.getTag());
		if (ticksToManifest > 0) {
			int additionalManifestAdvanceSteps = getManifestAdvanceSteps(world, blockPos);
			if (additionalManifestAdvanceSteps > 0) {
				int newTicksToManifest = ticksToManifest - additionalManifestAdvanceSteps;
				if (newTicksToManifest <= 0) {
					this.manifest(world, blockPos);
				} else {
					Optional<EntityType<?>> entityTypeOptional = MemoryItem.getEntityType(this.memoryItemStack.getTag());
					if (entityTypeOptional.isPresent()) {
						MemoryItem.setTicksToManifest(this.memoryItemStack, newTicksToManifest);
						SpectrumS2CPacketSender.playMemoryManifestingParticles(world, blockPos, entityTypeOptional.get(), 3);
						world.playSound(null, this.worldPosition, SpectrumSoundEvents.BLOCK_MEMORY_ADVANCE, SoundSource.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
						this.setChanged();
					}
				}
			}
		}
	}
	
	public void manifest(@NotNull ServerLevel world, BlockPos blockPos) {
		manifest(world, blockPos, this.memoryItemStack, this.ownerUUID);
	}
	
	public static void manifest(@NotNull ServerLevel world, BlockPos blockPos, ItemStack memoryItemStack, @Nullable UUID ownerUUID) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof SimpleWaterloggedBlock && blockState.getValue(BlockStateProperties.WATERLOGGED)) {
			world.setBlockAndUpdate(blockPos, Blocks.WATER.defaultBlockState());
		} else {
			world.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
		}
		
		Optional<Entity> hatchedEntityOptional = hatchEntity(world, blockPos, memoryItemStack);
		
		if (hatchedEntityOptional.isPresent()) {
			Entity hatchedEntity = hatchedEntityOptional.get();
			
			SpectrumS2CPacketSender.playMemoryManifestingParticles(world, blockPos, hatchedEntity.getType(), 10);
			
			if (hatchedEntity instanceof Mob hatchedMobEntity) {
				hatchedMobEntity.setPersistenceRequired();
				hatchedMobEntity.playAmbientSound();
				hatchedMobEntity.spawnAnim();
			}
			if (ownerUUID != null) {
				EntityHelper.addPlayerTrust(hatchedEntity, ownerUUID);
			}
			
			Player owner = PlayerOwned.getPlayerEntityIfOnline(ownerUUID);
			if (owner instanceof ServerPlayer serverPlayerEntity) {
				SpectrumAdvancementCriteria.MEMORY_MANIFESTING.trigger(serverPlayerEntity, hatchedEntity);
			}
		}
	}
	
	public int getEggColor(int tintIndex) {
		if (tint1 == -1) {
			if (this.memoryItemStack == null) {
				this.tint1 = 0x222222;
				this.tint2 = 0xDDDDDD;
			} else {
				this.tint1 = MemoryItem.getEggColor(this.memoryItemStack.getTag(), 0);
				this.tint2 = MemoryItem.getEggColor(this.memoryItemStack.getTag(), 1);
			}
		}
		
		if (tintIndex == 0) {
			return tint1;
		} else {
			return tint2;
		}
	}
	
	public void updateInClientWorld() {
		((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}
	
	// Called when the chunk is first loaded to initialize this be
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbtCompound = new CompoundTag();
		this.saveAdditional(nbtCompound);
		return nbtCompound;
	}
	
	protected static Optional<Entity> hatchEntity(ServerLevel world, BlockPos blockPos, ItemStack memoryItemStack) {
		CompoundTag nbt = memoryItemStack.getTag();
		if (nbt == null) {
			return Optional.empty();
		}
		
		Optional<EntityType<?>> entityType = MemoryItem.getEntityType(nbt);
		if (entityType.isPresent()) {
			// alignPosition: center the mob in the center of the blockPos
			Entity entity = entityType.get().spawn(world, memoryItemStack, null, blockPos, MobSpawnType.SPAWN_EGG, true, false);
			if (entity != null) {
				if (entity instanceof Mob mobEntity) {
					if (!nbt.getBoolean("SpawnAsAdult")) {
						mobEntity.setBaby(true);
					}
					if (memoryItemStack.hasCustomHoverName()) {
						mobEntity.setCustomName(memoryItemStack.getHoverName());
					}
				}
				return Optional.of(entity);
			}
		}
		return Optional.empty();
	}
	
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public void setOwner(@NotNull Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		setChanged();
	}
	
	public ItemStack getMemoryItemStack() {
		return this.memoryItemStack;
	}
	
}
