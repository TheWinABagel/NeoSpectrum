package de.dafuqs.spectrum.recipe.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.level.Level;

public abstract class SingleItemCraftingRecipe extends CustomRecipe {
	
	public SingleItemCraftingRecipe(ResourceLocation identifier) {
		super(identifier, CraftingBookCategory.MISC);
	}

	public SingleItemCraftingRecipe(ResourceLocation identifier, CraftingBookCategory category) {
		super(identifier, category);
	}
	
	@Override
	public boolean matches(CraftingContainer craftingInventory, Level world) {
		boolean matchingItemFound = false;
		
		for (int slot = 0; slot < craftingInventory.getContainerSize(); ++slot) {
			ItemStack itemStack = craftingInventory.getItem(slot);
			if (itemStack.isEmpty()) {
				continue;
			}
			
			if (!matchingItemFound && matches(world, itemStack)) {
				matchingItemFound = true;
			} else {
				return false;
			}
		}
		
		return matchingItemFound;
	}
	
	@Override
	public ItemStack craft(CraftingContainer craftingInventory, RegistryAccess drm) {
		ItemStack stack;
		for (int slot = 0; slot < craftingInventory.getContainerSize(); ++slot) {
			stack = craftingInventory.getItem(slot);
			if (!stack.isEmpty()) {
				return craft(stack.copy());
			}
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height > 0;
	}
	
	public abstract boolean matches(Level world, ItemStack stack);
	
	public abstract ItemStack craft(ItemStack stack);
	
}
