package de.dafuqs.spectrum.entity.type_specific_predicates;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.entity.SpectrumTypeSpecificPredicates;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class ShulkerPredicate implements EntitySubPredicate {
	
	private static final String COLOR_KEY = "color";
	
	private final @Nullable DyeColor color;
	
	private ShulkerPredicate(@Nullable DyeColor color) {
		this.color = color;
	}
	
	public static ShulkerPredicate of(@Nullable DyeColor color) {
		return new ShulkerPredicate(color);
	}
	
	public static ShulkerPredicate fromJson(JsonObject json) {
		String c = json.get(COLOR_KEY).getAsString();
		return new ShulkerPredicate(DyeColor.valueOf(c.isBlank() ? null : c.toUpperCase(Locale.ROOT)));
	}
	
	@Override
	public JsonObject serializeCustomData() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(COLOR_KEY, new JsonPrimitive(this.color == null ? "" : this.color.toString().toLowerCase(Locale.ROOT)));
		return jsonObject;
	}
	
	@Override
	public Type type() {
		return SpectrumTypeSpecificPredicates.SHULKER;
	}
	
	@Override
	public boolean matches(Entity entity, ServerLevel world, @Nullable Vec3 pos) {
		if (!(entity instanceof Shulker shulkerEntity)) {
			return false;
		} else if (shulkerEntity.getColor() == null) {
			return this.color == null;
		} else {
			return shulkerEntity.getColor().equals(this.color);
		}
	}
}
