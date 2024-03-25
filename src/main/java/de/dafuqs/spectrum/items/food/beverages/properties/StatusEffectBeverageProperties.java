package de.dafuqs.spectrum.items.food.beverages.properties;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.List;

public class StatusEffectBeverageProperties extends BeverageProperties {
	
	public List<MobEffectInstance> statusEffects;
	
	public StatusEffectBeverageProperties(long ageDays, int alcPercent, float thickness, List<MobEffectInstance> statusEffects) {
		super(ageDays, alcPercent, thickness);
		this.statusEffects = statusEffects;
	}
	
	public StatusEffectBeverageProperties(CompoundTag nbtCompound) {
		super(nbtCompound);
		
		this.statusEffects = Lists.newArrayList();
		if (nbtCompound != null) {
			PotionUtils.getCustomEffects(nbtCompound, statusEffects);
		}
	}
	
	public static StatusEffectBeverageProperties getFromStack(ItemStack itemStack) {
		return new StatusEffectBeverageProperties(itemStack.getTag());
	}
	
	@Override
	public void addTooltip(ItemStack itemStack, List<Component> tooltip) {
		super.addTooltip(itemStack, tooltip);
		PotionUtils.addPotionTooltip(itemStack, tooltip, 1.0F);
	}
	
	@Override
	public void toNbt(CompoundTag nbtCompound) {
		super.toNbt(nbtCompound);
		
		ListTag nbtList = nbtCompound.getList("CustomPotionEffects", 9);
		for (MobEffectInstance statusEffectInstance : this.statusEffects) {
			nbtList.add(statusEffectInstance.save(new CompoundTag()));
		}
		nbtCompound.put("CustomPotionEffects", nbtList);
		
	}
}
