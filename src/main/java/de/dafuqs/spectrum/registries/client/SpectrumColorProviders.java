package de.dafuqs.spectrum.registries.client;

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
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class SpectrumColorProviders { //todoforge check if block tint and item tints are working properly

    public static ToggleableBlockColorProvider coloredLeavesBlockColorProvider;
    public static ToggleableItemColorProvider coloredLeavesItemColorProvider;

    public static ToggleableBlockColorProvider amaranthBushelBlockColorProvider;
    public static ToggleableItemColorProvider amaranthBushelItemColorProvider;
    public static ToggleableBlockColorProvider amaranthCropBlockColorProvider;
    public static ToggleableItemColorProvider amaranthCropItemColorProvider;


    //had to split up as it is two separate events
    @SubscribeEvent
    public static void itemColors(RegisterColorHandlersEvent.Item e) {
        // Biome Colors for colored leaves items and blocks
        // They don't use it, but their decoy oak leaves do
        registerColoredLeavesItems(e);

        // Same for Amaranth
        registerAmaranthItems(e);

        registerPotionFillables(e, SpectrumItems.LESSER_POTION_PENDANT, SpectrumItems.GREATER_POTION_PENDANT, SpectrumItems.MALACHITE_GLASS_AMPOULE);
        registerPickyPotionFillables(e, SpectrumItems.NIGHTFALLS_BLADE);
        registerSingleInkStorages(e, SpectrumItems.INK_FLASK);
        registerBrewColors(SpectrumItems.INFUSED_BEVERAGE, e);
        registerMemoryItems(e, SpectrumBlocks.MEMORY);
    }

    @SubscribeEvent
    public static void blockColors(RegisterColorHandlersEvent.Block e) {
        // Biome Colors for colored leaves items and blocks
        // They don't use it, but their decoy oak leaves do
        registerColoredLeavesBlocks(e);

        // Same for Amaranth
        registerAmaranthBlocks(e);

        registerClovers(e, SpectrumBlocks.CLOVER, SpectrumBlocks.FOUR_LEAF_CLOVER);
        registerMemoryBlocks(e, SpectrumBlocks.MEMORY);
    }

    private static void registerColoredLeavesBlocks(RegisterColorHandlersEvent.Block e) {

        for (DyeColor dyeColor : DyeColor.values()) {
            Block block = ColoredLeavesBlock.byColor(dyeColor);
            //copied from vanilla oak leaves
            BlockColor leavesBlockColorProvider = (pState, pLevel, pPos, pTintIndex) -> {
                return pLevel != null && pPos != null ? BiomeColors.getAverageFoliageColor(pLevel, pPos) : FoliageColor.getDefaultColor();
            };
            coloredLeavesBlockColorProvider = new ToggleableBlockColorProvider(leavesBlockColorProvider);
            e.register(coloredLeavesBlockColorProvider, block);
        }
    }

    private static void registerColoredLeavesItems(RegisterColorHandlersEvent.Item e) {
        // Biome Colors for colored leaves items and blocks
        // They don't use it, but their decoy oak leaves do
        for (DyeColor dyeColor : DyeColor.values()) {
            Block block = ColoredLeavesBlock.byColor(dyeColor);
            //copied from vanilla oak leaves
            ItemColor leavesBlockColorProvider = (pStack, pTintIndex) -> {
                BlockState blockstate = ((BlockItem) pStack.getItem()).getBlock().defaultBlockState();
                return e.getBlockColors().getColor(blockstate, null, null, pTintIndex);
            };
            coloredLeavesItemColorProvider = new ToggleableItemColorProvider(leavesBlockColorProvider);
            e.register(coloredLeavesItemColorProvider, block);
        }
    }

    private static void registerAmaranthBlocks(RegisterColorHandlersEvent.Block e) {
        //copied from vanilla fern blocks
        BlockColor fernBlockColorProvider = (pState, pLevel, pPos, pTintIndex) -> {
            return pLevel != null && pPos != null ? BiomeColors.getAverageGrassColor(pLevel, pPos) : GrassColor.getDefaultColor();
        };
        amaranthBushelBlockColorProvider = new ToggleableBlockColorProvider(fernBlockColorProvider);
        e.register(amaranthBushelBlockColorProvider, SpectrumBlocks.AMARANTH_BUSHEL);
        e.register(amaranthBushelBlockColorProvider, SpectrumBlocks.POTTED_AMARANTH_BUSHEL);

        //copied from vanilla tall fern blocks
        BlockColor largeFernBlockColorProvider = (pState, pLevel, pPos, pTintIndex) -> {
            return pLevel != null && pPos != null ? BiomeColors.getAverageGrassColor(pLevel, pState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pPos.below() : pPos) : GrassColor.getDefaultColor();
        };
        amaranthCropBlockColorProvider = new ToggleableBlockColorProvider(largeFernBlockColorProvider);
        e.register(amaranthCropBlockColorProvider, SpectrumBlocks.AMARANTH);
    }

    private static void registerAmaranthItems(RegisterColorHandlersEvent.Item e) {
        ItemColor fernItemColorProvider = (pStack, pTintIndex) -> {
            BlockState blockstate = ((BlockItem) pStack.getItem()).getBlock().defaultBlockState();
            return e.getBlockColors().getColor(blockstate, null, null, pTintIndex);
        };
        amaranthBushelItemColorProvider = new ToggleableItemColorProvider(fernItemColorProvider);
        e.register(amaranthBushelItemColorProvider, SpectrumBlocks.AMARANTH_BUSHEL);

        ItemColor largeFernItemColorProvider = (pStack, pTintIndex) -> {
            return GrassColor.get(0.5D, 1.0D);
        };
        amaranthCropItemColorProvider = new ToggleableItemColorProvider(largeFernItemColorProvider);
        e.register(amaranthCropItemColorProvider, SpectrumBlocks.AMARANTH);
    }

    private static void registerClovers(RegisterColorHandlersEvent.Block e, Block... clovers) {
        BlockColor grassBlockColorProvider = (p_276237_, p_276238_, p_276239_, p_276240_) -> {
            return p_276238_ != null && p_276239_ != null ? BiomeColors.getAverageGrassColor(p_276238_, p_276239_) : GrassColor.getDefaultColor();
        };
        e.register(grassBlockColorProvider, clovers);
    }

    private static void registerSingleInkStorages(RegisterColorHandlersEvent.Item e, Item... items) {
        e.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                InkFlaskItem i = (InkFlaskItem) stack.getItem();
                SingleInkStorage storage = i.getEnergyStorage(stack);
                return ColorHelper.getInt(storage.getStoredColor().getDyeColor());
            }
            return -1;
        }, items);
    }

    private static void registerPickyPotionFillables(RegisterColorHandlersEvent.Item e, Item... items) {
        e.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                List<InkPoweredStatusEffectInstance> effects = InkPoweredStatusEffectInstance.getEffects(stack);
                if (effects.size() > 0) {
                    return effects.get(0).getColor();
                }
            }
            return -1;
        }, items);
    }

    private static void registerPotionFillables(RegisterColorHandlersEvent.Item e, Item... items) {
        e.register((stack, tintIndex) -> {
            if (tintIndex > 0) {
                List<InkPoweredStatusEffectInstance> effects = InkPoweredStatusEffectInstance.getEffects(stack);
                if (effects.size() > tintIndex - 1) {
                    return effects.get(tintIndex - 1).getColor();
                }
            }
            return -1;
        }, items);
    }

    private static void registerMemoryBlocks(RegisterColorHandlersEvent.Block e, Block memory) {
        e.register((state, world, pos, tintIndex) -> {
            if (world == null) {
                return 0x0;
            }
            if (world.getBlockEntity(pos) instanceof MemoryBlockEntity memoryBlockEntity) {
                return memoryBlockEntity.getEggColor(tintIndex);
            }
            return 0x0;
        }, memory);
    }

    private static void registerMemoryItems(RegisterColorHandlersEvent.Item e, Block memory) {
        e.register((stack, tintIndex) -> {
            if (tintIndex == 2)
                return 0xFFFFFF;

            return MemoryItem.getEggColor(stack.getTag(), tintIndex);
        }, memory.asItem());
    }

    public static void registerBrewColors(Item brew, RegisterColorHandlersEvent.Item e) {
        e.register((stack, tintIndex) -> {
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