package de.dafuqs.spectrum.mixin.accessors;

import com.google.common.collect.BiMap;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntitySubPredicate.Types.class)
public interface TypeSpecificPredicateDeserializerMixin {
	
	@Mutable
	@Accessor("TYPES")
	static void setTypes(BiMap<String, EntitySubPredicate.Type> types) {
		throw new AssertionError();
	}
	
}
