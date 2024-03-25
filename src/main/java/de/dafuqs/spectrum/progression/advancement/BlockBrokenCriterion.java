package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.predicate.block.BrokenBlockPredicate;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockBrokenCriterion extends SimpleCriterionTrigger<BlockBrokenCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("block_broken");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	public void trigger(ServerPlayer player, BlockState minedBlock) {
		this.trigger(player, (conditions) -> conditions.matches(minedBlock));
	}

	@Override
	protected Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate playerPredicate, DeserializationContext predicateDeserializer) {
		BrokenBlockPredicate brokenBlockPredicate = BrokenBlockPredicate.fromJson(jsonObject.get("broken_block"));
		return new BlockBrokenCriterion.Conditions(playerPredicate, brokenBlockPredicate);
	}

	public static class Conditions extends AbstractCriterionTriggerInstance {
		
		private final BrokenBlockPredicate brokenBlockPredicate;
		
		public Conditions(ContextAwarePredicate player, @Nullable BrokenBlockPredicate brokenBlockPredicate) {
			super(ID, player);
			this.brokenBlockPredicate = brokenBlockPredicate;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("broken_block", this.brokenBlockPredicate.toJson());
			return jsonObject;
		}
		
		public boolean matches(BlockState blockState) {
			return this.brokenBlockPredicate.test(blockState);
		}
	}
	
}
