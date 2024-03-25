package de.dafuqs.spectrum.entity.variants;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.registries.SpectrumRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public record LizardHornVariant(ResourceLocation texture) {
	
	public static final LizardHornVariant HORNY = register("horny", "textures/entity/lizard/horns_horny.png");
	public static final LizardHornVariant STRAIGHT = register("straight", "textures/entity/lizard/horns_straight.png");
	public static final LizardHornVariant FLEXIBLE = register("flexible", "textures/entity/lizard/horns_flexible.png");
	public static final LizardHornVariant QUEER = register("queer", "textures/entity/lizard/horns_queer.png");
	public static final LizardHornVariant POLY = register("poly", "textures/entity/lizard/horns_poly.png");
	public static final LizardHornVariant ONLY_LIKES_YOU_AS_A_FRIEND = register("friendzoned", "textures/entity/lizard/horns_friendzoned.png");
	
	private static LizardHornVariant register(String name, String textureId) {
		return Registry.register(SpectrumRegistries.LIZARD_HORN_VARIANT, SpectrumCommon.locate(name), new LizardHornVariant(SpectrumCommon.locate(textureId)));
	}

	public static final TagKey<LizardHornVariant> NATURAL_VARIANT = getReference("natural");

	private static TagKey<LizardHornVariant> getReference(String name) {
		return TagKey.create(SpectrumRegistries.LIZARD_HORN_VARIANT_KEY, SpectrumCommon.locate(name));
	}
	
	public static void init() {
	}
}