package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.blocks.conditional.colored_tree.ColoredLogBlock;
import de.dafuqs.spectrum.blocks.conditional.colored_tree.ColoredStrippedLogBlock;
import de.dafuqs.spectrum.blocks.conditional.colored_tree.ColoredStrippedWoodBlock;
import de.dafuqs.spectrum.blocks.conditional.colored_tree.ColoredWoodBlock;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.world.item.DyeColor;

public class SpectrumStrippableBlocks {
	
	public static void register() {
		StrippableBlockRegistry.register(SpectrumBlocks.SLATE_NOXCAP_STEM, SpectrumBlocks.STRIPPED_SLATE_NOXCAP_STEM);
		StrippableBlockRegistry.register(SpectrumBlocks.EBONY_NOXCAP_STEM, SpectrumBlocks.STRIPPED_EBONY_NOXCAP_STEM);
		StrippableBlockRegistry.register(SpectrumBlocks.IVORY_NOXCAP_STEM, SpectrumBlocks.STRIPPED_IVORY_NOXCAP_STEM);
		StrippableBlockRegistry.register(SpectrumBlocks.CHESTNUT_NOXCAP_STEM, SpectrumBlocks.STRIPPED_CHESTNUT_NOXCAP_STEM);
		
		for (DyeColor dyeColor : DyeColor.values()) {
			StrippableBlockRegistry.register(ColoredLogBlock.byColor(dyeColor), ColoredStrippedLogBlock.byColor(dyeColor));
			StrippableBlockRegistry.register(ColoredWoodBlock.byColor(dyeColor), ColoredStrippedWoodBlock.byColor(dyeColor));
		}
	}
	
}
