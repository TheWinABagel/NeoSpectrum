package de.dafuqs.spectrum.blocks.idols;

import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ProjectileIdolBlock extends IdolBlock {
	
	protected final EntityType<?> entityType;
	protected final SoundEvent triggerSoundEvent;
	protected final float speed;
	protected final float divergence;
	
	public ProjectileIdolBlock(Properties settings, ParticleOptions particleEffect, EntityType<?> entityType, SoundEvent triggerSoundEvent, float speed, float divergence) {
		super(settings, particleEffect);
		this.entityType = entityType;
		this.triggerSoundEvent = triggerSoundEvent;
		this.speed = speed;
		this.divergence = divergence;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("block.spectrum.projectile_idol.tooltip", this.entityType.getDescription()));
	}
	
	@Override
	public boolean trigger(@NotNull ServerLevel world, BlockPos blockPos, BlockState state, @Nullable Entity entity, Direction side) {
		side = side.getOpposite(); // shoot out the other side of the block
		
		BlockPos outputBlockPos = blockPos.relative(side);
		if (world.getBlockState(outputBlockPos).getCollisionShape(world, outputBlockPos).isEmpty()) {
			BlockSource pointer = new BlockSourceImpl(world, blockPos);
			Position outputLocation = getOutputLocation(pointer, side);
			
			Projectile projectileEntity = createProjectile(world, blockPos, outputLocation, side);
			projectileEntity.shoot(side.getStepX(), side.getStepY(), side.getStepZ(), this.speed, this.divergence);
			world.addFreshEntity(projectileEntity);
			world.playSound(null, blockPos, this.triggerSoundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
		}
		
		return true;
	}
	
	public abstract Projectile createProjectile(ServerLevel world, BlockPos mobBlockPos, Position projectilePos, Direction side);
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		// lets the projectiles start really close to the block without blowing itself up
		if (context instanceof EntityCollisionContext entityShapeContext) {
			Entity entity = entityShapeContext.getEntity();
			if (entity != null && entity.getType() == this.entityType && entity.tickCount < 2) {
				return Shapes.empty();
			}
		}
		return state.getShape(world, pos);
	}
	
}
