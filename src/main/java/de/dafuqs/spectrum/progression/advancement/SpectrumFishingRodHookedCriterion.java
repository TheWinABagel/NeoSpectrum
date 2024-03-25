package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.entity.SpectrumFishingBobberEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Collection;

/**
 * Advanced fishing criterion that can also:
 * - match the fluid that was fished in
 * - fished entities
 */
public class SpectrumFishingRodHookedCriterion extends SimpleCriterionTrigger<SpectrumFishingRodHookedCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("fishing_rod_hooked");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	protected Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate playerPredicate, DeserializationContext predicateDeserializer) {
		ItemPredicate rod = ItemPredicate.fromJson(jsonObject.get("rod"));
		ContextAwarePredicate bobber = EntityPredicate.fromJson(jsonObject, "bobber", predicateDeserializer);
		ContextAwarePredicate fishing = EntityPredicate.fromJson(jsonObject, "fishing", predicateDeserializer);
		ContextAwarePredicate fishedEntity = EntityPredicate.fromJson(jsonObject, "fished_entity", predicateDeserializer);
		ItemPredicate fishedItem = ItemPredicate.fromJson(jsonObject.get("item"));
		FluidPredicate fluidPredicate = FluidPredicate.fromJson(jsonObject.get("fluid"));
		return new SpectrumFishingRodHookedCriterion.Conditions(playerPredicate, rod, bobber, fishing, fishedEntity, fishedItem, fluidPredicate);
	}
	
	public void trigger(ServerPlayer player, ItemStack rod, SpectrumFishingBobberEntity bobber, Entity fishedEntity, Collection<ItemStack> fishingLoots) {
		LootContext bobberContext = EntityPredicate.createContext(player, bobber);
		LootContext hookedEntityContext = bobber.getHookedEntity() == null ? null : EntityPredicate.createContext(player, bobber.getHookedEntity());
		LootContext fishedEntityContext = fishedEntity == null ? null : EntityPredicate.createContext(player, fishedEntity);
		this.trigger(player, (conditions) -> conditions.matches(rod, bobberContext, hookedEntityContext, fishedEntityContext, fishingLoots, (ServerLevel) bobber.level(), bobber.blockPosition()));
		
		// also trigger vanilla fishing criterion
		// since that one requires a FishingBobberEntity and SpectrumFishingBobberEntity
		// does not extend that we have to do some hacky shenanigans running trigger() directly
		LootContext hookedEntityOrBobberContext = EntityPredicate.createContext(player, (bobber.getHookedEntity() != null ? bobber.getHookedEntity() : bobber));
		CriteriaTriggers.FISHING_ROD_HOOKED.trigger(player, (conditions) -> conditions.matches(rod, hookedEntityOrBobberContext, fishingLoots));
	}

	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ItemPredicate rod;
		private final ContextAwarePredicate bobber;
		private final ContextAwarePredicate hookedEntity;
		private final ContextAwarePredicate fishedEntity;
		private final ItemPredicate caughtItem;
		private final FluidPredicate fluidPredicate;
		
		public Conditions(ContextAwarePredicate player, ItemPredicate rod, ContextAwarePredicate bobber, ContextAwarePredicate hookedEntity, ContextAwarePredicate fishedEntity, ItemPredicate caughtItem, FluidPredicate fluidPredicate) {
			super(SpectrumFishingRodHookedCriterion.ID, player);
			this.rod = rod;
			this.bobber = bobber;
			this.hookedEntity = hookedEntity;
			this.fishedEntity = fishedEntity;
			this.caughtItem = caughtItem;
			this.fluidPredicate = fluidPredicate;
		}
		
		public static SpectrumFishingRodHookedCriterion.Conditions create(ItemPredicate rod, ContextAwarePredicate bobber, ContextAwarePredicate hookedEntity, ContextAwarePredicate fishedEntity, ItemPredicate item, FluidPredicate fluidPredicate) {
			return new SpectrumFishingRodHookedCriterion.Conditions(ContextAwarePredicate.ANY, rod, bobber, hookedEntity, fishedEntity, item, fluidPredicate);
		}
		
		public boolean matches(ItemStack rod, LootContext bobberContext, LootContext hookedEntityContext, LootContext fishedEntityContext, Collection<ItemStack> fishingLoots, ServerLevel world, BlockPos blockPos) {
			if (!this.rod.matches(rod)) return false;
			if (!this.bobber.matches(bobberContext)) return false;
			if (!this.fluidPredicate.matches(world, blockPos)) return false;
			if (fishedEntityContext == null && !fishedEntity.equals(ContextAwarePredicate.ANY) ||
					!this.fishedEntity.matches(fishedEntityContext)) return false;
			if (hookedEntityContext == null && !hookedEntity.equals(ContextAwarePredicate.ANY) ||
					!this.hookedEntity.matches(hookedEntityContext)) return false;
			
			if (this.caughtItem != ItemPredicate.ANY) {
				if (hookedEntityContext != null) {
					Entity entity = hookedEntityContext.getParamOrNull(LootContextParams.THIS_ENTITY);
					if (entity instanceof ItemEntity itemEntity &&
							this.caughtItem.matches(itemEntity.getItem())) return true;
				}
				for (ItemStack itemStack : fishingLoots) {
					if (this.caughtItem.matches(itemStack)) return true;
				}
				
				return false;
			}
			
			return true;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("rod", this.rod.serializeToJson());
			jsonObject.add("bobber", this.bobber.toJson(predicateSerializer));
			jsonObject.add("hooked_entity", this.hookedEntity.toJson(predicateSerializer));
			jsonObject.add("fished_entity", this.fishedEntity.toJson(predicateSerializer));
			jsonObject.add("item", this.caughtItem.serializeToJson());
			jsonObject.add("fluid", this.fluidPredicate.serializeToJson());
			return jsonObject;
		}
	}
	
}
