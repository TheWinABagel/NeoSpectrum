package de.dafuqs.spectrum.compat.patchouli.pages;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.fluid_converting.LiquidCrystalConvertingRecipe;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.resources.ResourceLocation;

public class PageLiquidCrystalConverting extends PageFluidConverting<LiquidCrystalConvertingRecipe> {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/patchouli/liquid_crystal.png");
	
	public PageLiquidCrystalConverting() {
		super(SpectrumRecipeTypes.LIQUID_CRYSTAL_CONVERTING);
	}
	
	@Override
	public ResourceLocation getBackgroundTexture() {
		return BACKGROUND_TEXTURE;
	}
	
}