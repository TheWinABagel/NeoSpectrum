package de.dafuqs.spectrum.compat.REI;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.api.recipe.GatedRecipe;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collections;
import java.util.List;

public abstract class GatedSpectrumDisplay extends BasicDisplay implements GatedRecipeDisplay {

	private final ResourceLocation requiredAdvancementIdentifier;
	private final boolean secret;

	// 1 input => 1 output
	public GatedSpectrumDisplay(GatedRecipe recipe, Ingredient input, ItemStack output) {
		this(recipe, Collections.singletonList(EntryIngredients.ofIngredient(input)), Collections.singletonList(EntryIngredients.of(output)));
	}

	// n inputs => 1 output
	public GatedSpectrumDisplay(GatedRecipe recipe, List<EntryIngredient> inputs, ItemStack output) {
		this(recipe, inputs, Collections.singletonList(EntryIngredients.of(output)));
	}

	// n inputs => m outputs
	public GatedSpectrumDisplay(GatedRecipe recipe, List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
		super(inputs, outputs);
		this.secret = recipe.isSecret();
		this.requiredAdvancementIdentifier = recipe.getRequiredAdvancementIdentifier();
	}

	@Override
	public boolean isUnlocked() {
		Minecraft client = Minecraft.getInstance();
		return AdvancementHelper.hasAdvancement(client.player, this.requiredAdvancementIdentifier);
	}

	@Override
	public boolean isSecret() {
		return secret;
	}

}