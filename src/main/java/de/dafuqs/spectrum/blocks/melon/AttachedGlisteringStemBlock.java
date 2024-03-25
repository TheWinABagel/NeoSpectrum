package de.dafuqs.spectrum.blocks.melon;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.StemGrownBlock;

import java.util.function.Supplier;

public class AttachedGlisteringStemBlock extends AttachedStemBlock {
	
	public AttachedGlisteringStemBlock(StemGrownBlock gourdBlock, Supplier<Item> supplier, Properties settings) {
		super(gourdBlock, supplier, settings);
	}
	
}
