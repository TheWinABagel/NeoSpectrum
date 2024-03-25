package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.entity.variants.LizardFrillVariant;
import de.dafuqs.spectrum.entity.variants.LizardHornVariant;
import de.dafuqs.spectrum.explosion.ExplosionModifier;
import de.dafuqs.spectrum.explosion.ExplosionModifierType;
import de.dafuqs.spectrum.items.tools.GlassArrowVariant;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Optional;

public class SpectrumRegistries {
	
	private static final ResourceLocation INK_COLORS_ID = SpectrumCommon.locate("ink_color");
	public static final ResourceKey<Registry<InkColor>> INK_COLORS_KEY = ResourceKey.createRegistryKey(INK_COLORS_ID);
	public static final Registry<InkColor> INK_COLORS = FabricRegistryBuilder.createSimple(INK_COLORS_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	private static final ResourceLocation LIZARD_FRILL_VARIANT_ID = SpectrumCommon.locate("lizard_frill_variant");
	public static final ResourceKey<Registry<LizardFrillVariant>> LIZARD_FRILL_VARIANT_KEY = ResourceKey.createRegistryKey(LIZARD_FRILL_VARIANT_ID);
	public static final Registry<LizardFrillVariant> LIZARD_FRILL_VARIANT = FabricRegistryBuilder.createSimple(LIZARD_FRILL_VARIANT_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	private static final ResourceLocation LIZARD_HORN_VARIANT_ID = SpectrumCommon.locate("lizard_horn_variant");
	public static final ResourceKey<Registry<LizardHornVariant>> LIZARD_HORN_VARIANT_KEY = ResourceKey.createRegistryKey(LIZARD_HORN_VARIANT_ID);
	public static final Registry<LizardHornVariant> LIZARD_HORN_VARIANT = FabricRegistryBuilder.createSimple(LIZARD_HORN_VARIANT_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	private static final ResourceLocation GLASS_ARROW_VARIANT_ID = SpectrumCommon.locate("glass_arrow_variant");
	public static final ResourceKey<Registry<GlassArrowVariant>> GLASS_ARROW_VARIANT_KEY = ResourceKey.createRegistryKey(GLASS_ARROW_VARIANT_ID);
	public static final Registry<GlassArrowVariant> GLASS_ARROW_VARIANT = FabricRegistryBuilder.createSimple(GLASS_ARROW_VARIANT_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	private static final ResourceLocation EXPLOSION_MODIFIER_TYPES_ID = SpectrumCommon.locate("explosion_effect_family");
	public static final ResourceKey<Registry<ExplosionModifierType>> EXPLOSION_MODIFIER_TYPES_KEY = ResourceKey.createRegistryKey(EXPLOSION_MODIFIER_TYPES_ID);
	public static final Registry<ExplosionModifierType> EXPLOSION_MODIFIER_TYPES = FabricRegistryBuilder.createSimple(EXPLOSION_MODIFIER_TYPES_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	private static final ResourceLocation EXPLOSION_MODIFIERS_ID = SpectrumCommon.locate("explosion_effect_modifier");
	public static final ResourceKey<Registry<ExplosionModifier>> EXPLOSION_MODIFIERS_KEY = ResourceKey.createRegistryKey(EXPLOSION_MODIFIERS_ID);
	public static final Registry<ExplosionModifier> EXPLOSION_MODIFIERS = FabricRegistryBuilder.createSimple(EXPLOSION_MODIFIERS_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	public static <T> T getRandomTagEntry(Registry<T> registry, TagKey<T> tag, RandomSource random, T fallback) {
		Optional<HolderSet.Named<T>> tagEntries = registry.getTag(tag);
		if (tagEntries.isPresent()) {
			return tagEntries.get().get(random.nextInt(tagEntries.get().size())).value();
		} else {
			return fallback;
		}
	}
	
	public static <T> List<Holder<T>> getEntries(Registry<T> registry, TagKey<T> tag) {
		Optional<HolderSet.Named<T>> tagEntries = registry.getTag(tag);
		return tagEntries.map(registryEntries -> registryEntries.stream().toList()).orElseGet(List::of);
	}

	public static void register() {
		LizardFrillVariant.init();
		LizardHornVariant.init();
		GlassArrowVariant.init();
	}
	
}