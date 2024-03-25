package de.dafuqs.spectrum.blocks.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DecayingLightBlock extends WandLightBlock {
	
	public DecayingLightBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		super.randomTick(state, world, pos, random);
		int light = state.getValue(LightBlock.LEVEL);
		if (light < 2) {
			if (state.getValue(WATERLOGGED)) {
				world.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
			} else {
				world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			}
		} else {
			world.setBlock(pos, state.setValue(LightBlock.LEVEL, light - 1), 3);
		}
	}
	
}
