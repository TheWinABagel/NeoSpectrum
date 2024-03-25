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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KnockbackIdolBlock extends IdolBlock {
	
	protected final float horizontalKnockback;
	protected final float verticalKnockback;
	
	public KnockbackIdolBlock(Properties settings, ParticleOptions particleEffect, float horizontalKnockback, float verticalKnockback) {
		super(settings, particleEffect);
		this.horizontalKnockback = horizontalKnockback;
		this.verticalKnockback = verticalKnockback;
	}
	
	@Override
	public boolean trigger(ServerLevel world, BlockPos blockPos, BlockState state, @Nullable Entity entity, Direction side) {
		if (entity != null) {
			switch (side) {
				case NORTH -> {
					entity.push(0, verticalKnockback, -horizontalKnockback);
					entity.hurtMarked = true;
				}
				case EAST -> {
					entity.push(horizontalKnockback, verticalKnockback, 0);
					entity.hurtMarked = true;
				}
				case SOUTH -> {
					entity.push(0, verticalKnockback, horizontalKnockback);
					entity.hurtMarked = true;
				}
				case WEST -> {
					entity.push(-horizontalKnockback, verticalKnockback, 0);
					entity.hurtMarked = true;
				}
				case UP -> {
					entity.push(0, (horizontalKnockback / 4), 0);
					entity.hurtMarked = true;
				}
				default -> {
					entity.push(0, -(horizontalKnockback / 4), 0);
					entity.hurtMarked = true;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("block.spectrum.knockback_idol.tooltip"));
	}
	
}
