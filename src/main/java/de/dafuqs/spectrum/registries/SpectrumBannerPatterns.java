package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;

public class SpectrumBannerPatterns {
	
	public static Holder<BannerPattern> SPECTRUM_LOGO;
	public static Holder<BannerPattern> AMETHYST_CLUSTER;
	public static Holder<BannerPattern> AMETHYST_SHARD;
	public static Holder<BannerPattern> CRAFTING_TABLET;
	public static Holder<BannerPattern> FOUR_LEAF_CLOVER;
	public static Holder<BannerPattern> INK_FLASK;
	public static Holder<BannerPattern> KNOWLEDGE_GEM;
	public static Holder<BannerPattern> GUIDEBOOK;
	public static Holder<BannerPattern> MULTITOOL;
	public static Holder<BannerPattern> NEOLITH;
	public static Holder<BannerPattern> PALETTE;
	public static Holder<BannerPattern> PIGMENT;
	public static Holder<BannerPattern> RAW_AZURITE;
	public static Holder<BannerPattern> SHIMMER;
	public static Holder<BannerPattern> VEGETAL;
	public static Holder<BannerPattern> BEDROCK_DUST;
	public static Holder<BannerPattern> SHIMMERSTONE;
	public static Holder<BannerPattern> JADE_VINE;
	
	public static final TagKey<BannerPattern> SPECTRUM_LOGO_TAG = of("pattern_item/logo");
	public static final TagKey<BannerPattern> AMETHYST_CLUSTER_TAG = of("pattern_item/amethyst_cluster");
	public static final TagKey<BannerPattern> AMETHYST_SHARD_TAG = of("pattern_item/amethyst_shard");
	
	private static TagKey<BannerPattern> of(String id) {
		return TagKey.create(BuiltInRegistries.BANNER_PATTERN.key(), SpectrumCommon.locate(id));
	}
	
	private static Holder<BannerPattern> registerPattern(String id, String shortId) {
		BannerPattern pattern = Registry.register(BuiltInRegistries.BANNER_PATTERN, SpectrumCommon.locate(id), new BannerPattern(SpectrumCommon.MOD_ID + "_" + shortId));
		return BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow(BuiltInRegistries.BANNER_PATTERN.getResourceKey(pattern).get());
	}
	
	public static void register() {
		SPECTRUM_LOGO = registerPattern("logo", "l");
		AMETHYST_CLUSTER = registerPattern("amethyst_cluster", "acl");
		AMETHYST_SHARD = registerPattern("amethyst_shard", "as");
		CRAFTING_TABLET = registerPattern("crafting_tablet", "ct");
		FOUR_LEAF_CLOVER = registerPattern("four_leaf_clover", "flc");
		INK_FLASK = registerPattern("ink_flask", "if");
		KNOWLEDGE_GEM = registerPattern("knowledge_gem", "kg");
		GUIDEBOOK = registerPattern("guidebook", "gui");
		MULTITOOL = registerPattern("multitool", "mul");
		NEOLITH = registerPattern("neolith", "neo");
		PALETTE = registerPattern("palette", "pql");
		PIGMENT = registerPattern("pigment", "pg");
		RAW_AZURITE = registerPattern("raw_azurite", "raz");
		SHIMMER = registerPattern("shimmer", "sh");
		VEGETAL = registerPattern("vegetal", "ve");
		BEDROCK_DUST = registerPattern("bedrock_dust", "bd");
		SHIMMERSTONE = registerPattern("shimmerstone", "sp");
		JADE_VINE = registerPattern("jade_vine", "jv");
	}
	
}
