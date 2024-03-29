package de.dafuqs.spectrum.items.tooltip;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class CraftingTabletTooltipComponent extends SpectrumTooltipComponent {
	
	private final ItemStack itemStack;
	private final FormattedCharSequence description;
	
	public CraftingTabletTooltipComponent(CraftingTabletTooltipData data) {
		this.itemStack = data.getItemStack();
		this.description = data.getDescription().getVisualOrderText();
	}
	
	@Override
	public int getHeight() {
		return 20 + 4;
	}
	
	@Override
	public int getWidth(Font textRenderer) {
		return textRenderer.width(this.description) + 28;
	}
	
	@Override
	public void renderImage(Font textRenderer, int x, int y, GuiGraphics context) {
		int n = x + 1;
		int o = y + 1;
		this.drawSlot(context, n, o, 0, itemStack, textRenderer);
		this.drawOutline(context, x, y, 1, 1);
	}
	
	@Override
	public void renderText(Font textRenderer, int x, int y, Matrix4f matrix4f, MultiBufferSource.BufferSource immediate) {
		textRenderer.drawInBatch(this.description, (float) x + 26, (float) y + 6, 11053224, true, matrix4f, immediate, Font.DisplayMode.NORMAL, 15728880, 0);
	}
	
}
