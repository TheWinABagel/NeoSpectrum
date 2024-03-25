package de.dafuqs.spectrum.recipe.crafting;

import de.dafuqs.spectrum.api.item.InkPoweredPotionFillable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

public class ClearPotionFillableRecipe extends SingleItemCraftingRecipe {
	
	public static final RecipeSerializer<ClearPotionFillableRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(ClearPotionFillableRecipe::new);
	
	public ClearPotionFillableRecipe(ResourceLocation identifier, CraftingBookCategory category) {
		super(identifier, category);
	}
	
	@Override
	public boolean matches(Level world, ItemStack stack) {
		return stack.getItem() instanceof InkPoweredPotionFillable inkPoweredPotionFillable && inkPoweredPotionFillable.isAtLeastPartiallyFilled(stack);
	}
	
	@Override
	public ItemStack craft(ItemStack stack) {
		if (stack.getItem() instanceof InkPoweredPotionFillable inkPoweredPotionFillable) {
			stack = stack.copy();
			stack.setCount(1);
			inkPoweredPotionFillable.clearEffects(stack);
		}
		return stack;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
}
