package de.dafuqs.spectrum.blocks.decoration;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class GlowBlock extends Block {
	
	private static final Map<DyeColor, GlowBlock> GLOWBLOCKS = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public GlowBlock(Properties settings, DyeColor color) {
		super(settings);
		this.color = color;
		GLOWBLOCKS.put(color, this);
	}
	
	public DyeColor getColor() {
		return this.color;
	}
	
	public static GlowBlock byColor(DyeColor color) {
		return GLOWBLOCKS.get(color);
	}
	
	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return 1.0F;
	}
	
}
