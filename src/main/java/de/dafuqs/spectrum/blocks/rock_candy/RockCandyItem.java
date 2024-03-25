package de.dafuqs.spectrum.blocks.rock_candy;

import net.minecraft.world.item.Item;

public class RockCandyItem extends Item implements RockCandy {
	
	protected final RockCandyVariant rockCandyVariant;
	
	public RockCandyItem(Properties settings, RockCandyVariant rockCandyVariant) {
		super(settings);
		this.rockCandyVariant = rockCandyVariant;
	}
	
	@Override
	public RockCandyVariant getVariant() {
		return rockCandyVariant;
	}
	
}
