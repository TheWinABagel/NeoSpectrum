package de.dafuqs.spectrum.blocks.dd_deco;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import org.jetbrains.annotations.Nullable;

public class SpectrumSpreadableBlock extends SnowyDirtBlock {
	
	protected final @Nullable Block blockAbleToSpreadTo;
	private final BlockState deadState;
	
	public SpectrumSpreadableBlock(Properties settings, @Nullable Block blockAbleToSpreadTo, BlockState deadState) {
		super(settings);
		this.blockAbleToSpreadTo = blockAbleToSpreadTo;
		this.deadState = deadState;
	}
	
	private static boolean canSpread(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos blockPos = pos.above();
		return ableToSurvive(state, world, pos) && !world.getFluidState(blockPos).is(FluidTags.WATER);
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (!ableToSurvive(state, world, pos)) {
			world.setBlockAndUpdate(pos, deadState);
		} else {
			if (world.getMaxLocalRawBrightness(pos.above()) >= 9) {
				BlockState blockState = this.defaultBlockState();
				
				for (int i = 0; i < 4; ++i) {
					BlockPos blockPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
					if (blockAbleToSpreadTo != null && world.getBlockState(blockPos).is(blockAbleToSpreadTo) && canSpread(blockState, world, blockPos)) {
						world.setBlockAndUpdate(blockPos, blockState.setValue(SNOWY, world.getBlockState(blockPos.above()).is(Blocks.SNOW)));
					}
				}
			}
		}
	}

	private static boolean ableToSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos blockPos = pos.above();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.is(Blocks.SNOW) && blockState.getValue(SnowLayerBlock.LAYERS) == 1) {
			return true;
		} else if (blockState.getFluidState().getAmount() == 8) {
			return false;
		} else {
			int i = LightEngine.getLightBlockInto(world, state, pos, blockState, blockPos, Direction.UP, blockState.getLightBlock(world, blockPos));
			return i < world.getMaxLightLevel();
		}
	}
	
}