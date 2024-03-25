package de.dafuqs.spectrum.api.recipe;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public interface DescriptiveGatedRecipe extends GatedRecipe {
	
	Component getDescription();
	
	Item getItem();
	
}
