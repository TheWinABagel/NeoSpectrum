package de.dafuqs.spectrum.inventories.widgets;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.blocks.energy.ColorPickerBlockEntity;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static de.dafuqs.spectrum.helpers.RenderHelper.fillQuad;

@Environment(EnvType.CLIENT)
public class ColorSelectionWidget extends AbstractWidget {
	
	protected final ColorPickerBlockEntity colorPicker;
	
	@Nullable
	private Consumer<InkColor> changedListener;
	protected final Screen screen;
	
	final List<Tuple<InkColor, Boolean>> usableColors = new ArrayList<>(); // stores if a certain color should be displayed
	
	final int selectedDotX;
	final int selectedDotY;
	
	public ColorSelectionWidget(int x, int y, int selectedDotX, int selectedDotY, Screen screen, ColorPickerBlockEntity colorPicker) {
		super(x, y, 56, 14, Component.literal(""));
		this.colorPicker = colorPicker;
		this.selectedDotX = selectedDotX;
		this.selectedDotY = selectedDotY;
		this.screen = screen;
		
		for (InkColor inkColor : InkColor.all()) {
			usableColors.add(new Tuple<>(inkColor, AdvancementHelper.hasAdvancementClient(inkColor.getRequiredAdvancement())));
		}
	}
	
	public void setChangedListener(@Nullable Consumer<InkColor> changedListener) {
		this.changedListener = changedListener;
	}
	
	private void onChanged(InkColor newColor) {
		if (this.changedListener != null) {
			this.changedListener.accept(newColor);
		}
	}

	@Override
	protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {

	}

	@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Minecraft client = Minecraft.getInstance();
		boolean colorUnselectionClicked = mouseX >= (double) selectedDotX && mouseX < (double) (selectedDotX + 4) && mouseY >= (double) selectedDotY && mouseY < (double) (selectedDotY + 4);
		if (colorUnselectionClicked) {
			client.player.playNotifySound(SpectrumSoundEvents.BUTTON_CLICK, SoundSource.NEUTRAL, 1.0F, 1.0F);
			onChanged(null);
		}
		
		boolean colorSelectionClicked = mouseX >= (double) this.getX() && mouseX < (double) (this.getX() + this.width) && mouseY >= (double) this.getY() && mouseY < (double) (this.getY() + this.height);
		if (colorSelectionClicked && button == 0) {
			int xOffset = Mth.floor(mouseX) - this.getX();
			int yOffset = Mth.floor(mouseY) - this.getY();
			
			int horizontalColorOffset = xOffset / 7;
			int verticalColorOffset = yOffset / 7;
			int newColorIndex = horizontalColorOffset + verticalColorOffset * 8;
			InkColor newColor = InkColor.all().get(newColorIndex);
			if (this.colorPicker.getSelectedColor() != newColor) {
				if (AdvancementHelper.hasAdvancementClient(newColor.getRequiredAdvancement())) {
					client.player.playNotifySound(SpectrumSoundEvents.BUTTON_CLICK, SoundSource.NEUTRAL, 1.0F, 1.0F);
					onChanged(newColor);
				} else {
					client.player.playNotifySound(SpectrumSoundEvents.USE_FAIL, SoundSource.NEUTRAL, 1.0F, 1.0F);
					onChanged(null);
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {
		builder.add(NarratedElementType.TITLE, Component.translatable("spectrum.narration.color_selection", this.colorPicker.getSelectedColor()));
	}

	public void draw(GuiGraphics drawContext) {
		// draw selection icons
		int i = -1;
		int currentX = this.getX() + 1;
		int currentY = this.getY() + 1;
		for (Tuple<InkColor, Boolean> color : usableColors) {
			if (color.getB()) {
				fillQuad(drawContext.pose(), currentX, currentY, 5, 5, color.getA().getColor());
			}
			i = i + 1;
			currentX = currentX + 7;
			if (i == 7) {
				currentY = currentY + 7;
				currentX = this.getX() + 1;
			}
		}
		
		// draw currently selected icon
		InkColor selectedColor = this.colorPicker.getSelectedColor();
		if (selectedColor != null) {
			fillQuad(drawContext.pose(), selectedDotX, selectedDotY, 4, 4, selectedColor.getColor());
		}
	}
	
	public void drawMouseoverTooltip(GuiGraphics drawContext, int mouseX, int mouseY) {
		Minecraft client = Minecraft.getInstance();
		boolean overUnselection = mouseX >= (double) selectedDotX && mouseX < (double) (selectedDotX + 4) && mouseY >= (double) selectedDotY && mouseY < (double) (selectedDotY + 4);
		if (overUnselection) {
			drawContext.renderTooltip(client.font, List.of(Component.translatable("spectrum.tooltip.ink_powered.unselect_color")), Optional.empty(), getX(), getY());
		} else {
			
			int xOffset = Mth.floor(mouseX) - this.getX();
			int yOffset = Mth.floor(mouseY) - this.getY();
			
			int horizontalColorOffset = xOffset / 7;
			int verticalColorOffset = yOffset / 7;
			int newColorIndex = horizontalColorOffset + verticalColorOffset * 8;
			InkColor newColor = InkColor.all().get(newColorIndex);
			
			if (AdvancementHelper.hasAdvancementClient(newColor.getRequiredAdvancement())) {
				drawContext.renderTooltip(client.font, List.of(newColor.getName()), Optional.empty(), getX(), getY());
			} else {
				drawContext.renderTooltip(client.font, List.of(Component.translatable("spectrum.tooltip.ink_powered.unselect_color")), Optional.empty(), getX(), getY());
			}
		}
	}
}
