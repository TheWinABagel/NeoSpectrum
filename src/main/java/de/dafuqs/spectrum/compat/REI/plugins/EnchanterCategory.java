package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class EnchanterCategory<T extends EnchanterDisplay> extends GatedDisplayCategory<T> {
	
	public final static ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/container/enchanter.png");
	public static final EntryIngredient ENCHANTER = EntryIngredients.of(SpectrumBlocks.ENCHANTER);
	
	@Override
	public Renderer getIcon() {
		return EntryStacks.of(SpectrumBlocks.ENCHANTER);
	}
	
	@Override
	public void setupWidgets(Point startPoint, Rectangle bounds, List<Widget> widgets, @NotNull T display) {
		// enchanter structure background					            destinationX	 destinationY   sourceX, sourceY, width, height
		widgets.add(Widgets.createTexturedWidget(BACKGROUND_TEXTURE, startPoint.x - 8 + 12, startPoint.y - 7 + 21, 0, 0, 54, 54));
		
		// Knowledge Gem and Enchanter
		List<EntryIngredient> inputs = display.getInputEntries();
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8 + 111, startPoint.y - 7 + 14)).markInput().entries(inputs.get(9)));
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8 + 111, startPoint.y - 7 + 60)).entries(ENCHANTER).disableBackground());
		
		// center input slot
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8 + 31, startPoint.y - 7 + 40)).markInput().entries(inputs.get(0)));
		
		// surrounding input slots
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8 + 18, startPoint.y - 7 + 9)).markInput().entries(inputs.get(1)));
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8 + 44, startPoint.y - 7 + 9)).markInput().entries(inputs.get(2)));
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8 + 62, startPoint.y - 7 + 27)).markInput().entries(inputs.get(3)));
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8 + 62, startPoint.y - 7 + 53)).markInput().entries(inputs.get(4)));
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8 + 44, startPoint.y - 7 + 71)).markInput().entries(inputs.get(5)));
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8 + 18, startPoint.y - 7 + 71)).markInput().entries(inputs.get(6)));
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8, startPoint.y - 7 + 53)).markInput().entries(inputs.get(7)));
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8, startPoint.y - 7 + 27)).markInput().entries(inputs.get(8)));
		
		// output arrow and slot
		List<EntryIngredient> output = display.getOutputEntries();
		widgets.add(Widgets.createArrow(new Point(startPoint.x - 8 + 80, startPoint.y - 7 + 40)).animationDurationTicks(getCraftingTime(display)));
		widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x - 8 + 111, startPoint.y - 7 + 40)));
		widgets.add(Widgets.createSlot(new Point(startPoint.x - 8 + 111, startPoint.y - 7 + 40)).markOutput().disableBackground().entries(output.get(0)));
		
		widgets.add(Widgets.createLabel(new Point(startPoint.x - 11 + 70, startPoint.y - 11 + 85), getDescriptionText(display)).leftAligned().color(0x3f3f3f).noShadow());
	}
	
	public abstract int getCraftingTime(@NotNull T display);
	
	public abstract Component getDescriptionText(@NotNull T display);
	
	@Override
	public int getDisplayHeight() {
		return 92;
	}
	
}
