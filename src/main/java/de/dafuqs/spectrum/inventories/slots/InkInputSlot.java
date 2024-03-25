package de.dafuqs.spectrum.inventories.slots;

import de.dafuqs.spectrum.api.energy.InkStorageItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class InkInputSlot extends Slot {
	
	public InkInputSlot(Container inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	@Override
	public boolean mayPlace(ItemStack stack) {
		return super.mayPlace(stack)
			&& stack.getItem() instanceof InkStorageItem<?> inkStorageItem
			&& (inkStorageItem.getDrainability() == InkStorageItem.Drainability.ALWAYS || inkStorageItem.getDrainability() == InkStorageItem.Drainability.MACHINE_ONLY);
	}
	
}
