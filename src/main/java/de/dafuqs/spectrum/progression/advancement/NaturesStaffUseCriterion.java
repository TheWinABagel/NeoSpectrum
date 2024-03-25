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

public class NaturesStaffUseCriterion extends SimpleCriterionTrigger<NaturesStaffUseCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("natures_staff_conversion");
	
	@Nullable
	private static Block getBlock(JsonObject obj, String propertyName) {
		if (obj.has(propertyName)) {
			ResourceLocation identifier = new ResourceLocation(GsonHelper.getAsString(obj, propertyName));
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
	public NaturesStaffUseCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		Block sourceBlock = getBlock(jsonObject, "source_block");
		StatePropertiesPredicate sourceStatePredicate = StatePropertiesPredicate.fromJson(jsonObject.get("source_state"));
		
		if (sourceBlock != null) {
			sourceStatePredicate.checkState(sourceBlock.getStateDefinition(), (name) -> {
				throw new JsonSyntaxException("Block " + sourceBlock + " has no property " + name);
			});
		}
		
		Block targetBlock = getBlock(jsonObject, "target_block");
		StatePropertiesPredicate targetStatePredicate = StatePropertiesPredicate.fromJson(jsonObject.get("target_state"));
		if (targetBlock != null) {
			targetStatePredicate.checkState(targetBlock.getStateDefinition(), (name) -> {
				throw new JsonSyntaxException("Block " + targetBlock + " has no property " + name);
			});
		}
		
		return new NaturesStaffUseCriterion.Conditions(extended, sourceBlock, sourceStatePredicate, targetBlock, targetStatePredicate);
	}
	
	public void trigger(ServerPlayer player, BlockState sourceBlockState, BlockState targetBlockState) {
		this.trigger(player, (conditions) -> conditions.matches(sourceBlockState, targetBlockState));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		
		@Nullable
		private final Block sourceBlock;
		private final StatePropertiesPredicate sourceBlockState;
		@Nullable
		private final Block targetBlock;
		private final StatePropertiesPredicate targetBlockState;
		
		public Conditions(ContextAwarePredicate player, @Nullable Block sourceBlock, StatePropertiesPredicate sourceBlockState, @Nullable Block targetBlock, StatePropertiesPredicate targetBlockState) {
			super(ID, player);
			this.sourceBlock = sourceBlock;
			this.sourceBlockState = sourceBlockState;
			this.targetBlock = targetBlock;
			this.targetBlockState = targetBlockState;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			if (this.sourceBlock != null) {
				jsonObject.addProperty("source_block", BuiltInRegistries.BLOCK.getKey(this.sourceBlock).toString());
			}
			jsonObject.add("source_state:", this.sourceBlockState.serializeToJson());
			if (this.targetBlock != null) {
				jsonObject.addProperty("target_block", BuiltInRegistries.BLOCK.getKey(this.targetBlock).toString());
			}
			jsonObject.add("target_state", this.targetBlockState.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(BlockState sourceBlockState, BlockState targetBlockState) {
			if (this.sourceBlock != null && !sourceBlockState.is(this.sourceBlock)) {
				return false;
			}
			if (!this.sourceBlockState.matches(sourceBlockState)) {
				return false;
			}
			if (this.targetBlock != null && !targetBlockState.is(this.targetBlock)) {
				return false;
			} else {
				return this.targetBlockState.matches(targetBlockState);
			}
		}
		
	}
	
}
