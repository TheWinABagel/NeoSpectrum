package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;

public class SpectrumBannerPatternTags {
	
	public static TagKey<BannerPattern> SPECTRUM_LOGO_TAG = of("pattern_item/logo");
	public static TagKey<BannerPattern> AMETHYST_CLUSTER_TAG = of("pattern_item/amethyst_cluster");
	public static TagKey<BannerPattern> AMETHYST_SHARD_TAG = of("pattern_item/amethyst_shard");
	
	private static TagKey<BannerPattern> of(String id) {
		return TagKey.create(Registries.BANNER_PATTERN, SpectrumCommon.locate(id));
	}
	
}
