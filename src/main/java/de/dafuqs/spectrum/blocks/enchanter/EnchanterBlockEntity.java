package de.dafuqs.spectrum.blocks.enchanter;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.MultiblockCrafter;
import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.blocks.InWorldInteractionBlockEntity;
import de.dafuqs.spectrum.blocks.item_bowl.ItemBowlBlockEntity;
import de.dafuqs.spectrum.blocks.upgrade.Upgradeable;
import de.dafuqs.spectrum.compat.biome_makeover.BiomeMakeoverCompat;
import de.dafuqs.spectrum.enchantments.SpectrumEnchantment;
import de.dafuqs.spectrum.helpers.ExperienceHelper;
import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.items.magic_items.KnowledgeGemItem;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.particle.VectorPattern;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.enchanter.EnchanterRecipe;
import de.dafuqs.spectrum.recipe.enchantment_upgrade.EnchantmentUpgradeRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.sound.CraftingBlockSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EnchanterBlockEntity extends InWorldInteractionBlockEntity implements MultiblockCrafter {

	public static final List<Vec3i> ITEM_BOWL_OFFSETS = new ArrayList<>() {{
		add(new Vec3i(5, 0, -3));
		add(new Vec3i(5, 0, 3));
		add(new Vec3i(3, 0, 5));
		add(new Vec3i(-3, 0, 5));
		add(new Vec3i(-5, 0, 3));
		add(new Vec3i(-5, 0, -3));
		add(new Vec3i(-3, 0, -5));
		add(new Vec3i(3, 0, -5));
	}};
	
	public static final int REQUIRED_TICKS_FOR_EACH_EXPERIENCE_POINT = 4;
	public static final ResourceLocation APPLY_CONFLICTING_ENCHANTMENTS_ADVANCEMENT_IDENTIFIER = SpectrumCommon.locate("milestones/unlock_conflicted_enchanting_with_enchanter");
	public static final ResourceLocation OVERENCHANTING_ADVANCEMENT_IDENTIFIER = SpectrumCommon.locate("milestones/unlock_overenchanting_with_enchanter");
	
	public static final int INVENTORY_SIZE = 2; // 0: any itemstack, 1: Knowledge Gem
	
	protected UUID ownerUUID;
	protected boolean canOwnerApplyConflictingEnchantments;
	protected boolean canOwnerOverenchant;
	
	// since the item bowls around the enchanter hold some items themselves
	// they get cached here for faster recipe lookup
	// virtualInventoryRecipeOrientation is the order the items are ordered for the recipe to match (rotations from 0-3)
	protected SimpleContainer virtualInventoryIncludingBowlStacks;
	protected int virtualInventoryRecipeOrientation;
	
	protected boolean inventoryChanged;
	private UpgradeHolder upgrades;
	
	private GatedSpectrumRecipe currentRecipe;
	private int craftingTime;
	private int craftingTimeTotal;
	private int currentItemProcessingTime;
	
	@Nullable
	private Direction itemFacing; // for rendering the item on the enchanter only
	
	public EnchanterBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.ENCHANTER, pos, state, INVENTORY_SIZE);
		this.virtualInventoryIncludingBowlStacks = new SimpleContainer(INVENTORY_SIZE + 8);
		this.currentItemProcessingTime = -1;
	}
	
	public static void clientTick(Level world, BlockPos blockPos, BlockState blockState, @NotNull EnchanterBlockEntity enchanterBlockEntity) {
		if (enchanterBlockEntity.currentRecipe != null) {
			ItemStack experienceStack = enchanterBlockEntity.getItem(1);
			if (!experienceStack.isEmpty() && experienceStack.getItem() instanceof ExperienceStorageItem) {
				int experience = ExperienceStorageItem.getStoredExperience(experienceStack);
				int amount = ExperienceHelper.getExperienceOrbSizeForExperience(experience);
				
				if (world.random.nextInt(10) < amount) {
					float randomX = 0.2F + world.getRandom().nextFloat() * 0.6F;
					float randomZ = 0.2F + world.getRandom().nextFloat() * 0.6F;
					float randomY = -0.1F + world.getRandom().nextFloat() * 0.4F;
					world.addParticle(SpectrumParticleTypes.LIME_SPARKLE_RISING, blockPos.getX() + randomX, blockPos.getY() + 2.5 + randomY, blockPos.getZ() + randomZ, 0.0D, -0.1D, 0.0D);
				}
			}
		} else if (enchanterBlockEntity.currentItemProcessingTime > -1) {
			float randomX = 0.2F + world.getRandom().nextFloat() * 0.6F;
			float randomZ = 0.2F + world.getRandom().nextFloat() * 0.6F;
			float randomY = -0.2F + world.getRandom().nextFloat() * 0.4F;
			world.addParticle(SpectrumParticleTypes.LIME_SPARKLE_RISING, blockPos.getX() + randomX, blockPos.getY() + 2.5 + randomY, blockPos.getZ() + randomZ, 0.0D, -0.1D, 0.0D);
			
			if (world.getGameTime() % 12 == 0) {
				world.playSound(null, enchanterBlockEntity.worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.8F * SpectrumCommon.CONFIG.BlockSoundVolume, 0.8F + world.random.nextFloat() * 0.4F);
				enchanterBlockEntity.doItemBowlOrbs(world);
			}
		}
	}
	
	public static void serverTick(Level world, BlockPos blockPos, BlockState blockState, @NotNull EnchanterBlockEntity enchanterBlockEntity) {
		if (enchanterBlockEntity.upgrades == null) {
			enchanterBlockEntity.calculateUpgrades();
		}
		
		if (enchanterBlockEntity.inventoryChanged) {
			calculateCurrentRecipe(world, enchanterBlockEntity);
			
			// if no default recipe found => check in-code recipe for enchanting the center item with enchanted books
			if (enchanterBlockEntity.currentRecipe == null) {
				if (isValidCenterEnchantingSetup(enchanterBlockEntity)) {
					int requiredExperience = getRequiredExperienceToEnchantCenterItem(enchanterBlockEntity);
					if (requiredExperience > 0) {
						enchanterBlockEntity.currentItemProcessingTime = requiredExperience * REQUIRED_TICKS_FOR_EACH_EXPERIENCE_POINT;
					} else {
						enchanterBlockEntity.currentItemProcessingTime = -1;
					}
				} else {
					enchanterBlockEntity.currentItemProcessingTime = -1;
				}
				enchanterBlockEntity.updateInClientWorld();
			}

			enchanterBlockEntity.inventoryChanged = false;
		}
		
		boolean craftingSuccess = false;
		
		if (enchanterBlockEntity.currentRecipe != null || enchanterBlockEntity.currentItemProcessingTime > 1) {
			if (enchanterBlockEntity.craftingTime % 60 == 1) {
				if (!checkRecipeRequirements(world, blockPos, enchanterBlockEntity)) {
					enchanterBlockEntity.craftingTime = 0;
					SpectrumS2CPacketSender.sendCancelBlockBoundSoundInstance((ServerLevel) enchanterBlockEntity.getLevel(), enchanterBlockEntity.worldPosition);
					return;
				}
			}
			if (enchanterBlockEntity.craftingTime == 1) {
				SpectrumS2CPacketSender.sendPlayBlockBoundSoundInstance(SpectrumSoundEvents.ENCHANTER_WORKING, (ServerLevel) enchanterBlockEntity.getLevel(), enchanterBlockEntity.worldPosition, Integer.MAX_VALUE);
			}
			
			if (enchanterBlockEntity.currentRecipe instanceof EnchanterRecipe enchanterRecipe) {
				enchanterBlockEntity.craftingTime++;
				
				// looks cooler this way
				if (enchanterBlockEntity.craftingTime == enchanterBlockEntity.craftingTimeTotal - 20) {
					enchanterBlockEntity.doItemBowlOrbs(world);
				} else if (enchanterBlockEntity.craftingTime == enchanterBlockEntity.craftingTimeTotal) {
					playCraftingFinishedEffects(enchanterBlockEntity);
					craftEnchanterRecipe(world, enchanterBlockEntity, enchanterRecipe);
					craftingSuccess = true;
				}
				enchanterBlockEntity.setChanged();
			} else if (enchanterBlockEntity.currentRecipe instanceof EnchantmentUpgradeRecipe enchantmentUpgradeRecipe) {
				enchanterBlockEntity.currentItemProcessingTime++;
				if (enchanterBlockEntity.currentItemProcessingTime == REQUIRED_TICKS_FOR_EACH_EXPERIENCE_POINT) {
					enchanterBlockEntity.currentItemProcessingTime = 0;
					
					int consumedItems = tickEnchantmentUpgradeRecipe(world, enchanterBlockEntity, enchantmentUpgradeRecipe, enchanterBlockEntity.craftingTimeTotal - enchanterBlockEntity.craftingTime);
					if(consumedItems == 0) {
						enchanterBlockEntity.inventoryChanged();
					} else {
						enchanterBlockEntity.craftingTime += consumedItems;
						if (enchanterBlockEntity.craftingTime >= enchanterBlockEntity.craftingTimeTotal) {
							playCraftingFinishedEffects(enchanterBlockEntity);
							craftEnchantmentUpgradeRecipe(world, enchanterBlockEntity, enchantmentUpgradeRecipe);
							craftingSuccess = true;
						}
					}
				}
				enchanterBlockEntity.setChanged();
			} else if (enchanterBlockEntity.currentItemProcessingTime > -1) {
				int speedTicks = Support.getIntFromDecimalWithChance(enchanterBlockEntity.upgrades.getEffectiveValue(UpgradeType.SPEED), world.random);
				enchanterBlockEntity.craftingTime += speedTicks;
				if (world.getGameTime() % REQUIRED_TICKS_FOR_EACH_EXPERIENCE_POINT == 0) {
					// in-code recipe for item + books => enchanted item
					boolean drained = enchanterBlockEntity.drainExperience(speedTicks);
					if (!drained) {
						enchanterBlockEntity.currentItemProcessingTime = -1;
						enchanterBlockEntity.updateInClientWorld();
					}
				}
				if (enchanterBlockEntity.currentItemProcessingTime > 0 && enchanterBlockEntity.craftingTime >= enchanterBlockEntity.currentItemProcessingTime) {
					playCraftingFinishedEffects(enchanterBlockEntity);
					enchantCenterItem(enchanterBlockEntity);
					
					enchanterBlockEntity.currentItemProcessingTime = -1;
					enchanterBlockEntity.craftingTime = 0;
					enchanterBlockEntity.updateInClientWorld();
					
					craftingSuccess = true;
				}
				enchanterBlockEntity.setChanged();
			}
			
			if (craftingSuccess) {
				enchanterBlockEntity.currentItemProcessingTime = -1;
				enchanterBlockEntity.craftingTime = 0;
				enchanterBlockEntity.inventoryChanged();
			}
		} else {
			SpectrumS2CPacketSender.sendCancelBlockBoundSoundInstance((ServerLevel) enchanterBlockEntity.getLevel(), enchanterBlockEntity.worldPosition);
		}
	}
	
	/**
	 * For an enchanting setup to be valid there has to be an enchantable stack in the center, an ExperienceStorageItem
	 * and Enchanted Books in the Item Bowls
	 *
	 * @param enchanterBlockEntity The Enchanter to check
	 * @return True if the enchanters inventory matches an enchanting setup
	 */
	public static boolean isValidCenterEnchantingSetup(@NotNull EnchanterBlockEntity enchanterBlockEntity) {
		ItemStack centerStack = enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem(0);
		boolean isEnchantableBookInCenter = SpectrumEnchantmentHelper.isEnchantableBook(centerStack);
		if (!centerStack.isEmpty() && (isEnchantableBookInCenter || centerStack.getItem().isEnchantable(centerStack)) && enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem(1).getItem() instanceof ExperienceStorageItem) {
			// gilded books can copy enchantments from any source item
			boolean centerStackIsGildedBook = centerStack.is(SpectrumItems.GILDED_BOOK);
			boolean enchantedBookWithAdditionalEnchantmentsFound = false;
			Map<Enchantment, Integer> existingEnchantments = EnchantmentHelper.getEnchantments(centerStack);
			for (int i = 0; i < 8; i++) {
				ItemStack virtualSlotStack = enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem(2 + i);
				// empty slots do not count
				if (!virtualSlotStack.isEmpty()) {
					if (centerStackIsGildedBook || virtualSlotStack.getItem() instanceof EnchantedBookItem) {
						Map<Enchantment, Integer> currentEnchantedBookEnchantments = EnchantmentHelper.getEnchantments(virtualSlotStack);
						for (Enchantment enchantment : currentEnchantedBookEnchantments.keySet()) {
							if ((isEnchantableBookInCenter || enchantment.canEnchant(centerStack)) && (!existingEnchantments.containsKey(enchantment) || existingEnchantments.get(enchantment) < currentEnchantedBookEnchantments.get(enchantment))) {
								if (enchanterBlockEntity.canOwnerApplyConflictingEnchantments) {
									enchantedBookWithAdditionalEnchantmentsFound = true;
									break;
								} else if (SpectrumEnchantmentHelper.canCombineAny(existingEnchantments, currentEnchantedBookEnchantments)) {
									enchantedBookWithAdditionalEnchantmentsFound = true;
									break;
								}
							}
						}
					} else {
						return false;
					}
				}
			}
			return enchantedBookWithAdditionalEnchantmentsFound;
		}
		return false;
	}
	
	public static void playCraftingFinishedEffects(@NotNull EnchanterBlockEntity enchanterBlockEntity) {
		enchanterBlockEntity.getLevel().playSound(null, enchanterBlockEntity.worldPosition, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.0F);
		
		SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) enchanterBlockEntity.getLevel(),
				new Vec3(enchanterBlockEntity.worldPosition.getX() + 0.5D, enchanterBlockEntity.worldPosition.getY() + 0.5, enchanterBlockEntity.worldPosition.getZ() + 0.5D),
				SpectrumParticleTypes.LIME_SPARKLE_RISING, 75, new Vec3(0.5D, 0.5D, 0.5D),
				new Vec3(0.1D, -0.1D, 0.1D));
	}
	
	private static boolean checkRecipeRequirements(Level world, BlockPos blockPos, @NotNull EnchanterBlockEntity enchanterBlockEntity) {
		Player lastInteractedPlayer = enchanterBlockEntity.getOwnerIfOnline();
		
		if (lastInteractedPlayer == null) {
			return false;
		}
		
		boolean playerCanCraft = true;
		if (enchanterBlockEntity.currentRecipe instanceof EnchanterRecipe enchanterRecipe) {
			playerCanCraft = enchanterRecipe.canPlayerCraft(lastInteractedPlayer);
		} else if (enchanterBlockEntity.currentRecipe instanceof EnchantmentUpgradeRecipe enchantmentUpgradeRecipe) {
			playerCanCraft = enchantmentUpgradeRecipe.canPlayerCraft(lastInteractedPlayer) && (enchanterBlockEntity.canOwnerOverenchant || !enchantmentUpgradeRecipe.requiresUnlockedOverEnchanting());
		}
		boolean structureComplete = EnchanterBlock.verifyStructure(world, blockPos, null);
		
		if (!playerCanCraft || !structureComplete) {
			if (!structureComplete) {
				world.playSound(null, enchanterBlockEntity.getBlockPos(), SpectrumSoundEvents.CRAFTING_ABORTED, SoundSource.BLOCKS, 0.9F + world.random.nextFloat() * 0.2F, 0.9F + world.random.nextFloat() * 0.2F);
				world.playSound(null, enchanterBlockEntity.getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.9F + world.random.nextFloat() * 0.2F, 0.5F + world.random.nextFloat() * 0.2F);
				EnchanterBlock.scatterContents(world, blockPos);
			}
			return false;
		}
		return true;
	}
	
	public static void enchantCenterItem(@NotNull EnchanterBlockEntity enchanterBlockEntity) {
		ItemStack centerStack = enchanterBlockEntity.getItem(0);
		ItemStack centerStackCopy = centerStack.copy();
		Map<Enchantment, Integer> highestEnchantments = getHighestEnchantmentsInItemBowls(enchanterBlockEntity);
		
		for (Enchantment enchantment : highestEnchantments.keySet()) {
			centerStackCopy = SpectrumEnchantmentHelper.addOrUpgradeEnchantment(centerStackCopy, enchantment, highestEnchantments.get(enchantment), false, enchanterBlockEntity.canOwnerApplyConflictingEnchantments).getB();
		}
		
		// START BIOME MAKEOVER COMPAT
		// If any input item has the "BMCursed" tag, the output item will also get it
		// This resolves exploits where players are able to indefinitely increase the
		// enchantment level by transferring the enchantment to new items
		for (int i = 0; i < 8; i++) {
			ItemStack bowlStack = enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem(2 + i);
			BiomeMakeoverCompat.transferBMCursedTag(bowlStack, centerStackCopy);
		}
		// END BIOME MAKEOVER COMPAT
		
		int spentExperience = enchanterBlockEntity.currentItemProcessingTime / EnchanterBlockEntity.REQUIRED_TICKS_FOR_EACH_EXPERIENCE_POINT;
		if (centerStack.getCount() > 1) {
			centerStackCopy.setCount(1);
			MultiblockCrafter.spawnOutputAsItemEntity(enchanterBlockEntity.getLevel(), enchanterBlockEntity.worldPosition, centerStackCopy);
			centerStack.shrink(1);
		} else {
			enchanterBlockEntity.setItem(0, centerStackCopy);
		}
		
		// vanilla
		grantPlayerEnchantingAdvancementCriterion(enchanterBlockEntity.ownerUUID, centerStackCopy, spentExperience);
		
		// enchanter enchanting criterion
		ServerPlayer serverPlayerEntity = (ServerPlayer) enchanterBlockEntity.getOwnerIfOnline();
		if (serverPlayerEntity != null) {
			SpectrumAdvancementCriteria.ENCHANTER_ENCHANTING.trigger(serverPlayerEntity, centerStackCopy, spentExperience);
		}
	}
	
	public static Map<Enchantment, Integer> getHighestEnchantmentsInItemBowls(@NotNull EnchanterBlockEntity enchanterBlockEntity) {
		List<ItemStack> bowlStacks = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			bowlStacks.add(enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem(2 + i));
		}
		
		return SpectrumEnchantmentHelper.collectHighestEnchantments(bowlStacks);
	}
	
	public static int getRequiredExperienceToEnchantCenterItem(@NotNull EnchanterBlockEntity enchanterBlockEntity) {
		boolean valid = false;
		ItemStack centerStack = enchanterBlockEntity.getItem(0);
		if (!centerStack.isEmpty() && (centerStack.getItem().isEnchantable(centerStack) || SpectrumEnchantmentHelper.isEnchantableBook(centerStack))) {
			ItemStack centerStackCopy = centerStack.copy();
			Map<Enchantment, Integer> highestEnchantmentLevels = getHighestEnchantmentsInItemBowls(enchanterBlockEntity);
			int requiredExperience = 0;
			for (Enchantment enchantment : highestEnchantmentLevels.keySet()) {
				int enchantmentLevel = highestEnchantmentLevels.get(enchantment);
				int currentRequired = getRequiredExperienceToEnchantWithEnchantment(centerStackCopy, enchantment, enchantmentLevel, enchanterBlockEntity.canOwnerApplyConflictingEnchantments);
				if (currentRequired > 0) {
					centerStackCopy = SpectrumEnchantmentHelper.addOrUpgradeEnchantment(centerStackCopy, enchantment, enchantmentLevel, false, enchanterBlockEntity.canOwnerApplyConflictingEnchantments).getB();
					requiredExperience += currentRequired;
					valid = true;
				} else {
					requiredExperience += 50; // conflicting enchantments (like more enchantments in a book where not all can be applied cost extra
				}
			}
			if (valid) { // and applicable enchantment found
				return requiredExperience;
			} else {
				return -1; // all enchantments already applied
			}
		}
		return -1;
	}
	
	/**
	 * Returns the experience required to enchant the given stack with the enchantment at that level
	 * Returns -1 if the enchantment is not valid for that stack or the item can not be enchanted
	 *
	 * @param stack       The item stack to enchant
	 * @param enchantment The enchantment
	 * @param level       The enchantments level
	 * @return The required experience to enchant. -1 if the enchantment is not applicable
	 */
	public static int getRequiredExperienceToEnchantWithEnchantment(ItemStack stack, Enchantment enchantment, int level, boolean allowEnchantmentConflicts) {
		if (!enchantment.canEnchant(stack) && !SpectrumEnchantmentHelper.isEnchantableBook(stack)) {
			return -1;
		}
		
		int existingLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
		if (existingLevel >= level) {
			return -1;
		}
		
		boolean conflicts = SpectrumEnchantmentHelper.hasEnchantmentThatConflictsWith(stack, enchantment);
		if (conflicts && !allowEnchantmentConflicts) {
			return -1;
		}
		
		Integer requiredExperience = getEnchantingPrice(stack, enchantment, level);
		if (conflicts) {
			requiredExperience *= 4;
		}
		return requiredExperience;
	}
	
	public static Integer getEnchantingPrice(ItemStack stack, Enchantment enchantment, int level) {
		int enchantability = Math.max(1, stack.getItem().getEnchantmentValue()); // items like Elytras have an enchantability of 0, but can get unbreaking
		if (enchantment.canEnchant(stack) || SpectrumEnchantmentHelper.isEnchantableBook(stack)) {
			return getRequiredExperienceForEnchantment(enchantability, enchantment, level);
		}
		return -1;
	}
	
	public static int getRequiredExperienceForEnchantment(int enchantability, Enchantment enchantment, int level) {
		if (enchantability > 0) {
			int rarityCost;
			Enchantment.Rarity rarity = enchantment.getRarity();
			switch (rarity) {
				case COMMON -> rarityCost = 10;
				case UNCOMMON -> rarityCost = 25;
				case RARE -> rarityCost = 50;
				default -> rarityCost = 80;
			}
			
			float levelCost = level + ((float) level / enchantment.getMaxLevel()); // the higher the level, the pricier. But not as bad for enchantments with high max levels
			float specialMulti = enchantment.isTreasureOnly() ? 2.0F : enchantment.isCurse() ? 1.5F : 1.0F;
			float selectionAvailabilityMod = 1.0F;
			if (!(enchantment instanceof SpectrumEnchantment)) {
				selectionAvailabilityMod = (enchantment.isDiscoverable() ? 0.5F : 0.75F) + (enchantment.isTradeable() ? 0.5F : 0.75F);
			}
			float enchantabilityMod = (4.0F / (2 + enchantability)) * 4.0F;
			return (int) Math.floor(rarityCost * levelCost * specialMulti * selectionAvailabilityMod * enchantabilityMod);
		}
		return -1;
	}
	
	public static int getExperienceWithMod(int experience, double mod, RandomSource random) {
		double modNormalized = 1.0 / (1.0 + Math.log10(mod));
		return Support.getIntFromDecimalWithChance(experience * modNormalized, random);
	}
	
	public static void craftEnchanterRecipe(Level world, @NotNull EnchanterBlockEntity enchanterBlockEntity, @NotNull EnchanterRecipe enchanterRecipe) {
		enchanterBlockEntity.drainExperience(enchanterRecipe.getRequiredExperience());
		
		// decrement stacks in item bowls
		for (int i = 0; i < 8; i++) {
			int resultAmountAfterEfficiencyMod = 1;
			if (!enchanterRecipe.areYieldAndEfficiencyUpgradesDisabled() && enchanterBlockEntity.upgrades.getEffectiveValue(UpgradeType.EFFICIENCY) != 1.0) {
				double efficiencyModifier = 1.0 / enchanterBlockEntity.upgrades.getEffectiveValue(UpgradeType.EFFICIENCY);
				resultAmountAfterEfficiencyMod = Support.getIntFromDecimalWithChance(efficiencyModifier, world.random);
			}

			if (resultAmountAfterEfficiencyMod > 0) {
				// since this recipe uses 1 item in each slot we can just iterate them all and decrement with 1
				BlockPos itemBowlPos = enchanterBlockEntity.worldPosition.offset(getItemBowlPositionOffset(i, enchanterBlockEntity.virtualInventoryRecipeOrientation));
				BlockEntity blockEntity = world.getBlockEntity(itemBowlPos);
				if (blockEntity instanceof ItemBowlBlockEntity itemBowlBlockEntity) {
					itemBowlBlockEntity.decrementBowlStack(new Vec3(enchanterBlockEntity.worldPosition.getX(), enchanterBlockEntity.worldPosition.getY() + 1, enchanterBlockEntity.worldPosition.getX() + 0.5), resultAmountAfterEfficiencyMod, false);
					itemBowlBlockEntity.updateInClientWorld();
				}
			}
		}

		// if there is room: place the output on the table
		// otherwise: pop it off
		ItemStack resultStack = enchanterRecipe.getResultItem(world.registryAccess()).copy();
		ItemStack existingCenterStack = enchanterBlockEntity.getItem(0);

		if (!enchanterRecipe.areYieldAndEfficiencyUpgradesDisabled() && enchanterBlockEntity.upgrades.getEffectiveValue(UpgradeType.YIELD) != 1.0) {
			int resultCountMod = Support.getIntFromDecimalWithChance(resultStack.getCount() * enchanterBlockEntity.upgrades.getEffectiveValue(UpgradeType.YIELD), world.random);
			resultStack.setCount(resultCountMod);
		}

		if (existingCenterStack.getCount() > 1) {
			existingCenterStack.shrink(1);
			MultiblockCrafter.spawnItemStackAsEntitySplitViaMaxCount(world, enchanterBlockEntity.worldPosition, resultStack, resultStack.getCount(), MultiblockCrafter.RECIPE_STACK_VELOCITY);
		} else {
			enchanterBlockEntity.setItem(0, resultStack);
		}
		
		// vanilla
		grantPlayerEnchantingAdvancementCriterion(enchanterBlockEntity.ownerUUID, resultStack, enchanterRecipe.getRequiredExperience());
		
		// enchanter crafting criterion
		ServerPlayer serverPlayerEntity = (ServerPlayer) enchanterBlockEntity.getOwnerIfOnline();
		if (serverPlayerEntity != null) {
			SpectrumAdvancementCriteria.ENCHANTER_CRAFTING.trigger(serverPlayerEntity, resultStack, enchanterRecipe.getRequiredExperience());
		}
	}
	
	public static int tickEnchantmentUpgradeRecipe(Level world, @NotNull EnchanterBlockEntity enchanterBlockEntity, @NotNull EnchantmentUpgradeRecipe enchantmentUpgradeRecipe, int itemsToConsumeLeft) {
		int itemCountToConsume = Math.min(itemsToConsumeLeft, Support.getIntFromDecimalWithChance(enchanterBlockEntity.upgrades.getEffectiveValue(UpgradeType.SPEED), world.random));

		int consumedAmount = 0;
		int bowlsChecked = 0;
		int randomBowlPosition = world.random.nextInt(8);

		int itemCountToConsumeAfterMod = itemCountToConsume;
		if (enchanterBlockEntity.upgrades.getEffectiveValue(UpgradeType.EFFICIENCY) != 1.0) {
			itemCountToConsumeAfterMod = Support.getIntFromDecimalWithChance(itemCountToConsume / enchanterBlockEntity.upgrades.getEffectiveValue(UpgradeType.EFFICIENCY), world.random);
		}

		// cycle at least once for fancy particles
		while ((consumedAmount < itemCountToConsumeAfterMod && bowlsChecked < 8) || (itemCountToConsumeAfterMod == 0 & consumedAmount == 0)) {
			Vec3i bowlOffset = getItemBowlPositionOffset(randomBowlPosition + bowlsChecked, enchanterBlockEntity.virtualInventoryRecipeOrientation);

			BlockEntity blockEntity = world.getBlockEntity(enchanterBlockEntity.worldPosition.offset(bowlOffset));
			if (blockEntity instanceof ItemBowlBlockEntity itemBowlBlockEntity) {
				if (itemCountToConsumeAfterMod == 0) {
					itemBowlBlockEntity.spawnOrbParticles(new Vec3(enchanterBlockEntity.worldPosition.getX() + 0.5, enchanterBlockEntity.worldPosition.getY() + 1.0, enchanterBlockEntity.worldPosition.getZ() + 0.5));
					consumedAmount += itemCountToConsume;
				} else {
					int decrementedAmount = itemBowlBlockEntity.decrementBowlStack(new Vec3(enchanterBlockEntity.worldPosition.getX() + 0.5, enchanterBlockEntity.worldPosition.getY() + 1.0, enchanterBlockEntity.worldPosition.getZ() + 0.5), itemCountToConsumeAfterMod, true);
					consumedAmount += decrementedAmount;
				}
			}
			bowlsChecked++;
		}
		
		return consumedAmount;
	}
	
	public static void craftEnchantmentUpgradeRecipe(Level world, @NotNull EnchanterBlockEntity enchanterBlockEntity, @NotNull EnchantmentUpgradeRecipe enchantmentUpgradeRecipe) {
		enchanterBlockEntity.drainExperience(enchantmentUpgradeRecipe.getRequiredExperience());
		
		ItemStack resultStack = enchanterBlockEntity.getItem(0);
		resultStack = SpectrumEnchantmentHelper.addOrUpgradeEnchantment(resultStack, enchantmentUpgradeRecipe.getEnchantment(), enchantmentUpgradeRecipe.getEnchantmentDestinationLevel(), false, true).getB();
		enchanterBlockEntity.setItem(0, resultStack);
		
		// vanilla
		grantPlayerEnchantingAdvancementCriterion(enchanterBlockEntity.ownerUUID, resultStack, enchantmentUpgradeRecipe.getRequiredExperience());
		
		// enchantment upgrading criterion
		ServerPlayer serverPlayerEntity = (ServerPlayer) enchanterBlockEntity.getOwnerIfOnline();
		if (serverPlayerEntity != null) {
			SpectrumAdvancementCriteria.ENCHANTER_UPGRADING.trigger(serverPlayerEntity, enchantmentUpgradeRecipe.getEnchantment(), enchantmentUpgradeRecipe.getEnchantmentDestinationLevel(), enchantmentUpgradeRecipe.getRequiredExperience());
		}
	}
	
	public static Vec3i getItemBowlPositionOffset(int index, int orientation) {
		int offset = (orientation * 2 + index) % 8;
		return ITEM_BOWL_OFFSETS.get(offset);
	}
	
	/**
	 * Calculates and sets a new recipe for the enchanter based on it's inventory
	 *
	 * @param world                The Enchanter World
	 * @param enchanterBlockEntity The Enchanter Block Entity
	 */
	private static void calculateCurrentRecipe(@NotNull Level world, @NotNull EnchanterBlockEntity enchanterBlockEntity) {
		if (enchanterBlockEntity.currentRecipe != null) {
			if (enchanterBlockEntity.currentRecipe.matches(enchanterBlockEntity.virtualInventoryIncludingBowlStacks, world)) {
				return;
			}
		}
		
		enchanterBlockEntity.craftingTime = 0;
		GatedSpectrumRecipe previousRecipe = enchanterBlockEntity.currentRecipe;
		enchanterBlockEntity.currentRecipe = null;
		int previousOrientation = enchanterBlockEntity.virtualInventoryRecipeOrientation;
		
		SimpleContainer recipeTestInventory = new SimpleContainer(enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getContainerSize());
		recipeTestInventory.setItem(0, enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem(0));
		recipeTestInventory.setItem(1, enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem(1));
		
		EnchantmentUpgradeRecipe enchantmentUpgradeRecipe = world.getRecipeManager().getRecipeFor(SpectrumRecipeTypes.ENCHANTMENT_UPGRADE, enchanterBlockEntity.virtualInventoryIncludingBowlStacks, world).orElse(null);
		if (enchantmentUpgradeRecipe == null) {
			// Cycle through 4 phases of recipe orientations
			// This way the player can arrange the ingredients in the item bowls as they like
			// without resorting to specifying a fixed orientation
			for (int orientation = 0; orientation < 4; orientation++) {
				int offset = orientation * 2;
				recipeTestInventory.setItem(2, enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem((offset + 8) % 8 + 2));
				recipeTestInventory.setItem(3, enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem((offset + 1 + 8) % 8 + 2));
				recipeTestInventory.setItem(4, enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem((offset + 2 + 8) % 8 + 2));
				recipeTestInventory.setItem(5, enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem((offset + 3 + 8) % 8 + 2));
				recipeTestInventory.setItem(6, enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem((offset + 4 + 8) % 8 + 2));
				recipeTestInventory.setItem(7, enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem((offset + 5 + 8) % 8 + 2));
				recipeTestInventory.setItem(8, enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem((offset + 6 + 8) % 8 + 2));
				recipeTestInventory.setItem(9, enchanterBlockEntity.virtualInventoryIncludingBowlStacks.getItem((offset + 7 + 8) % 8 + 2));
				
				EnchanterRecipe enchanterRecipe = world.getRecipeManager().getRecipeFor(SpectrumRecipeTypes.ENCHANTER, recipeTestInventory, world).orElse(null);
				if (enchanterRecipe != null) {
					enchanterBlockEntity.currentRecipe = enchanterRecipe;
					enchanterBlockEntity.virtualInventoryRecipeOrientation = orientation;
					enchanterBlockEntity.virtualInventoryIncludingBowlStacks = recipeTestInventory;
					enchanterBlockEntity.craftingTimeTotal = (int) Math.ceil(enchanterRecipe.getCraftingTime() / enchanterBlockEntity.upgrades.getEffectiveValue(Upgradeable.UpgradeType.SPEED));
					break;
				}
			}
		} else {
			if (enchanterBlockEntity.canOwnerOverenchant || !enchantmentUpgradeRecipe.requiresUnlockedOverEnchanting()) {
				enchanterBlockEntity.currentRecipe = enchantmentUpgradeRecipe;
				enchanterBlockEntity.currentItemProcessingTime = 0;
				enchanterBlockEntity.virtualInventoryRecipeOrientation = previousOrientation;
				enchanterBlockEntity.virtualInventoryIncludingBowlStacks = recipeTestInventory;
				enchanterBlockEntity.craftingTimeTotal = enchantmentUpgradeRecipe.getRequiredItemCount();
			}
		}
		
		if (enchanterBlockEntity.currentRecipe != previousRecipe) {
			enchanterBlockEntity.updateInClientWorld();
		}
		
	}
	
	private static void grantPlayerEnchantingAdvancementCriterion(UUID playerUUID, ItemStack resultStack, int experience) {
		int levels = ExperienceHelper.getLevelForExperience(experience);
		ServerPlayer serverPlayerEntity = (ServerPlayer) PlayerOwned.getPlayerEntityIfOnline(playerUUID);
		if (serverPlayerEntity != null) {
			serverPlayerEntity.awardStat(Stats.ENCHANT_ITEM);
			CriteriaTriggers.ENCHANTED_ITEM.trigger(serverPlayerEntity, resultStack, levels);
		}
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.craftingTime = nbt.getInt("crafting_time");
		this.craftingTimeTotal = nbt.getInt("crafting_time_total");
		this.currentItemProcessingTime = nbt.getInt("current_item_processing_time");
		
		this.inventoryChanged = nbt.getBoolean("inventory_changed");
		this.canOwnerApplyConflictingEnchantments = nbt.getBoolean("owner_can_apply_conflicting_enchantments");
		this.canOwnerOverenchant = nbt.getBoolean("owner_can_overenchant");
		this.virtualInventoryRecipeOrientation = nbt.getInt("virtual_recipe_orientation");
		this.virtualInventoryIncludingBowlStacks = new SimpleContainer(INVENTORY_SIZE + 8);
		this.virtualInventoryIncludingBowlStacks.fromTag(nbt.getList("virtual_inventory", 10));
		if (nbt.contains("item_facing", Tag.TAG_STRING)) {
			this.itemFacing = Direction.valueOf(nbt.getString("item_facing").toUpperCase(Locale.ROOT));
		}
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
		
		this.currentRecipe = null;
		if (nbt.contains("CurrentRecipe")) {
			String recipeString = nbt.getString("CurrentRecipe");
			if (!recipeString.isEmpty() && SpectrumCommon.minecraftServer != null) {
				Optional<? extends Recipe<?>> optionalRecipe = SpectrumCommon.minecraftServer.getRecipeManager().byKey(new ResourceLocation(recipeString));
				if (optionalRecipe.isPresent() && optionalRecipe.get() instanceof GatedSpectrumRecipe gatedSpectrumRecipe) {
					if (optionalRecipe.get() instanceof EnchanterRecipe || optionalRecipe.get() instanceof EnchantmentUpgradeRecipe) {
						this.currentRecipe = gatedSpectrumRecipe;
					}
				}
			}
		}
		
		if (this.currentRecipe == null && this.level != null && this.level.isClientSide) {
			stopCraftingMusic();
		}

		if (nbt.contains("Upgrades", Tag.TAG_LIST)) {
			this.upgrades = UpgradeHolder.fromNbt(nbt.getList("Upgrades", Tag.TAG_COMPOUND));
		} else {
			this.upgrades = new UpgradeHolder();
		}
	}
	
	@Environment(EnvType.CLIENT)
	protected void stopCraftingMusic() {
		CraftingBlockSoundInstance.stopPlayingOnPos(this.worldPosition);
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putInt("crafting_time", this.craftingTime);
		nbt.putInt("crafting_time_total", this.craftingTimeTotal);
		nbt.putInt("current_item_processing_time", this.currentItemProcessingTime);
		nbt.putInt("virtual_recipe_orientation", this.virtualInventoryRecipeOrientation);
		nbt.putBoolean("inventory_changed", this.inventoryChanged);
		nbt.putBoolean("owner_can_apply_conflicting_enchantments", this.canOwnerApplyConflictingEnchantments);
		nbt.putBoolean("owner_can_overenchant", this.canOwnerOverenchant);
		nbt.put("virtual_inventory", this.virtualInventoryIncludingBowlStacks.createTag());
		if (this.itemFacing != null) {
			nbt.putString("item_facing", this.itemFacing.toString().toUpperCase(Locale.ROOT));
		}
		if (this.upgrades != null) {
			nbt.put("Upgrades", this.upgrades.toNbt());
		}
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
		if (this.currentRecipe != null) {
			nbt.putString("CurrentRecipe", this.currentRecipe.getId().toString());
		}
	}
	
	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public void updateInClientWorld() {
		level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), Block.UPDATE_INVISIBLE);
	}
	
	public Direction getItemFacingDirection() {
		// if placed via pipe or other sources
		return Objects.requireNonNullElse(this.itemFacing, Direction.NORTH);
	}
	
	public void setItemFacingDirection(Direction facingDirection) {
		this.itemFacing = facingDirection;
	}
	
	private void doItemBowlOrbs(Level world) {
		for (int i = 0; i < 8; i++) {
			BlockPos itemBowlPos = worldPosition.offset(getItemBowlPositionOffset(i, 0));
			BlockEntity blockEntity = world.getBlockEntity(itemBowlPos);
			if (blockEntity instanceof ItemBowlBlockEntity itemBowlBlockEntity) {
				itemBowlBlockEntity.spawnOrbParticles(new Vec3(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + 0.5));
			}
		}
	}
	
	public boolean drainExperience(int amount) {
		ItemStack experienceProviderStack = getItem(1);
		if (experienceProviderStack.getItem() instanceof ExperienceStorageItem experienceStorageItem) {
			int currentStoredExperience = ExperienceStorageItem.getStoredExperience(experienceProviderStack);
			if (currentStoredExperience > 0) {
				int amountAfterExperienceMod = getExperienceWithMod(amount, this.upgrades.getEffectiveValue(UpgradeType.EXPERIENCE), level.random);
				int drainedExperience = Math.min(currentStoredExperience, amountAfterExperienceMod);
				
				if (experienceStorageItem instanceof KnowledgeGemItem knowledgeGemItem) {
					if (knowledgeGemItem.changedDisplayTier(currentStoredExperience, currentStoredExperience - drainedExperience)) {
						// There was enough experience drained from the knowledge gem that the visual changed
						// To display the updated knowledge gem size clientside the inventory has to be synched
						// to the clients for rendering purposes
						SpectrumS2CPacketSender.playParticleWithPatternAndVelocity(null, (ServerLevel) level, new Vec3(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 2.5, this.worldPosition.getZ() + 0.5), SpectrumParticleTypes.LIME_CRAFTING, VectorPattern.SIXTEEN, 0.05F);
						this.updateInClientWorld();
					}
				}
				
				this.setChanged();
				return ExperienceStorageItem.removeStoredExperience(experienceProviderStack, drainedExperience);
			}
		}
		return false;
	}
	
	@Override
	public void inventoryChanged() {
		virtualInventoryIncludingBowlStacks = new SimpleContainer(INVENTORY_SIZE + 8);
		virtualInventoryIncludingBowlStacks.setItem(0, this.getItem(0)); // center item
		virtualInventoryIncludingBowlStacks.setItem(1, this.getItem(1)); // knowledge gem
		virtualInventoryIncludingBowlStacks.setItem(2, getItemBowlStack(this.getLevel(), worldPosition.offset(5, 0, -3)));
		virtualInventoryIncludingBowlStacks.setItem(3, getItemBowlStack(this.getLevel(), worldPosition.offset(5, 0, 3)));
		virtualInventoryIncludingBowlStacks.setItem(4, getItemBowlStack(this.getLevel(), worldPosition.offset(3, 0, 5)));
		virtualInventoryIncludingBowlStacks.setItem(5, getItemBowlStack(this.getLevel(), worldPosition.offset(-3, 0, 5)));
		virtualInventoryIncludingBowlStacks.setItem(6, getItemBowlStack(this.getLevel(), worldPosition.offset(-5, 0, 3)));
		virtualInventoryIncludingBowlStacks.setItem(7, getItemBowlStack(this.getLevel(), worldPosition.offset(-5, 0, -3)));
		virtualInventoryIncludingBowlStacks.setItem(8, getItemBowlStack(this.getLevel(), worldPosition.offset(-3, 0, -5)));
		virtualInventoryIncludingBowlStacks.setItem(9, getItemBowlStack(this.getLevel(), worldPosition.offset(3, 0, -5)));
		
		virtualInventoryIncludingBowlStacks.setChanged();
		inventoryChanged = true;
		currentItemProcessingTime = -1;
		
		super.inventoryChanged();
	}
	
	public ItemStack getItemBowlStack(Level world, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof ItemBowlBlockEntity itemBowlBlockEntity) {
			return itemBowlBlockEntity.getItem(0);
		} else {
			return ItemStack.EMPTY;
		}
	}
	
	public void playSound(SoundEvent soundEvent, float volume) {
		RandomSource random = level.random;
		level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), soundEvent, SoundSource.BLOCKS, volume, 0.9F + random.nextFloat() * 0.15F);
	}
	
	// PLAYER OWNED
	// "owned" is not to be taken literally here. The owner
	// is always set to the last player interacted with to trigger advancements
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public void setOwner(Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		this.canOwnerApplyConflictingEnchantments = AdvancementHelper.hasAdvancement(playerEntity, APPLY_CONFLICTING_ENCHANTMENTS_ADVANCEMENT_IDENTIFIER);
		this.canOwnerOverenchant = AdvancementHelper.hasAdvancement(playerEntity, OVERENCHANTING_ADVANCEMENT_IDENTIFIER);
		setChanged();
	}
	
	// UPGRADEABLE
	@Override
	public void resetUpgrades() {
		this.upgrades = null;
		this.setChanged();
	}
	
	@Override
	public void calculateUpgrades() {
		this.upgrades = Upgradeable.calculateUpgradeMods4(level, worldPosition, 3, 0, this.ownerUUID);
		this.setChanged();
	}

	@Override
	public UpgradeHolder getUpgradeHolder() {
		return this.upgrades;
	}

}
