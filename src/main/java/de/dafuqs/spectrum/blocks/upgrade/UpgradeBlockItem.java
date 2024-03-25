package de.dafuqs.spectrum.blocks.upgrade;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class UpgradeBlockItem extends BlockItem {
	
	private final String tooltipString;
	
	public UpgradeBlockItem(Block block, Properties settings, String tooltipString) {
		super(block, settings);
		this.tooltipString = tooltipString;
	}
	
	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, tooltip, tooltipContext);
		tooltip.add(Component.translatable("item.spectrum." + this.tooltipString + ".tooltip").withStyle(ChatFormatting.GRAY));
	}
	
}
