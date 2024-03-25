package de.dafuqs.spectrum.compat.emi;

import de.dafuqs.spectrum.api.recipe.GatedRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;

import java.util.List;

public abstract class GatedSpectrumEmiRecipe<T extends GatedRecipe> extends SpectrumEmiRecipe {
	public final T recipe;

	public GatedSpectrumEmiRecipe(EmiRecipeCategory category, T recipe, int width, int height) {
		super(category, recipe.getRecipeTypeUnlockIdentifier(), recipe.getId(), width, height);
		this.recipe = recipe;
		this.outputs = List.of(EmiStack.of(recipe.getResultItem(getRegistryManager())));
	}
	
	@Override
	public boolean isUnlocked() {
		return hasAdvancement(recipe.getRequiredAdvancementIdentifier()) && super.isUnlocked();
	}
	
	@Override
	public boolean hideCraftable() {
		return recipe.isSecret() || super.hideCraftable();
	}
	
	
}