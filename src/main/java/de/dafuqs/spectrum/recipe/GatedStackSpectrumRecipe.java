package de.dafuqs.spectrum.recipe;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public abstract class GatedStackSpectrumRecipe extends GatedSpectrumRecipe {
	
	protected GatedStackSpectrumRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier) {
		super(id, group, secret, requiredAdvancementIdentifier);
	}
	
	public abstract List<IngredientStack> getIngredientStacks();
	
	
	/**
	 * Gets the recipes required ingredients
	 *
	 * @deprecated should not be used. Instead, use getIngredientStacks(), which includes item counts
	 */
	@Override
	@Deprecated
	public NonNullList<Ingredient> getIngredients() {
		return IngredientStack.listIngredients(getIngredientStacks());
	}
	
}
