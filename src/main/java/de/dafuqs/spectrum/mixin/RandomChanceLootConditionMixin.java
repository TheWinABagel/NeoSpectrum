package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.enchantments.CloversFavorEnchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootItemRandomChanceCondition.class)
public abstract class RandomChanceLootConditionMixin {
	
	@Shadow
	@Final
	float probability;
	
	@Inject(at = @At("RETURN"), method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", cancellable = true)
	public void spectrum$applyRareLootEnchantment(LootContext lootContext, CallbackInfoReturnable<Boolean> cir) {
		// if the result was to not drop a drop before reroll
		// gets more probable with each additional level of Clovers Favor
		if (!cir.getReturnValue() && this.probability < 1.0F) {
			cir.setReturnValue(lootContext.getRandom().nextFloat() < CloversFavorEnchantment.rollChance(this.probability, lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY)));
		}
	}
	
}
