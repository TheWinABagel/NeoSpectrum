package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.spectrum.compat.REI.SpectrumPlugins;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class EnchanterEnchantingCategory extends EnchanterCategory<EnchanterEnchantingDisplay> {
	
	@Override
	public CategoryIdentifier<EnchanterEnchantingDisplay> getCategoryIdentifier() {
		return SpectrumPlugins.ENCHANTER_CRAFTING;
	}
	
	@Override
	public Component getTitle() {
		return Component.translatable("container.spectrum.rei.enchanting.title");
	}
	
	@Override
	public int getCraftingTime(@NotNull EnchanterEnchantingDisplay display) {
		return display.craftingTime;
	}
	
	@Override
	public Component getDescriptionText(@NotNull EnchanterEnchantingDisplay display) {
		// duration and XP requirements
		// special handling for "1 second". Looks nicer
		if (display.craftingTime == 20) {
			return Component.translatable("container.spectrum.rei.enchanting.crafting_time_one_second", 1);
		} else {
			return Component.translatable("container.spectrum.rei.enchanting.crafting_time", (display.craftingTime / 20));
		}
	}
	
}
