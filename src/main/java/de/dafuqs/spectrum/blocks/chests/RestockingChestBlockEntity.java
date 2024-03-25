package de.dafuqs.spectrum.blocks.chests;

import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.inventories.RestockingChestScreenHandler;
import de.dafuqs.spectrum.items.CraftingTabletItem;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RestockingChestBlockEntity extends SpectrumChestBlockEntity implements WorldlyContainer {
	
	public static final int INVENTORY_SIZE = 27 + 4 + 4; // 27 items, 4 crafting tablets, 4 result slots
	public static final int[] CHEST_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
	public static final int[] RECIPE_SLOTS = new int[]{27, 28, 29, 30};
	public static final int[] RESULT_SLOTS = new int[]{31, 32, 33, 34};
	private int coolDownTicks = 0;
	
	public RestockingChestBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(SpectrumBlockEntities.RESTOCKING_CHEST, blockPos, blockState);
	}
	
	public static void tick(Level world, BlockPos pos, BlockState state, RestockingChestBlockEntity restockingChestBlockEntity) {
		if (world.isClientSide) {
			restockingChestBlockEntity.lidAnimator.tickLid();
		} else {
			if (tickCooldown(restockingChestBlockEntity)) {
				for (int i = 0; i < 4; i++) {
					ItemStack outputItemStack = restockingChestBlockEntity.inventory.get(RESULT_SLOTS[i]);
					ItemStack craftingTabletItemStack = restockingChestBlockEntity.inventory.get(RECIPE_SLOTS[i]);
					if (!craftingTabletItemStack.isEmpty() && (outputItemStack.isEmpty() || outputItemStack.getCount() < outputItemStack.getMaxStackSize())) {
						boolean couldCraft = restockingChestBlockEntity.tryCraft(restockingChestBlockEntity, i);
						if (couldCraft) {
							restockingChestBlockEntity.setCooldown(restockingChestBlockEntity, 20);
							restockingChestBlockEntity.setChanged();
							return;
						}
					}
				}
			}
		}
	}
	
	private static boolean tickCooldown(RestockingChestBlockEntity restockingChestBlockEntity) {
		restockingChestBlockEntity.coolDownTicks--;
		if (restockingChestBlockEntity.coolDownTicks > 0) {
			return false;
		} else {
			restockingChestBlockEntity.coolDownTicks = 0;
		}
		return true;
	}
	
	@Override
	protected Component getDefaultName() {
		return Component.translatable("block.spectrum.restocking_chest");
	}
	
	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return new RestockingChestScreenHandler(syncId, playerInventory, this);
	}
	
	private void setCooldown(RestockingChestBlockEntity restockingChestBlockEntity, int cooldownTicks) {
		restockingChestBlockEntity.coolDownTicks = cooldownTicks;
	}
	
	private boolean tryCraft(RestockingChestBlockEntity restockingChestBlockEntity, int index) {
		ItemStack craftingTabletItemStack = restockingChestBlockEntity.inventory.get(RECIPE_SLOTS[index]);
		if (craftingTabletItemStack.is(SpectrumItems.CRAFTING_TABLET)) {
			Recipe<?> recipe = CraftingTabletItem.getStoredRecipe(level, craftingTabletItemStack);
			if (recipe instanceof ShapelessRecipe || recipe instanceof ShapedRecipe) {
				NonNullList<Ingredient> ingredients = recipe.getIngredients();
				ItemStack outputItemStack = recipe.getResultItem(level.registryAccess());
				ItemStack currentItemStack = restockingChestBlockEntity.inventory.get(RESULT_SLOTS[index]);
				if (InventoryHelper.canCombineItemStacks(currentItemStack, outputItemStack) && InventoryHelper.hasInInventory(ingredients, restockingChestBlockEntity)) {
					List<ItemStack> remainders = InventoryHelper.removeFromInventoryWithRemainders(ingredients, restockingChestBlockEntity);
					
					if (currentItemStack.isEmpty()) {
						restockingChestBlockEntity.inventory.set(RESULT_SLOTS[index], outputItemStack.copy());
					} else {
						currentItemStack.grow(outputItemStack.getCount());
					}
					
					for (ItemStack remainder : remainders) {
						InventoryHelper.smartAddToInventory(remainder, restockingChestBlockEntity, null);
					}
					return true;
				}
				
			}
		}
		return false;
	}
	
	@Override
	public int getContainerSize() {
		return INVENTORY_SIZE;
	}
	
	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("cooldown", coolDownTicks);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("cooldown")) {
			coolDownTicks = tag.getInt("cooldown");
		}
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.DOWN) {
			return RESULT_SLOTS;
		} else {
			return CHEST_SLOTS;
		}
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
		return slot <= CHEST_SLOTS[CHEST_SLOTS.length - 1];
	}
	
	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return true;
	}
	
}
