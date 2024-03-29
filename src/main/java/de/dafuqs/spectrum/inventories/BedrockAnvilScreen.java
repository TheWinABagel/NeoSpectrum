package de.dafuqs.spectrum.inventories;

import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.LoreHelper;
import de.dafuqs.spectrum.helpers.RenderHelper;
import de.dafuqs.spectrum.networking.SpectrumC2SPackets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class BedrockAnvilScreen extends AbstractContainerScreen<BedrockAnvilScreenHandler> implements ContainerListener {
	
	private static final ResourceLocation TEXTURE = SpectrumCommon.locate("textures/gui/container/bedrock_anvil.png");
	private final Player player;
	private EditBox nameField;
	private EditBox loreField;
	
	public BedrockAnvilScreen(BedrockAnvilScreenHandler handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
		this.player = inventory.player;
		
		this.titleLabelX = 60;
		this.titleLabelY = this.titleLabelY + 2;
		this.inventoryLabelY = 95;
		this.imageHeight = 190;
	}
	
	@Override
	public void containerTick() {
		super.containerTick();
		this.nameField.tick();
		this.loreField.tick();
	}
	
	@Override
	protected void init() {
		super.init();
		this.setup();
		menu.addSlotListener(this);
	}
	
	@Override
	public void removed() {
		super.removed();
		menu.removeSlotListener(this);
	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
		renderBackground(drawContext);
		super.render(drawContext, mouseX, mouseY, delta);
		
		RenderSystem.disableBlend();
		renderForeground(drawContext, mouseX, mouseY, delta);
		renderTooltip(drawContext, mouseX, mouseY);
	}
	
	protected void setup() {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		
		this.nameField = new EditBox(this.font, i + 62, j + 24, 98, 12, Component.translatable("container.spectrum.bedrock_anvil"));
		this.nameField.setCanLoseFocus(false);
		this.nameField.setEditable(false);
		this.nameField.setTextColor(-1);
		this.nameField.setTextColorUneditable(-1);
		this.nameField.setBordered(false);
		this.nameField.setMaxLength(BedrockAnvilScreenHandler.MAX_NAME_LENGTH);
		this.nameField.setValue("");
		this.nameField.setResponder(this::onRenamed);
		this.addWidget(this.nameField);
		
		this.loreField = new EditBox(this.font, i + 45, j + 76, 116, 12, Component.translatable("container.spectrum.bedrock_anvil.lore"));
		this.loreField.setCanLoseFocus(false);
		this.loreField.setEditable(false);
		this.loreField.setTextColor(-1);
		this.loreField.setTextColorUneditable(-1);
		this.loreField.setBordered(false);
		this.loreField.setMaxLength(BedrockAnvilScreenHandler.MAX_LORE_LENGTH);
		this.loreField.setValue("");
		this.loreField.setResponder(this::onLoreChanged);
		this.addWidget(this.loreField);
	}
	
	@Override
	public void resize(Minecraft client, int width, int height) {
		String string = this.nameField.getValue();
		init(client, width, height);
		nameField.setValue(string);
		
		String string2 = this.loreField.getValue();
		init(client, width, height);
		loreField.setValue(string2);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			minecraft.player.closeContainer();
		}
		
		if (keyCode == GLFW.GLFW_KEY_TAB) {
			GuiEventListener focusedElement = getFocused();
			if (focusedElement == this.nameField) {
				this.nameField.setFocused(false);
				setFocused(this.loreField);
			} else if (focusedElement == this.loreField) {
				this.loreField.setFocused(false);
				setFocused(this.nameField);
			}
		}
		
		return this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.canConsumeInput()
				|| this.loreField.keyPressed(keyCode, scanCode, modifiers) || this.loreField.canConsumeInput()
				|| super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	private void onRenamed(String name) {
		if (!name.isEmpty()) {
			String string = name;
			Slot slot = menu.getSlot(0);
			if (slot != null && slot.hasItem() && !slot.getItem().hasCustomHoverName() && name.equals(slot.getItem().getHoverName().getString())) {
				string = "";
			}
			
			if (menu.setNewItemName(string)) {
				FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
				packetByteBuf.writeUtf(name);
				ClientPlayNetworking.send(SpectrumC2SPackets.RENAME_ITEM_IN_BEDROCK_ANVIL_PACKET_ID, packetByteBuf);
			}
		}
	}
	
	private void onLoreChanged(String lore) {
		menu.setNewItemLore(lore);
		
		FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
		packetByteBuf.writeUtf(lore);
		ClientPlayNetworking.send(SpectrumC2SPackets.ADD_LORE_IN_BEDROCK_ANVIL_PACKET_ID, packetByteBuf);
	}
	
	@Override
	protected void renderLabels(GuiGraphics drawContext, int mouseX, int mouseY) {
		RenderSystem.disableBlend();
		var textRenderer = this.font;
		drawContext.drawString(textRenderer, this.title, this.titleLabelX, this.titleLabelY, RenderHelper.GREEN_COLOR, false);
		drawContext.drawString(textRenderer, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, RenderHelper.GREEN_COLOR, false);
		drawContext.drawString(textRenderer, Component.translatable("container.spectrum.bedrock_anvil.lore"), inventoryLabelX, 76, RenderHelper.GREEN_COLOR, false);

		int levelCost = (this.menu).getLevelCost();
		if (levelCost > 0 || this.menu.getSlot(2).hasItem()) {
			int textColor = 8453920;
			Component costText;
			if (!menu.getSlot(2).hasItem()) {
				costText = null;
			} else {
				costText = Component.translatable("container.repair.cost", levelCost);
				if (!menu.getSlot(2).mayPickup(this.player)) {
					textColor = 16736352;
				}
			}
			
			if (costText != null) {
				int k = this.imageWidth - 8 - this.font.width(costText) - 2;
				drawContext.fill(k - 2, 67 + 24, this.imageWidth - 8, 79 + 24, 1325400064);
				drawContext.drawString(textRenderer, costText, k, 93, textColor, true);
			}
		}
	}
	
	@Override
	protected void renderBg(GuiGraphics drawContext, float delta, int mouseX, int mouseY) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		
		// the background
		drawContext.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		// the text field backgrounds
		drawContext.blit(TEXTURE, i + 59, j + 20, 0, this.imageHeight + (menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
		drawContext.blit(TEXTURE, i + 42, j + 72, 0, this.imageHeight + (menu.getSlot(0).hasItem() ? 32 : 48), 127, 16);
		
		if ((menu.getSlot(0).hasItem() || menu.getSlot(1).hasItem()) && !menu.getSlot(2).hasItem()) {
			drawContext.blit(TEXTURE, i + 99, j + 45, this.imageWidth, 0, 28, 21);
		}
	}
	
	public void renderForeground(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
		this.nameField.render(drawContext, mouseX, mouseY, delta);
		this.loreField.render(drawContext, mouseX, mouseY, delta);
	}
	
	@Override
	public void slotChanged(AbstractContainerMenu handler, int slotId, ItemStack stack) {
		if (slotId == 0) {
			if (stack.isEmpty()) {
				this.nameField.setEditable(false);
				this.loreField.setEditable(false);
				this.nameField.setCanLoseFocus(false);
				this.loreField.setCanLoseFocus(false);
				this.nameField.setFocused(false);
				this.loreField.setFocused(false);
				this.nameField.setResponder(null);
				this.loreField.setResponder(null);
				this.nameField.setValue("");
				this.loreField.setValue("");
				this.nameField.setResponder(this::onRenamed);
				this.loreField.setResponder(this::onLoreChanged);
			} else {
				this.nameField.setEditable(true);
				this.loreField.setEditable(true);
				this.nameField.setCanLoseFocus(true);
				this.loreField.setCanLoseFocus(true);
				this.nameField.setFocused(true);
				this.nameField.setValue(stack.getHoverName().getString());
				
				String loreString = LoreHelper.getStringFromLoreTextArray(LoreHelper.getLoreList(stack));
				this.loreField.setValue(loreString);
			}
			this.setFocused(this.nameField);
		}
	}
	
	@Override
	public void dataChanged(AbstractContainerMenu handler, int property, int value) {
	
	}
	
}
