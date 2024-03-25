package de.dafuqs.spectrum.inventories.slots;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ExtractOnlySlot extends Slot {
	
	public ExtractOnlySlot(Container inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean mayPickup(Player playerEntity) {
		return true;
	}
	
	
}
