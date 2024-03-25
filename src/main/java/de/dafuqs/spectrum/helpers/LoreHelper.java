package de.dafuqs.spectrum.helpers;

import com.google.gson.JsonParseException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LoreHelper {
	
	public static @NotNull List<Component> getLoreTextArrayFromString(@NotNull String string) {
		List<Component> loreText = new ArrayList<>();
		
		for (String split : string.split("\\\\n")) {
			loreText.add(0, Component.literal(split));
		}
		
		return loreText;
	}
	
	public static @NotNull String getStringFromLoreTextArray(@NotNull List<Component> lore) {
		if (lore.size() == 0) {
			return "";
		} else {
			StringBuilder loreString = new StringBuilder();
			for (int i = 0; i < lore.size(); i++) {
				loreString.append(lore.get(i).getString());
				if (i != lore.size() - 1) {
					loreString.append("\\n");
				}
			}
			return loreString.toString();
		}
	}
	
	public static void setLore(@NotNull ItemStack itemStack, @Nullable List<Component> lore) {
		CompoundTag nbtCompound = itemStack.getOrCreateTagElement(ItemStack.TAG_DISPLAY);
		if (lore != null) {
			ListTag nbtList = new ListTag();
			
			for (Component loreText : lore) {
				StringTag nbtString = StringTag.valueOf(Component.Serializer.toJson(loreText));
				nbtList.addTag(0, nbtString);
			}
			
			nbtCompound.put(ItemStack.TAG_LORE, nbtList);
		} else {
			nbtCompound.remove(ItemStack.TAG_LORE);
		}
	}
	
	public static void removeLore(@NotNull ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getTagElement(ItemStack.TAG_DISPLAY);
		if (nbtCompound != null) {
			nbtCompound.remove(ItemStack.TAG_LORE);
			if (nbtCompound.isEmpty()) {
				itemStack.removeTagKey(ItemStack.TAG_DISPLAY);
			}
		}
		
		if (itemStack.getTag() != null && itemStack.getTag().isEmpty()) {
			itemStack.setTag(null);
		}
	}
	
	public static boolean hasLore(@NotNull ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getTagElement(ItemStack.TAG_DISPLAY);
		return nbtCompound != null && nbtCompound.contains(ItemStack.TAG_LORE, 8);
	}
	
	public static @NotNull List<Component> getLoreList(@NotNull ItemStack itemStack) {
		List<Component> lore = new ArrayList<>();
		
		CompoundTag nbtCompound = itemStack.getTagElement(ItemStack.TAG_DISPLAY);
		if (nbtCompound != null && nbtCompound.contains(ItemStack.TAG_LORE, 8)) {
			try {
				ListTag nbtList = nbtCompound.getList(ItemStack.TAG_LORE, 8);
				for (int i = 0; i < nbtList.size(); i++) {
					String s = nbtList.getString(i);
					Component text = Component.Serializer.fromJson(s);
					lore.add(text);
				}
			} catch (JsonParseException e) {
				nbtCompound.remove(ItemStack.TAG_LORE);
			}
		}
		
		return lore;
	}
	
	public static boolean equalsLore(List<Component> lore, ItemStack stack) {
		if (hasLore(stack)) {
			List<Component> loreList = getLoreList(stack);
			
			if (lore.size() != loreList.size()) {
				return false;
			}
			
			for (int i = 0; i < lore.size(); i++) {
				if (!lore.get(i).equals(loreList.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static void setLore(@NotNull ItemStack stack, @Nullable Component lore) {
		CompoundTag nbtCompound = stack.getOrCreateTagElement(ItemStack.TAG_DISPLAY);
		if (lore != null) {
			ListTag nbtList = new ListTag();
			nbtList.addTag(0, StringTag.valueOf(Component.Serializer.toJson(lore)));
			nbtCompound.put(ItemStack.TAG_LORE, nbtList);
		} else {
			nbtCompound.remove(ItemStack.TAG_LORE);
		}
	}
	
}
