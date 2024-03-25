package de.dafuqs.spectrum.items.food.beverages.properties;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class VariantBeverageProperties extends StatusEffectBeverageProperties {
	
	public final String variant;
	
	public VariantBeverageProperties(long ageDays, int alcPercent, float thickness, String variant, List<MobEffectInstance> statusEffects) {
		super(ageDays, alcPercent, thickness, statusEffects);
		this.variant = variant;
	}
	
	public VariantBeverageProperties(CompoundTag nbtCompound, String variant) {
		super(nbtCompound);
		this.variant = variant;
	}
	
	public static VariantBeverageProperties getFromStack(ItemStack itemStack) {
		String variant;
		
		CompoundTag nbtCompound = itemStack.getTag();
		variant = nbtCompound != null && nbtCompound.contains("Variant") ? nbtCompound.getString("Variant") : "unknown";
		
		return new VariantBeverageProperties(nbtCompound, variant);
	}
	
	@Override
	public void addTooltip(ItemStack itemStack, List<Component> tooltip) {
		tooltip.add(Component.translatable("item.spectrum.infused_beverage.tooltip.variant." + variant).withStyle(ChatFormatting.YELLOW));
		super.addTooltip(itemStack, tooltip);
	}
	
	@Override
	public void toNbt(CompoundTag nbtCompound) {
		super.toNbt(nbtCompound);
		nbtCompound.putString("Variant", variant);
	}
	
}
