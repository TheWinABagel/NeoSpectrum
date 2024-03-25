package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.compat.REI.GatedSpectrumDisplay;
import de.dafuqs.spectrum.recipe.fluid_converting.FluidConvertingRecipe;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public abstract class FluidConvertingDisplay extends GatedSpectrumDisplay {
	
	public FluidConvertingDisplay(FluidConvertingRecipe recipe) {
		super(recipe, recipe.getIngredients().get(0), recipe.getResultItem(BasicDisplay.registryAccess()));
	}
	
	public final EntryIngredient getIn() {
		return getInputEntries().get(0);
	}
	
	public final EntryIngredient getOut() {
		return getOutputEntries().get(0);
	}
	
	@Override
    public boolean isUnlocked() {
		Minecraft client = Minecraft.getInstance();
		return AdvancementHelper.hasAdvancement(client.player, getUnlockIdentifier()) && super.isUnlocked();
	}
	
	public abstract ResourceLocation getUnlockIdentifier();
	
}
