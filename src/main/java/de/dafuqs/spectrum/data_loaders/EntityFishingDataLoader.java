package de.dafuqs.spectrum.data_loaders;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.predicate.entity.EntityFishingPredicate;
import de.dafuqs.spectrum.helpers.NbtHelper;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityFishingDataLoader extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
	
	public static final String ID = "entity_fishing";
	public static final EntityFishingDataLoader INSTANCE = new EntityFishingDataLoader();
	
	protected static final List<EntityFishingEntry> ENTITY_FISHING_ENTRIES = new ArrayList<>();
	
	public record EntityFishingEntity(
			EntityType<?> entityType,
			Optional<CompoundTag> nbt
	) {
	}

	public record EntityFishingEntry(
			EntityFishingPredicate predicate,
			float entityChance,
			SimpleWeightedRandomList<EntityFishingEntity> weightedEntities
	) {
	}
	
	private EntityFishingDataLoader() {
		super(new Gson(), ID);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller profiler) {
		ENTITY_FISHING_ENTRIES.clear();
		prepared.forEach((identifier, jsonElement) -> {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			
			EntityFishingPredicate predicate = EntityFishingPredicate.fromJson(jsonObject.get("location").getAsJsonObject());
			float chance = GsonHelper.getAsFloat(jsonObject, "chance");
			JsonArray entityArray = GsonHelper.getAsJsonArray(jsonObject, "entities");
			
			SimpleWeightedRandomList.Builder<EntityFishingEntity> entities = SimpleWeightedRandomList.builder();
			entityArray.forEach(entryElement -> {
				JsonObject entryObject = entryElement.getAsJsonObject();
				
				EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(GsonHelper.getAsString(entryObject, "id")));
				
				Optional<CompoundTag> nbt = NbtHelper.getNbtCompound(entryObject.get("nbt"));

				int weight = 1;
				if (GsonHelper.isNumberValue(entryObject, "weight")) {
					weight = GsonHelper.getAsInt(entryObject, "weight");
				}
				entities.add(new EntityFishingEntity(entityType, nbt), weight);
			});
			
			ENTITY_FISHING_ENTRIES.add(new EntityFishingEntry(
					predicate,
					chance,
					entities.build()
			));
		});
	}
	
	@Override
	public ResourceLocation getFabricId() {
		return SpectrumCommon.locate(ID);
	}
	
	public static Optional<EntityFishingEntity> tryCatchEntity(ServerLevel world, BlockPos pos, int bigCatchLevel) {
		for (EntityFishingEntry entry : ENTITY_FISHING_ENTRIES) {
			if (entry.predicate.test(world, pos)) {
				if (world.random.nextFloat() < entry.entityChance * (1 + bigCatchLevel)) {
					var x = entry.weightedEntities.getRandom(world.random);
					if (x.isPresent()) {
						return Optional.of(x.get().getData());
					}
				}
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
	
}