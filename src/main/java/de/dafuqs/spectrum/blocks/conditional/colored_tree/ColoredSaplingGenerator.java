package de.dafuqs.spectrum.blocks.conditional.colored_tree;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class ColoredSaplingGenerator extends AbstractTreeGrower {
	
	private final ResourceKey<ConfiguredFeature<?, ?>> treeFeature;
	
	public ColoredSaplingGenerator(DyeColor dyeColor) {
		this.treeFeature = ResourceKey.create(Registries.CONFIGURED_FEATURE, SpectrumCommon.locate("colored_trees/" + dyeColor.toString()));
	}
	
	@Nullable
	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean bees) {
		return treeFeature;
	}
	
}
