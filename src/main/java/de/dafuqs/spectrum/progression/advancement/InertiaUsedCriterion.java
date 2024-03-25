package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class InertiaUsedCriterion extends SimpleCriterionTrigger<InertiaUsedCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("inertia_used");
	
	@Nullable
	private static Block getBlock(JsonObject obj) {
		if (obj.has("block")) {
			ResourceLocation identifier = new ResourceLocation(GsonHelper.getAsString(obj, "block"));
			return BuiltInRegistries.BLOCK.getOptional(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
		} else {
			return null;
		}
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public InertiaUsedCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate predicate, DeserializationContext advancementEntityPredicateDeserializer) {
		Block block = getBlock(jsonObject);
		StatePropertiesPredicate statePredicate = StatePropertiesPredicate.fromJson(jsonObject.get("state"));
		if (block != null) {
			statePredicate.checkState(block.getStateDefinition(), (name) -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + name);
			});
		}
		MinMaxBounds.Ints amountRange = MinMaxBounds.Ints.fromJson(jsonObject.get("amount"));
		
		return new InertiaUsedCriterion.Conditions(predicate, block, statePredicate, amountRange);
	}
	
	public void trigger(ServerPlayer player, BlockState state, int amount) {
		this.trigger(player, (conditions) -> conditions.matches(state, amount));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		@Nullable
		private final Block block;
		private final StatePropertiesPredicate state;
		private final MinMaxBounds.Ints amountRange;
		
		public Conditions(ContextAwarePredicate player, @Nullable Block block, StatePropertiesPredicate state, MinMaxBounds.Ints amountRange) {
			super(InertiaUsedCriterion.ID, player);
			this.block = block;
			this.state = state;
			this.amountRange = amountRange;
		}
		
		public static InertiaUsedCriterion.Conditions block(Block block, MinMaxBounds.Ints amountRange) {
			return new InertiaUsedCriterion.Conditions(ContextAwarePredicate.ANY, block, StatePropertiesPredicate.ANY, amountRange);
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			if (this.block != null) {
				jsonObject.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block).toString());
			}
			
			jsonObject.add("state", this.state.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(BlockState state, int amount) {
			if (this.block != null && !state.is(this.block)) {
				return false;
			} else {
				return this.state.matches(state) && amountRange.matches(amount);
			}
		}
	}
}
