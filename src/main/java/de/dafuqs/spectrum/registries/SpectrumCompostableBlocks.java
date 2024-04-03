package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.blocks.conditional.colored_tree.ColoredLeavesBlock;
import de.dafuqs.spectrum.blocks.conditional.colored_tree.ColoredSaplingBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

public class SpectrumCompostableBlocks {
	
	private static final float LOW = 0.3F;
	private static final float MEDIUM = 0.5F;
	private static final float HIGH = 0.65F;
	private static final float HIGHER = 0.85F;
	private static final float ALWAYS = 1.0F;
	
	public static void register() {
		add(SpectrumItems.VEGETAL, ALWAYS);
		add(SpectrumItems.BONE_ASH, ALWAYS);
		
		add(SpectrumItems.ALOE_LEAF, LOW);
		add(SpectrumItems.SAWBLADE_HOLLY_BERRY, HIGH);
		
		add(SpectrumBlocks.CLOVER, MEDIUM);
		add(SpectrumBlocks.FOUR_LEAF_CLOVER, MEDIUM);
		add(SpectrumBlocks.BRISTLE_SPROUTS, MEDIUM);
		add(SpectrumBlocks.SNAPPING_IVY, HIGHER);
		
		add(SpectrumItems.HIBERNATING_JADE_VINE_BULB, HIGH);
		add(SpectrumItems.GERMINATED_JADE_VINE_BULB, HIGH);
		add(SpectrumItems.JADE_VINE_PETALS, HIGH);
		add(SpectrumItems.NEPHRITE_BLOSSOM_BULB, HIGH);
		add(SpectrumItems.JADEITE_LOTUS_BULB, HIGH);
		add(SpectrumItems.JADEITE_PETALS, HIGH);
		
		add(SpectrumBlocks.NEPHRITE_BLOSSOM_LEAVES, LOW);
		add(SpectrumBlocks.JADEITE_LOTUS_FLOWER, HIGHER);
		add(SpectrumBlocks.JADE_VINE_PETAL_BLOCK, HIGH);
		add(SpectrumBlocks.JADE_VINE_PETAL_CARPET, HIGH);
		
		add(SpectrumBlocks.SMALL_RED_DRAGONJAG, LOW);
		add(SpectrumBlocks.SMALL_YELLOW_DRAGONJAG, LOW);
		add(SpectrumBlocks.SMALL_PINK_DRAGONJAG, LOW);
		add(SpectrumBlocks.SMALL_PURPLE_DRAGONJAG, LOW);
		add(SpectrumBlocks.SMALL_BLACK_DRAGONJAG, LOW);
		
		add(SpectrumBlocks.SLATE_NOXSHROOM, HIGH);
		add(SpectrumBlocks.EBONY_NOXSHROOM, HIGH);
		add(SpectrumBlocks.IVORY_NOXSHROOM, HIGH);
		add(SpectrumBlocks.CHESTNUT_NOXSHROOM, HIGH);
		
		add(SpectrumBlocks.SLATE_NOXCAP_BLOCK, HIGHER);
		add(SpectrumBlocks.SLATE_NOXCAP_GILLS, HIGHER);
		add(SpectrumBlocks.EBONY_NOXCAP_BLOCK, HIGHER);
		add(SpectrumBlocks.EBONY_NOXCAP_GILLS, HIGHER);
		add(SpectrumBlocks.IVORY_NOXCAP_BLOCK, HIGHER);
		add(SpectrumBlocks.IVORY_NOXCAP_GILLS, HIGHER);
		add(SpectrumBlocks.CHESTNUT_NOXCAP_BLOCK, HIGHER);
		add(SpectrumBlocks.CHESTNUT_NOXCAP_GILLS, HIGHER);
		
		for (DyeColor dyeColor : DyeColor.values()) {
			add(ColoredSaplingBlock.byColor(dyeColor), LOW);
			add(ColoredLeavesBlock.byColor(dyeColor), LOW);
		}
	}
	
	private static void add(ItemLike item, float chance) {
		ComposterBlock.COMPOSTABLES.put(item.asItem(), chance);
	}
	
}
