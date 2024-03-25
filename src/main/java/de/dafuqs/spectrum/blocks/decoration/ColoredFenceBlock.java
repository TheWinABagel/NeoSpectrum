package de.dafuqs.spectrum.blocks.decoration;

import com.google.common.collect.Maps;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.FenceBlock;

import java.util.Map;

public class ColoredFenceBlock extends FenceBlock {
	
	private static final Map<DyeColor, ColoredFenceBlock> BLOCKS = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public ColoredFenceBlock(Properties settings, DyeColor color) {
		super(settings);
		this.color = color;
		BLOCKS.put(color, this);
	}
	
	public DyeColor getColor() {
		return this.color;
	}
	
	public static ColoredFenceBlock byColor(DyeColor color) {
		return BLOCKS.get(color);
	}
	
}
