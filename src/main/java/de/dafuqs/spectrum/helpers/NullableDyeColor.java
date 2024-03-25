package de.dafuqs.spectrum.helpers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public enum NullableDyeColor implements StringRepresentable {
	WHITE(0, "white", DyeColor.WHITE),
	ORANGE(1, "orange", DyeColor.ORANGE),
	MAGENTA(2, "magenta", DyeColor.MAGENTA),
	LIGHT_BLUE(3, "light_blue", DyeColor.LIGHT_BLUE),
	YELLOW(4, "yellow", DyeColor.YELLOW),
	LIME(5, "lime", DyeColor.LIME),
	PINK(6, "pink", DyeColor.PINK),
	GRAY(7, "gray", DyeColor.GRAY),
	LIGHT_GRAY(8, "light_gray", DyeColor.LIGHT_GRAY),
	CYAN(9, "cyan", DyeColor.CYAN),
	PURPLE(10, "purple", DyeColor.PURPLE),
	BLUE(11, "blue", DyeColor.BLUE),
	BROWN(12, "brown", DyeColor.BROWN),
	GREEN(13, "green", DyeColor.GREEN),
	RED(14, "red", DyeColor.RED),
	BLACK(15, "black", DyeColor.BLACK),
	NONE(16, "none", null);
	
	private static final NullableDyeColor[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(NullableDyeColor::getId)).toArray(NullableDyeColor[]::new);
	private final int id;
	private final String name;
	private final @Nullable DyeColor dyeColor;
	
	NullableDyeColor(int id, String name, @Nullable DyeColor dyeColor) {
		this.id = id;
		this.name = name;
		this.dyeColor = dyeColor;
	}
	
	public static NullableDyeColor get(@Nullable DyeColor dyeColor) {
		if (dyeColor == null) {
			return NONE;
		}
		return byId(dyeColor.getId());
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public @Nullable DyeColor getDyeColor() {
		return this.dyeColor;
	}
	
	public static NullableDyeColor byId(int id) {
		if (id < 0 || id >= VALUES.length) {
			id = 0;
		}
		
		return VALUES[id];
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
	
	public static final String COLOR_NBT_KEY = "color";
	
	public static void set(ItemStack stack, NullableDyeColor color) {
		stack.getOrCreateTag().putString(NullableDyeColor.COLOR_NBT_KEY, color.getName().toLowerCase(Locale.ROOT));
	}
	
	public static NullableDyeColor get(@Nullable CompoundTag nbt) {
		if (nbt == null || !nbt.contains(COLOR_NBT_KEY, Tag.TAG_STRING)) {
			return NullableDyeColor.NONE;
		}
		return NullableDyeColor.valueOf(nbt.getString(COLOR_NBT_KEY).toUpperCase(Locale.ROOT));
	}
	
	public static void addTooltip(ItemStack stack, List<Component> tooltip) {
		NullableDyeColor color = NullableDyeColor.get(stack.getTag());
		if (color != NullableDyeColor.NONE) {
			tooltip.add(Component.translatable("spectrum.ink.color." + color.getName()));
		}
	}
	
}