package de.dafuqs.spectrum.compat.gofish;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.fml.ModList;

import java.util.Map;

public class GoFishCompat {
	
	public static final ResourceLocation DEFAULT_CRATES_LOOT_TABLE_ID = new ResourceLocation("gofish", "gameplay/fishing/crates");
	public static final ResourceLocation NETHER_CRATES_LOOT_TABLE_ID = new ResourceLocation("gofish", "gameplay/fishing/nether/crates");
	public static final ResourceLocation END_CRATES_LOOT_TABLE_ID = new ResourceLocation("gofish", "gameplay/fishing/end/crates");
	
	public static final ResourceLocation NETHER_FISH_LOOT_TABLE_ID = new ResourceLocation("gofish", "gameplay/fishing/nether/fish");
	public static final ResourceLocation END_FISH_LOOT_TABLE_ID = new ResourceLocation("gofish", "gameplay/fishing/end/fish");
	
	public static final ResourceLocation DEEPFRY_ENCHANTMENT_ID = new ResourceLocation("gofish", "deepfry");
	
	public static boolean isLoaded() {
		return ModList.get().isLoaded("go-fish");
	}
	
	public static boolean hasDeepfry(ItemStack itemStack) {
		if (!isLoaded()) {
			return false;
		}
		
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
		for (Enchantment enchantment : enchantments.keySet()) {
			if (isDeepfry(enchantment)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isDeepfry(Enchantment enchantment) {
		ResourceLocation id = EnchantmentHelper.getEnchantmentId(enchantment);
		return id != null && id.equals(GoFishCompat.DEEPFRY_ENCHANTMENT_ID);
	}
	
}
