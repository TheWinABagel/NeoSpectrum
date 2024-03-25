package de.dafuqs.spectrum.api.item;

import de.dafuqs.spectrum.items.food.beverages.properties.BeverageProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface FermentedItem {
	
	BeverageProperties getBeverageProperties(ItemStack itemStack);
	
	static boolean isPreviewStack(ItemStack stack) {
		CompoundTag nbtCompound = stack.getTag();
		return nbtCompound != null && nbtCompound.getBoolean("Preview");
	}
	
	static void setPreviewStack(ItemStack stack) {
		CompoundTag compound = stack.getOrCreateTag();
		compound.putBoolean("Preview", true);
		stack.setTag(compound);
	}
	
}
