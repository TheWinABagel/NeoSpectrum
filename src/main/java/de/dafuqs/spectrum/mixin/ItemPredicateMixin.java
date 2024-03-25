package de.dafuqs.spectrum.mixin;

import com.google.common.collect.ImmutableSet;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.HashSet;
import java.util.Set;

@Mixin(ItemPredicate.class)
public abstract class ItemPredicateMixin {
	
	// thank you so, so much @williewillus / @Botania for this snippet of code
	// https://github.com/VazkiiMods/Botania/blob/1.18.x/Fabric/src/main/java/vazkii/botania/fabric/mixin/FabricMixinItemPredicate.java
	@ModifyVariable(at = @At("HEAD"), method = "<init>(Lnet/minecraft/registry/tag/TagKey;Ljava/util/Set;Lnet/minecraft/predicate/NumberRange$IntRange;Lnet/minecraft/predicate/NumberRange$IntRange;[Lnet/minecraft/predicate/item/EnchantmentPredicate;[Lnet/minecraft/predicate/item/EnchantmentPredicate;Lnet/minecraft/potion/Potion;Lnet/minecraft/predicate/NbtPredicate;)V", argsOnly = true)
	private static Set<Item> addSpectrumShears(Set<Item> set) {
		if (set != null && set.contains(Items.SHEARS)) {
			set = new HashSet<>(set);
			set.add(SpectrumItems.BEDROCK_SHEARS);
			set = ImmutableSet.copyOf(set);
		}
		return set;
	}
	
}