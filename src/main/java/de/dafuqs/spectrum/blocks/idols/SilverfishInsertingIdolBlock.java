package de.dafuqs.spectrum.blocks.idols;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SilverfishInsertingIdolBlock extends IdolBlock {
	
	public SilverfishInsertingIdolBlock(Properties settings, ParticleOptions particleEffect) {
		super(settings, particleEffect);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("block.spectrum.silverfish_inserting_idol.tooltip"));
	}
	
	@Override
	public boolean trigger(ServerLevel world, BlockPos blockPos, BlockState state, @Nullable Entity entity, Direction side) {
		int startDirection = world.random.nextInt(4);
		for (int i = 0; i < 4; i++) {
			Direction currentDirection = Direction.from2DDataValue(startDirection + i);
			BlockPos offsetPos = blockPos.relative(currentDirection);
			BlockState offsetState = world.getBlockState(offsetPos);
			if (InfestedBlock.isCompatibleHostBlock(offsetState)) {
				BlockState infestedState = InfestedBlock.infestedStateByHost(offsetState);
				world.setBlockAndUpdate(offsetPos, infestedState);
				world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, offsetPos, Block.getId(offsetState)); // processed in WorldRenderer processGlobalEvent()
				return true;
			}
		}
		
		return false;
	}
	
}
