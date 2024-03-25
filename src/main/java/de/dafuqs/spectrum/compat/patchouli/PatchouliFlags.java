package de.dafuqs.spectrum.compat.patchouli;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.Map;

public class PatchouliFlags {
	
	public static void register() {
		for (Map.Entry<ResourceKey<Enchantment>, Enchantment> entry : BuiltInRegistries.ENCHANTMENT.entrySet()) {
			ResourceLocation id = entry.getKey().location();
			PatchouliAPI.get().setConfigFlag("spectrum:enchantment_exists_" + id.getNamespace() + "_" + id.getPath(), true);
		}
	}
	
}
