package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.chests.CompactingChestBlockEntity;
import de.dafuqs.spectrum.helpers.RenderHelper;
import de.dafuqs.spectrum.networking.SpectrumC2SPackets;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CompactingChestScreen extends AbstractContainerScreen<CompactingChestScreenHandler> {
	
	public static final ResourceLocation BACKGROUND = SpectrumCommon.locate("textures/gui/container/compacting_chest.png");
	private AutoCompactingInventory.AutoCraftingMode autoCraftingMode;
	
	public CompactingChestScreen(CompactingChestScreenHandler handler, Inventory playerInventory, Component title) {
		super(handler, playerInventory, title);
		this.imageHeight = 178;
		this.autoCraftingMode = handler.getCurrentCraftingMode();
	}
	
	@Override
	protected void init() {
		super.init();
		
		//client.keyboard.setRepeatEvents(true);
		setupInputFields(menu.getBlockEntity());
	}
	
	protected void setupInputFields(CompactingChestBlockEntity compactingChestBlockEntity) {
		int x = (this.width - this.imageWidth) / 2 + 3;
		int y = (this.height - this.imageHeight) / 2 + 3;
		
		Button craftingModeButton = Button.builder(Component.literal("Mode"), this::craftingModeButtonPressed)
				.size(16, 16)
				.pos(x + 154, y + 6)
				.build();
		//new ButtonWidget(x + 154, y + 6, 16, 16, Text.literal("Mode"), this::craftingModeButtonPressed);
		addWidget(craftingModeButton);
	}
	
	private void craftingModeButtonPressed(Button buttonWidget) {
		autoCraftingMode = AutoCompactingInventory.AutoCraftingMode.values()[(autoCraftingMode.ordinal() + 1) % AutoCompactingInventory.AutoCraftingMode.values().length];
		this.onValuesChanged();
	}
	
	@Override
	protected void renderLabels(GuiGraphics drawContext, int mouseX, int mouseY) {
		// draw "title" and "inventory" texts
		int titleX = (imageWidth - font.width(title)) / 2; // 8;
		int titleY = 6;
		Component title = this.title;
		int inventoryX = 8;
		int intInventoryY = 83;

		drawContext.drawString(this.font, title, titleX, titleY, RenderHelper.GREEN_COLOR, false);
		drawContext.drawString(this.font, this.playerInventoryTitle, inventoryX, intInventoryY, RenderHelper.GREEN_COLOR, false);
	}
	
	@Override
	protected void renderBg(GuiGraphics drawContext, float delta, int mouseX, int mouseY) {
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		drawContext.blit(BACKGROUND, x, y, 0 ,0, imageWidth, imageHeight);

		// the selected crafting mode
		drawContext.blit(BACKGROUND, x + 154, y + 6, 176, 16 * autoCraftingMode.ordinal(), 16, 16);

	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
		renderBackground(drawContext);
		super.render(drawContext, mouseX, mouseY, delta);
		
		if (mouseX > leftPos + 153 && mouseX < leftPos + 153 + 16 && mouseY > topPos + 5 && mouseY < topPos + 5 + 16) {
			drawContext.renderTooltip(this.font, Component.translatable("block.spectrum.compacting_chest.toggle_crafting_mode"), mouseX, mouseY);
		} else {
			renderTooltip(drawContext, mouseX, mouseY);
		}
	}
	
	private void onValuesChanged() { //todoforge packets
//		FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
//		packetByteBuf.writeInt(autoCraftingMode.ordinal());
//		ClientPlayNetworking.send(SpectrumC2SPackets.CHANGE_COMPACTING_CHEST_SETTINGS_PACKET_ID, packetByteBuf);
	}
	
}