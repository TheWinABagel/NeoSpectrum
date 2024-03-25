package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.compat.REI.GatedSpectrumDisplay;
import de.dafuqs.spectrum.compat.REI.REIHelper;
import de.dafuqs.spectrum.compat.REI.SpectrumPlugins;
import de.dafuqs.spectrum.recipe.fusion_shrine.FusionShrineRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class FusionShrineDisplay extends GatedSpectrumDisplay {
	
	protected final float experience;
	protected final int craftingTime;
	protected final Optional<Component> description;
	
	public FusionShrineDisplay(@NotNull FusionShrineRecipe recipe) {
		super(recipe, buildIngredients(recipe), recipe.getResultItem(BasicDisplay.registryAccess()));
		this.experience = recipe.getExperience();
		this.craftingTime = recipe.getCraftingTime();
		this.description = recipe.getDescription();
	}
	
	private static List<EntryIngredient> buildIngredients(FusionShrineRecipe recipe) {
		List<EntryIngredient> inputs = REIHelper.toEntryIngredients(recipe.getIngredientStacks());
		inputs.add(0, EntryIngredients.of(recipe.getFluidInput()));
		return inputs;
	}
	
	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return SpectrumPlugins.FUSION_SHRINE;
	}
	
	@Override
    public boolean isUnlocked() {
		Minecraft client = Minecraft.getInstance();
		return AdvancementHelper.hasAdvancement(client.player, FusionShrineRecipe.UNLOCK_IDENTIFIER) && super.isUnlocked();
	}
	
	public Optional<Component> getDescription() {
		return this.description;
	}
	
}
