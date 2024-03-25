package de.dafuqs.spectrum.inventories.slots;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class LockableCraftingResultSlot extends ResultSlot {
	
	private final Player player;
	protected final TransientCraftingContainer input;
	protected final int craftingGridStartIndex;
	protected final int craftingGridEndIndex;
	boolean locked;
	
	public LockableCraftingResultSlot(Container craftingResultInventory, int index, int x, int y, Player player, TransientCraftingContainer input, int craftingGridStartIndex, int craftingGridEndIndex) {
		super(player, input, craftingResultInventory, index, x, y);
		this.player = player;
		this.input = input;
		this.craftingGridStartIndex = craftingGridStartIndex;
		this.craftingGridEndIndex = craftingGridEndIndex;
		
		this.locked = false;
	}
	
	@Override
	public boolean mayPickup(Player playerEntity) {
		return !locked;
	}
	
	public void lock() {
		this.locked = true;
	}
	
	public void unlock() {
		this.locked = false;
	}
	
	@Override
	public void onTake(Player player, ItemStack stack) {
		this.checkTakeAchievements(stack);
		NonNullList<ItemStack> defaultedList = player.level().getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, this.input, player.level());
		
		for (int i = craftingGridStartIndex; i < craftingGridEndIndex + 1; ++i) {
			ItemStack slotStack = this.input.getItem(i);
			ItemStack remainingStacks = defaultedList.get(i);
			if (!slotStack.isEmpty()) {
				this.input.removeItem(i, 1);
				slotStack = this.input.getItem(i);
			}
			
			if (!remainingStacks.isEmpty()) {
				if (slotStack.isEmpty()) {
					this.input.setItem(i, remainingStacks);
				} else if (ItemStack.isSameItemSameTags(slotStack, remainingStacks)) {
					remainingStacks.grow(slotStack.getCount());
					this.input.setItem(i, remainingStacks);
				} else if (!this.player.getInventory().add(remainingStacks)) {
					this.player.drop(remainingStacks, false);
				}
			}
		}
	}
	
}
