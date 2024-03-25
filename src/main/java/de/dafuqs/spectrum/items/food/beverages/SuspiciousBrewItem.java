package de.dafuqs.spectrum.items.food.beverages;

import de.dafuqs.spectrum.api.item.FermentedItem;
import de.dafuqs.spectrum.items.food.beverages.properties.BeverageProperties;
import de.dafuqs.spectrum.items.food.beverages.properties.StatusEffectBeverageProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class SuspiciousBrewItem extends BeverageItem {
	
	public SuspiciousBrewItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public BeverageProperties getBeverageProperties(ItemStack itemStack) {
		return StatusEffectBeverageProperties.getFromStack(itemStack);
	}
	
	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, tooltip, tooltipContext);
		if (FermentedItem.isPreviewStack(itemStack)) {
			tooltip.add(Component.translatable("item.spectrum.suspicious_brew.tooltip.preview").withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.translatable("item.spectrum.suspicious_brew.tooltip.preview2").withStyle(ChatFormatting.GRAY));
		}
	}
	
}
