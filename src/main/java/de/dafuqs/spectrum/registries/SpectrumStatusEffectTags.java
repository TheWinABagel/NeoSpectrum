package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public class SpectrumStatusEffectTags {
	
	public static TagKey<MobEffect> UNCURABLE;
	public static TagKey<MobEffect> NO_DURATION_EXTENSION;
	
	public static void register() {
		UNCURABLE = of("uncurable");
		NO_DURATION_EXTENSION = of("no_duration_extension");
	}
	
	private static TagKey<MobEffect> of(String id) {
		return TagKey.create(Registries.MOB_EFFECT, SpectrumCommon.locate(id));
	}

	public static boolean isIn(TagKey<MobEffect> tag, MobEffect effect) {
		return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect).is(tag);
	}
	
	public static boolean isUncurable(MobEffect statusEffect) {
		return isIn(SpectrumStatusEffectTags.UNCURABLE, statusEffect);
	}
	
}
