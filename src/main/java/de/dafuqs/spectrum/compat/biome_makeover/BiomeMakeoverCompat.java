package de.dafuqs.spectrum.compat.biome_makeover;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class BiomeMakeoverCompat {
	
	public static final String CURSED_TAG = "BMCursed";
	
	// If any input item has the "BMCursed" tag, the output item will also get it
	// This resolves exploits where players are able to indefinitely increase the
	// enchantment level by transferring the enchantment to new items
	public static void transferBMCursedTag(ItemStack sourceStack, ItemStack destinationStack) {
		for (int i = 0; i < 8; i++) {
			CompoundTag bowlCompound = sourceStack.getTag();
			if (bowlCompound != null && bowlCompound.getBoolean(CURSED_TAG)) {
				destinationStack.getOrCreateTag().putBoolean(CURSED_TAG, true);
			}
		}
	}
	
}
