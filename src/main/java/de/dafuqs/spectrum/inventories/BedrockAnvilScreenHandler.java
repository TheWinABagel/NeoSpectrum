package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.LoreHelper;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BedrockAnvilScreenHandler extends AbstractContainerMenu {
	
	public static final int MAX_NAME_LENGTH = 50;
	public static final int MAX_LORE_LENGTH = 200;
	
	public static final int FIRST_INPUT_SLOT_INDEX = 0;
	public static final int SECOND_INPUT_SLOT_INDEX = 1;
	public static final int OUTPUT_SLOT_INDEX = 2;
	private static final int PLAYER_INVENTORY_START_INDEX = 3;
	private static final int PLAYER_INVENTORY_END_INDEX = 39;
	
	protected final ResultContainer output = new ResultContainer();
	protected final ContainerLevelAccess context;
	protected final Container input = new SimpleContainer(2) {
		@Override
		public void setChanged() {
			super.setChanged();
			slotsChanged(this);
		}
	};
	protected final Player player;
	private final DataSlot levelCost;
	private int repairItemCount;
	private String newItemName;
	private String newLoreString;
	
	public BedrockAnvilScreenHandler(int syncId, Inventory inventory) {
		this(syncId, inventory, ContainerLevelAccess.NULL);
	}
	
	public BedrockAnvilScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
		super(SpectrumScreenHandlerTypes.BEDROCK_ANVIL, syncId);
		this.levelCost = DataSlot.standalone();
		this.addDataSlot(this.levelCost);
		
		this.context = context;
		this.player = playerInventory.player;
		this.addSlot(new Slot(this.input, FIRST_INPUT_SLOT_INDEX, 27, 47));
		this.addSlot(new Slot(this.input, SECOND_INPUT_SLOT_INDEX, 76, 47));
		this.addSlot(new Slot(this.output, OUTPUT_SLOT_INDEX, 134, 47) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
			
			@Override
			public boolean mayPickup(Player playerEntity) {
				return canTakeOutput(playerEntity, this.hasItem());
			}
			
			@Override
			public void onTake(Player player, ItemStack stack) {
				onTakeOutput(player, stack);
			}
		});
		
		int k;
		for (k = 0; k < 3; ++k) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 8 + j * 18, 24 + 84 + k * 18));
			}
		}
		
		for (k = 0; k < 9; ++k) {
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 24 + 142));
		}
	}
	
	public static int getNextCost(int cost) {
		return cost * 2 + 1;
	}
	
	@Override
	public void slotsChanged(Container inventory) {
		super.slotsChanged(inventory);
		if (inventory == this.input) {
			this.updateResult();
		}
	}
	
	@Override
	public void removed(Player player) {
		super.removed(player);
		this.context.execute((world, pos) -> this.clearContainer(player, this.input));
	}
	
	@Override
	public boolean stillValid(Player player) {
		return this.context.evaluate((world, pos) -> this.canUse(world.getBlockState(pos)) && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack itemStack2 = slot.getItem();
			itemStack = itemStack2.copy();
			if (index == 2) {
				if (!this.moveItemStackTo(itemStack2, PLAYER_INVENTORY_START_INDEX, PLAYER_INVENTORY_END_INDEX, true)) {
					return ItemStack.EMPTY;
				}
				
				slot.onQuickCraft(itemStack2, itemStack);
			} else if (index != 0 && index != 1) {
				if (index >= PLAYER_INVENTORY_START_INDEX && index < PLAYER_INVENTORY_END_INDEX) {
					int i = 0;
					if (!this.moveItemStackTo(itemStack2, i, 2, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.moveItemStackTo(itemStack2, PLAYER_INVENTORY_START_INDEX, PLAYER_INVENTORY_END_INDEX, false)) {
				return ItemStack.EMPTY;
			}
			
			if (itemStack2.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			
			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}
			
			slot.onTake(player, itemStack2);
		}
		
		return itemStack;
	}
	
	protected boolean canUse(BlockState state) {
		return state.is(BlockTags.ANVIL);
	}
	
	protected boolean canTakeOutput(Player player, boolean present) {
		return player.getAbilities().instabuild || player.experienceLevel >= this.levelCost.get();
	}
	
	protected void onTakeOutput(Player player, ItemStack stack) {
		if (!player.getAbilities().instabuild) {
			player.giveExperienceLevels(-this.levelCost.get());
		}
		
		this.input.setItem(0, ItemStack.EMPTY);
		if (this.repairItemCount > 0) {
			ItemStack itemStack = this.input.getItem(1);
			if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemCount) {
				itemStack.shrink(this.repairItemCount);
				this.input.setItem(1, itemStack);
			} else {
				this.input.setItem(1, ItemStack.EMPTY);
			}
		} else {
			this.input.setItem(1, ItemStack.EMPTY);
		}
		
		this.levelCost.set(0);
		this.context.execute((world, pos) -> world.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0));
	}
	
	public void updateResult() {
		boolean combined = false;
		
		ItemStack inputStack = this.input.getItem(0);
		this.levelCost.set(0);
		int enchantmentLevelCost = 0;
		int repairLevelCost = 0;
		int k = 0;
		if (inputStack.isEmpty()) {
			this.output.setItem(0, ItemStack.EMPTY);
			this.levelCost.set(0);
		} else {
			ItemStack outputStack = inputStack.copy();
			ItemStack repairSlotStack = this.input.getItem(1);
			Map<Enchantment, Integer> enchantmentLevelMap = EnchantmentHelper.getEnchantments(outputStack);
			repairLevelCost += inputStack.getBaseRepairCost() + (repairSlotStack.isEmpty() ? 0 : repairSlotStack.getBaseRepairCost());
			this.repairItemCount = 0;
			if (!repairSlotStack.isEmpty()) {
				combined = true;
				
				boolean enchantedBookInInputSlot = inputStack.is(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantments(inputStack).isEmpty();
				boolean enchantedBookInRepairSlot = repairSlotStack.is(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantments(repairSlotStack).isEmpty();

				int o;
				int repairItemCount;
				int newOutputStackDamage;
				if (outputStack.isDamageableItem() && outputStack.getItem().isValidRepairItem(inputStack, repairSlotStack)) {
					o = Math.min(outputStack.getDamageValue(), outputStack.getMaxDamage() / 4);
					if (o <= 0) {
						this.output.setItem(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}
					
					for (repairItemCount = 0; o > 0 && repairItemCount < repairSlotStack.getCount(); ++repairItemCount) {
						newOutputStackDamage = outputStack.getDamageValue() - o;
						outputStack.setDamageValue(newOutputStackDamage);
						++enchantmentLevelCost;
						o = Math.min(outputStack.getDamageValue(), outputStack.getMaxDamage() / 4);
					}
					
					this.repairItemCount = repairItemCount;
				} else {
					if (!enchantedBookInRepairSlot && (!outputStack.is(repairSlotStack.getItem()) || !outputStack.isDamageableItem())) {
						this.output.setItem(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}
					
					if (outputStack.isDamageableItem() && !enchantedBookInRepairSlot) {
						o = inputStack.getMaxDamage() - inputStack.getDamageValue();
						repairItemCount = repairSlotStack.getMaxDamage() - repairSlotStack.getDamageValue();
						newOutputStackDamage = repairItemCount + outputStack.getMaxDamage() * 12 / 100;
						int r = o + newOutputStackDamage;
						int s = outputStack.getMaxDamage() - r;
						if (s < 0) {
							s = 0;
						}
						
						if (s < outputStack.getDamageValue()) {
							outputStack.setDamageValue(s);
							enchantmentLevelCost += 2;
						}
					}
					
					Map<Enchantment, Integer> currentEnchantments = EnchantmentHelper.getEnchantments(repairSlotStack);
					boolean bl2 = false;
					boolean bl3 = false;
					Iterator<Enchantment> enchantmentIterator = currentEnchantments.keySet().iterator();
					
					label155:
					while (true) {
						Enchantment enchantment;
						do {
							if (!enchantmentIterator.hasNext()) {
								if (bl3 && !bl2) {
									this.output.setItem(0, ItemStack.EMPTY);
									this.levelCost.set(0);
									return;
								}
								break label155;
							}
							enchantment = enchantmentIterator.next();
						} while (enchantment == null);
						
						int t = enchantmentLevelMap.getOrDefault(enchantment, 0);
						int newEnchantmentLevel = currentEnchantments.get(enchantment);
						newEnchantmentLevel = t == newEnchantmentLevel ? newEnchantmentLevel + 1 : Math.max(newEnchantmentLevel, t);
						boolean itemStackIsAcceptableForStack = enchantment.canEnchant(inputStack);
						if (this.player.getAbilities().instabuild || inputStack.is(Items.ENCHANTED_BOOK)) {
							itemStackIsAcceptableForStack = true;
						}
						
						for (Enchantment enchantment2 : enchantmentLevelMap.keySet()) {
							if (enchantment2 != enchantment && !enchantment.isCompatibleWith(enchantment2)) {
								itemStackIsAcceptableForStack = false;
								++enchantmentLevelCost;
							}
						}
						
						if (!itemStackIsAcceptableForStack) {
							bl3 = true;
						} else {
							bl2 = true;
							boolean capToMaxLevel = enchantedBookInInputSlot || !SpectrumCommon.CONFIG.BedrockAnvilCanExceedMaxVanillaEnchantmentLevel;
							if (capToMaxLevel && newEnchantmentLevel > enchantment.getMaxLevel()) {
								newEnchantmentLevel = enchantment.getMaxLevel();
							}
							
							enchantmentLevelMap.put(enchantment, newEnchantmentLevel);
							int enchantmentRarityInt = switch (enchantment.getRarity()) {
								case COMMON -> 1;
								case UNCOMMON -> 2;
								case RARE -> 4;
								case VERY_RARE -> 8;
							};
							
							if (enchantedBookInRepairSlot) {
								enchantmentRarityInt = Math.max(1, enchantmentRarityInt / 2);
							}
							
							enchantmentLevelCost += enchantmentRarityInt * newEnchantmentLevel;
							if (inputStack.getCount() > 1) {
								enchantmentLevelCost = 40;
							}
						}
					}
				}
			}
			
			if (StringUtils.isBlank(this.newItemName)) {
				if (inputStack.hasCustomHoverName()) {
					outputStack.resetHoverName();
				}
			} else if (!this.newItemName.equals(inputStack.getHoverName().getString())) {
				outputStack.setHoverName(Component.literal(this.newItemName));
			}
			
			if (StringUtils.isBlank(this.newLoreString)) {
				if (LoreHelper.hasLore(inputStack)) {
					LoreHelper.removeLore(outputStack);
				}
			} else {
				List<Component> lore = LoreHelper.getLoreTextArrayFromString(this.newLoreString);
				if (!LoreHelper.equalsLore(lore, inputStack)) {
					LoreHelper.setLore(outputStack, lore);
				}
			}
			
			this.levelCost.set(repairLevelCost + enchantmentLevelCost);
			if (enchantmentLevelCost < 0) {
				outputStack = ItemStack.EMPTY;
			}
			
			if (!combined) {
				// renaming and lore is free
				this.levelCost.set(0);
			} else if (!outputStack.isEmpty()) {
				int repairCost = outputStack.getBaseRepairCost();
				if (!repairSlotStack.isEmpty() && repairCost < repairSlotStack.getBaseRepairCost()) {
					repairCost = repairSlotStack.getBaseRepairCost();
				}
				if (k != enchantmentLevelCost) {
					repairCost = getNextCost(repairCost);
					outputStack.setRepairCost(repairCost);
				}
				EnchantmentHelper.setEnchantments(enchantmentLevelMap, outputStack);
			}
			
			this.output.setItem(0, outputStack);
			this.broadcastChanges();
		}
	}
	
	public boolean setNewItemName(String newItemName) {
		String string = sanitize(newItemName, MAX_NAME_LENGTH);
		if (!string.equals(this.newItemName)) {
			this.newItemName = string;
			if (this.getSlot(2).hasItem()) {
				ItemStack itemStack = this.getSlot(2).getItem();
				if (Util.isBlank(string)) {
					itemStack.resetHoverName();
				} else {
					itemStack.setHoverName(Component.literal(string));
				}
			}
			
			this.updateResult();
			return true;
		} else {
			return false;
		}
	}
	
	private static String sanitize(String name, int maxLength) {
		String s = SharedConstants.filterText(name);
		return s.length() > maxLength ? s.substring(0, maxLength) : s;
	}
	
	public void setNewItemLore(String newLoreString) {
		this.newLoreString = sanitize(newLoreString, MAX_LORE_LENGTH);
		
		if (this.getSlot(2).hasItem()) {
			ItemStack itemStack = this.getSlot(2).getItem();
			if (StringUtils.isBlank(newLoreString)) {
				LoreHelper.removeLore(itemStack);
			} else {
				LoreHelper.setLore(itemStack, LoreHelper.getLoreTextArrayFromString(this.newLoreString));
			}
		}
		this.updateResult();
	}
	
	public int getLevelCost() {
		return this.levelCost.get();
	}
	
}
