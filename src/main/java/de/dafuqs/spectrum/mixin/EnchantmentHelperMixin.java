package de.dafuqs.spectrum.mixin;

import com.google.common.collect.Lists;
import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	
	@Inject(method = "getPossibleEntries(ILnet/minecraft/item/ItemStack;Z)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
	private static void getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
		if (stack.getItem() instanceof ExtendedEnchantable) {
			List<EnchantmentInstance> list = Lists.newArrayList();
			Iterator<Enchantment> enchantments = BuiltInRegistries.ENCHANTMENT.iterator();
			
			while (true) {
				Enchantment enchantment;
				do {
					do {
						do {
							if (!enchantments.hasNext()) {
								cir.setReturnValue(list);
								return;
							}
							
							enchantment = enchantments.next();
						} while (enchantment.isTreasureOnly() && !treasureAllowed);
					} while (!enchantment.isDiscoverable());
				} while (!enchantment.canEnchant(stack)); // this line is the only change, away from "enchantment.type.isAcceptableItem(item)"
				for (int level = enchantment.getMaxLevel(); level > enchantment.getMinLevel() - 1; --level) {
					if (power >= enchantment.getMinCost(level) && power <= enchantment.getMaxCost(level)) {
						list.add(new EnchantmentInstance(enchantment, level));
						break;
					}
				}
			}
		}
	}
	
}
