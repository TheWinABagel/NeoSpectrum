package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentUpgradedCriterion extends SimpleCriterionTrigger<EnchantmentUpgradedCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("enchantment_upgraded");
	
	public static EnchantmentUpgradedCriterion.Conditions create(Enchantment enchantment, MinMaxBounds.Ints enchantmentLevelRange, MinMaxBounds.Ints experienceRange) {
		return new EnchantmentUpgradedCriterion.Conditions(ContextAwarePredicate.ANY, enchantment, enchantmentLevelRange, experienceRange);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public EnchantmentUpgradedCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ResourceLocation identifier = new ResourceLocation(GsonHelper.getAsString(jsonObject, "enchantment_identifier"));
		Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(identifier);
		MinMaxBounds.Ints enchantmentLevelRange = MinMaxBounds.Ints.fromJson(jsonObject.get("enchantment_level"));
		MinMaxBounds.Ints experienceRange = MinMaxBounds.Ints.fromJson(jsonObject.get("spent_experience"));
		return new EnchantmentUpgradedCriterion.Conditions(extended, enchantment, enchantmentLevelRange, experienceRange);
	}
	
	public void trigger(ServerPlayer player, Enchantment enchantment, int enchantmentLevel, int spentExperience) {
		this.trigger(player, (conditions) -> conditions.matches(enchantment, enchantmentLevel, spentExperience));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final Enchantment enchantment;
		private final MinMaxBounds.Ints enchantmentLevelRange;
		private final MinMaxBounds.Ints experienceRange;
		
		public Conditions(ContextAwarePredicate player, Enchantment enchantment, MinMaxBounds.Ints enchantmentLevelRange, MinMaxBounds.Ints experienceRange) {
			super(ID, player);
			this.enchantment = enchantment;
			this.enchantmentLevelRange = enchantmentLevelRange;
			this.experienceRange = experienceRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.addProperty("enchantment_identifier", BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString());
			jsonObject.add("enchantment_level", this.enchantmentLevelRange.serializeToJson());
			jsonObject.add("spent_experience", this.experienceRange.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(Enchantment enchantment, int enchantmentLevel, int spentExperience) {
			if (this.enchantment == null || this.enchantment.equals(enchantment)) {
				return this.enchantmentLevelRange.matches(enchantmentLevel) && this.experienceRange.matches(spentExperience);
			}
			return false;
		}
	}
	
}
