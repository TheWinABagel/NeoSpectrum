package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CrystallarieumGrownCriterion extends SimpleCriterionTrigger<CrystallarieumGrownCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("crystallarieum_growing");
	
	public static CrystallarieumGrownCriterion.Conditions create(ItemPredicate item, BlockPredicate blockPredicate) {
		return new CrystallarieumGrownCriterion.Conditions(ContextAwarePredicate.ANY, item, blockPredicate);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public CrystallarieumGrownCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		BlockPredicate grownBlockPredicate = BlockPredicate.fromJson(jsonObject.get("grown_block"));
		ItemPredicate catalystPredicate = ItemPredicate.fromJson(jsonObject.get("used_catalyst"));
		return new CrystallarieumGrownCriterion.Conditions(extended, catalystPredicate, grownBlockPredicate);
	}
	
	public void trigger(ServerPlayer player, ServerLevel world, BlockPos pos, ItemStack catalystStack) {
		this.trigger(player, (conditions) -> conditions.matches(world, pos, catalystStack));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ItemPredicate catalystPredicate;
		private final BlockPredicate blockPredicate;
		
		public Conditions(ContextAwarePredicate player, ItemPredicate catalystPredicate, BlockPredicate blockPredicate) {
			super(ID, player);
			this.catalystPredicate = catalystPredicate;
			this.blockPredicate = blockPredicate;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("grown_block", this.blockPredicate.serializeToJson());
			jsonObject.add("used_catalyst", this.catalystPredicate.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(ServerLevel world, BlockPos blockPos, ItemStack catalystStack) {
			return this.blockPredicate.matches(world, blockPos) && this.catalystPredicate.matches(catalystStack);
		}
	}
	
}
