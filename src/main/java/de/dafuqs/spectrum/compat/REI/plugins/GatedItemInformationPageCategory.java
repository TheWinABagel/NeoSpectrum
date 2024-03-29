package de.dafuqs.spectrum.compat.REI.plugins;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class GatedItemInformationPageCategory extends GatedDisplayCategory<GatedItemInformationDisplay> {
	
	@Override
    public void setupWidgets(Point startPoint, Rectangle bounds, List<Widget> widgets, @NotNull GatedItemInformationDisplay display) {
		Minecraft client = Minecraft.getInstance();
		Item item = display.getItem();
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 5, startPoint.y + 3)).entries(display.getInputEntries().get(0)).markInput());
		widgets.add(Widgets.createLabel(new Point(startPoint.x + 15, startPoint.y + 7), item.getDescription()).leftAligned().color(0x000000).noShadow());
		
		Rectangle rectangle = new Rectangle(bounds.getCenterX() - (bounds.width / 2), bounds.y + 30, bounds.width, bounds.height - 30);
		widgets.add(Widgets.createSlotBase(rectangle));
		
		List<FormattedCharSequence> descriptionLines = client.font.split(display.getDescription(), bounds.width - 11);
		widgets.add(new ScrollableTextWidget(rectangle, descriptionLines));
		
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 112, startPoint.y + 70)).entry(getBackgroundEntryStack()).disableBackground().notInteractable().disableHighlight().disableTooltips());
	}
	
	@Override
	public int getDisplayHeight() {
		return 100;
	}
	
	public abstract EntryStack<?> getBackgroundEntryStack();
	
}
