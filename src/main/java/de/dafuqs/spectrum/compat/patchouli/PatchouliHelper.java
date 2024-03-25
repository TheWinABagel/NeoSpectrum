package de.dafuqs.spectrum.compat.patchouli;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

import java.util.List;

public class PatchouliHelper {
	
	public static void renderIngredientStack(GuiGraphics dc, GuiBookEntry bookEntry, int x, int y, int mouseX, int mouseY, IngredientStack ingr) {
		List<ItemStack> stacks = ingr.getStacks();
		if (!stacks.isEmpty()) {
			bookEntry.renderItemStack(dc, x, y, mouseX, mouseY, stacks.get(bookEntry.ticksInBook / 20 % stacks.size()));
		}
	}
	
}
