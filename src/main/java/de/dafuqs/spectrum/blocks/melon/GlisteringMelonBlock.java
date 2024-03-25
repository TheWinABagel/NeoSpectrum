package de.dafuqs.spectrum.blocks.melon;

import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.MelonBlock;
import net.minecraft.world.level.block.StemBlock;

public class GlisteringMelonBlock extends MelonBlock {
	
	public GlisteringMelonBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public StemBlock getStem() {
		return (StemBlock) SpectrumBlocks.GLISTERING_MELON_STEM;
	}
	
	@Override
	public AttachedStemBlock getAttachedStem() {
		return (AttachedStemBlock) SpectrumBlocks.ATTACHED_GLISTERING_MELON_STEM;
	}
	
}
