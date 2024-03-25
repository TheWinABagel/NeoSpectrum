package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.api.item.DamageAwareItem;
import de.dafuqs.spectrum.api.item.GravitableItem;
import de.dafuqs.spectrum.api.item.ItemDamageImmunity;
import de.dafuqs.spectrum.api.item.TickAwareItem;
import de.dafuqs.spectrum.recipe.primordial_fire_burning.PrimordialFireBurningRecipe;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	
	@Shadow
	public abstract ItemStack getStack();
	
	@Shadow
	public abstract void setNeverDespawn();
	
	@Shadow
	public abstract boolean damage(DamageSource source, float amount);
	
	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;DDD)V")
	public void ItemEntity(Level world, double x, double y, double z, ItemStack stack, double velocityX, double velocityY, double velocityZ, CallbackInfo ci) {
		// item stacks that are enchanted with damage proof should never despawn
		if (EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.STEADFAST, stack) > 0) {
			setNeverDespawn();
		}
	}
	
	@Inject(at = @At("TAIL"), method = "tick()V")
	public void tick(CallbackInfo ci) {
		// protect steadfast enchanted item stacks from the void by letting them float above it
		ItemEntity thisItemEntity = ((ItemEntity) (Object) this);
		if (!thisItemEntity.isNoGravity() && thisItemEntity.level().getGameTime() % 8 == 0) {
			int worldMinY = thisItemEntity.level().getMinBuildHeight();
			if (!thisItemEntity.onGround()
					&& thisItemEntity.position().y() < worldMinY + 2
					&& EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.STEADFAST, thisItemEntity.getItem()) > 0) {
				
				if (thisItemEntity.position().y() < worldMinY + 1) {
					thisItemEntity.setPos(thisItemEntity.position().x, worldMinY + 1, thisItemEntity.position().z);
				}
				
				thisItemEntity.setDeltaMovement(0, 0, 0);
				thisItemEntity.setNoGravity(true);
			}
		}
		
		if (thisItemEntity.getItem().getItem() instanceof TickAwareItem tickingItem) {
			tickingItem.onItemEntityTicked(thisItemEntity);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
	public void spectrumItemStackDamageActions(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (amount > 0 && this.getStack().getItem() instanceof DamageAwareItem damageAwareItem) {
			damageAwareItem.onItemEntityDamaged(source, amount, (ItemEntity) (Object) this);
		}
	}
	
	@Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
	private void isDamageProof(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (ItemDamageImmunity.isImmuneTo(((ItemEntity) (Object) this).getItem(), source)) {
			callbackInfoReturnable.setReturnValue(true);
		}
		if(source.is(SpectrumDamageTypes.PRIMORDIAL_FIRE)) {
			ItemEntity thisItemEntity = ((ItemEntity) (Object) this);
			Level world = thisItemEntity.level();

			if(PrimordialFireBurningRecipe.processItemEntity(world, thisItemEntity)) {
				callbackInfoReturnable.setReturnValue(true);
			}
		}
	}
	
	@Inject(method = "isFireImmune()Z", at = @At("HEAD"), cancellable = true)
	private void isFireProof(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (ItemDamageImmunity.isImmuneTo(((ItemEntity) (Object) this).getItem(), DamageTypeTags.IS_FIRE)) {
			callbackInfoReturnable.setReturnValue(true);
		}
	}
	
	@Inject(method = "tick()V", at = @At("TAIL"))
	public void doGravityEffects(CallbackInfo ci) {
		ItemEntity itemEntity = ((ItemEntity) (Object) this);
		
		if (itemEntity.isNoGravity()) {
			return;
		}
		
		ItemStack stack = itemEntity.getItem();
		Item item = stack.getItem();
		
		if (item instanceof GravitableItem gravitableItem) {
			// if the stack is floating really high => delete it
			gravitableItem.applyGravity(stack, itemEntity.level(), itemEntity);
		}
		
	}
	
}
