package de.dafuqs.spectrum.recipe.crafting;

import de.dafuqs.spectrum.registries.SpectrumItemTags;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class RepairAnythingRecipe extends CustomRecipe {
	
	public static final RecipeSerializer<RepairAnythingRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(RepairAnythingRecipe::new);
	
	private static final Ingredient MOONSTRUCK_NECTAR = Ingredient.of(SpectrumItems.MOONSTRUCK_NECTAR);
	
	public RepairAnythingRecipe(ResourceLocation identifier, CraftingBookCategory category) {
		super(identifier, category);
	}
	
	@Override
	public boolean matches(CraftingContainer craftingInventory, Level world) {
		boolean nectarFound = false;
		boolean itemFound = false;
		
		for (int j = 0; j < craftingInventory.getContainerSize(); ++j) {
			ItemStack itemStack = craftingInventory.getItem(j);
			if (!itemStack.isEmpty()) {
				if (MOONSTRUCK_NECTAR.test(itemStack)) {
					if (nectarFound) {
						return false;
					}
					nectarFound = true;
				} else if (itemStack.isDamageableItem() && itemStack.isDamaged() && !itemStack.is(SpectrumItemTags.INDESTRUCTIBLE_BLACKLISTED)) {
					if (itemFound) {
						return false;
					}
					itemFound = true;
				}
			}
		}
		
		return nectarFound && itemFound;
	}
	
	@Override
	public ItemStack craft(CraftingContainer craftingInventory, RegistryAccess drm) {
		ItemStack itemStack = ItemStack.EMPTY;
		for (int j = 0; j < craftingInventory.getContainerSize(); ++j) {
			itemStack = craftingInventory.getItem(j);
			if (!itemStack.isEmpty() && !MOONSTRUCK_NECTAR.test(itemStack)) {
				break;
			}
		}
		
		if (itemStack.isDamageableItem() && itemStack.isDamaged() && !itemStack.is(SpectrumItemTags.INDESTRUCTIBLE_BLACKLISTED)) {
			ItemStack returnStack = itemStack.copy();
			int damage = returnStack.getDamageValue();
			int maxDamage = returnStack.getMaxDamage();
			
			int newDamage = Math.max(0, damage - maxDamage / 3);
			returnStack.setDamageValue(newDamage);
			return returnStack;
		} else {
			return ItemStack.EMPTY;
		}
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
}
