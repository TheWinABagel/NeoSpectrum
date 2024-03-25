package de.dafuqs.spectrum.inventories.slots;

import de.dafuqs.spectrum.recipe.potion_workshop.PotionWorkshopReactingRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ReagentSlot extends Slot {
	
	public ReagentSlot(Container inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	@Override
	public boolean mayPlace(ItemStack stack) {
		return super.mayPlace(stack) && PotionWorkshopReactingRecipe.isReagent(stack.getItem());
	}
	
}
