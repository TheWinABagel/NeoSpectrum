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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class LineTeleportingIdolBlock extends IdolBlock {
	
	protected final int range;
	
	public LineTeleportingIdolBlock(Properties settings, ParticleOptions particleEffect, int range) {
		super(settings, particleEffect);
		this.range = range;
	}
	
	public static Direction getLookDirection(@NotNull Entity entity, boolean mirrorVertical, boolean mirrorHorizontal) {
		double pitch = entity.getXRot();
		if (pitch < -60) {
			return mirrorVertical ? Direction.UP : Direction.DOWN;
		} else if (pitch > 60) {
			return mirrorVertical ? Direction.DOWN : Direction.UP;
		} else {
			return mirrorHorizontal ? entity.getMotionDirection().getOpposite() : entity.getMotionDirection();
		}
	}
	
	public static Optional<BlockPos> searchForBlock(Level world, BlockPos pos, BlockState searchedState, Direction direction, int range) {
		BlockPos.MutableBlockPos mutable = pos.mutable();
		for (int i = 1; i < range; i++) {
			BlockPos currPos = mutable.relative(direction, i);
			if (world.getBlockState(currPos) == searchedState) {
				return Optional.of(currPos);
			}
		}
		return Optional.empty();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("block.spectrum.line_teleporting_idol.tooltip", range));
		tooltip.add(Component.translatable("block.spectrum.line_teleporting_idol.tooltip2", range));
	}
	
	@Override
	public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
		if (!world.isClientSide && !hasCooldown(state)) {
			if (trigger((ServerLevel) world, pos, state, entity, getLookDirection(entity, true, false).getOpposite())) { // we want the movement direction here, instead of only "top"
				playTriggerParticles((ServerLevel) world, pos);
				playTriggerSound(world, pos);
				triggerCooldown(world, pos);
			}
		}
	}
	
	@Override
	public boolean trigger(ServerLevel world, BlockPos blockPos, BlockState state, @Nullable Entity entity, Direction side) {
		if (entity != null) {
			Optional<BlockPos> foundBlockPos = searchForBlock(world, blockPos, state, side.getOpposite(), this.range);
			if (foundBlockPos.isPresent()) {
				BlockPos targetPos = foundBlockPos.get();
				triggerCooldown(world, targetPos);
				RandomTeleportingIdolBlock.teleportTo(world, entity, targetPos);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int getCooldownTicks() {
		return 10;
	}
	
}
