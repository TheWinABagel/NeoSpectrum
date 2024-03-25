package de.dafuqs.spectrum.blocks.conditional;

import de.dafuqs.spectrum.api.item.GemstoneColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.state.BlockState;

public class GemstoneOreBlock extends CloakedOreBlock {
	
	private final GemstoneColor gemstoneColor;
	
	public GemstoneOreBlock(Properties settings, UniformInt experienceDropped, GemstoneColor gemstoneColor, ResourceLocation cloakAdvancementIdentifier, BlockState cloakBlockState) {
		super(settings, experienceDropped, cloakAdvancementIdentifier, cloakBlockState);
		this.gemstoneColor = gemstoneColor;
	}
	
	public GemstoneColor getGemstoneColor() {
		return gemstoneColor;
	}
	
}
