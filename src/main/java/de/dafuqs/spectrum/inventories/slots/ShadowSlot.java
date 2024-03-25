package de.dafuqs.spectrum.inventories.slots;

import de.dafuqs.spectrum.api.gui.SlotWithOnClickAction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;

public class ShadowSlot extends ReadOnlySlot implements SlotWithOnClickAction {
	
	public ShadowSlot(Container inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	/**
	 * Called when this slot is clicked, before calling any item methods.
	 * Used to determine whether the click is consumed by the slot.
	 *
	 * @param heldStack the stack the in the player's cursor
	 * @param type      the click type, either left or right click
	 * @param player    the player, the held stack can be safely mutated
	 * @return whether to consume the click event or not, returning false will have the event processed by items, and if left unconsumed will be processed by the screen handler
	 */
	@Override
	public boolean onClicked(ItemStack heldStack, ClickAction type, Player player) {
		ItemStack newStack = heldStack.copy();
		newStack.setCount(1);
		this.setByPlayer(newStack);
		
		return true;
	}
	
}
