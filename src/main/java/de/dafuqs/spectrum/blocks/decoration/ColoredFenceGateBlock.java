package de.dafuqs.spectrum.blocks.decoration;

import com.google.common.collect.Maps;
import de.dafuqs.spectrum.registries.SpectrumWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class ColoredFenceGateBlock extends FenceGateBlock {
	
	private static final Map<DyeColor, ColoredFenceGateBlock> BLOCKS = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public ColoredFenceGateBlock(Properties settings, DyeColor color) {
		super(settings, SpectrumWoodTypes.COLORED_WOOD);
		this.color = color;
		BLOCKS.put(color, this);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 5;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 20;
	}

	public DyeColor getColor() {
		return this.color;
	}
	
	public static ColoredFenceGateBlock byColor(DyeColor color) {
		return BLOCKS.get(color);
	}
	
}
