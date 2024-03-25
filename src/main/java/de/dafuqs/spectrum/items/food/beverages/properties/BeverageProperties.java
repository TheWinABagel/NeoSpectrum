package de.dafuqs.spectrum.items.food.beverages.properties;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

// wrapper for beverage itemstack nbt
// unique for each beverage
public class BeverageProperties {
	
	public long ageDays = 0;
	public int alcPercent = 0;
	public float thickness = 0;
	
	public BeverageProperties(long ageDays, int alcPercent, float thickness) {
		this.ageDays = ageDays;
		this.alcPercent = alcPercent;
		this.thickness = thickness;
	}
	
	public BeverageProperties(CompoundTag nbtCompound) {
		if (nbtCompound != null) {
			this.ageDays = nbtCompound.contains("AgeDays") ? nbtCompound.getLong("AgeDays") : 0;
			this.alcPercent = nbtCompound.contains("AlcPercent") ? nbtCompound.getInt("AlcPercent") : 0;
			this.thickness = nbtCompound.contains("Thickness") ? nbtCompound.getFloat("Thickness") : 0;
		}
	}
	
	public static BeverageProperties getFromStack(ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getTag();
		return new BeverageProperties(nbtCompound);
	}
	
	public void addTooltip(ItemStack itemStack, List<Component> tooltip) {
		tooltip.add(Component.translatable("item.spectrum.infused_beverage.tooltip.age", ageDays, alcPercent).withStyle(ChatFormatting.GRAY));
	}
	
	protected void toNbt(CompoundTag nbtCompound) {
		nbtCompound.putLong("AgeDays", this.ageDays);
		nbtCompound.putInt("AlcPercent", this.alcPercent);
		nbtCompound.putFloat("Thickness", this.thickness);
	}
	
	public ItemStack getStack(ItemStack itemStack) {
		CompoundTag compound = itemStack.getOrCreateTag();
		toNbt(compound);
		itemStack.setTag(compound);
		return itemStack;
	}
	
}
