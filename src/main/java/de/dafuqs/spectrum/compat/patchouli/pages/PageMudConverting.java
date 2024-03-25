package de.dafuqs.spectrum.compat.patchouli.pages;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.fluid_converting.MudConvertingRecipe;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.resources.ResourceLocation;

public class PageMudConverting extends PageFluidConverting<MudConvertingRecipe> {

    private static final ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/patchouli/mud.png");

    public PageMudConverting() {
        super(SpectrumRecipeTypes.MUD_CONVERTING);
    }

    @Override
    public ResourceLocation getBackgroundTexture() {
        return BACKGROUND_TEXTURE;
    }

}