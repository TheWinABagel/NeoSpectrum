package de.dafuqs.spectrum.inventories;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.TransientCraftingContainer;

public abstract class AutoInventory extends TransientCraftingContainer {
	
	public AutoInventory(int width, int height) {
		super(null, width, height);
	}
	
	@Override
	public boolean stillValid(Player player) {
		return false;
	}
	
}
