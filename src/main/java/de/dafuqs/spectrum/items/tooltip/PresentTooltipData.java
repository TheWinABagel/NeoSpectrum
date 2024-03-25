package de.dafuqs.spectrum.items.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PresentTooltipData implements TooltipComponent {
	
	private final List<ItemStack> itemStacks;
	
	public PresentTooltipData(List<ItemStack> itemStacks) {
		this.itemStacks = itemStacks;
	}
	
	public List<ItemStack> getItemStacks() {
		return this.itemStacks;
	}
	
}
