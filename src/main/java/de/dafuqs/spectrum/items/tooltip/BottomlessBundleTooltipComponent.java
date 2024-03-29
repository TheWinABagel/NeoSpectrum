package de.dafuqs.spectrum.items.tooltip;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BottomlessBundleTooltipComponent extends SpectrumTooltipComponent {
	
	private static final int MAX_DISPLAYED_SLOTS = 5;
	private final List<ItemStack> itemStacks;
	
	private final int displayedSlotCount;
	private final boolean drawDots;
	
	public BottomlessBundleTooltipComponent(BottomlessBundleTooltipData data) {
		int amount = data.getAmount();
		
		int maxCount = data.getItemStack().getMaxStackSize();
		double totalStacks = (float) amount / (float) maxCount;
		this.displayedSlotCount = Math.max(2, Math.min(MAX_DISPLAYED_SLOTS + 1, (int) Math.ceil(totalStacks) + 1));
		
		this.itemStacks = NonNullList.withSize(5, ItemStack.EMPTY);
		for (int i = 0; i < Math.min(5, displayedSlotCount + 1); i++) {
			ItemStack slotStack = data.getItemStack().copy();
			int stackAmount = Math.min(maxCount, amount - i * maxCount);
			slotStack.setCount(stackAmount);
			this.itemStacks.set(i, slotStack);
		}
		drawDots = totalStacks > MAX_DISPLAYED_SLOTS;
	}
	
	@Override
	public int getHeight() {
		return 20 + 2 + 4;
	}
	
	@Override
	public int getWidth(Font textRenderer) {
		return this.displayedSlotCount * 20 + 2 + 4;
	}
	
	@Override
	public void renderImage(Font textRenderer, int x, int y, GuiGraphics context) {
		int n = x + 1;
		int o = y + 1;
		
		for (int i = 0; i < Math.min(MAX_DISPLAYED_SLOTS + 1, displayedSlotCount); i++) {
			if (i == displayedSlotCount - 1) {
				if (displayedSlotCount == MAX_DISPLAYED_SLOTS + 1) {
					if (drawDots) {
						this.drawDottedSlot(context, n + 5 * 18, o);
					} else {
						this.drawSlot(context, n + i * 18, o, i, ItemStack.EMPTY, textRenderer);
					}
				} else {
					if (this.itemStacks.size() - 1 < i) {
						this.drawSlot(context, n + i * 18, o, i, ItemStack.EMPTY, textRenderer);
					} else {
						this.drawSlot(context, n + i * 18, o, i, this.itemStacks.get(i), textRenderer);
					}
				}
			} else {
				this.drawSlot(context, n + i * 18, o, i, this.itemStacks.get(i), textRenderer);
			}
		}
		this.drawOutline(context, x, y, displayedSlotCount, 1);
	}
	
}
