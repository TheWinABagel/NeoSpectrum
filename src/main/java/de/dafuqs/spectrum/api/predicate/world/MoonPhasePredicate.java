package de.dafuqs.spectrum.api.predicate.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class MoonPhasePredicate implements WorldConditionPredicate {
	public static final MoonPhasePredicate ANY = new MoonPhasePredicate(null);
	
	public final Integer moonPhase;
	
	public MoonPhasePredicate(Integer moonPhase) {
		this.moonPhase = moonPhase;
	}
	
	public static MoonPhasePredicate fromJson(JsonObject json) {
		if (json == null || json.isJsonNull()) return ANY;
		JsonElement jsonElement = json.get("moon_phase");
		String s = jsonElement.getAsString();
		if ("full_moon".equals(s)) {
			return new MoonPhasePredicate(0);
		} else if ("new_moon".equals(s)) {
			return new MoonPhasePredicate(4);
		} else {
			return new MoonPhasePredicate(jsonElement.getAsInt());
		}
		
	}
	
	@Override
	public boolean test(ServerLevel world, BlockPos pos) {
		if (this == ANY) return true;
		return this.moonPhase == world.getMoonPhase();
	}
	
}