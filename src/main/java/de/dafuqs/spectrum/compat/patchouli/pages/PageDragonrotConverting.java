package de.dafuqs.spectrum.compat.patchouli.pages;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.fluid_converting.DragonrotConvertingRecipe;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.resources.ResourceLocation;

public class PageDragonrotConverting extends PageFluidConverting<DragonrotConvertingRecipe> {

    private static final ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/patchouli/dragonrot.png");

    public PageDragonrotConverting() {
        super(SpectrumRecipeTypes.DRAGONROT_CONVERTING);
    }

    @Override
    public ResourceLocation getBackgroundTexture() {
        return BACKGROUND_TEXTURE;
    }

}