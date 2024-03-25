package de.dafuqs.spectrum.progression.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class InkProjectileKillingCriterion extends SimpleCriterionTrigger<InkProjectileKillingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("ink_projectile_killing");
	
	public InkProjectileKillingCriterion() {
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public InkProjectileKillingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ContextAwarePredicate[] victims = EntityPredicate.fromJsonArray(jsonObject, "victims", advancementEntityPredicateDeserializer);
		Ints intRange = Ints.fromJson(jsonObject.get("unique_entity_types"));
		return new InkProjectileKillingCriterion.Conditions(extended, victims, intRange);
	}
	
	public void trigger(ServerPlayer player, Collection<Entity> piercingKilledEntities) {
		List<LootContext> list = Lists.newArrayList();
		Set<EntityType<?>> set = Sets.newHashSet();
		
		for (Entity entity : piercingKilledEntities) {
			set.add(entity.getType());
			list.add(EntityPredicate.createContext(player, entity));
		}
		
		this.trigger(player, (conditions) -> conditions.matches(list, set.size()));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ContextAwarePredicate[] victims;
		private final Ints uniqueEntityTypes;
		
		public Conditions(ContextAwarePredicate player, ContextAwarePredicate[] victims, Ints uniqueEntityTypes) {
			super(InkProjectileKillingCriterion.ID, player);
			this.victims = victims;
			this.uniqueEntityTypes = uniqueEntityTypes;
		}
		
		public static InkProjectileKillingCriterion.Conditions create(EntityPredicate.Builder... victimPredicates) {

			ContextAwarePredicate[] extendeds = new ContextAwarePredicate[victimPredicates.length];
			for (int i = 0; i < victimPredicates.length; i++) {
				var predicate = EntityPredicate.wrap(victimPredicates[i].build());
				extendeds[i] = predicate;
			}

			return new InkProjectileKillingCriterion.Conditions(ContextAwarePredicate.ANY, extendeds, Ints.ANY);
		}
		
		public static InkProjectileKillingCriterion.Conditions create(Ints uniqueEntityTypes) {
			ContextAwarePredicate[] extendeds = new ContextAwarePredicate[0];
			return new InkProjectileKillingCriterion.Conditions(ContextAwarePredicate.ANY, extendeds, uniqueEntityTypes);
		}
		
		public boolean matches(Collection<LootContext> victimContexts, int uniqueEntityTypeCount) {
			if (this.victims.length > 0) {
				List<LootContext> list = Lists.newArrayList(victimContexts);
				
				for (ContextAwarePredicate extended : this.victims) {
					boolean bl = false;
					
					Iterator<LootContext> iterator = list.iterator();
					while (iterator.hasNext()) {
						LootContext lootContext = iterator.next();
						if (extended.matches(lootContext)) {
							iterator.remove();
							bl = true;
							break;
						}
					}
					
					if (!bl) {
						return false;
					}
				}
			}
			
			return this.uniqueEntityTypes.matches(uniqueEntityTypeCount);
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("victims", ContextAwarePredicate.toJson(this.victims, predicateSerializer));
			jsonObject.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
			return jsonObject;
		}
	}
}
