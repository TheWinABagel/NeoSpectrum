package de.dafuqs.spectrum.blocks;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockWithTooltip extends Block {
	
	protected final Component tooltipText;
	
	public BlockWithTooltip(Properties settings, Component tooltipText) {
		super(settings);
		this.tooltipText = tooltipText;
	}
	
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(tooltipText);
	}
}
