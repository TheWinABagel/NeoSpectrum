package de.dafuqs.spectrum.items.food;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class MoonstruckNectarItem extends DrinkItem {
	
	public MoonstruckNectarItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, tooltip, tooltipContext);
		tooltip.add(Component.translatable("item.spectrum.moonstruck_nectar.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.moonstruck_nectar.tooltip2").withStyle(ChatFormatting.GRAY));
	}
	
}
