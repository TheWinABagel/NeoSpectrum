package de.dafuqs.spectrum.entity.type_specific_predicates;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.entity.SpectrumTypeSpecificPredicates;
import de.dafuqs.spectrum.entity.entity.KindlingEntity;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class KindlingPredicate implements EntitySubPredicate {
	
	private static final String CLIPPED_KEY = "clipped";
	private static final String ANGRY_KEY = "angry";
	
	private final Optional<Boolean> clipped;
	private final Optional<Boolean> angry;
	
	private KindlingPredicate(Optional<Boolean> clipped, Optional<Boolean> angry) {
		this.clipped = clipped;
		this.angry = angry;
	}
	
	public static KindlingPredicate of(Optional<Boolean> clipped, Optional<Boolean> angry) {
		return new KindlingPredicate(clipped, angry);
	}
	
	public static KindlingPredicate fromJson(JsonObject json) {
		JsonElement clippedElement = json.get(CLIPPED_KEY);
		Optional<Boolean> clippedOptional = clippedElement == null ? Optional.empty() : Optional.of(clippedElement.getAsBoolean());
		JsonElement angryElement = json.get(ANGRY_KEY);
		Optional<Boolean> angryOptional = angryElement == null ? Optional.empty() : Optional.of(angryElement.getAsBoolean());
		return new KindlingPredicate(clippedOptional, angryOptional);
	}
	
	@Override
	public JsonObject serializeCustomData() {
		JsonObject jsonObject = new JsonObject();
		this.clipped.ifPresent(aBoolean -> jsonObject.add(CLIPPED_KEY, new JsonPrimitive(aBoolean)));
		this.angry.ifPresent(aBoolean -> jsonObject.add(ANGRY_KEY, new JsonPrimitive(aBoolean)));
		return jsonObject;
	}
	
	@Override
	public Type type() {
		return SpectrumTypeSpecificPredicates.KINDLING;
	}
	
	@Override
	public boolean matches(Entity entity, ServerLevel world, @Nullable Vec3 pos) {
		if (!(entity instanceof KindlingEntity kindling)) {
			return false;
		} else {
			return (this.clipped.isEmpty() || this.clipped.get() == kindling.isClipped())
					&& (this.angry.isEmpty() || this.angry.get() == (kindling.getRemainingPersistentAngerTime() == 0));
		}
	}
	
}
