package de.dafuqs.spectrum.blocks.decoration;

import com.google.common.collect.Maps;
import de.dafuqs.spectrum.registries.SpectrumWoodTypes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.FenceGateBlock;

import java.util.Map;

public class ColoredFenceGateBlock extends FenceGateBlock {
	
	private static final Map<DyeColor, ColoredFenceGateBlock> BLOCKS = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public ColoredFenceGateBlock(Properties settings, DyeColor color) {
		super(settings, SpectrumWoodTypes.COLORED_WOOD);
		this.color = color;
		BLOCKS.put(color, this);
	}
	
	public DyeColor getColor() {
		return this.color;
	}
	
	public static ColoredFenceGateBlock byColor(DyeColor color) {
		return BLOCKS.get(color);
	}
	
}
