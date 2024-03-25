package de.dafuqs.spectrum.api.item;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.Support;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public interface ExperienceStorageItem {
	
	/**
	 * Returns the amount of experience this item stack has stored
	 *
	 * @param itemStack The item stack
	 * @return The amount of stored experience
	 */
	static int getStoredExperience(ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getTag();
		if (nbtCompound == null || !nbtCompound.contains("stored_experience", Tag.TAG_ANY_NUMERIC)) {
			return 0;
		} else {
			return nbtCompound.getInt("stored_experience");
		}
	}
	
	/**
	 * Adds amount experience to an ExperienceProviderItem stack.
	 * If the experience would be overflowing return the amount of experience that could not be stored
	 *
	 * @param itemStack The item stack
	 * @param amount    The amount of experience to store
	 * @param random    A random
	 * @return The overflow amount that could not be stored
	 */
	static int addStoredExperience(ItemStack itemStack, float amount, RandomSource random) {
		if (amount > 0) {
			int intAmount = Support.getIntFromDecimalWithChance(amount, random);
			return addStoredExperience(itemStack, intAmount);
		}
		return 0;
	}
	
	/**
	 * Adds amount experience to an ExperienceProviderItem stack.
	 * If the experience would be overflowing return the amount of experience that could not be stored
	 *
	 * @param itemStack The item stack
	 * @param amount    The amount of experience to store
	 * @return The overflow amount that could not be stored
	 */
	static int addStoredExperience(ItemStack itemStack, int amount) {
		if (amount <= 0) {
			return 0;
		}
		
		if (itemStack.getItem() instanceof ExperienceStorageItem experienceStorageItem) {
			int maxStorage = experienceStorageItem.getMaxStoredExperience(itemStack);
			
			CompoundTag nbtCompound = itemStack.getOrCreateTag();
			if (!nbtCompound.contains("stored_experience", Tag.TAG_ANY_NUMERIC)) {
				nbtCompound.putInt("stored_experience", amount);
				itemStack.setTag(nbtCompound);
				return 0;
			} else {
				int existingStoredExperience = nbtCompound.getInt("stored_experience");
				int experienceOverflow = maxStorage - amount - existingStoredExperience;
				
				if (experienceOverflow < 0) {
					nbtCompound.putInt("stored_experience", maxStorage);
					itemStack.setTag(nbtCompound);
					return -experienceOverflow;
				} else {
					nbtCompound.putInt("stored_experience", existingStoredExperience + amount);
					itemStack.setTag(nbtCompound);
					return 0;
				}
			}
		} else if (!itemStack.isEmpty()) {
			SpectrumCommon.logError("Tried to add stored Experience to a non-ExperienceStorageItem item: " + itemStack.getItem().getDescription().getString());
		}
		
		return 0;
	}
	
	/**
	 * Removes amount experience from an ExperienceProviderItem stack.
	 * If there is not enough experience that could be removed do nothing and return false
	 *
	 * @param itemStack The item stack
	 * @param amount    The amount of experience to remove
	 * @return If there was enough experience that could be removed
	 */
	static boolean removeStoredExperience(ItemStack itemStack, int amount) {
		if (itemStack.getItem() instanceof ExperienceStorageItem) {
			CompoundTag nbtCompound = itemStack.getTag();
			if (nbtCompound == null || !nbtCompound.contains("stored_experience", Tag.TAG_ANY_NUMERIC)) {
				return false;
			} else {
				int existingStoredExperience = nbtCompound.getInt("stored_experience");
				if (existingStoredExperience < amount) {
					return false;
				} else {
					nbtCompound.putInt("stored_experience", existingStoredExperience - amount);
					itemStack.setTag(nbtCompound);
					return true;
				}
			}
		} else {
			SpectrumCommon.logError("Tried to remove stored Experience from a non-ExperienceStorageItem: " + itemStack.getItem().getDescription().getString());
			return false;
		}
	}
	
	int getMaxStoredExperience(ItemStack itemStack);
	
}
