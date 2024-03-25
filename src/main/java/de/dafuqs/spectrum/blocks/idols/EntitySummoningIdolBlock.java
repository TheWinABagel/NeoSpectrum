package de.dafuqs.spectrum.blocks.idols;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class EntitySummoningIdolBlock extends IdolBlock {
	
	protected final EntityType<?> entityType;
	
	public EntitySummoningIdolBlock(Properties settings, ParticleOptions particleEffect, EntityType<?> entityType) {
		super(settings, particleEffect);
		this.entityType = entityType;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("block.spectrum.entity_summoning_idol.tooltip", entityType.getDescription()));
	}
	
	@Override
	public boolean trigger(ServerLevel world, BlockPos blockPos, BlockState state, @Nullable Entity entity, Direction side) {
		// alignPosition: center the mob in the center of the blockPos
		Entity summonedEntity = entityType.create(world);
		if (summonedEntity != null) {
			summonedEntity.moveTo(blockPos.above(), 0.0F, 0.0F);
			if (summonedEntity instanceof Mob mobEntity) {
				mobEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
			}
			afterSummon(world, summonedEntity);
			world.addFreshEntityWithPassengers(summonedEntity);
		}
		return true;
	}
	
	public abstract void afterSummon(ServerLevel world, Entity entity);
	
}
