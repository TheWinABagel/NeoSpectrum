package de.dafuqs.spectrum.items.food.beverages;

import de.dafuqs.spectrum.api.item.FermentedItem;
import de.dafuqs.spectrum.items.food.DrinkItem;
import de.dafuqs.spectrum.items.food.beverages.properties.BeverageProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class BeverageItem extends DrinkItem implements FermentedItem {
	
	public BeverageItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public abstract BeverageProperties getBeverageProperties(ItemStack stack);
	
	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		super.appendHoverText(stack, world, tooltip, tooltipContext);
		getBeverageProperties(stack).addTooltip(stack, tooltip);
	}
	
}
