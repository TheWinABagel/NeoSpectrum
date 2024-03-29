package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.blocks.pedestal.PedestalBlockEntity;
import de.dafuqs.spectrum.inventories.slots.DisabledSlot;
import de.dafuqs.spectrum.inventories.slots.PedestalPreviewSlot;
import de.dafuqs.spectrum.inventories.slots.StackFilterSlot;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipeTier;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class PedestalScreenHandler extends RecipeBookMenu<Container> {
	
	protected final Level world;
	private final Container inventory;
	private final ContainerData propertyDelegate;
	private final RecipeBookType category;
	
	private final BlockPos pedestalPos;
	private final PedestalRecipeTier pedestalRecipeTier;
	private final PedestalRecipeTier maxPedestalRecipeTier;
	
	public PedestalScreenHandler(int syncId, Inventory playerInventory, @NotNull FriendlyByteBuf buf) {
		this(SpectrumScreenHandlerTypes.PEDESTAL, ContainerLevelAccess.NULL, RecipeBookType.CRAFTING, syncId, playerInventory, buf.readInt(), buf.readInt(), buf.readBlockPos());
	}
	
	protected PedestalScreenHandler(MenuType<?> type, ContainerLevelAccess context, RecipeBookType recipeBookCategory, int i, Inventory playerInventory, int variant, int maxRecipeTier, BlockPos pedestalPos) {
		this(type, context, recipeBookCategory, i, playerInventory, (Container) playerInventory.player.level().getBlockEntity(pedestalPos), new SimpleContainerData(2), variant, maxRecipeTier, pedestalPos);
	}
	
	public PedestalScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate, int variant, int maxRecipeTier, BlockPos pedestalPos) {
		this(SpectrumScreenHandlerTypes.PEDESTAL, ContainerLevelAccess.NULL, RecipeBookType.CRAFTING, syncId, playerInventory, inventory, propertyDelegate, variant, maxRecipeTier, pedestalPos);
	}
	
	protected PedestalScreenHandler(MenuType<?> type, ContainerLevelAccess context, RecipeBookType recipeBookCategory, int i, @NotNull Inventory playerInventory, Container inventory, ContainerData propertyDelegate, int pedestalRecipeTier, int maxRecipeTier, BlockPos pedestalPos) {
		super(type, i);
		this.inventory = inventory;
		this.category = recipeBookCategory;
		this.propertyDelegate = propertyDelegate;
		this.world = playerInventory.player.level();
		
		this.pedestalPos = pedestalPos;
		this.pedestalRecipeTier = PedestalRecipeTier.values()[pedestalRecipeTier];
		this.maxPedestalRecipeTier = PedestalRecipeTier.values()[maxRecipeTier];
		
		checkContainerSize(inventory, PedestalBlockEntity.INVENTORY_SIZE);
		checkContainerDataCount(propertyDelegate, 2);
		inventory.startOpen(playerInventory.player);
		
		// crafting slots
		int m;
		int n;
		for (m = 0; m < 3; ++m) {
			for (n = 0; n < 3; ++n) {
				this.addSlot(new Slot(inventory, n + m * 3, 30 + n * 18, 19 + m * 18));
			}
		}
		
		// gemstone powder slots
		switch (this.pedestalRecipeTier) {
			case BASIC, SIMPLE -> {
				this.addSlot(new StackFilterSlot(inventory, 9, 44 + 18, 77, SpectrumItems.TOPAZ_POWDER));
				this.addSlot(new StackFilterSlot(inventory, 10, 44 + 2 * 18, 77, SpectrumItems.AMETHYST_POWDER));
				this.addSlot(new StackFilterSlot(inventory, 11, 44 + 3 * 18, 77, SpectrumItems.CITRINE_POWDER));
				this.addSlot(new DisabledSlot(inventory, 12, -2000, 77));
				this.addSlot(new DisabledSlot(inventory, 13, -2000, 77));
			}
			case ADVANCED -> {
				this.addSlot(new StackFilterSlot(inventory, 9, 35 + 18, 77, SpectrumItems.TOPAZ_POWDER));
				this.addSlot(new StackFilterSlot(inventory, 10, 35 + 2 * 18, 77, SpectrumItems.AMETHYST_POWDER));
				this.addSlot(new StackFilterSlot(inventory, 11, 35 + 3 * 18, 77, SpectrumItems.CITRINE_POWDER));
				this.addSlot(new StackFilterSlot(inventory, 12, 35 + 4 * 18, 77, SpectrumItems.ONYX_POWDER));
				this.addSlot(new DisabledSlot(inventory, 13, -2000, 77));
			}
			case COMPLEX -> {
				this.addSlot(new StackFilterSlot(inventory, 9, 44, 77, SpectrumItems.TOPAZ_POWDER));
				this.addSlot(new StackFilterSlot(inventory, 10, 44 + 18, 77, SpectrumItems.AMETHYST_POWDER));
				this.addSlot(new StackFilterSlot(inventory, 11, 44 + 2 * 18, 77, SpectrumItems.CITRINE_POWDER));
				this.addSlot(new StackFilterSlot(inventory, 12, 44 + 3 * 18, 77, SpectrumItems.ONYX_POWDER));
				this.addSlot(new StackFilterSlot(inventory, 13, 44 + 4 * 18, 77, SpectrumItems.MOONSTONE_POWDER));
			}
		}
		
		// crafting tablet slot
		this.addSlot(new StackFilterSlot(inventory, PedestalBlockEntity.CRAFTING_TABLET_SLOT_ID, 93, 19, SpectrumItems.CRAFTING_TABLET));
		
		// preview slot
		this.addSlot(new PedestalPreviewSlot(inventory, 15, 127, 37));
		
		// player inventory
		int l;
		for (l = 0; l < 3; ++l) {
			for (int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, 112 + l * 18));
			}
		}
		
		// player hotbar
		for (l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 170));
		}
		
		this.addDataSlots(propertyDelegate);
	}
	
	@Override
	public void fillCraftSlotsStackedContents(StackedContents recipeMatcher) {
		if (this.inventory != null) {
			((StackedContentsCompatible) this.inventory).fillStackedContents(recipeMatcher);
		}
	}
	
	@Override
	public void clearCraftingContent() {
		for (int i = 0; i < 9; i++) {
			this.getSlot(i).setByPlayer(ItemStack.EMPTY);
		}
	}
	
	@Override
	public boolean recipeMatches(Recipe<? super Container> recipe) {
		return recipe.matches(this.inventory, this.world);
	}
	
	@Override
	public int getResultSlotIndex() {
		return 16;
	}
	
	@Override
	public int getGridWidth() {
		return 3;
	}
	
	@Override
	public int getGridHeight() {
		return 3;
	}
	
	@Override
	public int getSize() {
		return 9;
	}
	
	@Override
	public boolean stillValid(Player player) {
		return this.inventory.stillValid(player);
	}
	
	@Environment(EnvType.CLIENT)
	public int getCraftingProgress() {
		int craftingTime = this.propertyDelegate.get(0); // craftingTime
		int craftingTimeTotal = this.propertyDelegate.get(1); // craftingTimeTotal
		return craftingTimeTotal != 0 && craftingTime != 0 ? craftingTime * 24 / craftingTimeTotal : 0;
	}
	
	public boolean isCrafting() {
		return this.propertyDelegate.get(0) > 0; // craftingTime
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public RecipeBookType getRecipeBookType() {
		return this.category;
	}
	
	@Override
	public boolean shouldMoveToInventory(int index) {
		return index != 1;
	}
	
	// Shift-Clicking
	// 0-8: crafting slots
	// 9-13: powder slots
	// 14: crafting tablet
	// 15: preview slot
	// 16: hidden output slot
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack clickedStackCopy = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		
		BlockEntity blockEntity = world.getBlockEntity(pedestalPos);
		if (blockEntity instanceof PedestalBlockEntity pedestalBlockEntity) {
			pedestalBlockEntity.setInventoryChanged();
		}
		
		if (slot.hasItem()) {
			ItemStack clickedStack = slot.getItem();
			clickedStackCopy = clickedStack.copy();
			
			if (index < 15) {
				// pedestal => player inv
				if (!this.moveItemStackTo(clickedStack, 16, 51, false)) {
					return ItemStack.EMPTY;
				}
			} else if (clickedStackCopy.is(SpectrumItems.TOPAZ_POWDER)) {
				if (!this.moveItemStackTo(clickedStack, 9, 10, false)) {
					return ItemStack.EMPTY;
				}
			} else if (clickedStackCopy.is(SpectrumItems.AMETHYST_POWDER)) {
				if (!this.moveItemStackTo(clickedStack, 10, 11, false)) {
					return ItemStack.EMPTY;
				}
			} else if (clickedStackCopy.is(SpectrumItems.CITRINE_POWDER)) {
				if (!this.moveItemStackTo(clickedStack, 11, 12, false)) {
					return ItemStack.EMPTY;
				}
			} else if (clickedStackCopy.is(SpectrumItems.ONYX_POWDER)) {
				if (!this.moveItemStackTo(clickedStack, 12, 13, false)) {
					return ItemStack.EMPTY;
				}
			} else if (clickedStackCopy.is(SpectrumItems.MOONSTONE_POWDER)) {
				if (!this.moveItemStackTo(clickedStack, 13, 14, false)) {
					return ItemStack.EMPTY;
				}
			} else if (clickedStackCopy.is(SpectrumItems.CRAFTING_TABLET)) {
				if (!this.moveItemStackTo(clickedStack, PedestalBlockEntity.CRAFTING_TABLET_SLOT_ID, PedestalBlockEntity.CRAFTING_TABLET_SLOT_ID + 1, false)) {
					return ItemStack.EMPTY;
				}
			}
			
			// crafting grid
			if (!this.moveItemStackTo(clickedStack, 0, 9, false)) {
				return ItemStack.EMPTY;
			}
			
			if (clickedStack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			
			if (clickedStack.getCount() == clickedStackCopy.getCount()) {
				return ItemStack.EMPTY;
			}
			
			slot.onTake(player, clickedStack);
		}
		
		return clickedStackCopy;
	}
	
	public BlockPos getPedestalPos() {
		return this.pedestalPos;
	}
	
	public PedestalRecipeTier getPedestalRecipeTier() {
		return this.pedestalRecipeTier;
	}
	
	public PedestalRecipeTier getMaxPedestalRecipeTier() {
		return this.maxPedestalRecipeTier;
	}
	
	@Override
	public void removed(Player player) {
		super.removed(player);
		this.inventory.stopOpen(player);
	}
	
}
