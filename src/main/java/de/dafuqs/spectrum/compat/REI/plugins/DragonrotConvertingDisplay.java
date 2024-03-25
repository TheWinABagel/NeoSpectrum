package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.spectrum.compat.REI.SpectrumPlugins;
import de.dafuqs.spectrum.recipe.fluid_converting.DragonrotConvertingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.resources.ResourceLocation;

public class DragonrotConvertingDisplay extends FluidConvertingDisplay {
	
	public DragonrotConvertingDisplay(DragonrotConvertingRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public ResourceLocation getUnlockIdentifier() {
		return DragonrotConvertingRecipe.UNLOCK_IDENTIFIER;
	}
	
	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return SpectrumPlugins.DRAGONROT_CONVERTING;
	}
	
}