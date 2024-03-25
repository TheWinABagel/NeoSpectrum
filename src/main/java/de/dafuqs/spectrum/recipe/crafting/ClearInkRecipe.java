package de.dafuqs.spectrum.recipe.crafting;

import de.dafuqs.spectrum.api.energy.InkStorageItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

public class ClearInkRecipe extends SingleItemCraftingRecipe {
	
	public static final RecipeSerializer<ClearInkRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(ClearInkRecipe::new);
	
	public ClearInkRecipe(ResourceLocation identifier, CraftingBookCategory category) {
		super(identifier, category);
	}
	
	@Override
	public boolean matches(Level world, ItemStack stack) {
		return stack.getItem() instanceof InkStorageItem;
	}
	
	@Override
	public ItemStack craft(ItemStack stack) {
		if (stack.getItem() instanceof InkStorageItem<?> inkStorageItem) {
			stack = stack.copy();
			stack.setCount(1);
			inkStorageItem.clearEnergyStorage(stack);
		}
		return stack;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
}
