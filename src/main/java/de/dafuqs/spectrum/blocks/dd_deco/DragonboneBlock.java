package de.dafuqs.spectrum.blocks.dd_deco;

import de.dafuqs.spectrum.api.block.MoonstoneStrikeableBlock;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DragonboneBlock extends CrackedDragonboneBlock implements MoonstoneStrikeableBlock {
	
	public DragonboneBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public void onMoonstoneStrike(Level world, BlockPos pos, @Nullable LivingEntity striker) {
		crack(world, pos);
	}
	
	public void crack(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof DragonboneBlock) {
			world.setBlockAndUpdate(pos, SpectrumBlocks.CRACKED_DRAGONBONE.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
			if (world.isClientSide) {
				world.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 1.0F, Mth.randomBetween(world.random, 0.8F, 1.2F));
			}
		}
	}
	
	@Override
	public BlockState getStateForExplosion(Level world, BlockPos blockPos, BlockState stateAtPos) {
		if (stateAtPos.getBlock() instanceof RotatedPillarBlock) {
			return SpectrumBlocks.CRACKED_DRAGONBONE.defaultBlockState().setValue(RotatedPillarBlock.AXIS, stateAtPos.getValue(RotatedPillarBlock.AXIS));
		}
		return Blocks.AIR.defaultBlockState();
	}
	
}
