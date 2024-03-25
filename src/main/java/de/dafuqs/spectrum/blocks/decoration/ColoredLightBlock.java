package de.dafuqs.spectrum.blocks.decoration;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class ColoredLightBlock extends RedstoneLampBlock {
	
	private static final Map<DyeColor, ColoredLightBlock> LIGHTS = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public ColoredLightBlock(Properties settings, DyeColor color) {
		super(settings);
		this.color = color;
		LIGHTS.put(color, this);
	}
	
	public DyeColor getColor() {
		return this.color;
	}
	
	public static ColoredLightBlock byColor(DyeColor color) {
		return LIGHTS.get(color);
	}
	
	/**
	 * Disable culling for this block
	 * => the translucent outlines will be rendered
	 * even if the side is obstructed by a block
	 * (disabling culling is not nice for performance,
	 * but usually most sides will be visible either way)
	 */
	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
		return Shapes.empty();
	}
	
}
