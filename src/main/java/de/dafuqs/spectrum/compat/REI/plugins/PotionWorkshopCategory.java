package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class PotionWorkshopCategory<T extends PotionWorkshopRecipeDisplay> extends GatedDisplayCategory<T> {
	
	public final static ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/container/potion_workshop_3_slots.png");
	
	@Override
	public Renderer getIcon() {
		return EntryStacks.of(SpectrumBlocks.POTION_WORKSHOP);
	}
	
	
	@Override
	public void setupWidgets(Point startPoint, Rectangle bounds, List<Widget> widgets, @NotNull T display) {
		// bubbles
		widgets.add(Widgets.createTexturedWidget(BACKGROUND_TEXTURE, startPoint.x + 17, startPoint.y + 21, 176, 0, 11, 27));
		widgets.add(Widgets.createArrow(new Point(startPoint.x + 60, startPoint.y + 1 + 18)).animationDurationTicks(display.craftingTime));
		
		// input slots
		List<EntryIngredient> inputs = display.getInputEntries();
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 15, startPoint.y + 1 + 49)).entries(inputs.get(0)).markInput()); // mermaids gem
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 63, startPoint.y + 1)).entries(inputs.get(1)).markInput()); // base input
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 15, startPoint.y + 1)).entries(inputs.get(2)).markInput()); // input 1
		widgets.add(Widgets.createSlot(new Point(startPoint.x, startPoint.y + 1 + 20)).entries(inputs.get(3)).markInput()); // input 2
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 30, startPoint.y + 1 + 20)).entries(inputs.get(4)).markInput()); // input 3
		
		// output slot
		List<EntryIngredient> results = display.getOutputEntries();
		EntryIngredient result = EntryIngredient.of(results.get(0));
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 95, startPoint.y + 1 + 19)).entries(result).markOutput());
		
		// description text
		Component text = Component.translatable("container.spectrum.rei.potion_workshop.crafting_time", (display.craftingTime / 20));
		widgets.add(Widgets.createLabel(new Point(startPoint.x + 40, startPoint.y + 1 + 54), text).leftAligned().color(0x3f3f3f).noShadow());
	}
	
	@Override
	public int getDisplayHeight() {
		return 77;
	}
	
}
