package de.dafuqs.spectrum.compat.REI.plugins;

import com.google.common.collect.Lists;
import de.dafuqs.spectrum.compat.REI.GatedRecipeDisplay;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class GatedDisplayCategory<T extends GatedRecipeDisplay> implements DisplayCategory<T> {
	
	@Override
	public List<Widget> setupDisplay(@NotNull T display, @NotNull Rectangle bounds) {
		Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - getDisplayHeight() / 2 + 5);
		List<Widget> widgets = Lists.newArrayList();
		
		widgets.add(Widgets.createRecipeBase(bounds));
		
		if (display.isUnlocked()) {
			setupWidgets(startPoint, bounds, widgets, display);
		} else {
			widgets.add(Widgets.createLabel(new Point(startPoint.x - 6, bounds.getCenterY() - 9), Component.translatable("container.spectrum.rei.pedestal_crafting.recipe_not_unlocked_line_1")).leftAligned().color(0x3f3f3f).noShadow());
			widgets.add(Widgets.createLabel(new Point(startPoint.x - 6, bounds.getCenterY() + 1), Component.translatable("container.spectrum.rei.pedestal_crafting.recipe_not_unlocked_line_2")).leftAligned().color(0x3f3f3f).noShadow());
		}
		
		return widgets;
	}
	
	public abstract void setupWidgets(Point startPoint, Rectangle bounds, List<Widget> widgets, @NotNull T display);
	
}
