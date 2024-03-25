package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

public class SpectrumEnchantmentTags {
	
	public static final TagKey<Enchantment> SPECTRUM_ENCHANTMENT = of("enchantments");
	
	private static TagKey<Enchantment> of(String id) {
		return TagKey.create(Registries.ENCHANTMENT, SpectrumCommon.locate(id));
	}
	
	public static boolean isIn(TagKey<Enchantment> tag, Enchantment enchantment) {
		Optional<ResourceKey<Enchantment>> optionalKey = BuiltInRegistries.ENCHANTMENT.getResourceKey(enchantment);
		if (optionalKey.isEmpty()) {
			return false;
		}
		Optional<Holder.Reference<Enchantment>> registryEntry = BuiltInRegistries.ENCHANTMENT.getHolder(optionalKey.get());
		if (registryEntry.isEmpty()) {
			return false;
		}
		return BuiltInRegistries.ENCHANTMENT.getOrCreateTag(tag).contains(registryEntry.get());
	}
	
}
