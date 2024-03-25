package de.dafuqs.spectrum.api.energy;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.helpers.Support;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface InkPowered {
	
	/**
	 * The advancement the player needs to have in order to use ink powered tools
	 */
	ResourceLocation REQUIRED_ADVANCEMENT = SpectrumCommon.locate("milestones/unlock_ink_use");
	
	@Environment(EnvType.CLIENT)
    static boolean canUseClient() {
		Minecraft client = Minecraft.getInstance();
		return canUse(client.player);
	}
	
	static boolean canUse(Player playerEntity) {
		return AdvancementHelper.hasAdvancement(playerEntity, InkPowered.REQUIRED_ADVANCEMENT);
	}
	
	/**
	 * The colors that the object requires for working.
	 * These are added as the player facing tooltip
	 **/
	List<InkColor> getUsedColors();
	
	/**
	 * The colors that the object requires for working.
	 * These are added as the player facing tooltip
	 **/
	@Environment(EnvType.CLIENT)
	default void addInkPoweredTooltip(List<Component> tooltip) {
		if (canUseClient()) {
			if (getUsedColors().size() > 1) {
				tooltip.add(Component.translatable("spectrum.tooltip.ink_powered.prefix").withStyle(ChatFormatting.GRAY));
				for (InkColor color : getUsedColors()) {
					tooltip.add(Component.translatable("spectrum.tooltip.ink_powered.bullet." + color.toString()));
				}
			} else {
				tooltip.add(Component.translatable("spectrum.tooltip.ink_powered." + getUsedColors().get(0).toString()).withStyle(ChatFormatting.GRAY));
			}
		}
	}
	
	private static long tryDrainEnergy(@NotNull ItemStack stack, InkColor color, long amount, boolean viaPlayer) {
		if (stack.getItem() instanceof InkStorageItem<?> inkStorageItem) {
			if (!inkStorageItem.getDrainability().canDrain(viaPlayer)) {
				return 0;
			}
			
			InkStorage inkStorage = inkStorageItem.getEnergyStorage(stack);
			long drained = inkStorage.drainEnergy(color, amount);
			if (drained > 0) {
				inkStorageItem.setEnergyStorage(stack, inkStorage);
			}
			return drained;
		} else {
			return 0;
		}
	}
	
	private static long tryGetEnergy(@NotNull ItemStack stack, InkColor color) {
		if (stack.getItem() instanceof InkStorageItem<?> inkStorageItem) {
			InkStorage inkStorage = inkStorageItem.getEnergyStorage(stack);
			return inkStorage.getEnergy(color);
		} else {
			return 0;
		}
	}
	
	/**
	 * Searches an inventory for InkEnergyStorageItems and tries to drain the color energy.
	 * If enough could be drained returns true, else false.
	 * If not enough energy is available it will be drained as much as is available
	 * but return will still be false
	 **/
	static boolean tryDrainEnergy(@NotNull Container inventory, InkColor color, long amount) {
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack currentStack = inventory.getItem(i);
			if (!currentStack.isEmpty()) { // fast fail
				amount -= tryDrainEnergy(currentStack, color, amount, false);
				if (amount <= 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	static boolean tryDrainEnergy(@NotNull Player player, @NotNull InkCost inkCost) {
		return tryDrainEnergy(player, inkCost.getColor(), inkCost.getCost());
	}
	
	static boolean tryDrainEnergy(@NotNull Player player, @NotNull InkCost inkCost, float costModifier) {
		return tryDrainEnergy(player, inkCost.getColor(), Support.getIntFromDecimalWithChance(inkCost.getCost() * costModifier, player.getRandom()));
	}
	
	/**
	 * Searches the players Trinkets for energy storage first and inventory second
	 * for PigmentEnergyStorageItem and tries to drain the color energy.
	 * If enough could be drained returns true, else false.
	 * If not enough energy is available it will be drained as much as is available
	 * but return will still be false
	 * <p>
	 * Check Order:
	 * - Offhand
	 * - Trinket Slots
	 * - Inventory
	 **/
	static boolean tryDrainEnergy(@NotNull Player player, @NotNull InkColor color, long amount) {
		if (player.isCreative()) {
			return true;
		}
		if (!canUse(player)) {
			return false;
		}
		
		// hands (main hand, too, if someone uses the staff from the offhand)
		for (ItemStack itemStack : player.getHandSlots()) {
			amount -= tryDrainEnergy(itemStack, color, amount, true);
			if (amount <= 0) {
				return true;
			}
		}
		
		// trinket slots
		Optional<TrinketComponent> optionalTrinketComponent = TrinketsApi.getTrinketComponent(player);
		if (optionalTrinketComponent.isPresent()) {
			List<Tuple<SlotReference, ItemStack>> trinketInkStorages = optionalTrinketComponent.get().getEquipped(itemStack -> itemStack.getItem() instanceof InkStorageItem<?>);
			for (Tuple<SlotReference, ItemStack> trinketEnergyStorageStack : trinketInkStorages) {
				amount -= tryDrainEnergy(trinketEnergyStorageStack.getB(), color, amount, true);
				if (amount <= 0) {
					return true;
				}
			}
		}
		
		// inventory
		for (ItemStack itemStack : player.getInventory().items) {
			amount -= tryDrainEnergy(itemStack, color, amount, true);
			if (amount <= 0) {
				return true;
			}
		}
		
		return false;
	}
	
	static long getAvailableInk(@NotNull Player player, InkColor color) {
		if (player.isCreative()) {
			return Long.MAX_VALUE;
		}
		if (!canUse(player)) {
			return 0;
		}
		
		long available = 0;
		// offhand
		for (ItemStack itemStack : player.getHandSlots()) {
			available += tryGetEnergy(itemStack, color);
		}
		
		// trinket slot
		Optional<TrinketComponent> optionalTrinketComponent = TrinketsApi.getTrinketComponent(player);
		if (optionalTrinketComponent.isPresent()) {
			List<Tuple<SlotReference, ItemStack>> trinketInkStorages = optionalTrinketComponent.get().getEquipped(itemStack -> itemStack.getItem() instanceof InkStorageItem<?>);
			for (Tuple<SlotReference, ItemStack> trinketEnergyStorageStack : trinketInkStorages) {
				available += tryGetEnergy(trinketEnergyStorageStack.getB(), color);
			}
		}
		
		// inventory
		for (ItemStack itemStack : player.getInventory().items) {
			available += tryGetEnergy(itemStack, color);
		}
		return available;
	}
	
	static boolean hasAvailableInk(Player player, InkCost inkCost) {
		return hasAvailableInk(player, inkCost.getColor(), inkCost.getCost());
	}
	
	static boolean hasAvailableInk(Player player, InkColor color, long amount) {
		if (!canUse(player)) {
			return false;
		}
		
		// offhand
		for (ItemStack itemStack : player.getInventory().offhand) {
			amount -= tryGetEnergy(itemStack, color);
			if (amount <= 0) {
				return true;
			}
		}
		
		// trinket slot
		Optional<TrinketComponent> optionalTrinketComponent = TrinketsApi.getTrinketComponent(player);
		if (optionalTrinketComponent.isPresent()) {
			List<Tuple<SlotReference, ItemStack>> trinketInkStorages = optionalTrinketComponent.get().getEquipped(itemStack -> itemStack.getItem() instanceof InkStorageItem<?>);
			for (Tuple<SlotReference, ItemStack> trinketEnergyStorageStack : trinketInkStorages) {
				amount -= tryGetEnergy(trinketEnergyStorageStack.getB(), color);
				if (amount <= 0) {
					return true;
				}
			}
		}
		
		// inventory
		for (ItemStack itemStack : player.getInventory().items) {
			amount -= tryGetEnergy(itemStack, color);
			if (amount <= 0) {
				return true;
			}
		}
		
		return false;
	}
	
}
