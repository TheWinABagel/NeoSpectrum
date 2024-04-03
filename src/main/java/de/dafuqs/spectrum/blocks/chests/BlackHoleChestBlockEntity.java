package de.dafuqs.spectrum.blocks.chests;

import de.dafuqs.spectrum.api.block.FilterConfigurable;
import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.events.SpectrumGameEvents;
import de.dafuqs.spectrum.events.listeners.EventQueue;
import de.dafuqs.spectrum.events.listeners.ExperienceOrbEventQueue;
import de.dafuqs.spectrum.events.listeners.ItemAndExperienceEventQueue;
import de.dafuqs.spectrum.events.listeners.ItemEntityEventQueue;
import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.inventories.BlackHoleChestScreenHandler;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class BlackHoleChestBlockEntity extends SpectrumChestBlockEntity implements WorldlyContainer, EventQueue.Callback<Object> {
	
	public static final int INVENTORY_SIZE = 28;
	public static final int ITEM_FILTER_SLOT_COUNT = 5;
	public static final int EXPERIENCE_STORAGE_PROVIDER_ITEM_SLOT = 27;
	private static final int RANGE = 12;
	private final ItemAndExperienceEventQueue itemAndExperienceEventQueue;
	private final List<Item> filterItems;
	
	public BlackHoleChestBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(SpectrumBlockEntities.BLACK_HOLE_CHEST, blockPos, blockState);
		this.itemAndExperienceEventQueue = new ItemAndExperienceEventQueue(new BlockPositionSource(this.worldPosition), RANGE, this);
		this.filterItems = NonNullList.withSize(ITEM_FILTER_SLOT_COUNT, Items.AIR);
	}

	public static void tick(@NotNull Level world, BlockPos pos, BlockState state, BlackHoleChestBlockEntity blockEntity) {
		if (world.isClientSide) {
			blockEntity.lidAnimator.tickLid();
		} else {
			blockEntity.itemAndExperienceEventQueue.tick(world);
			if (world.getGameTime() % 80 == 0 && !SpectrumChestBlock.isChestBlocked(world, pos)) {
				searchForNearbyEntities(blockEntity);
			}
		}
	}

	private static void searchForNearbyEntities(@NotNull BlackHoleChestBlockEntity blockEntity) {
		List<ItemEntity> itemEntities = blockEntity.getLevel().getEntities(EntityType.ITEM, getBoxWithRadius(blockEntity.worldPosition, RANGE), Entity::isAlive);
		for (ItemEntity itemEntity : itemEntities) {
			if (itemEntity.isAlive() && !itemEntity.getItem().isEmpty()) {
				itemEntity.gameEvent(SpectrumGameEvents.ENTITY_SPAWNED);
			}
		}

		List<ExperienceOrb> experienceOrbEntities = blockEntity.getLevel().getEntities(EntityType.EXPERIENCE_ORB, getBoxWithRadius(blockEntity.worldPosition, RANGE), Entity::isAlive);
		for (ExperienceOrb experienceOrbEntity : experienceOrbEntities) {
			if (experienceOrbEntity.isAlive()) {
				experienceOrbEntity.gameEvent(SpectrumGameEvents.ENTITY_SPAWNED);
			}
		}
	}
	
	@Contract("_, _ -> new")
	protected static @NotNull AABB getBoxWithRadius(BlockPos blockPos, int radius) {
		return AABB.ofSize(Vec3.atCenterOf(blockPos), radius, radius, radius);
	}
	
	@Override
	protected Component getDefaultName() {
		return Component.translatable("block.spectrum.black_hole_chest");
	}
	
	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return new BlackHoleChestScreenHandler(syncId, playerInventory, this);
	}
	
	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		for (int i = 0; i < ITEM_FILTER_SLOT_COUNT; i++) {
			tag.putString("Filter" + i, BuiltInRegistries.ITEM.getKey(this.filterItems.get(i)).toString());
		}
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		for (int i = 0; i < ITEM_FILTER_SLOT_COUNT; i++) {
			if (tag.contains("Filter" + i, Tag.TAG_STRING)) {
				this.filterItems.set(i, BuiltInRegistries.ITEM.get(new ResourceLocation(tag.getString("Filter" + i))));
			}
		}
	}
	
	@Override
	public int getContainerSize() {
		return 27 + 1; // 3 rows, 1 knowledge gem, 5 item filters (they are not real slots, though)
	}
	
	public ItemAndExperienceEventQueue getEventListener() {
		return this.itemAndExperienceEventQueue;
	}
	
	@Override
	public boolean canAcceptEvent(Level world, GameEventListener listener, GameEvent.ListenerInfo event, Vec3 sourcePos) {
		if (SpectrumChestBlock.isChestBlocked(world, this.worldPosition)) {
			return false;
		}
		Entity entity = event.context().sourceEntity();
		if (entity instanceof ItemEntity) {
			return true;
		}
		return entity instanceof ExperienceOrb && hasExperienceStorageItem();
	}
	
	@Override
	public void triggerEvent(Level world, GameEventListener listener, Object entry) {
		if (SpectrumChestBlock.isChestBlocked(world, worldPosition)) {
			return;
		}
		
		if (entry instanceof ExperienceOrbEventQueue.EventEntry experienceEntry) {
			ExperienceOrb experienceOrbEntity = experienceEntry.experienceOrbEntity;
			if (experienceOrbEntity != null && experienceOrbEntity.isAlive() && hasExperienceStorageItem()) {
				ExperienceStorageItem.addStoredExperience(this.inventory.get(EXPERIENCE_STORAGE_PROVIDER_ITEM_SLOT), experienceOrbEntity.getValue()); // overflow experience is void, to not lag the world on large farms

				sendPlayExperienceOrbEntityAbsorbedParticle((ServerLevel) world, experienceOrbEntity);
				world.playSound(null, experienceOrbEntity.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.9F + world.random.nextFloat() * 0.2F, 0.9F + world.random.nextFloat() * 0.2F);
				experienceOrbEntity.remove(Entity.RemovalReason.DISCARDED);
			}
		} else if (entry instanceof ItemEntityEventQueue.EventEntry itemEntry) {
			ItemEntity itemEntity = itemEntry.itemEntity;
			if (itemEntity != null && itemEntity.isAlive() && acceptsItemStack(itemEntity.getItem())) {
				int previousAmount = itemEntity.getItem().getCount();
				ItemStack remainingStack = InventoryHelper.smartAddToInventory(itemEntity.getItem(), this, Direction.UP);
				
				if (remainingStack.isEmpty()) {
					sendPlayItemEntityAbsorbedParticle((ServerLevel) world, itemEntity);
					world.playSound(null, itemEntity.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.9F + world.random.nextFloat() * 0.2F, 0.9F + world.random.nextFloat() * 0.2F);
					itemEntity.setItem(ItemStack.EMPTY);
					itemEntity.discard();
				} else {
					if (remainingStack.getCount() != previousAmount) {
						sendPlayItemEntityAbsorbedParticle((ServerLevel) world, itemEntity);
						world.playSound(null, itemEntity.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.9F + world.random.nextFloat() * 0.2F, 0.9F + world.random.nextFloat() * 0.2F);
						itemEntity.setItem(remainingStack);
					}
				}
			}
		}
	}

	public static void sendPlayItemEntityAbsorbedParticle(ServerLevel world, @NotNull ItemEntity itemEntity) {
		SpectrumS2CPacketSender.playParticleWithExactVelocity(world, itemEntity.position(),
				SpectrumParticleTypes.BLUE_BUBBLE_POP,
				1, Vec3.ZERO);
	}

	public static void sendPlayExperienceOrbEntityAbsorbedParticle(ServerLevel world, @NotNull ExperienceOrb experienceOrbEntity) {
		SpectrumS2CPacketSender.playParticleWithExactVelocity(world, experienceOrbEntity.position(),
				SpectrumParticleTypes.GREEN_BUBBLE_POP,
				1, Vec3.ZERO);
	}

	@Override
	public SoundEvent getOpenSound() {
		return SpectrumSoundEvents.BLACK_HOLE_CHEST_OPEN;
	}

	@Override
	public SoundEvent getCloseSound() {
		return SpectrumSoundEvents.BLACK_HOLE_CHEST_CLOSE;
	}
	//todoforge write screen opening data thing
//	@Override
	public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
		buf.writeBlockPos(this.worldPosition);
		FilterConfigurable.writeScreenOpeningData(buf, filterItems);
	}

	public List<Item> getItemFilters() {
		return this.filterItems;
	}

	public void setFilterItem(int slot, Item item) {
		this.filterItems.set(slot, item);
		this.setChanged();
	}
	
	public boolean acceptsItemStack(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return false;
		}
		
		boolean allAir = true;
		for (int i = 0; i < ITEM_FILTER_SLOT_COUNT; i++) {
			Item filterItem = this.filterItems.get(i);
			if (filterItem.equals(itemStack.getItem())) {
				return true;
			} else if (!filterItem.equals(Items.AIR)) {
				allAir = false;
			}
		}
		return allAir;
	}
	
	public boolean hasExperienceStorageItem() {
		return this.inventory.get(EXPERIENCE_STORAGE_PROVIDER_ITEM_SLOT).getItem() instanceof ExperienceStorageItem;
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return IntStream.rangeClosed(0, EXPERIENCE_STORAGE_PROVIDER_ITEM_SLOT - 1).toArray();
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
		return true;
	}
	
	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return true;
	}
}
