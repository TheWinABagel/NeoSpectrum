package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WitherBoss.class)
public abstract class WitherEntityMixin {
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setCovetedItem()V"),
			method = "dropEquipment(Lnet/minecraft/entity/damage/DamageSource;IZ)V", locals = LocalCapture.CAPTURE_FAILSOFT)
	private void spawnEntity(DamageSource source, int lootingMultiplier, boolean allowDrops, CallbackInfo ci, ItemEntity itemEntity) {
		Entity attackerEntity = source.getEntity();
		if (attackerEntity instanceof LivingEntity livingAttacker) {
			Level world = attackerEntity.level();
			int cloversFavorLevel = SpectrumEnchantmentHelper.getUsableLevel(SpectrumEnchantments.CLOVERS_FAVOR, livingAttacker.getMainHandItem(), livingAttacker);
			if (cloversFavorLevel > 0) {
				int additionalCount = (int) (cloversFavorLevel / 2.0F + world.random.nextFloat() * cloversFavorLevel);
				itemEntity.getItem().setCount(itemEntity.getItem().getCount() + additionalCount);
			}
		}
	}
	
}
