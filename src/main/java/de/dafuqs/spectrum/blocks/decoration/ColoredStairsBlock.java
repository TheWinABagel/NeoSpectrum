package de.dafuqs.spectrum.blocks.decoration;

import com.google.common.collect.Maps;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class ColoredStairsBlock extends StairBlock {
	
	private static final Map<DyeColor, ColoredStairsBlock> BLOCKS = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public ColoredStairsBlock(BlockState baseBlockState, Properties settings, DyeColor color) {
		super(baseBlockState, settings);
		this.color = color;
		BLOCKS.put(color, this);
	}
	
	public DyeColor getColor() {
		return this.color;
	}
	
	public static ColoredStairsBlock byColor(DyeColor color) {
		return BLOCKS.get(color);
	}
	
}
