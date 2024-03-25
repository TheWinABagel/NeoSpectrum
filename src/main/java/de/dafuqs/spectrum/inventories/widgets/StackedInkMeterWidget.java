package de.dafuqs.spectrum.inventories.widgets;

import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageBlockEntity;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.helpers.RenderHelper;
import de.dafuqs.spectrum.helpers.Support;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class StackedInkMeterWidget implements Renderable, GuiEventListener, NarratableEntry {
	
	public final int x;
	public final int y;
	public final int width;
	public final int height;
	protected boolean hovered;
	protected boolean focused;
	
	protected final Screen screen;
	protected final InkStorageBlockEntity<?> blockEntity;
	
	public StackedInkMeterWidget(int x, int y, int width, int height, Screen screen, InkStorageBlockEntity<?> blockEntity) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.screen = screen;
		this.blockEntity = blockEntity;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= (double) this.x && mouseX < (double) (this.x + this.width) && mouseY >= (double) this.y && mouseY < (double) (this.y + this.height);
	}
	
	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}
	
	@Override
	public boolean isFocused() {
		return focused;
	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
	}
	
	@Override
	public NarrationPriority narrationPriority() {
		return this.hovered ? NarrationPriority.HOVERED : NarrationPriority.NONE;
	}
	
	@Override
	public void updateNarration(NarrationElementOutput builder) {
	
	}
	
	public void drawMouseoverTooltip(GuiGraphics drawContext, int x, int y) {
		Minecraft client = Minecraft.getInstance();
		InkStorage inkStorage = this.blockEntity.getEnergyStorage();
		long currentTotal = inkStorage.getCurrentTotal();
		String readableCurrentTotalString = Support.getShortenedNumberString(currentTotal);
		String percent = Support.getSensiblePercent(inkStorage.getCurrentTotal(), (inkStorage.getMaxTotal()));
		drawContext.renderTooltip(client.font,List.of(Component.translatable("spectrum.tooltip.ink_powered.percent_filled", readableCurrentTotalString, percent)),
			Optional.empty(), x, y);
	}
	
	public void draw(GuiGraphics drawContext) {
		InkStorage inkStorage = this.blockEntity.getEnergyStorage();
		long currentTotal = inkStorage.getCurrentTotal();
		
		if (currentTotal > 0) {
			long maxTotal = inkStorage.getMaxTotal();
			
			int currentHeight = this.y + this.height;
			for (InkColor color : InkColor.all()) {
				long amount = inkStorage.getEnergy(color);
				if (amount > 0) {
					int height = Math.round(((float) amount / (float) maxTotal * this.height));
					if (height > 0) {
						RenderHelper.fillQuad(drawContext.pose(), this.x, currentHeight - height, height, this.width, color.getColor());
					}
					currentHeight -= height;
				}
			}
		}
	}
	
}
