package de.dafuqs.spectrum.enchantments;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DisarmingEnchantment extends SpectrumEnchantment {
	
	public DisarmingEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.WEAPON, slotTypes, unlockAdvancementIdentifier);
	}
	
	public static void disarmPlayer(Player player) {
		int equipmentSlotCount = EquipmentSlot.values().length;
		int randomSlot = (int) (Math.random() * equipmentSlotCount);
		int slotsChecked = 0;
		Level world = player.level();
		while (slotsChecked < equipmentSlotCount) {
			EquipmentSlot slot = EquipmentSlot.values()[randomSlot];
			ItemStack equippedStack = player.getItemBySlot(slot);
			if (!equippedStack.isEmpty()) {
				ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), equippedStack);
				itemEntity.setDeltaMovement(world.random.triangle(0.0, 0.11485000171139836), world.random.triangle(0.2, 0.11485000171139836), world.random.triangle(0.0, 0.11485000171139836));
				itemEntity.setPickUpDelay(120);
				world.addFreshEntity(itemEntity);
				
				player.setItemSlot(slot, ItemStack.EMPTY);
				world.playSound(null, player.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.NEUTRAL, 1.0F, 1.0F);
				break;
			}
			
			randomSlot = (randomSlot + 1) % equipmentSlotCount;
			slotsChecked++;
		}
	}
	
	public static void disarmEntity(LivingEntity livingEntity, NonNullList<ItemStack> syncedArmorStacks) {
		// since endermen save their carried block as blockState, not in hand
		// we have to use custom logic for them
		if (livingEntity instanceof EnderMan endermanEntity) {
			BlockState carriedBlockState = endermanEntity.getCarriedBlock();
			if (carriedBlockState != null) {
				Item item = carriedBlockState.getBlock().asItem();
				if (item != null) {
					endermanEntity.spawnAtLocation(item.getDefaultInstance());
					endermanEntity.setCarriedBlock(null);
				}
			}
			return;
		}
		
		int randomSlot = (int) (Math.random() * 6);
		int slotsChecked = 0;
		while (slotsChecked < 6) {
			if (randomSlot == 5) {
				if (livingEntity.getMainHandItem() != null && !livingEntity.getMainHandItem().isEmpty()) {
					livingEntity.spawnAtLocation(livingEntity.getMainHandItem());
					livingEntity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
					livingEntity.level().playSound(null, livingEntity.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.NEUTRAL, 1.0F, 1.0F);
					break;
				}
			} else if (randomSlot == 4) {
				if (livingEntity.getOffhandItem() != null && !livingEntity.getOffhandItem().isEmpty()) {
					livingEntity.spawnAtLocation(livingEntity.getOffhandItem());
					livingEntity.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
					livingEntity.level().playSound(null, livingEntity.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.NEUTRAL, 1.0F, 1.0F);
					break;
				}
			} else {
				if (syncedArmorStacks != null && !syncedArmorStacks.get(randomSlot).isEmpty()) {
					livingEntity.spawnAtLocation(syncedArmorStacks.get(randomSlot));
					syncedArmorStacks.set(randomSlot, ItemStack.EMPTY);
					livingEntity.level().playSound(null, livingEntity.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.NEUTRAL, 1.0F, 1.0F);
					break;
				}
			}
			
			randomSlot = (randomSlot + 1) % 6;
			slotsChecked++;
		}
	}
	
	@Override
	public int getMinCost(int level) {
		return 10;
	}
	
	@Override
	public int getMaxCost(int level) {
		return super.getMinCost(level) + 30;
	}
	
	@Override
	public int getMaxLevel() {
		return SpectrumCommon.CONFIG.DisarmingMaxLevel;
	}
	
	@Override
	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack) || stack.getItem() instanceof AxeItem;
	}
	
}
