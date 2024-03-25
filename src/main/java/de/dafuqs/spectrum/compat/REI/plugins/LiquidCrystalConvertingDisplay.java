package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.spectrum.compat.REI.SpectrumPlugins;
import de.dafuqs.spectrum.recipe.fluid_converting.LiquidCrystalConvertingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.resources.ResourceLocation;

public class LiquidCrystalConvertingDisplay extends FluidConvertingDisplay {
	
	public LiquidCrystalConvertingDisplay(LiquidCrystalConvertingRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return SpectrumPlugins.LIQUID_CRYSTAL_CONVERTING;
	}
	
	@Override
	public ResourceLocation getUnlockIdentifier() {
		return LiquidCrystalConvertingRecipe.UNLOCK_IDENTIFIER;
	}
	
}