package de.dafuqs.spectrum.blocks.decoration;

import com.google.common.collect.Maps;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class PigmentBlock extends Block {
	
	private static final Map<DyeColor, PigmentBlock> BLOCKS = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public PigmentBlock(Properties settings, DyeColor color) {
		super(settings);
		this.color = color;
		BLOCKS.put(color, this);
	}
	
	public DyeColor getColor() {
		return this.color;
	}
	
	public static PigmentBlock byColor(DyeColor color) {
		return BLOCKS.get(color);
	}
	
}
