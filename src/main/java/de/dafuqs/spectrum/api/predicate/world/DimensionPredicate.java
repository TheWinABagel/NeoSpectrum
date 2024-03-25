package de.dafuqs.spectrum.api.predicate.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class DimensionPredicate implements WorldConditionPredicate {
	public static final DimensionPredicate ANY = new DimensionPredicate(null);
	
	public final List<ResourceKey<Level>> dimensionKeys;
	
	public DimensionPredicate(List<ResourceKey<Level>> dimensionKeys) {
		this.dimensionKeys = dimensionKeys;
	}
	
	public static DimensionPredicate fromJson(JsonObject json) {
		if (json == null || json.isJsonNull()) return ANY;
		List<ResourceKey<Level>> dimensionKeys = new ArrayList<>();
		for (JsonElement element : json.get("worlds").getAsJsonArray()) {
			dimensionKeys.add(ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(element.getAsString())));
		}
		return new DimensionPredicate(dimensionKeys);
	}
	
	@Override
	public boolean test(ServerLevel world, BlockPos pos) {
		if (this == ANY) return true;
		return this.dimensionKeys.contains(world.dimension());
	}
	
}