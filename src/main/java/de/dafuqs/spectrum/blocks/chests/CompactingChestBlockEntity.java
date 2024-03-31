package de.dafuqs.spectrum.blocks.chests;

import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.inventories.AutoCompactingInventory;
import de.dafuqs.spectrum.inventories.CompactingChestScreenHandler;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class CompactingChestBlockEntity extends SpectrumChestBlockEntity {
	
	private static final Map<AutoCompactingInventory.AutoCraftingMode, Map<ItemStack, Optional<CraftingRecipe>>> cache = new EnumMap<>(AutoCompactingInventory.AutoCraftingMode.class);
	private final AutoCompactingInventory autoCompactingInventory = new AutoCompactingInventory();
	private AutoCompactingInventory.AutoCraftingMode autoCraftingMode;
	private CraftingRecipe lastCraftingRecipe; // cache
	private ItemStack lastItemVariant; // cache
	private boolean hasToCraft;
	
	public CompactingChestBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(SpectrumBlockEntities.COMPACTING_CHEST, blockPos, blockState);
		this.autoCraftingMode = AutoCompactingInventory.AutoCraftingMode.ThreeXThree;
		this.lastItemVariant = null;
		this.lastCraftingRecipe = null;
		this.hasToCraft = false;
	}
	
	public static void tick(Level world, BlockPos pos, BlockState state, CompactingChestBlockEntity compactingChestBlockEntity) {
		if (world.isClientSide) {
			compactingChestBlockEntity.lidAnimator.tickLid();
		} else {
			if (compactingChestBlockEntity.hasToCraft) {
				boolean couldCraft = compactingChestBlockEntity.tryCraftOnce();
				if (!couldCraft) {
					compactingChestBlockEntity.hasToCraft = false;
				}
			}
		}
	}
	
	private static boolean smartAddToInventory(List<ItemStack> itemStacks, List<ItemStack> inventory, boolean test) {
		List<ItemStack> additionStacks = new ArrayList<>();
		for (ItemStack itemStack : itemStacks) {
			additionStacks.add(itemStack.copy());
		}
		
		boolean tryStackExisting = true;
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack currentStack = inventory.get(i);
			for (ItemStack additionStack : additionStacks) {
				boolean doneStuff = false;
				if (additionStack.getCount() > 0) {
					if (currentStack.isEmpty() && (test || !tryStackExisting)) {
						int maxStackCount = currentStack.getMaxStackSize();
						int maxAcceptCount = Math.min(additionStack.getCount(), maxStackCount);
						
						if (!test) {
							ItemStack newStack = additionStack.copy();
							newStack.setCount(maxAcceptCount);
							inventory.set(i, newStack);
						}
						additionStack.setCount(additionStack.getCount() - maxAcceptCount);
						doneStuff = true;
					} else if (ItemStack.isSameItemSameTags(currentStack, additionStack)) {
						// add to stack;
						int maxStackCount = currentStack.getMaxStackSize();
						int canAcceptCount = maxStackCount - currentStack.getCount();
						
						if (canAcceptCount > 0) {
							if (!test) {
								inventory.get(i).grow(Math.min(additionStack.getCount(), canAcceptCount));
							}
							if (canAcceptCount >= additionStack.getCount()) {
								additionStack.setCount(0);
							} else {
								additionStack.setCount(additionStack.getCount() - canAcceptCount);
							}
							doneStuff = true;
						}
					}
					
					// if there were changes: check if all stacks have count 0
					if (doneStuff) {
						boolean allEmpty = true;
						for (ItemStack itemStack : additionStacks) {
							if (itemStack.getCount() > 0) {
								allEmpty = false;
								break;
							}
						}
						if (allEmpty) {
							return true;
						}
					}
				}
			}
			
			if (tryStackExisting && !test && i == inventory.size() - 1) {
				tryStackExisting = false;
				i = -1;
			}
		}
		return false;
	}
	
	public static void clearCache() {
		cache.clear();
	}
	
	@Override
	protected Component getDefaultName() {
		return Component.translatable("block.spectrum.compacting_chest");
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("AutoCraftingMode", Tag.TAG_ANY_NUMERIC)) {
			int autoCraftingModeInt = tag.getInt("AutoCraftingMode");
			this.autoCraftingMode = AutoCompactingInventory.AutoCraftingMode.values()[autoCraftingModeInt];
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("AutoCraftingMode", this.autoCraftingMode.ordinal());
	}
	
	@Override
	public int getContainerSize() {
		return 27;
	}
	
	@Override
	public void setItem(int slot, ItemStack stack) {
		super.setItem(slot, stack);
		this.hasToCraft = true;
	}
	
	public void inventoryChanged() {
		this.hasToCraft = true;
	}
	
	private boolean tryCraftOnce() {
		Optional<CraftingRecipe> optionalCraftingRecipe = Optional.empty();
		NonNullList<ItemStack> inventory = this.getItems();
		
		// try last recipe
		if (lastCraftingRecipe != null) {
			int requiredItemCount = this.autoCraftingMode.getItemCount();
			if (InventoryHelper.isItemCountInInventory(inventory, lastItemVariant, requiredItemCount)) {
				optionalCraftingRecipe = Optional.ofNullable(lastCraftingRecipe);
			} else {
				lastCraftingRecipe = null;
				lastItemVariant = null;
			}
		}
		// search for other recipes
		if (optionalCraftingRecipe.isEmpty()) {
			optionalCraftingRecipe = searchRecipeToCraft();
		}
		
		if (optionalCraftingRecipe.isPresent() && this.lastItemVariant != null) {
			if (tryCraftInInventory(inventory, optionalCraftingRecipe.get(), this.lastItemVariant)) {
				this.lastCraftingRecipe = optionalCraftingRecipe.get();
				return true;
			}
		}
		return false;
	}
	
	public Optional<CraftingRecipe> searchRecipeToCraft() {
		for (ItemStack itemStack : inventory) {
			if (itemStack.isEmpty()) {
				continue;
			}
			
			int requiredItemCount = this.autoCraftingMode.getItemCount();
			Tuple<Integer, List<ItemStack>> stackPair = InventoryHelper.getStackCountInInventory(itemStack, inventory, requiredItemCount);
			if (stackPair.getA() >= requiredItemCount) {
				Map<ItemStack, Optional<CraftingRecipe>> currentCache = cache.computeIfAbsent(autoCraftingMode, mode -> new HashMap<>());
				ItemStack itemKey = itemStack.copy();
				
				Optional<CraftingRecipe> recipe = currentCache.get(itemKey);
				if (recipe != null) {
					if (recipe.isEmpty()) {
						continue;
					}
					this.lastItemVariant = itemKey;
					return recipe;
				}
				
				autoCompactingInventory.setCompacting(autoCraftingMode, itemKey);
				Optional<CraftingRecipe> optionalCraftingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, autoCompactingInventory, level);
				if (optionalCraftingRecipe.isEmpty() || optionalCraftingRecipe.get().getResultItem(level.registryAccess()).isEmpty()) {
					optionalCraftingRecipe = Optional.empty();
					currentCache.put(itemKey, optionalCraftingRecipe);
				} else {
					currentCache.put(itemKey, optionalCraftingRecipe);
					
					this.lastItemVariant = itemKey;
					return optionalCraftingRecipe;
				}
			}
		}
		
		return Optional.empty();
	}
	
	public boolean tryCraftInInventory(NonNullList<ItemStack> inventory, CraftingRecipe craftingRecipe, ItemStack itemVariant) {
		ItemStack inputStack = itemVariant.copyWithCount(this.autoCraftingMode.getItemCount());
		List<ItemStack> remainders = InventoryHelper.removeFromInventoryWithRemainders(inputStack, this);
		
		boolean spaceInInventory;
		
		List<ItemStack> additionItemStacks = new ArrayList<>();
		additionItemStacks.add(craftingRecipe.getResultItem(level.registryAccess()));
		additionItemStacks.addAll(remainders);
		
		spaceInInventory = smartAddToInventory(additionItemStacks, inventory, true);
		if (spaceInInventory) {
			// craft
			smartAddToInventory(additionItemStacks, inventory, false);
			this.setItems(inventory);
			
			// cache
			return true;
		} else {
			smartAddToInventory(List.of(inputStack), inventory, false);
			return false;
		}
	}
	
	@Override
	public SoundEvent getOpenSound() {
		return SpectrumSoundEvents.COMPACTING_CHEST_OPEN;
	}
	
	@Override
	public SoundEvent getCloseSound() {
		return SpectrumSoundEvents.COMPACTING_CHEST_CLOSE;
	}
	
	public AutoCompactingInventory.AutoCraftingMode getAutoCraftingMode() {
		return this.autoCraftingMode;
	}
	
	public void applySettings(FriendlyByteBuf buf) {
		int autoCraftingModeInt = buf.readInt();
		this.autoCraftingMode = AutoCompactingInventory.AutoCraftingMode.values()[autoCraftingModeInt];
		this.lastCraftingRecipe = null;
	}
	
	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return new CompactingChestScreenHandler(syncId, playerInventory, this);
	}
	
	//todoforge screen open data
	public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
		buf.writeBlockPos(this.worldPosition);
		buf.writeInt(this.autoCraftingMode.ordinal());
	}
	
}
