package de.dafuqs.spectrum.helpers;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryHelper {
	
	public static int getItemCountInInventory(Container inventory, Item item) {
		int count = 0;
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack.is(item)) {
				count += stack.getCount();
			}
		}
		return count;
	}
	
	public static boolean removeFromInventoryWithRemainders(@NotNull Player playerEntity, @NotNull ItemStack stackToRemove) {
		if (playerEntity.isCreative()) {
			return true;
		} else {
			Container playerInventory = playerEntity.getInventory();
			List<Tuple<Integer, ItemStack>> matchingStacks = new ArrayList<>();
			int paymentStackItemCount = 0;
			for (int i = 0; i < playerInventory.getContainerSize(); i++) {
				ItemStack currentStack = playerInventory.getItem(i);
				if (currentStack.getItem().equals(stackToRemove.getItem())) {
					matchingStacks.add(new Tuple<>(i, currentStack));
					paymentStackItemCount += currentStack.getCount();
					if (paymentStackItemCount >= stackToRemove.getCount()) {
						break;
					}
				}
			}
			
			if (paymentStackItemCount < stackToRemove.getCount()) {
				return false;
			} else {
				int amountToRemove = stackToRemove.getCount();
				for (Tuple<Integer, ItemStack> matchingStack : matchingStacks) {
					if (matchingStack.getB().getCount() <= amountToRemove) {
						amountToRemove -= matchingStack.getB().getCount();
						playerEntity.getInventory().setItem(matchingStack.getA(), ItemStack.EMPTY);
						if (amountToRemove <= 0) {
							break;
						}
					} else {
						matchingStack.getB().shrink(amountToRemove);
						return true;
					}
				}
				return true;
			}
		}
	}
	
	public static boolean isItemCountInInventory(List<ItemStack> inventory, ItemVariant itemVariant, int maxSearchAmount) {
		int count = 0;
		for (ItemStack inventoryStack : inventory) {
			if (itemVariant.matches(inventoryStack)) {
				count += inventoryStack.getCount();
				if (count >= maxSearchAmount) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static Tuple<Integer, List<ItemStack>> getStackCountInInventory(ItemStack itemStack, List<ItemStack> inventory, int maxSearchAmount) {
		List<ItemStack> foundStacks = new ArrayList<>();
		int count = 0;
		for (ItemStack inventoryStack : inventory) {
			if (ItemStack.isSameItemSameTags(inventoryStack, itemStack)) {
				foundStacks.add(inventoryStack);
				count += inventoryStack.getCount();
				if (count >= maxSearchAmount) {
					return new Tuple<>(count, foundStacks);
				}
			}
		}
		return new Tuple<>(count, foundStacks);
	}
	
	/**
	 * Adds a single itemstack to an inventory
	 *
	 * @param itemStack the itemstack to add. The stack can have a size > maxStackSize and will be split accordingly
	 * @param inventory the inventory to add to
	 * @return The remaining stack that could not be added
	 */
	public static ItemStack smartAddToInventory(ItemStack itemStack, Container inventory, @Nullable Direction side) {
		if (inventory instanceof WorldlyContainer && side != null) {
			int[] acceptableSlots = ((WorldlyContainer) inventory).getSlotsForFace(side);
			for (int acceptableSlot : acceptableSlots) {
				if (((WorldlyContainer) inventory).canPlaceItemThroughFace(acceptableSlot, itemStack, side)) {
					itemStack = setOrCombineStack(inventory, acceptableSlot, itemStack);
					if (itemStack.isEmpty()) {
						break;
					}
				}
			}
		} else {
			for (int i = 0; i < inventory.getContainerSize(); i++) {
				itemStack = setOrCombineStack(inventory, i, itemStack);
				if (itemStack.isEmpty()) {
					break;
				}
			}
		}
		return itemStack;
	}
	
	public static ItemStack setOrCombineStack(Container inventory, int slot, ItemStack addingStack) {
		ItemStack existingStack = inventory.getItem(slot);
		if (existingStack.isEmpty()) {
			if (addingStack.getCount() > addingStack.getMaxStackSize()) {
				int amount = Math.min(addingStack.getMaxStackSize(), addingStack.getCount());
				amount = Math.min(amount, inventory.getMaxStackSize());
				ItemStack newStack = addingStack.copy();
				newStack.setCount(amount);
				addingStack.shrink(amount);
				inventory.setItem(slot, newStack);
			} else {
				inventory.setItem(slot, addingStack);
				return ItemStack.EMPTY;
			}
		} else {
			combineStacks(existingStack, addingStack);
		}
		return addingStack;
	}
	
	public static void combineStacks(ItemStack originalStack, ItemStack addingStack) {
		if (ItemStack.isSameItemSameTags(originalStack, addingStack)) {
			int leftOverAmountInExistingStack = originalStack.getMaxStackSize() - originalStack.getCount();
			if (leftOverAmountInExistingStack > 0) {
				int addAmount = Math.min(leftOverAmountInExistingStack, addingStack.getCount());
				originalStack.grow(addAmount);
				addingStack.shrink(addAmount);
			}
		}
	}
	
	/**
	 * Adds a single stacks to an inventory in a given slot range
	 *
	 * @param inventory  the inventory to add to
	 * @param stackToAdd the stack to add to the inventory
	 * @param rangeStart the start insert slot
	 * @param rangeEnd   the last insert slot
	 * @return false if the stack could not be completely added
	 */
	public static boolean addToInventory(Container inventory, ItemStack stackToAdd, int rangeStart, int rangeEnd) {
		for (int i = rangeStart; i < rangeEnd; i++) {
			ItemStack currentStack = inventory.getItem(i);
			if (currentStack.isEmpty()) {
				inventory.setItem(i, stackToAdd);
				return true;
			} else if (stackToAdd.isStackable()) {
				combineStacks(currentStack, stackToAdd);
				if (stackToAdd.isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Adds a list of stacks to an inventory in a given slot range
	 *
	 * @param inventory   the inventory to add to
	 * @param stacksToAdd the stacks to add to the inventory
	 * @param rangeStart  the start insert slot
	 * @param rangeEnd    the last insert slot
	 * @return false if not add stacksToAdd could be added
	 */
	public static boolean addToInventory(Container inventory, List<ItemStack> stacksToAdd, int rangeStart, int rangeEnd) {
		for (int i = rangeStart; i < rangeEnd; i++) {
			ItemStack inventoryStack = inventory.getItem(i);
			if (inventoryStack.isEmpty()) {
				inventory.setItem(i, stacksToAdd.get(0));
				stacksToAdd.remove(0);
				if (stacksToAdd.isEmpty()) {
					return true;
				}
			}
			for (int j = 0; j < stacksToAdd.size(); j++) {
				ItemStack stackToAdd = stacksToAdd.get(j);
				if (stackToAdd.isStackable()) {
					combineStacks(inventoryStack, stackToAdd);
					if (stackToAdd.isEmpty()) {
						stacksToAdd.remove(j);
						if (stacksToAdd.isEmpty()) {
							return true;
						}
						j--;
					}
				}
			}
		}
		return false;
	}
	
	public static void addToInventory(List<ItemStack> inventory, ItemStack itemStack, int rangeStart, int rangeEnd) {
		for (int i = rangeStart; i < rangeEnd; i++) {
			ItemStack currentStack = inventory.get(i);
			if (currentStack.isEmpty()) {
				inventory.set(i, itemStack);
				return;
			} else if (itemStack.isStackable()) {
				combineStacks(currentStack, itemStack);
				if (itemStack.isEmpty()) {
					return;
				}
			}
		}
	}
	
	public static boolean hasInInventory(List<Ingredient> ingredients, Container inventory) {
		List<Ingredient> ingredientsToFind = new ArrayList<>();
		List<Integer> requiredIngredientAmounts = new ArrayList<>();
		for (Ingredient ingredient : ingredients) {
			if (ingredient.isEmpty()) {
				continue;
			}
			
			ingredientsToFind.add(ingredient);
			if (ingredient.getItems().length > 0) {
				requiredIngredientAmounts.add(ingredient.getItems()[0].getCount());
			} else {
				requiredIngredientAmounts.add(1);
			}
		}
		
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (ingredientsToFind.size() == 0) {
				break;
			}
			ItemStack currentStack = inventory.getItem(i);
			if (!currentStack.isEmpty()) {
				int amount = currentStack.getCount();
				for (int j = 0; j < ingredientsToFind.size(); j++) {
					if (ingredientsToFind.get(j).test(currentStack)) {
						int ingredientCount = requiredIngredientAmounts.get(j);
						if (amount >= ingredientCount) {
							ingredientsToFind.remove(j);
							requiredIngredientAmounts.remove(j);
							j--;
						} else {
							requiredIngredientAmounts.set(j, requiredIngredientAmounts.get(j) - amount);
						}
						
						amount -= ingredientCount;
						if (amount < 1) {
							break;
						}
					}
				}
			}
		}
		
		return ingredientsToFind.size() == 0;
	}
	
	// return are the recipe remainders
	public static List<ItemStack> removeFromInventoryWithRemainders(List<Ingredient> ingredients, Container inventory) {
		List<ItemStack> remainders = new ArrayList<>();
		
		List<Ingredient> requiredIngredients = new ArrayList<>();
		List<Integer> requiredIngredientAmounts = new ArrayList<>();
		for (Ingredient ingredient : ingredients) {
			if (ingredient.isEmpty()) {
				continue;
			}
			
			requiredIngredients.add(ingredient);
			if (ingredient.getItems().length > 0) {
				requiredIngredientAmounts.add(ingredient.getItems()[0].getCount());
			} else {
				requiredIngredientAmounts.add(1);
			}
		}
		
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (requiredIngredients.size() == 0) {
				break;
			}
			
			ItemStack currentStack = inventory.getItem(i);
			if (!currentStack.isEmpty()) {
				for (int j = 0; j < requiredIngredients.size(); j++) {
					int currentStackCount = currentStack.getCount();
					if (requiredIngredients.get(j).test(currentStack)) {
						int ingredientCount = requiredIngredientAmounts.get(j);
						ItemStack remainder = currentStack.getRecipeRemainder();
						if (currentStackCount >= ingredientCount) {
							if (!remainder.isEmpty()) {
								remainder.setCount(requiredIngredientAmounts.get(j));
								remainders.add(remainder);
							}
							requiredIngredients.remove(j);
							requiredIngredientAmounts.remove(j);
							j--;
						} else {
							if (!remainder.isEmpty()) {
								remainder.setCount(currentStackCount);
								remainders.add(remainder);
							}
							
							requiredIngredientAmounts.set(j, requiredIngredientAmounts.get(j) - currentStackCount);
						}
						
						currentStack.setCount(currentStackCount - ingredientCount);
					}
				}
			}
		}
		
		return remainders;
	}
	
	// returns recipe remainders
	public static List<ItemStack> removeFromInventoryWithRemainders(ItemStack removeItemStack, Container inventory) {
		List<ItemStack> remainders = new ArrayList<>();
		
		int removeItemStackCount = removeItemStack.getCount();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack currentStack = inventory.getItem(i);
			if (ItemStack.isSameItemSameTags(currentStack, removeItemStack)) {
				ItemStack remainder = currentStack.getRecipeRemainder();
				
				int amountAbleToDecrement = Math.min(currentStack.getCount(), removeItemStackCount);
				currentStack.shrink(amountAbleToDecrement);
				removeItemStackCount -= amountAbleToDecrement;
				
				if (!remainder.isEmpty()) {
					remainder.setCount(amountAbleToDecrement);
					remainders.add(remainder);
				}
			}
			if (removeItemStackCount == 0) {
				return remainders;
			}
		}
		return remainders;
	}
	
	public static boolean canExtract(Container inv, ItemStack stack, int slot, Direction facing) {
		return !(inv instanceof WorldlyContainer) || ((WorldlyContainer) inv).canTakeItemThroughFace(slot, stack, facing);
	}
	
	public static boolean canCombineItemStacks(ItemStack currentItemStack, ItemStack additionalItemStack) {
		return currentItemStack.isEmpty() || additionalItemStack.isEmpty() || (ItemStack.isSameItemSameTags(currentItemStack, additionalItemStack) && (currentItemStack.getCount() + additionalItemStack.getCount() <= currentItemStack.getMaxStackSize()));
	}
	
	@Nullable
	public static Container getInventoryAt(Level world, double x, double y, double z) {
		Container inventory = null;
		BlockPos blockPos = BlockPos.containing(x, y, z);
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block instanceof WorldlyContainerHolder) {
			inventory = ((WorldlyContainerHolder) block).getContainer(blockState, world, blockPos);
		} else if (blockState.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof Container) {
				inventory = (Container) blockEntity;
				if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
					inventory = ChestBlock.getContainer((ChestBlock) block, blockState, world, blockPos, true);
				}
			}
		}
		
		if (inventory == null) {
			List<Entity> list = world.getEntities((Entity) null, new AABB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntitySelector.CONTAINER_ENTITY_SELECTOR);
			if (!list.isEmpty()) {
				inventory = (Container) list.get(world.random.nextInt(list.size()));
			}
		}
		
		return inventory;
	}
	
	public static Optional<ItemStack> extractLastStack(Container inventory) {
		ItemStack currentStack;
		for (int i = inventory.getContainerSize() - 1; i >= 0; i--) {
			currentStack = inventory.getItem(i);
			if (!currentStack.isEmpty()) {
				inventory.setItem(i, ItemStack.EMPTY);
				return Optional.of(currentStack);
			}
		}
		return Optional.empty();
	}
	
	public static ItemStack addToInventoryUpToSingleStackWithMaxTotalCount(ItemStack itemStack, Container inventory, int maxTotalCount) {
		// check if a stack that can be combined is in the inventory already
		int itemCount = 0;
		int firstEmptySlot = -1;
		ItemStack matchingStack = null;
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack slotStack = inventory.getItem(i);
			
			if (slotStack.isEmpty()) {
				if (firstEmptySlot == -1) {
					firstEmptySlot = i;
				}
			} else {
				itemCount += slotStack.getCount();
				if (ItemStack.isSameItemSameTags(itemStack, slotStack)) {
					matchingStack = slotStack;
				}
			}
		}
		
		int storageLeft = maxTotalCount - itemCount;
		if (storageLeft <= 0) {
			return itemStack;
		}
		
		if (matchingStack != null) {
			int addedCount = Math.min(matchingStack.getMaxStackSize() - matchingStack.getCount(), itemStack.getCount());
			addedCount = Math.min(storageLeft, addedCount);
			if (addedCount > 0) {
				matchingStack.setCount(matchingStack.getCount() + addedCount);
				itemStack.shrink(addedCount);
			}
			return itemStack;
		}
		
		if (firstEmptySlot == -1) {
			return itemStack;
		}
		
		inventory.setItem(firstEmptySlot, itemStack.split(storageLeft));
		return itemStack;
	}
	
	public static int countItemsInInventory(Container inventory) {
		int contentCount = 0;
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			contentCount += stack.getCount();
		}
		return contentCount;
	}
	
}
