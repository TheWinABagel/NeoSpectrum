package de.dafuqs.spectrum.items.food;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class RestorationTeaItem extends TeaItem {
	
	public RestorationTeaItem(Properties settings, FoodProperties bonusFoodComponentWithScone) {
		super(settings, bonusFoodComponentWithScone);
	}
	
	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		tooltip.add(Component.translatable("item.spectrum.restoration_tea.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.restoration_tea.tooltip2").withStyle(ChatFormatting.GRAY));
		super.appendHoverText(itemStack, world, tooltip, tooltipContext);
	}
	
}
