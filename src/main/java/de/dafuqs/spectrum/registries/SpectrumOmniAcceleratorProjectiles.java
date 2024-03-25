package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.api.interaction.OmniAcceleratorProjectile;
import de.dafuqs.spectrum.blocks.shooting_star.ShootingStarItem;
import de.dafuqs.spectrum.entity.entity.BlockFlooderProjectile;
import de.dafuqs.spectrum.entity.entity.ParametricMiningDeviceEntity;
import de.dafuqs.spectrum.entity.entity.ShootingStarEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class SpectrumOmniAcceleratorProjectiles {

	public static void register() {
		OmniAcceleratorProjectile.register(new OmniAcceleratorProjectile() {
			@Override
			public Entity createProjectile(ItemStack stack, LivingEntity shooter, Level world) {
				ThrownEnderpearl enderPearlEntity = new ThrownEnderpearl(world, shooter);
				enderPearlEntity.setItem(stack);
				enderPearlEntity.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 2.0F, 1.0F);
				world.addFreshEntity(enderPearlEntity);
				return enderPearlEntity;
			}
			
			@Override
			public SoundEvent getSoundEffect() {
				return SoundEvents.ENDER_PEARL_THROW;
			}
		}, Items.ENDER_PEARL);
		
		OmniAcceleratorProjectile.register(new OmniAcceleratorProjectile() {
			@Override
			public Entity createProjectile(ItemStack stack, LivingEntity shooter, Level world) {
				if (stack.getItem() instanceof ArrowItem arrowItem) {
					AbstractArrow arrowEntity = arrowItem.createArrow(world, stack, shooter);
					arrowEntity.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 2.0F, 1.0F);
					world.addFreshEntity(arrowEntity);
					return arrowEntity;
				}
				return null;
			}
			
			@Override
			public SoundEvent getSoundEffect() {
				return SoundEvents.ARROW_SHOOT;
			}
		}, ItemTags.ARROWS);
		
		OmniAcceleratorProjectile.register(new OmniAcceleratorProjectile() {
			@Override
			public Entity createProjectile(ItemStack stack, LivingEntity shooter, Level world) {
				Snowball snowballEntity = new Snowball(world, shooter);
				snowballEntity.setItem(stack);
				snowballEntity.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 2.0F, 1.0F);
				world.addFreshEntity(snowballEntity);
				return snowballEntity;
			}
			
			@Override
			public SoundEvent getSoundEffect() {
				return SoundEvents.SNOWBALL_THROW;
			}
		}, Items.SNOWBALL);
		
		OmniAcceleratorProjectile.register(new OmniAcceleratorProjectile() {
			@Override
			public Entity createProjectile(ItemStack stack, LivingEntity shooter, Level world) {
				ThrownEgg eggEntity = new ThrownEgg(world, shooter);
				eggEntity.setItem(stack);
				eggEntity.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 2.0F, 1.0F);
				world.addFreshEntity(eggEntity);
				return eggEntity;
			}
			
			@Override
			public SoundEvent getSoundEffect() {
				return SoundEvents.EGG_THROW;
			}
		}, Items.EGG);
		
		OmniAcceleratorProjectile.register(new OmniAcceleratorProjectile() {
			@Override
			public Entity createProjectile(ItemStack stack, LivingEntity shooter, Level world) {
				Vec3 pos = shooter.position();
				PrimedTnt tntEntity = new PrimedTnt(world, pos.x() + 0.5, pos.y(), pos.z() + 0.5, shooter);
				OmniAcceleratorProjectile.setVelocity(tntEntity, shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 2.0F, 1.0F);
				if (world.addFreshEntity(tntEntity)) {
					world.gameEvent(shooter, GameEvent.PRIME_FUSE, pos);
					return tntEntity;
				}
				return null;
			}
			
			@Override
			public SoundEvent getSoundEffect() {
				return SoundEvents.TNT_PRIMED;
			}
		}, Items.TNT);
		
		OmniAcceleratorProjectile.register(new OmniAcceleratorProjectile() {
			@Override
			public Entity createProjectile(ItemStack stack, LivingEntity shooter, Level world) {
				BlockFlooderProjectile blockFlooderProjectile = new BlockFlooderProjectile(world, shooter);
				blockFlooderProjectile.setItem(stack);
				blockFlooderProjectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 1.5F, 1.0F);
				world.addFreshEntity(blockFlooderProjectile);
				return blockFlooderProjectile;
			}
			
			@Override
			public SoundEvent getSoundEffect() {
				return SoundEvents.SNOWBALL_THROW;
			}
		}, SpectrumItems.BLOCK_FLOODER);
		
		OmniAcceleratorProjectile.register(new OmniAcceleratorProjectile() {
			@Override
			public Entity createProjectile(ItemStack stack, LivingEntity shooter, Level world) {
				ParametricMiningDeviceEntity entity = new ParametricMiningDeviceEntity(world, shooter);
				entity.setItem(stack);
				entity.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0, 1.5F, 0F);
				world.addFreshEntity(entity);
				return entity;
			}
			
			@Override
			public SoundEvent getSoundEffect() {
				return SpectrumSoundEvents.BLOCK_PARAMETRIC_MINING_DEVICE_THROWN;
			}
		}, SpectrumBlocks.PARAMETRIC_MINING_DEVICE);
		
		OmniAcceleratorProjectile.register(new OmniAcceleratorProjectile() {
			@Override
			public Entity createProjectile(ItemStack stack, LivingEntity shooter, Level world) {
				FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(world, stack, shooter);
				fireworkRocketEntity.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0, 1.5F, 0F);
				world.addFreshEntity(fireworkRocketEntity);
				return fireworkRocketEntity;
			}
			
			@Override
			public SoundEvent getSoundEffect() {
				return SoundEvents.FIREWORK_ROCKET_LAUNCH;
			}
		}, Items.FIREWORK_ROCKET);

		OmniAcceleratorProjectile.register(new OmniAcceleratorProjectile() {
			@Override
			public Entity createProjectile(ItemStack stack, LivingEntity shooter, Level world) {
				ShootingStarEntity shootingStarEntity = ((ShootingStarItem) stack.getItem()).getEntityForStack(world, shooter.getEyePosition(), stack);
				OmniAcceleratorProjectile.setVelocity(shootingStarEntity, shooter, shooter.getXRot(), shooter.getYRot(), 0, 3.0F, 0F);
				world.addFreshEntity(shootingStarEntity);

				shootingStarEntity.noPhysics = true;
				shootingStarEntity.move(MoverType.SELF, shootingStarEntity.getDeltaMovement()); // leave the owner
				shootingStarEntity.move(MoverType.SELF, shootingStarEntity.getDeltaMovement()); // leave the owner
				shootingStarEntity.noPhysics = false;

				return shootingStarEntity;
			}

			@Override
			public SoundEvent getSoundEffect() {
				return SpectrumSoundEvents.SHOOTING_STAR_CRACKER;
			}
		}, SpectrumBlocks.GLISTERING_SHOOTING_STAR, SpectrumBlocks.FIERY_SHOOTING_STAR, SpectrumBlocks.COLORFUL_SHOOTING_STAR, SpectrumBlocks.PRISTINE_SHOOTING_STAR, SpectrumBlocks.GEMSTONE_SHOOTING_STAR);

	}

}
