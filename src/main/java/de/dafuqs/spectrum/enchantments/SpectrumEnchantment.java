package de.dafuqs.spectrum.enchantments;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class SpectrumEnchantment extends Enchantment {
	
	protected final ResourceLocation unlockAdvancementIdentifier;
	
	protected SpectrumEnchantment(Rarity weight, EnchantmentCategory type, EquipmentSlot[] slotTypes, ResourceLocation unlockAdvancementIdentifier) {
		super(weight, type, slotTypes);
		this.unlockAdvancementIdentifier = unlockAdvancementIdentifier;
	}
	
	@Override
	public boolean isTreasureOnly() {
		return false;
	}
	
	@Override
	public boolean isTradeable() {
		return false;
	}
	
	@Override
	public boolean isDiscoverable() {
		return false;
	}
	
	@Environment(EnvType.CLIENT)
	@Override
    public Component getFullname(int level) {
		Minecraft client = Minecraft.getInstance();
		MutableComponent mutableText = Component.translatable(this.getDescriptionId());
		if (this.isCurse()) {
			mutableText.withStyle(ChatFormatting.RED);
		} else {
			mutableText.withStyle(ChatFormatting.GRAY);
		}
		if (!canEntityUse(client.player)) {
			mutableText.withStyle(ChatFormatting.getByCode('k'));
		}
		
		if (level != 1 || this.getMaxLevel() != 1) {
			mutableText.append(" ").append(Component.translatable("enchantment.level." + level));
		}
		
		return mutableText;
	}
	
	public boolean canEntityUse(Entity entity) {
		if (entity instanceof Player playerEntity) {
			return AdvancementHelper.hasAdvancement(playerEntity, unlockAdvancementIdentifier);
		} else {
			return false;
		}
	}
	
	public ResourceLocation getUnlockAdvancementIdentifier() {
		return unlockAdvancementIdentifier;
	}
	
}
