package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class SpectrumEntityTypeTags {
	
	public static final TagKey<EntityType<?>> POKING_DAMAGE_IMMUNE = getReference("poking_damage_immune");
	public static final TagKey<EntityType<?>> PRIMORDIAL_FIRE_IMMUNE = getReference("primordial_fire_immune");
	public static final TagKey<EntityType<?>> CONSTRUCTS = getReference("constructs");
	
	public static final TagKey<EntityType<?>> STAFF_OF_REMEMBRANCE_BLACKLISTED = getReference("staff_of_remembrance_blacklisted");
	public static final TagKey<EntityType<?>> SPAWNER_MANIPULATION_BLACKLISTED = getReference("spawner_manipulation_blacklisted");
	
	
	private static TagKey<EntityType<?>> getReference(String id) {
		return TagKey.create(Registries.ENTITY_TYPE, SpectrumCommon.locate(id));
	}
	
}
