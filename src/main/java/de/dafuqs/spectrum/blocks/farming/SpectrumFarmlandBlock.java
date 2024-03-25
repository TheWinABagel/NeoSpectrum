package de.dafuqs.spectrum.blocks.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpectrumFarmlandBlock extends FarmBlock {
	
	protected final BlockState bareState;
	
	public SpectrumFarmlandBlock(Properties settings, BlockState bareState) {
		super(settings);
		this.bareState = bareState;
	}
	
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (!state.canSurvive(world, pos)) {
			setBare(null, state, world, pos);
		}
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		int i = state.getValue(MOISTURE);
		if (!isNearWater(world, pos) && !world.isRainingAt(pos.above())) {
			if (i > 0) {
				world.setBlock(pos, state.setValue(MOISTURE, i - 1), 2);
			} else if (!shouldMaintainFarmland(world, pos)) {
				setBare(null, state, world, pos);
			}
		} else if (i < 7) {
			world.setBlock(pos, state.setValue(MOISTURE, 7), 2);
		}
	}
	
	@Override
	public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (!world.isClientSide && world.random.nextFloat() < fallDistance - 1F
				&& entity instanceof LivingEntity
				&& (entity instanceof Player || world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
				&& entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F) {
			
			setBare(entity, state, world, pos);
		}
		
		entity.causeFallDamage(fallDistance, 1.0F, world.damageSources().fall());
	}
	
	public void setBare(@Nullable Entity entity, BlockState state, Level world, BlockPos pos) {
		BlockState blockState = pushEntitiesUp(state, bareState, world, pos);
		world.setBlockAndUpdate(pos, blockState);
		world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockState));
	}
	
	public static boolean shouldMaintainFarmland(@NotNull BlockGetter world, @NotNull BlockPos pos) {
		Block block = world.getBlockState(pos.above()).getBlock();
		return block instanceof CropBlock || block instanceof StemBlock || block instanceof AttachedStemBlock;
	}

	protected boolean isNearWater(LevelReader world, BlockPos pos) {
		for (BlockPos testPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
			if (world.getFluidState(testPos).is(FluidTags.WATER)) {
				return true;
			}
		}
		return false;
	}

}
