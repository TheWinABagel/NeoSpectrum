package de.dafuqs.spectrum.api.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ActivatableItem {
	
	String NBT_STRING = "activated";
	
	static void setActivated(ItemStack stack, boolean activated) {
		CompoundTag compound = stack.getOrCreateTag();
		compound.putBoolean(NBT_STRING, activated);
		stack.setTag(compound);
	}
	
	static boolean isActivated(ItemStack stack) {
		CompoundTag compound = stack.getTag();
		if (compound != null && compound.contains(NBT_STRING)) {
			return compound.getBoolean(NBT_STRING);
		}
		return false;
	}
	
}
