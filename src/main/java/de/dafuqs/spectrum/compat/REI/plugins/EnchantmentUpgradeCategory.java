package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.spectrum.compat.REI.SpectrumPlugins;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class EnchantmentUpgradeCategory extends EnchanterCategory<EnchantmentUpgradeDisplay> {
	
	@Override
	public CategoryIdentifier<EnchantmentUpgradeDisplay> getCategoryIdentifier() {
		return SpectrumPlugins.ENCHANTMENT_UPGRADE;
	}
	
	@Override
	public Component getTitle() {
		return Component.translatable("container.spectrum.rei.enchantment_upgrading.title");
	}
	
	@Override
	public int getCraftingTime(@NotNull EnchantmentUpgradeDisplay display) {
		return display.requiredItemCount;
	}
	
	@Override
	public Component getDescriptionText(@NotNull EnchantmentUpgradeDisplay display) {
		if (display.requiredItemCount == 0) {
			return Component.empty();
		}
		return Component.translatable("container.spectrum.rei.enchantment_upgrade.required_item_count", display.requiredItemCount);
	}
	
}
