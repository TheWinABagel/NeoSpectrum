package de.dafuqs.spectrum.compat.patchouli.pages;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.fluid_converting.MidnightSolutionConvertingRecipe;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.resources.ResourceLocation;

public class PageMidnightSolutionConverting extends PageFluidConverting<MidnightSolutionConvertingRecipe> {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/patchouli/midnight_solution.png");
	
	public PageMidnightSolutionConverting() {
		super(SpectrumRecipeTypes.MIDNIGHT_SOLUTION_CONVERTING);
	}
	
	@Override
	public ResourceLocation getBackgroundTexture() {
		return BACKGROUND_TEXTURE;
	}
	
}