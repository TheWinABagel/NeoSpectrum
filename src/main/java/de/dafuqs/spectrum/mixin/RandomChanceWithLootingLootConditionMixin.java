package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.enchantments.CloversFavorEnchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootItemRandomChanceWithLootingCondition.class)
public abstract class RandomChanceWithLootingLootConditionMixin {
	
	@Shadow
	@Final
	float percent;
	
	@Inject(at = @At("RETURN"), method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", cancellable = true)
	public void spectrum$applyRareLootEnchantment(LootContext lootContext, CallbackInfoReturnable<Boolean> cir) {
		// if the result was to not drop a drop before reroll
		// gets more probable with each additional level of Clovers Favor
		if (!cir.getReturnValue() && this.percent < 1.0F) {
			cir.setReturnValue(lootContext.getRandom().nextFloat() < CloversFavorEnchantment.rollChance(this.percent, lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY)));
		}
	}
	
}
