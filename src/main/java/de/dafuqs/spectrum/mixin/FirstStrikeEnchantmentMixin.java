package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({LivingEntity.class, Player.class})
public abstract class FirstStrikeEnchantmentMixin {
	
	@ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	public float applyAdditionalFirstStrikeEnchantmentDamage(float amount, DamageSource source) {
		LivingEntity target = (LivingEntity) (Object) this;
		
		if (source.getEntity() instanceof LivingEntity livingAttacker) {
			if (amount != 0F && target.getHealth() == target.getMaxHealth()) {
				ItemStack mainHandStack = livingAttacker.getMainHandItem();
				int level = SpectrumEnchantmentHelper.getUsableLevel(SpectrumEnchantments.FIRST_STRIKE, mainHandStack, livingAttacker);
				if (level > 0) {
					float additionalDamage = getAdditionalFirstStrikeEnchantmentDamage(level);
					amount += additionalDamage;
				}
			}
		}
		return amount;
	}
	
	@Unique
	private float getAdditionalFirstStrikeEnchantmentDamage(int level) {
		return SpectrumCommon.CONFIG.FirstStrikeDamagePerLevel * level;
	}
	
}