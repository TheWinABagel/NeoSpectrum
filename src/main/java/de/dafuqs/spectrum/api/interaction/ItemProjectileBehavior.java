package de.dafuqs.spectrum.api.interaction;

import de.dafuqs.spectrum.entity.entity.ItemProjectileEntity;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface ItemProjectileBehavior {

	List<Tuple<ItemPredicate, ItemProjectileBehavior>> BEHAVIORS = new ArrayList<>();

	ItemProjectileBehavior DEFAULT = new Default();

	static void register(ItemProjectileBehavior behavior, ItemPredicate predicate) {
		BEHAVIORS.add(new Tuple<>(predicate, behavior));
	}

	static void register(ItemProjectileBehavior behavior, Item... items) {
		BEHAVIORS.add(new Tuple<>(ItemPredicate.Builder.item().of(items).build(), behavior));
	}

	static void register(ItemProjectileBehavior behavior, TagKey<Item> tag) {
		BEHAVIORS.add(new Tuple<>(ItemPredicate.Builder.item().of(tag).build(), behavior));
	}
	
	static @Nullable ItemProjectileBehavior get(ItemStack stack) {
		for (Tuple<ItemPredicate, ItemProjectileBehavior> entry : BEHAVIORS) {
			if (entry.getA().matches(stack)) {
				return entry.getB();
			}
		}
		return DEFAULT;
	}
	
	/**
	 * Invoked when the projectile hits an entity.
	 *
	 * @param projectile The ItemProjectile
	 * @param stack      The stack contained in the ItemProjectile. Quick access to projectile.getStack()
	 * @param owner      The owner of the projectile
	 * @param hitResult  The EntityHitResult. Contains the entity hit and position
	 * @return The stack that should be dropped. If the stack has a count > 0, it automatically gets dropped at the position of the impact. If the item should get consumed, decrement the stack from the parameters and return it here
	 */
	ItemStack onEntityHit(ItemProjectileEntity projectile, ItemStack stack, @Nullable Entity owner, EntityHitResult hitResult);
	
	/**
	 * Invoked when the projectile hits a block
	 *
	 * @param projectile The ItemProjectile
	 * @param stack      The stack contained in the ItemProjectile. Quick access to projectile.getStack()
	 * @param owner      The owner of the projectile
	 * @param hitResult  The EntityHitResult. Contains the entity hit and position
	 * @return The stack that should be dropped. If the stack has a count > 0, it automatically gets dropped at the position of the impact. If the item should get consumed, decrement the stack from the parameters and return it here
	 */
	ItemStack onBlockHit(ItemProjectileEntity projectile, ItemStack stack, @Nullable Entity owner, BlockHitResult hitResult);
	
	
	static ItemProjectileBehavior damaging(float damage, boolean destroyItemOnHit) {
		return new Damaging() {
			@Override
			public boolean destroyItemOnHit() {
				return destroyItemOnHit;
			}
			
			@Override
			public boolean dealDamage(ThrowableItemProjectile projectile, Entity owner, Entity target) {
				return target.hurt(target.damageSources().thrown(projectile, owner), damage);
			}
		};
	}
	
	class Default implements ItemProjectileBehavior {
		
		@Override
		public ItemStack onEntityHit(ItemProjectileEntity projectile, ItemStack stack, Entity owner, EntityHitResult hitResult) {
			Entity target = hitResult.getEntity();
			// Lots of fun(tm) is to be had
			if (target instanceof LivingEntity livingTarget) {
				// attaching name tags, saddle horses, memorize entities...
				if (owner instanceof Player playerOwner) {
					stack.interactLivingEntity(playerOwner, livingTarget, InteractionHand.MAIN_HAND);
				}
				
				// Force-feeds food, applies potions, ...
				stack.getItem().finishUsingItem(stack, livingTarget.level(), livingTarget);
			}
			return stack;
		}
		
		@Override
		public ItemStack onBlockHit(ItemProjectileEntity projectile, ItemStack stack, Entity owner, BlockHitResult hitResult) {
			Level world = projectile.level();
			BlockPos hitPos = hitResult.getBlockPos();
			
			hitResult.withDirection(hitResult.getDirection());
			Direction facing = hitResult.getDirection().getOpposite();
			BlockPos placementPos = hitPos.relative(facing);
			Direction placementDirection = world.isEmptyBlock(placementPos.below()) ? facing : Direction.UP;
			stack.useOn(new DirectionalPlaceContext(world, placementPos, facing, stack, placementDirection));
			
			return stack;
		}
	}
	
	abstract class Damaging implements ItemProjectileBehavior {
		@Override
		public ItemStack onEntityHit(ItemProjectileEntity projectile, ItemStack stack, Entity owner, EntityHitResult hitResult) {
			Entity target = hitResult.getEntity();
			
			if (owner instanceof LivingEntity livingOwner) {
				livingOwner.setLastHurtMob(target);
			}
			
			if (dealDamage(projectile, owner, target)) {
				int targetFireTicks = target.getRemainingFireTicks();
				if (projectile.isOnFire()) {
					target.setRemainingFireTicks(targetFireTicks);
				}
				
				if (target instanceof LivingEntity livingTarget) {
					if (!target.level().isClientSide() && owner instanceof LivingEntity livingOwner) {
						EnchantmentHelper.doPostHurtEffects(livingTarget, livingOwner);
						EnchantmentHelper.doPostDamageEffects(livingOwner, target);
					}
					if (target != owner && target instanceof Player && owner instanceof ServerPlayer serverPlayerOwner && !projectile.isSilent()) {
						serverPlayerOwner.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
					}
					projectile.playSound(SpectrumSoundEvents.BLOCK_CITRINE_CLUSTER_HIT, 1.0F, 1.2F / (projectile.level().getRandom().nextFloat() * 0.2F + 0.9F));
				}
			}
			
			if (destroyItemOnHit()) {
				stack.shrink(1);
			}
			return stack;
		}
		
		public abstract boolean destroyItemOnHit();
		
		public abstract boolean dealDamage(ThrowableItemProjectile projectile, Entity owner, Entity target);
		
		@Override
		public ItemStack onBlockHit(ItemProjectileEntity projectile, ItemStack stack, Entity owner, BlockHitResult hitResult) {
			if(destroyItemOnHit()) {
				stack.shrink(1);
			}
			return stack;
		}
	}

}
