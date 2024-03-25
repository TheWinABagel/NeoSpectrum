package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnchantmentCanvasItem extends Item {
	
	public EnchantmentCanvasItem(Properties settings) {
		super(settings);
	}
	
	/**
	 * clicked onto another stack
	 */
	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickType, Player player) {
		if (clickType == ClickAction.SECONDARY) {
			ItemStack otherStack = slot.getItem();
			if (!otherStack.isEmpty() && tryExchangeEnchantments(stack, otherStack, player)) {
				if (player != null) {
					playExchangeSound(player);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * itemStack is right-clicked onto this
	 */
	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference) {
		if (clickType == ClickAction.SECONDARY && !otherStack.isEmpty() && slot.allowModification(player)) {
			if (tryExchangeEnchantments(stack, otherStack, player)) {
				if (player != null) {
					playExchangeSound(player);
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean tryExchangeEnchantments(ItemStack canvasStack, ItemStack targetStack, @Nullable Entity receiver) {
		Optional<Item> itemLock = getItemBoundTo(canvasStack);
		if (itemLock.isPresent() && !targetStack.is(itemLock.get())) {
			return false;
		}
		
		Map<Enchantment, Integer> canvasEnchantments = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(canvasStack));
		Map<Enchantment, Integer> targetEnchantments = EnchantmentHelper.deserializeEnchantments(targetStack.getEnchantmentTags());
		if (canvasEnchantments.isEmpty() && targetEnchantments.isEmpty()) {
			return false;
		}
		
		boolean drop = false;
		if (canvasStack.getCount() >= 1) {
			canvasStack = canvasStack.split(1);
			drop = true;
		}
		
		// if the canvas received enchantments: bind it to the other stack
		if (itemLock.isEmpty() && !targetEnchantments.isEmpty()) {
			bindTo(canvasStack, targetStack);
		}
		SpectrumEnchantmentHelper.setStoredEnchantments(targetEnchantments, canvasStack);
		EnchantmentHelper.setEnchantments(canvasEnchantments, targetStack);
		
		if (drop && receiver != null) {
			if(receiver instanceof Player player) {
				player.getInventory().placeItemBackInInventory(canvasStack);
			} else {
				receiver.spawnAtLocation(canvasStack);
			}
		}
		
		return true;
	}
	
	private void playExchangeSound(Entity entity) {
		entity.playSound(SoundEvents.GRINDSTONE_USE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
		Optional<Item> boundItem = getItemBoundTo(stack);
		if (boundItem.isPresent()) {
			tooltip.add(Component.translatable("item.spectrum.enchantment_canvas.tooltip.bound_to").append(boundItem.get().getDescription()));
		} else {
			tooltip.add(Component.translatable("item.spectrum.enchantment_canvas.tooltip.not_bound"));
			tooltip.add(Component.translatable("item.spectrum.enchantment_canvas.tooltip.not_bound2"));
		}
		ItemStack.appendEnchantmentNames(tooltip, EnchantedBookItem.getEnchantments(stack));
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return !EnchantedBookItem.getEnchantments(stack).isEmpty();
	}
	
	private static void bindTo(ItemStack enchantmentExchangerStack, ItemStack targetStack) {
		CompoundTag nbt = enchantmentExchangerStack.getOrCreateTag();
		nbt.putString("BoundItem", BuiltInRegistries.ITEM.getKey(targetStack.getItem()).toString());
		enchantmentExchangerStack.setTag(nbt);
	}
	
	private static Optional<Item> getItemBoundTo(ItemStack enchantmentExchangerStack) {
		CompoundTag nbt = enchantmentExchangerStack.getTag();
		if (nbt == null || !nbt.contains("BoundItem", Tag.TAG_STRING)) {
			return Optional.empty();
		}
		String targetItemString = nbt.getString("BoundItem");
		return Optional.of(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(targetItemString)));
	}
	
}
