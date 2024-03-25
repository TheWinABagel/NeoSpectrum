package de.dafuqs.spectrum.blocks.idols;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StatusEffectIdolBlock extends IdolBlock {
	
	protected final MobEffect statusEffect;
	protected final int amplifier;
	protected final int duration;
	
	public StatusEffectIdolBlock(Properties settings, ParticleOptions particleEffect, MobEffect statusEffect, int amplifier, int duration) {
		super(settings, particleEffect);
		this.statusEffect = statusEffect;
		this.amplifier = amplifier;
		this.duration = duration;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("block.spectrum.potion_effect_idol.tooltip", this.statusEffect.getDisplayName()));
	}
	
	@Override
	public boolean trigger(ServerLevel world, BlockPos blockPos, BlockState state, @Nullable Entity entity, Direction side) {
		if (entity instanceof LivingEntity livingEntity) {
			livingEntity.addEffect(new MobEffectInstance(statusEffect, duration, amplifier, true, true));
			
			// if entity is burning: put out fire
			if (statusEffect == MobEffects.FIRE_RESISTANCE && livingEntity.isOnFire()) {
				livingEntity.setRemainingFireTicks(0);
			}
			
			return true;
		}
		return false;
	}
	
}
