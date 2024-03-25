package de.dafuqs.spectrum.enchantments;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class TreasureHunterEnchantment extends SpectrumEnchantment {
	
	public TreasureHunterEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.WEAPON, slotTypes, unlockAdvancementIdentifier);
	}
	
	public static void doTreasureHunterForPlayer(ServerPlayer thisEntity, DamageSource source) {
		if (!thisEntity.isSpectator() && source.getEntity() instanceof LivingEntity) {
			int damageSourceTreasureHunt = EnchantmentHelper.getEnchantmentLevel(SpectrumEnchantments.TREASURE_HUNTER, (LivingEntity) source.getEntity());
			if (damageSourceTreasureHunt > 0) {
				ServerLevel serverWorld = ((ServerLevel) thisEntity.level());
				boolean shouldDropHead = serverWorld.getRandom().nextFloat() < 0.2 * damageSourceTreasureHunt;
				if (shouldDropHead) {
					ItemStack headItemStack = new ItemStack(Items.PLAYER_HEAD);
					
					CompoundTag compoundTag = new CompoundTag();
					compoundTag.putString("SkullOwner", thisEntity.getName().getString());
					
					headItemStack.setTag(compoundTag);
					
					ItemEntity headEntity = new ItemEntity(serverWorld, thisEntity.getX(), thisEntity.getY(), thisEntity.getZ(), headItemStack);
					serverWorld.addFreshEntity(headEntity);
				}
			}
		}
	}
	
	@Override
	public int getMinCost(int level) {
		return 15;
	}
	
	@Override
	public int getMaxCost(int level) {
		return super.getMinCost(level) + 30;
	}
	
	@Override
	public int getMaxLevel() {
		return SpectrumCommon.CONFIG.TreasureHunterMaxLevel;
	}
	
	@Override
	public boolean checkCompatibility(Enchantment other) {
		return super.checkCompatibility(other) && other != Enchantments.MOB_LOOTING;
	}
	
	@Override
	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack) || stack.getItem() instanceof AxeItem || stack.is(ItemTags.AXES);
	}
	
}