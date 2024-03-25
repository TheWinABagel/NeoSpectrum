package de.dafuqs.spectrum.blocks.melon;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;

import java.util.function.Supplier;

public class GlisteringStemBlock extends StemBlock {
	
	public GlisteringStemBlock(StemGrownBlock gourdBlock, Supplier<Item> supplier, Properties settings) {
		super(gourdBlock, supplier, settings);
	}
	
}
