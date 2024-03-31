package de.dafuqs.spectrum.helpers;

import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import de.dafuqs.spectrum.enchantments.SpectrumEnchantment;
import de.dafuqs.spectrum.registries.SpectrumItemTags;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpectrumEnchantmentHelper {
	
	/**
	 * Adds an enchantment to an ItemStack. If the stack already has that enchantment, it gets upgraded instead
	 *
	 * @param stack                     the stack that receives the enchantments
	 * @param enchantment               the enchantment to add
	 * @param level                     the level of the enchantment
	 * @param forceEvenIfNotApplicable  add enchantments to the item, even if the item does usually not support that enchantment
	 * @param allowEnchantmentConflicts add enchantments to the item, even if there are enchantment conflicts
	 * @return the enchanted stack and a boolean if the enchanting was successful
	 */
	public static Tuple<Boolean, ItemStack> addOrUpgradeEnchantment(ItemStack stack, Enchantment enchantment, int level, boolean forceEvenIfNotApplicable, boolean allowEnchantmentConflicts) {
		// can this enchant even go on that tool?
		if (!enchantment.canEnchant(stack)
				&& !stack.is(Items.ENCHANTED_BOOK)
				&& !SpectrumEnchantmentHelper.isEnchantableBook(stack)) {
			
			return new Tuple<>(false, stack);
		}
		
		// if not forced check if the stack already has enchantments
		// that conflict with the new one
		if (!allowEnchantmentConflicts && hasEnchantmentThatConflictsWith(stack, enchantment)) {
			return new Tuple<>(false, stack);
		}

		// If it's in the tag, there's nothing more to check here. Enchant away!
		if (!stack.is(Items.ENCHANTED_BOOK)) {
			if (isEnchantableBook(stack)) {
				ItemStack enchantedBookStack = new ItemStack(Items.ENCHANTED_BOOK, stack.getCount());
				enchantedBookStack.setTag(stack.getTag());
				stack = enchantedBookStack;
			} else if (!forceEvenIfNotApplicable && !enchantment.canEnchant(stack)) {
				if (stack.getItem() instanceof ExtendedEnchantable extendedEnchantable) {
					// ExtendedEnchantable explicitly states this enchantment is acceptable
					if (!extendedEnchantable.acceptsEnchantment(enchantment)) {
						return new Tuple<>(false, stack);
					}
				}
			}
		}
		
		CompoundTag nbtCompound = stack.getOrCreateTag();
		String nbtString;
		if (stack.is(Items.ENCHANTED_BOOK) || stack.is(SpectrumItems.ENCHANTMENT_CANVAS)) {
			nbtString = EnchantedBookItem.TAG_STORED_ENCHANTMENTS;
		} else {
			nbtString = ItemStack.TAG_ENCH;
		}
		if (!nbtCompound.contains(nbtString, Tag.TAG_LIST)) {
			nbtCompound.put(nbtString, new ListTag());
		}
		
		ResourceLocation enchantmentIdentifier = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
		ListTag nbtList = nbtCompound.getList(nbtString, Tag.TAG_COMPOUND);
		for (int i = 0; i < nbtList.size(); i++) {
			CompoundTag enchantmentCompound = nbtList.getCompound(i);
			if (enchantmentCompound.contains("id", Tag.TAG_STRING) && ResourceLocation.tryParse(enchantmentCompound.getString("id")).equals(enchantmentIdentifier)) {
				boolean isEqualOrDowngrade = enchantmentCompound.contains("lvl", Tag.TAG_SHORT) && enchantmentCompound.getInt("lvl") >= level;
				if (isEqualOrDowngrade) {
					return new Tuple<>(false, stack);
				}

				nbtList.remove(i);
				i--;
			}
		}
		
		nbtList.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(enchantment), (byte) level));
		nbtCompound.put(nbtString, nbtList);
		stack.setTag(nbtCompound);
		
		return new Tuple<>(true, stack);
	}
	
	public static void setStoredEnchantments(Map<Enchantment, Integer> enchantments, ItemStack stack) {
		stack.removeTagKey(EnchantedBookItem.TAG_STORED_ENCHANTMENTS); // clear existing enchantments
		for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : enchantments.entrySet()) {
			Enchantment enchantment = enchantmentIntegerEntry.getKey();
			if (enchantment != null) {
				EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(enchantment, enchantmentIntegerEntry.getValue()));
			}
		}
	}
	
	/**
	 * Clears all enchantments of modifiedStack and replaces them with the ones present in enchantmentSourceStacks
	 * The enchantments are applied in order, so if there are conflicts, the first enchant in enchantmentSourceStacks gets chosen
	 *
	 * @param modifiedStack             the stack that receives the enchantments
	 * @param forceEvenIfNotApplicable  add enchantments to the item, even if the item does usually not support that enchantment
	 * @param allowEnchantmentConflicts add enchantments to the item, even if there are enchantment conflicts
	 * @param enchantmentSourceStacks   enchantmentSourceStacks the stacks that supply the enchantments
	 * @return the resulting stack
	 */
	public static ItemStack clearAndCombineEnchantments(ItemStack modifiedStack, boolean forceEvenIfNotApplicable, boolean allowEnchantmentConflicts, ItemStack... enchantmentSourceStacks) {
		EnchantmentHelper.setEnchantments(Map.of(), modifiedStack); // clear current ones
		for (ItemStack stack : enchantmentSourceStacks) {
			for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
				modifiedStack = SpectrumEnchantmentHelper.addOrUpgradeEnchantment(modifiedStack, entry.getKey(), entry.getValue(), forceEvenIfNotApplicable, allowEnchantmentConflicts).getB();
			}
		}
		return modifiedStack;
	}
	
	/**
	 * Checks if an itemstack can be used as the source to create an enchanted book
	 *
	 * @param stack The itemstack to check
	 * @return true if it is a book that can be turned into an enchanted book by enchanting
	 */
	public static boolean isEnchantableBook(@NotNull ItemStack stack) {
		return stack.is(SpectrumItemTags.ENCHANTABLE_BOOKS) || stack.getItem() instanceof BookItem;
	}
	
	public static boolean hasEnchantmentThatConflictsWith(ItemStack itemStack, Enchantment enchantment) {
		Map<Enchantment, Integer> existingEnchantments = EnchantmentHelper.getEnchantments(itemStack);
		for (Enchantment existingEnchantment : existingEnchantments.keySet()) {
			if (!existingEnchantment.equals(enchantment)) {
				if (!existingEnchantment.isCompatibleWith(enchantment)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static Map<Enchantment, Integer> collectHighestEnchantments(List<ItemStack> itemStacks) {
		Map<Enchantment, Integer> enchantmentLevelMap = new LinkedHashMap<>();
		
		for (ItemStack itemStack : itemStacks) {
			Map<Enchantment, Integer> itemStackEnchantments = EnchantmentHelper.getEnchantments(itemStack);
			for (Enchantment enchantment : itemStackEnchantments.keySet()) {
				int level = itemStackEnchantments.get(enchantment);
				if (enchantmentLevelMap.containsKey(enchantment)) {
					int storedLevel = enchantmentLevelMap.get(enchantment);
					if (level > storedLevel) {
						enchantmentLevelMap.put(enchantment, level);
					}
				} else {
					enchantmentLevelMap.put(enchantment, level);
				}
			}
		}
		
		return enchantmentLevelMap;
	}
	
	public static boolean canCombineAny(Map<Enchantment, Integer> existingEnchantments, Map<Enchantment, Integer> newEnchantments) {
		if (existingEnchantments.isEmpty()) {
			return true;
		} else {
			for (Enchantment existingEnchantment : existingEnchantments.keySet()) {
				for (Enchantment newEnchantment : newEnchantments.keySet()) {
					boolean canCurrentCombine = existingEnchantment.isCompatibleWith(newEnchantment);
					if (canCurrentCombine) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Removes the enchantments on a stack of items / enchanted book
	 * @param itemStack    the stack
	 * @param enchantments the enchantments to remove
	 * @return The resulting stack & the count of enchants that were removed
	 */
	public static Tuple<ItemStack, Integer> removeEnchantments(@NotNull ItemStack itemStack, Enchantment... enchantments) {
		CompoundTag compound = itemStack.getTag();
		if (compound == null) {
			return new Tuple<>(itemStack, 0);
		}

		ListTag enchantmentList;
		if (itemStack.is(Items.ENCHANTED_BOOK)) {
			enchantmentList = compound.getList(EnchantedBookItem.TAG_STORED_ENCHANTMENTS, 10);
		} else {
			enchantmentList = compound.getList(ItemStack.TAG_ENCH, 10);
		}

		List<ResourceLocation> enchantIDs = new ArrayList<>();
		for(Enchantment enchantment : enchantments) {
			enchantIDs.add(ForgeRegistries.ENCHANTMENTS.getKey(enchantment));
		}

		int removals = 0;
		for (int i = 0; i < enchantmentList.size(); i++) {
			CompoundTag currentCompound = enchantmentList.getCompound(i);
			if (currentCompound.contains("id", Tag.TAG_STRING)) {
				ResourceLocation currentID = new ResourceLocation(currentCompound.getString("id"));
				if(enchantIDs.contains(currentID)) {
					enchantmentList.remove(i);
					removals++;
					break;
				}
			}
		}
		
		if (itemStack.is(Items.ENCHANTED_BOOK)) {
			if(enchantmentList.isEmpty()) {
				ItemStack newStack = new ItemStack(Items.BOOK);
				newStack.setCount(itemStack.getCount());
				return new Tuple<>(newStack, removals);
			}
			compound.put(EnchantedBookItem.TAG_STORED_ENCHANTMENTS, enchantmentList);
		} else {
			compound.put(ItemStack.TAG_ENCH, enchantmentList);
		}
		itemStack.setTag(compound);

		return new Tuple<>(itemStack, removals);
	}
	
	public static <T extends Item & ExtendedEnchantable> ItemStack getMaxEnchantedStack(@NotNull T item) {
		ItemStack itemStack = item.getDefaultInstance();
		for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValues()) {
			if (item.acceptsEnchantment(enchantment)) {
				int maxLevel = enchantment.getMaxLevel();
				itemStack = addOrUpgradeEnchantment(itemStack, enchantment, maxLevel, true, true).getB();
			}
		}
		return itemStack;
	}
	
	public static int getUsableLevel(SpectrumEnchantment enchantment, ItemStack itemStack, Entity entity) {
		int level = itemStack.getEnchantmentLevel(enchantment);
		if (level > 0 && !enchantment.canEntityUse(entity)) {
			level = 0;
		}
		return level;
	}

}
