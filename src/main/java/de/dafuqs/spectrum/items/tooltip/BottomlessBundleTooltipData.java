package de.dafuqs.spectrum.items.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class BottomlessBundleTooltipData implements TooltipComponent {
	
	private final ItemStack itemStack;
	private final int amount;
	
	public BottomlessBundleTooltipData(ItemStack itemStack, int amount) {
		this.itemStack = itemStack;
		this.amount = amount;
	}
	
	public ItemStack getItemStack() {
		return this.itemStack;
	}
	
	public int getAmount() {
		return this.amount;
	}
	
}
