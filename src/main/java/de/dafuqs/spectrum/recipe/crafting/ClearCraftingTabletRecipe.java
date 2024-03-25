package de.dafuqs.spectrum.recipe.crafting;

import de.dafuqs.spectrum.items.CraftingTabletItem;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ClearCraftingTabletRecipe extends SingleItemCraftingRecipe {
	
	public static final RecipeSerializer<ClearCraftingTabletRecipe> SERIALIZER = new EmptyRecipeSerializer<>(ClearCraftingTabletRecipe::new);
	
	public ClearCraftingTabletRecipe(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public boolean matches(Level world, ItemStack stack) {
		return stack.getItem() instanceof CraftingTabletItem && CraftingTabletItem.getStoredRecipe(world, stack) != null;
	}
	
	@Override
	public ItemStack craft(ItemStack stack) {
		CraftingTabletItem.clearStoredRecipe(stack);
		return stack;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
}
