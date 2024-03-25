package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class JeopardantKillCriterion extends SimpleCriterionTrigger<JeopardantKillCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("jeopardant_kill");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	protected Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate playerPredicate, DeserializationContext predicateDeserializer) {
		return new JeopardantKillCriterion.Conditions(ID, playerPredicate,
				EntityPredicate.fromJson(jsonObject, "killed_entity", predicateDeserializer),
				DamageSourcePredicate.fromJson(jsonObject.get("damage_source")),
				MinMaxBounds.Ints.fromJson(jsonObject.get("health"))
		);
	}
	
	public void trigger(ServerPlayer player, Entity entity, DamageSource killingBlow) {
		LootContext lootContext = EntityPredicate.createContext(player, entity);
		this.trigger(player, (conditions) -> conditions.test(player, lootContext, killingBlow));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ContextAwarePredicate entity;
		private final DamageSourcePredicate killingBlow;
		private final MinMaxBounds.Ints health;
		
		public Conditions(ResourceLocation id, ContextAwarePredicate player, ContextAwarePredicate entity, DamageSourcePredicate killingBlow, MinMaxBounds.Ints health) {
			super(id, player);
			this.entity = entity;
			this.killingBlow = killingBlow;
			this.health = health;
		}
		
		public boolean test(ServerPlayer player, LootContext killedEntityContext, DamageSource killingBlow) {
			return this.killingBlow.matches(player, killingBlow)
					&& this.entity.matches(killedEntityContext)
					&& this.health.matches(Math.round(player.getHealth()));
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("entity", this.entity.toJson(predicateSerializer));
			jsonObject.add("killing_blow", this.killingBlow.serializeToJson());
			jsonObject.add("health", this.health.serializeToJson());
			return jsonObject;
		}
	}
	
}
