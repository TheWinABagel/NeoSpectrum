package de.dafuqs.spectrum.items.food.beverages;

import de.dafuqs.spectrum.items.food.beverages.properties.BeverageProperties;
import de.dafuqs.spectrum.items.food.beverages.properties.StatusEffectBeverageProperties;
import net.minecraft.world.item.ItemStack;

public class SimpleBeverageItem extends BeverageItem {
	
	public SimpleBeverageItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public BeverageProperties getBeverageProperties(ItemStack itemStack) {
		return StatusEffectBeverageProperties.getFromStack(itemStack);
	}
	
}
