package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import de.dafuqs.spectrum.api.item.LoomPatternProvider;
import de.dafuqs.spectrum.registries.SpectrumBannerPatterns;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KnowledgeGemItem extends Item implements ExperienceStorageItem, ExtendedEnchantable, LoomPatternProvider {
	
	private final int maxStorageBase;
	
	// these are copies from the item model file
	// and specify the sprite used for its texture
	protected final int[] displayTiers = {1, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000};
	
	public KnowledgeGemItem(Properties settings, int maxStorageBase) {
		super(settings);
		this.maxStorageBase = maxStorageBase;
	}
	
	public static ItemStack getKnowledgeDropStackWithXP(int experience, boolean noStoreTooltip) {
		ItemStack stack = new ItemStack(SpectrumItems.KNOWLEDGE_GEM);
		CompoundTag compound = new CompoundTag();
		compound.putInt("stored_experience", experience);
		if (noStoreTooltip) {
			compound.putBoolean("do_not_display_store_tooltip", true);
		}
		stack.setTag(compound);
		return stack;
	}

	@Override
	public int getMaxStoredExperience(ItemStack itemStack) {
		int efficiencyLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, itemStack);
		return maxStorageBase * (int) Math.pow(10, Math.min(5, efficiencyLevel)); // to not exceed int max
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}
	
	public int getTransferableExperiencePerTick(ItemStack itemStack) {
		int quickChargeLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, itemStack);
		return (int) (2 * Math.pow(2, Math.min(10, quickChargeLevel)));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		return ItemUtils.startUsingInstantly(world, user, hand);
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		super.onUseTick(world, user, stack, remainingUseTicks);
		if (user instanceof ServerPlayer serverPlayerEntity) {
			
			int playerExperience = serverPlayerEntity.totalExperience;
			int itemExperience = ExperienceStorageItem.getStoredExperience(stack);
			int transferableExperience = getTransferableExperiencePerTick(stack);
			
			if (serverPlayerEntity.isShiftKeyDown()) {
				int maxStorage = getMaxStoredExperience(stack);
				int experienceToTransfer = serverPlayerEntity.isCreative() ? Math.min(transferableExperience, maxStorage - itemExperience) : Math.min(Math.min(transferableExperience, playerExperience), maxStorage - itemExperience);
				
				// store experience in gem; drain from player
				if (experienceToTransfer > 0 && itemExperience < maxStorage && removePlayerExperience(serverPlayerEntity, experienceToTransfer)) {
					ExperienceStorageItem.addStoredExperience(stack, experienceToTransfer);
					
					if (remainingUseTicks % 4 == 0) {
						world.playSound(null, user.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.3F, 0.8F + world.getRandom().nextFloat() * 0.4F);
					}
				}
			} else {
				// drain experience from gem; give to player
				if (itemExperience > 0 && playerExperience != Integer.MAX_VALUE) {
					int experienceToTransfer = Math.min(Math.min(transferableExperience, itemExperience), Integer.MAX_VALUE - playerExperience);
					
					if (experienceToTransfer > 0) {
						if (!serverPlayerEntity.isCreative()) {
							serverPlayerEntity.giveExperiencePoints(experienceToTransfer);
						}
						ExperienceStorageItem.removeStoredExperience(stack, experienceToTransfer);

						if (remainingUseTicks % 4 == 0) {
							world.playSound(null, user.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.3F, 0.8F + world.getRandom().nextFloat() * 0.4F);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, tooltip, tooltipContext);
		
		int maxExperience = getMaxStoredExperience(itemStack);
		int storedExperience = ExperienceStorageItem.getStoredExperience(itemStack);
		if (storedExperience == 0) {
			tooltip.add(Component.literal("0 ").withStyle(ChatFormatting.DARK_GRAY).append(Component.translatable("item.spectrum.knowledge_gem.tooltip.stored_experience", maxExperience).withStyle(ChatFormatting.GRAY)));
		} else {
			tooltip.add(Component.literal(storedExperience + " ").withStyle(ChatFormatting.GREEN).append(Component.translatable("item.spectrum.knowledge_gem.tooltip.stored_experience", maxExperience).withStyle(ChatFormatting.GRAY)));
		}
		if (shouldDisplayUsageTooltip(itemStack)) {
			tooltip.add(Component.translatable("item.spectrum.knowledge_gem.tooltip.use", getTransferableExperiencePerTick(itemStack)).withStyle(ChatFormatting.GRAY));
			addBannerPatternProviderTooltip(tooltip);
		}
	}
	
	public boolean shouldDisplayUsageTooltip(ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getTag();
		return nbtCompound == null || !nbtCompound.getBoolean("do_not_display_store_tooltip");
	}
	
	public boolean removePlayerExperience(@NotNull Player playerEntity, int experience) {
		if (playerEntity.isCreative()) {
			return true;
		} else if (playerEntity.totalExperience < experience) {
			return false;
		} else {
			playerEntity.giveExperiencePoints(-experience);
			return true;
		}
	}
	
	public boolean changedDisplayTier(int currentStoredExperience, int destinationStoredExperience) {
		return getDisplayTierForExperience(currentStoredExperience) != getDisplayTierForExperience(destinationStoredExperience);
	}
	
	public int getDisplayTierForExperience(int experience) {
		for (int i = 0; i < displayTiers.length; i++) {
			if (experience < displayTiers[i]) {
				return i;
			}
		}
		return displayTiers.length;
	}
	
	@Override
	public Holder<BannerPattern> getPattern() {
		return SpectrumBannerPatterns.KNOWLEDGE_GEM;
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return stack.getCount() == 1;
	}
	
	@Override
	public boolean acceptsEnchantment(Enchantment enchantment) {
		return enchantment == Enchantments.BLOCK_EFFICIENCY || enchantment == Enchantments.QUICK_CHARGE;
	}
	
	@Override
	public int getEnchantmentValue() {
		return 5;
	}

//	@Override
//	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
//		super.appendStacks(group, stacks);
//		if (this.isIn(group)) {
//			ItemStack stack = getDefaultStack();
//			ExperienceStorageItem.addStoredExperience(stack, getMaxStoredExperience(stack));
//			stacks.add(stack);
//
//			ItemStack enchantedStack = SpectrumEnchantmentHelper.getMaxEnchantedStack(this);
//			ExperienceStorageItem.addStoredExperience(enchantedStack, getMaxStoredExperience(stack));
//			stacks.add(enchantedStack);
//		}
//	}

}
