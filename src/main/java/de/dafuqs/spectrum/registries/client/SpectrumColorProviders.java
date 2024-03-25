package de.dafuqs.spectrum.registries.client;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.energy.InkPoweredStatusEffectInstance;
import de.dafuqs.spectrum.api.energy.storage.SingleInkStorage;
import de.dafuqs.spectrum.blocks.conditional.colored_tree.ColoredLeavesBlock;
import de.dafuqs.spectrum.blocks.memory.MemoryBlockEntity;
import de.dafuqs.spectrum.blocks.memory.MemoryItem;
import de.dafuqs.spectrum.helpers.ColorHelper;
import de.dafuqs.spectrum.items.energy.InkFlaskItem;
import de.dafuqs.spectrum.progression.ToggleableBlockColorProvider;
import de.dafuqs.spectrum.progression.ToggleableItemColorProvider;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class SpectrumColorProviders {
	
	public static ToggleableBlockColorProvider coloredLeavesBlockColorProvider;
	public static ToggleableItemColorProvider coloredLeavesItemColorProvider;
	
	public static ToggleableBlockColorProvider amaranthBushelBlockColorProvider;
	public static ToggleableItemColorProvider amaranthBushelItemColorProvider;
	public static ToggleableBlockColorProvider amaranthCropBlockColorProvider;
	public static ToggleableItemColorProvider amaranthCropItemColorProvider;
	
	public static void registerClient() {
		SpectrumCommon.logInfo("Registering Block and Item Color Providers...");
		
		// Biome Colors for colored leaves items and blocks
		// They don't use it, but their decoy oak leaves do
		registerColoredLeaves();
		
		// Same for Amaranth
		registerAmaranth();
		
		registerClovers(SpectrumBlocks.CLOVER, SpectrumBlocks.FOUR_LEAF_CLOVER);
		registerMemory(SpectrumBlocks.MEMORY);
		registerPotionFillables(SpectrumItems.LESSER_POTION_PENDANT, SpectrumItems.GREATER_POTION_PENDANT, SpectrumItems.MALACHITE_GLASS_AMPOULE);
		registerPickyPotionFillables(SpectrumItems.NIGHTFALLS_BLADE);
		registerSingleInkStorages(SpectrumItems.INK_FLASK);
		registerBrewColors(SpectrumItems.INFUSED_BEVERAGE);
	}
	
	private static void registerColoredLeaves() {
		BlockColor leavesBlockColorProvider = ColorProviderRegistry.BLOCK.get(Blocks.OAK_LEAVES);
		ItemColor leavesItemColorProvider = ColorProviderRegistry.ITEM.get(Blocks.OAK_LEAVES);
		
		if (leavesBlockColorProvider != null && leavesItemColorProvider != null) {
			coloredLeavesBlockColorProvider = new ToggleableBlockColorProvider(leavesBlockColorProvider);
			coloredLeavesItemColorProvider = new ToggleableItemColorProvider(leavesItemColorProvider);
			
			for (DyeColor dyeColor : DyeColor.values()) {
				Block block = ColoredLeavesBlock.byColor(dyeColor);
				ColorProviderRegistry.BLOCK.register(coloredLeavesBlockColorProvider, block);
				ColorProviderRegistry.ITEM.register(coloredLeavesItemColorProvider, block);
			}
		}
	}
	
	private static void registerAmaranth() {
		BlockColor fernBlockColorProvider = ColorProviderRegistry.BLOCK.get(Blocks.FERN);
		ItemColor fernItemColorProvider = ColorProviderRegistry.ITEM.get(Blocks.FERN);
		if (fernBlockColorProvider != null && fernItemColorProvider != null) {
			amaranthBushelBlockColorProvider = new ToggleableBlockColorProvider(fernBlockColorProvider);
			amaranthBushelItemColorProvider = new ToggleableItemColorProvider(fernItemColorProvider);
			ColorProviderRegistry.BLOCK.register(amaranthBushelBlockColorProvider, SpectrumBlocks.AMARANTH_BUSHEL);
			ColorProviderRegistry.ITEM.register(amaranthBushelItemColorProvider, SpectrumBlocks.AMARANTH_BUSHEL);
			ColorProviderRegistry.BLOCK.register(amaranthBushelBlockColorProvider, SpectrumBlocks.POTTED_AMARANTH_BUSHEL);
		}
		
		BlockColor largeFernBlockColorProvider = ColorProviderRegistry.BLOCK.get(Blocks.LARGE_FERN);
		ItemColor largeFernItemColorProvider = ColorProviderRegistry.ITEM.get(Blocks.LARGE_FERN);
		if (largeFernBlockColorProvider != null && largeFernItemColorProvider != null) {
			amaranthCropBlockColorProvider = new ToggleableBlockColorProvider(largeFernBlockColorProvider);
			amaranthCropItemColorProvider = new ToggleableItemColorProvider(largeFernItemColorProvider);
			ColorProviderRegistry.BLOCK.register(amaranthCropBlockColorProvider, SpectrumBlocks.AMARANTH);
			ColorProviderRegistry.ITEM.register(amaranthCropItemColorProvider, SpectrumBlocks.AMARANTH);
		}
	}
	
	private static void registerClovers(Block... clovers) {
		BlockColor grassBlockColorProvider = ColorProviderRegistry.BLOCK.get(Blocks.GRASS);
		ItemColor grassItemColorProvider = ColorProviderRegistry.ITEM.get(Blocks.GRASS.asItem());
		
		if (grassBlockColorProvider != null && grassItemColorProvider != null) {
			ColorProviderRegistry.BLOCK.register(grassBlockColorProvider, clovers);
		}
	}
	
	private static void registerSingleInkStorages(Item... items) {
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if (tintIndex == 1) {
				InkFlaskItem i = (InkFlaskItem) stack.getItem();
				SingleInkStorage storage = i.getEnergyStorage(stack);
				return ColorHelper.getInt(storage.getStoredColor().getDyeColor());
			}
			return -1;
		}, items);
	}

	private static void registerPickyPotionFillables(Item... items) {
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if (tintIndex == 1) {
				List<InkPoweredStatusEffectInstance> effects = InkPoweredStatusEffectInstance.getEffects(stack);
				if (effects.size() > 0) {
					return effects.get(0).getColor();
				}
			}
			return -1;
		}, items);
	}
	
	private static void registerPotionFillables(Item... items) {
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if (tintIndex > 0) {
				List<InkPoweredStatusEffectInstance> effects = InkPoweredStatusEffectInstance.getEffects(stack);
				if (effects.size() > tintIndex - 1) {
					return effects.get(tintIndex - 1).getColor();
				}
			}
			return -1;
		}, items);
	}
	
	private static void registerMemory(Block memory) {
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
			if (world == null) {
				return 0x0;
			}
			if (world.getBlockEntity(pos) instanceof MemoryBlockEntity memoryBlockEntity) {
				return memoryBlockEntity.getEggColor(tintIndex);
			}
			return 0x0;
		}, memory);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if (tintIndex == 2)
				return 0xFFFFFF;

			return MemoryItem.getEggColor(stack.getTag(), tintIndex);
		}, memory.asItem());
	}
	
	public static void registerBrewColors(Item brew) {
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if (tintIndex == 0) {
				CompoundTag nbt = stack.getTag();
				return (nbt != null && nbt.contains("Color")) ? nbt.getInt("Color") : 0xf4c6cb;
			}
			return -1;
			
		}, brew);
	}
	
	public static void resetToggleableProviders() {
		coloredLeavesBlockColorProvider.setShouldApply(true);
		coloredLeavesItemColorProvider.setShouldApply(true);
		
		amaranthBushelBlockColorProvider.setShouldApply(true);
		amaranthBushelItemColorProvider.setShouldApply(true);
		amaranthCropBlockColorProvider.setShouldApply(true);
		amaranthCropItemColorProvider.setShouldApply(true);
	}
	
}