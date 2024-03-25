package de.dafuqs.spectrum.entity.type_specific_predicates;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.entity.SpectrumTypeSpecificPredicates;
import de.dafuqs.spectrum.entity.entity.EggLayingWoolyPigEntity;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class EggLayingWoolyPigPredicate implements EntitySubPredicate {
	
	private static final String COLOR_KEY = "color";
	
	private final DyeColor color;
	
	private EggLayingWoolyPigPredicate(DyeColor color) {
		this.color = color;
	}
	
	public static EggLayingWoolyPigPredicate of(DyeColor color) {
		return new EggLayingWoolyPigPredicate(color);
	}
	
	public static EggLayingWoolyPigPredicate fromJson(JsonObject json) {
		return new EggLayingWoolyPigPredicate(DyeColor.valueOf(json.get(COLOR_KEY).getAsString().toUpperCase(Locale.ROOT)));
	}
	
	@Override
	public JsonObject serializeCustomData() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(COLOR_KEY, new JsonPrimitive(this.color.toString().toLowerCase(Locale.ROOT)));
		return jsonObject;
	}
	
	@Override
	public Type type() {
		return SpectrumTypeSpecificPredicates.EGG_LAYING_WOOLY_PIG;
	}
	
	@Override
	public boolean matches(Entity entity, ServerLevel world, @Nullable Vec3 pos) {
		if (!(entity instanceof EggLayingWoolyPigEntity eggLayingWoolyPigEntity)) {
			return false;
		} else {
			return this.color == eggLayingWoolyPigEntity.getColor();
		}
	}
}
