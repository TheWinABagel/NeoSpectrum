package de.dafuqs.spectrum.items.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class CraftingTabletTooltipData implements TooltipComponent {
	
	private final ItemStack itemStack;
	private final Component description;
	
	public CraftingTabletTooltipData(Recipe<?> recipe) {
		Minecraft client = Minecraft.getInstance();
		this.itemStack = recipe.getResultItem(client.level.registryAccess());
		this.description = Component.translatable("item.spectrum.crafting_tablet.tooltip.recipe", this.itemStack.getCount(), this.itemStack.getHoverName().getString());
	}
	
	public ItemStack getItemStack() {
		return this.itemStack;
	}
	
	public Component getDescription() {
		return this.description;
	}
	
}
