package de.dafuqs.spectrum.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import static de.dafuqs.spectrum.SpectrumCommon.locate;

public class SpectrumDamageTypeTags {
	
	public static final TagKey<DamageType> DROPS_LOOT_LIKE_PLAYERS = TagKey.create(Registries.DAMAGE_TYPE, locate("drops_loot_like_players"));
	public static final TagKey<DamageType> USES_SET_HEALTH = TagKey.create(Registries.DAMAGE_TYPE, locate("uses_set_health"));
	public static final TagKey<DamageType> BYPASSES_DIKE = TagKey.create(Registries.DAMAGE_TYPE, locate("bypasses_dike"));
	public static final TagKey<DamageType> IS_PRIMORDIAL_FIRE = TagKey.create(Registries.DAMAGE_TYPE, locate("is_primordial_fire"));
	
}
