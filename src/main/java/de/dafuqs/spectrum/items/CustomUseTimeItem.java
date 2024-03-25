package de.dafuqs.spectrum.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CustomUseTimeItem extends Item {
	
	private final int useTime;
	
	public CustomUseTimeItem(Properties settings, int useTime) {
		super(settings);
		this.useTime = useTime;
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return useTime;
	}
}
