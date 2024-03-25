package de.dafuqs.spectrum.items.food;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class FreigeistItem extends DrinkItem {
	
	public FreigeistItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, tooltip, tooltipContext);
		tooltip.add(Component.translatable("item.spectrum.freigeist.tooltip"));
	}
	
}
