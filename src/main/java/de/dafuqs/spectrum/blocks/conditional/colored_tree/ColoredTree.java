package de.dafuqs.spectrum.blocks.conditional.colored_tree;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public interface ColoredTree {
	
	ResourceLocation SAPLING_CMY_ADVANCEMENT_IDENTIFIER = SpectrumCommon.locate("milestones/reveal_colored_saplings_cmy");
	ResourceLocation TREES_CMY_IDENTIFIER = SpectrumCommon.locate("milestones/reveal_colored_trees_cmy");
	ResourceLocation TREES_B_IDENTIFIER = SpectrumCommon.locate("milestones/reveal_colored_trees_k");
	ResourceLocation TREES_W_IDENTIFIER = SpectrumCommon.locate("milestones/reveal_colored_trees_w");
	
	enum TreePart {
		SAPLING,
		LOG,
		LEAVES,
		STRIPPED_LOG,
		WOOD,
		STRIPPED_WOOD
	}
	
	static ResourceLocation getTreeCloakAdvancementIdentifier(TreePart treePart, DyeColor color) {
		switch (color) {
			case WHITE, LIGHT_GRAY, GRAY -> {
				return TREES_W_IDENTIFIER;
			}
			case BLACK, BROWN -> {
				return TREES_B_IDENTIFIER;
			}
			default -> {
				return treePart == TreePart.SAPLING ? SAPLING_CMY_ADVANCEMENT_IDENTIFIER : TREES_CMY_IDENTIFIER;
			}
		}
	}
	
	DyeColor getColor();
	
}
