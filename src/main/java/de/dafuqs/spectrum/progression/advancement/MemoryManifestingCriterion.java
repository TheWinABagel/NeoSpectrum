package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class MemoryManifestingCriterion extends SimpleCriterionTrigger<MemoryManifestingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("memory_manifesting");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public MemoryManifestingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext predicateDeserializer) {
		return new MemoryManifestingCriterion.Conditions(ID, extended, EntityPredicate.fromJson(jsonObject, "manifested_entity", predicateDeserializer));
	}
	
	public void trigger(ServerPlayer player, Entity manifestedEntity) {
		LootContext lootContext = EntityPredicate.createContext(player, manifestedEntity);
		this.trigger(player, (conditions) -> conditions.matches(lootContext));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ContextAwarePredicate manifestedEntity;
		
		public Conditions(ResourceLocation id, ContextAwarePredicate player, ContextAwarePredicate manifestedEntity) {
			super(id, player);
			this.manifestedEntity = manifestedEntity;
		}
		
		public boolean matches(LootContext context) {
			return this.manifestedEntity.matches(context);
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("manifested_entity", this.manifestedEntity.toJson(predicateSerializer));
			return jsonObject;
		}
	}
	
}
