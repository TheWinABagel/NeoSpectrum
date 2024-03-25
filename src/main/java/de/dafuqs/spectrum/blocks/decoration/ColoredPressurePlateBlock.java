package de.dafuqs.spectrum.blocks.decoration;

import com.google.common.collect.Maps;
import de.dafuqs.spectrum.registries.SpectrumBlockSetTypes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.PressurePlateBlock;

import java.util.Map;

public class ColoredPressurePlateBlock extends PressurePlateBlock {
	
	private static final Map<DyeColor, ColoredPressurePlateBlock> BLOCKS = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity type, Properties settings, DyeColor color) {
		super(type, settings, SpectrumBlockSetTypes.COLORED_WOOD);
		this.color = color;
		BLOCKS.put(color, this);
	}
	
	public DyeColor getColor() {
		return this.color;
	}
	
	public static ColoredPressurePlateBlock byColor(DyeColor color) {
		return BLOCKS.get(color);
	}
	
}
