package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.spectrum.compat.REI.GatedSpectrumDisplay;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class EnchanterDisplay extends GatedSpectrumDisplay {
	
	// first input is the center, all others around clockwise
	public EnchanterDisplay(@NotNull GatedSpectrumRecipe recipe, List<EntryIngredient> inputs, ItemStack output) {
		super(recipe, inputs, output);
	}
	
}