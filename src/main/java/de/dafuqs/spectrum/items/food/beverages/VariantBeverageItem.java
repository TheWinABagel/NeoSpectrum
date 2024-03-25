package de.dafuqs.spectrum.items.food.beverages;

import de.dafuqs.spectrum.items.food.beverages.properties.BeverageProperties;
import de.dafuqs.spectrum.items.food.beverages.properties.VariantBeverageProperties;
import net.minecraft.world.item.ItemStack;

public class VariantBeverageItem extends BeverageItem {
	
	public VariantBeverageItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public BeverageProperties getBeverageProperties(ItemStack itemStack) {
		return VariantBeverageProperties.getFromStack(itemStack);
	}

}
