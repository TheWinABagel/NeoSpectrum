package de.dafuqs.spectrum.items.food.beverages.properties;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.List;

public class JadeWineBeverageProperties extends StatusEffectBeverageProperties {
	
	public final float bloominess;
	public final boolean sweetened;
	
	public JadeWineBeverageProperties(long ageDays, int alcPercent, float thickness, float bloominess, boolean sweetened, List<MobEffectInstance> statusEffects) {
		super(ageDays, alcPercent, thickness, statusEffects);
		this.bloominess = bloominess;
		this.sweetened = sweetened;
	}
	
	public JadeWineBeverageProperties(CompoundTag nbtCompound, float bloominess, boolean sweetened) {
		super(nbtCompound);
		this.bloominess = bloominess;
		this.sweetened = sweetened;
	}
	
	public static JadeWineBeverageProperties getFromStack(ItemStack itemStack) {
		float bloominess = 0;
		boolean sweetened = false;
		
		CompoundTag nbtCompound = itemStack.getTag();
		if (nbtCompound != null) {
			bloominess = nbtCompound.contains("Bloominess") ? nbtCompound.getFloat("Bloominess") : 0;
			sweetened = nbtCompound.contains("Sweetened") && nbtCompound.getBoolean("Sweetened");
		}
		
		return new JadeWineBeverageProperties(nbtCompound, bloominess, sweetened);
	}
	
	@Override
	public void addTooltip(ItemStack itemStack, List<Component> tooltip) {
		tooltip.add(Component.translatable("item.spectrum.infused_beverage.tooltip.age", ageDays, alcPercent).withStyle(ChatFormatting.GRAY));
		if (sweetened) {
			tooltip.add(Component.translatable("item.spectrum.jade_wine.tooltip.bloominess_sweetened", bloominess).withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.add(Component.translatable("item.spectrum.jade_wine.tooltip.bloominess", bloominess).withStyle(ChatFormatting.GRAY));
		}
		PotionUtils.addPotionTooltip(itemStack, tooltip, 1.0F);
	}
	
	@Override
	public void toNbt(CompoundTag nbtCompound) {
		super.toNbt(nbtCompound);
		nbtCompound.putFloat("Bloominess", bloominess);
		nbtCompound.putBoolean("Sweetened", sweetened);
	}
	
}
	