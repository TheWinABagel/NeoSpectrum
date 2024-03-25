package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.blocks.energy.ColorPickerBlockEntity;
import de.dafuqs.spectrum.helpers.RenderHelper;
import de.dafuqs.spectrum.inventories.widgets.ColorSelectionWidget;
import de.dafuqs.spectrum.inventories.widgets.InkGaugeWidget;
import de.dafuqs.spectrum.inventories.widgets.StackedInkMeterWidget;
import de.dafuqs.spectrum.networking.SpectrumC2SPacketSender;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.Consumer;

public class ColorPickerScreen extends AbstractContainerScreen<ColorPickerScreenHandler> implements Consumer<InkColor> {
	
	protected final ResourceLocation BACKGROUND = SpectrumCommon.locate("textures/gui/container/color_picker.png");
	protected ColorSelectionWidget colorSelectionWidget;
	protected InkGaugeWidget inkGaugeWidget;
	protected StackedInkMeterWidget inkMeterWidget;
	
	public ColorPickerScreen(ColorPickerScreenHandler handler, Inventory playerInventory, Component title) {
		super(handler, playerInventory, title);
		this.imageHeight = 166;
	}
	
	@Override
	protected void init() {
		super.init();
		
		int startX = (this.width - this.imageWidth) / 2;
		int startY = (this.height - this.imageHeight) / 2;
		
		this.colorSelectionWidget = new ColorSelectionWidget(startX + 113, startY + 55, startX + 139, startY + 25, this, this.menu.getBlockEntity());
		this.inkGaugeWidget = new InkGaugeWidget(startX + 54, startY + 21, 42, 42, this, this.menu.getBlockEntity());
		this.inkMeterWidget = new StackedInkMeterWidget(startX + 100, startY + 21, 4, 40, this, this.menu.getBlockEntity());
		
		this.colorSelectionWidget.setChangedListener(this);
		
		addWidget(this.colorSelectionWidget);
	}
	
	@Override
	protected void renderLabels(GuiGraphics drawContext, int mouseX, int mouseY) {
		// draw "title" and "inventory" texts
		int titleX = (imageWidth - font.width(title)) / 2;
		int titleY = 6;
		Component title = this.title;

		drawContext.drawString(this.font, title.getVisualOrderText(), titleX, titleY, RenderHelper.GREEN_COLOR, false);
		drawContext.drawString(this.font, this.playerInventoryTitle, ColorPickerScreenHandler.PLAYER_INVENTORY_START_X, ColorPickerScreenHandler.PLAYER_INVENTORY_START_Y - 10, RenderHelper.GREEN_COLOR, false);
	}
	
	@Override
	protected void renderBg(GuiGraphics drawContext, float delta, int mouseX, int mouseY) {
		int startX = (this.width - this.imageWidth) / 2;
		int startY = (this.height - this.imageHeight) / 2;
		
		// main background
		drawContext.blit(BACKGROUND, startX, startY, 0, 0, imageWidth, imageHeight);

		this.inkGaugeWidget.draw(drawContext);
		this.inkMeterWidget.draw(drawContext);
		this.colorSelectionWidget.draw(drawContext);
		
		// gauge blanket
		drawContext.blit(BACKGROUND, startX + 52, startY + 18, 176, 0, 46, 46);
	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
		renderBackground(drawContext);
		super.render(drawContext, mouseX, mouseY, delta);
		renderTooltip(drawContext, mouseX, mouseY);
	}
	
	@Override
	protected void renderTooltip(GuiGraphics drawContext, int x, int y) {
		if (this.inkGaugeWidget.isMouseOver(x, y)) {
			this.inkGaugeWidget.drawMouseoverTooltip(drawContext, x, y);
		} else if (this.inkMeterWidget.isMouseOver(x, y)) {
			this.inkMeterWidget.drawMouseoverTooltip(drawContext, x, y);
		} else if (this.colorSelectionWidget.isMouseOver(x, y)) {
			this.colorSelectionWidget.drawMouseoverTooltip(drawContext, x, y);
		} else {
			super.renderTooltip(drawContext, x, y);
		}
	}
	
	@Override
	public void accept(InkColor inkColor) {
		ColorPickerBlockEntity colorPicker = this.menu.getBlockEntity();
		colorPicker.setSelectedColor(inkColor);
		SpectrumC2SPacketSender.sendInkColorSelectedInGUI(inkColor);
	}
	
}