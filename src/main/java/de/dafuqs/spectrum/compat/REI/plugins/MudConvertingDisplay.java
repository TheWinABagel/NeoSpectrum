package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.spectrum.compat.REI.SpectrumPlugins;
import de.dafuqs.spectrum.recipe.fluid_converting.MudConvertingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.resources.ResourceLocation;

public class MudConvertingDisplay extends FluidConvertingDisplay {
	
	public MudConvertingDisplay(MudConvertingRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return SpectrumPlugins.MUD_CONVERTING;
	}
	
	@Override
	public ResourceLocation getUnlockIdentifier() {
		return MudConvertingRecipe.UNLOCK_IDENTIFIER;
	}
	
}