package de.dafuqs.spectrum.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.dafuqs.spectrum.entity.type_specific_predicates.EggLayingWoolyPigPredicate;
import de.dafuqs.spectrum.entity.type_specific_predicates.KindlingPredicate;
import de.dafuqs.spectrum.entity.type_specific_predicates.ShulkerPredicate;
import de.dafuqs.spectrum.mixin.accessors.TypeSpecificPredicateDeserializerMixin;
import net.minecraft.advancements.critereon.EntitySubPredicate;

public class SpectrumTypeSpecificPredicates {
	
	public static final EntitySubPredicate.Type EGG_LAYING_WOOLY_PIG = EggLayingWoolyPigPredicate::fromJson;
	public static final EntitySubPredicate.Type SHULKER = ShulkerPredicate::fromJson;
	public static final EntitySubPredicate.Type KINDLING = KindlingPredicate::fromJson;
	
	public static void register() {
		// creating a new map, in case the previous one was immutable (it usually is)
		BiMap<String, EntitySubPredicate.Type> newMap = HashBiMap.create(EntitySubPredicate.Types.TYPES);
		
		// TypeSpecificPredicates are not handled via identifiers, but we'll add our mod id anyway,
		// in case of collisions with future vanilla updates or other mods
		newMap.put("spectrum:egg_laying_wooly_pig", EGG_LAYING_WOOLY_PIG);
		newMap.put("spectrum:shulker", SHULKER);
		newMap.put("spectrum:kindling", KINDLING);
		
		TypeSpecificPredicateDeserializerMixin.setTypes(newMap);
	}
	
}
