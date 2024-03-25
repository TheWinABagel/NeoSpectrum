package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class FilteringScreen extends AbstractContainerScreen<FilteringScreenHandler> {

    public static final ResourceLocation BACKGROUND = SpectrumCommon.locate("textures/gui/container/filter.png");

    public FilteringScreen(FilteringScreenHandler handler, Inventory playerInventory, Component title) {
        super(handler, playerInventory, title);
        this.imageHeight = 133;
    }

    @Override
    protected void renderLabels(GuiGraphics drawContext, int mouseX, int mouseY) {
        // draw "title" and "inventory" texts
        int titleX = (imageWidth - font.width(title)) / 2;
        int titleY = 6;
        Component title = this.title;
        int inventoryX = 8;
        int intInventoryY = 41;

        drawContext.drawString(this.font, title, titleX, titleY, RenderHelper.GREEN_COLOR, false);
        drawContext.drawString(this.font, this.playerInventoryTitle, inventoryX, intInventoryY, RenderHelper.GREEN_COLOR, false);
    }

    @Override
    protected void renderBg(GuiGraphics drawContext, float delta, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        drawContext.blit(BACKGROUND, x, y, 0, 0, imageWidth, imageHeight);

        for (int i = 0; i < menu.filterInventory.getContainerSize(); i++) {
            Slot s = menu.getSlot(i);
            drawContext.blit(BACKGROUND, x + s.x - 1, y + s.y - 1, 176, 0, 18, 18);
        }
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);
        super.render(drawContext, mouseX, mouseY, delta);
        renderTooltip(drawContext, mouseX, mouseY);
    }

}