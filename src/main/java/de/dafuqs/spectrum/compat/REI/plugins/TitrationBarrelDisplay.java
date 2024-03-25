package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.api.recipe.FluidIngredient;
import de.dafuqs.spectrum.compat.REI.FluidIngredientREI;
import de.dafuqs.spectrum.compat.REI.GatedSpectrumDisplay;
import de.dafuqs.spectrum.compat.REI.REIHelper;
import de.dafuqs.spectrum.compat.REI.SpectrumPlugins;
import de.dafuqs.spectrum.recipe.titration_barrel.FermentationData;
import de.dafuqs.spectrum.recipe.titration_barrel.ITitrationBarrelRecipe;
import de.dafuqs.spectrum.recipe.titration_barrel.TitrationBarrelRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TitrationBarrelDisplay extends GatedSpectrumDisplay {
	
	protected final EntryIngredient tappingIngredient;
	protected final int minFermentationTimeHours;
	protected final FermentationData fermentationData;
	
	public TitrationBarrelDisplay(@NotNull ITitrationBarrelRecipe recipe) {
		super(recipe, buildInputs(recipe), List.of(buildOutputs(recipe)));
		if (recipe.getTappingItem() == Items.AIR) {
			this.tappingIngredient = EntryIngredient.empty();
		} else {
			this.tappingIngredient = EntryIngredients.of(recipe.getTappingItem().getDefaultInstance());
		}
		this.minFermentationTimeHours = recipe.getMinFermentationTimeHours();
		this.fermentationData = recipe.getFermentationData();
	}
	
	private static EntryIngredient buildOutputs(ITitrationBarrelRecipe recipe) {
		if (recipe instanceof TitrationBarrelRecipe titrationBarrelRecipe && titrationBarrelRecipe.getFermentationData() != null) {
			return EntryIngredients.ofItemStacks(titrationBarrelRecipe.getOutputVariations(TitrationBarrelRecipe.FERMENTATION_DURATION_DISPLAY_TIME_MULTIPLIERS));
		} else {
			return EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess()));
		}
	}
	
	public static List<EntryIngredient> buildInputs(ITitrationBarrelRecipe recipe) {
		List<EntryIngredient> inputs = REIHelper.toEntryIngredients(recipe.getIngredientStacks());
		if (recipe.getFluidInput() != FluidIngredient.EMPTY) {
			inputs.add(FluidIngredientREI.into(recipe.getFluidInput()));
		}
		return inputs;
	}
	
	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return SpectrumPlugins.TITRATION_BARREL;
	}
	
	@Override
    public boolean isUnlocked() {
		Minecraft client = Minecraft.getInstance();
		return AdvancementHelper.hasAdvancement(client.player, TitrationBarrelRecipe.UNLOCK_ADVANCEMENT_IDENTIFIER) && super.isUnlocked();
	}
	
}
