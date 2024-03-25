package de.dafuqs.spectrum.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.TimeHelper;
import de.dafuqs.spectrum.loot.SpectrumLootFunctionTypes;
import de.dafuqs.spectrum.mixin.accessors.BiomeAccessor;
import de.dafuqs.spectrum.recipe.titration_barrel.FermentationData;
import de.dafuqs.spectrum.recipe.titration_barrel.TitrationBarrelRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FermentRandomlyLootFunction extends LootItemConditionalFunction {
	
	final @Nullable ResourceLocation fermentationRecipeIdentifier;
	final @Nullable FermentationData fermentationData;
	final NumberProvider daysFermented;
	final NumberProvider thickness;
	
	public FermentRandomlyLootFunction(LootItemCondition[] conditions, @NotNull ResourceLocation fermentationRecipeIdentifier, NumberProvider daysFermented, NumberProvider thickness) {
		super(conditions);
		this.fermentationRecipeIdentifier = fermentationRecipeIdentifier;
		this.fermentationData = null;
		this.daysFermented = daysFermented;
		this.thickness = thickness;
	}
	
	public FermentRandomlyLootFunction(LootItemCondition[] conditions, @NotNull FermentationData fermentationData, NumberProvider daysFermented, NumberProvider thickness) {
		super(conditions);
		this.fermentationRecipeIdentifier = null;
		this.fermentationData = fermentationData;
		this.daysFermented = daysFermented;
		this.thickness = thickness;
	}
	
	@Override
	public LootItemFunctionType getType() {
		return SpectrumLootFunctionTypes.FERMENT_RANDOMLY;
	}
	
	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		FermentationData fermentationData = null;
		if (this.fermentationRecipeIdentifier != null) {
			Optional<? extends Recipe<?>> recipe = SpectrumCommon.minecraftServer.getRecipeManager().byKey(this.fermentationRecipeIdentifier);
			if (recipe.isPresent() && recipe.get() instanceof TitrationBarrelRecipe titrationBarrelRecipe) {
				fermentationData = titrationBarrelRecipe.getFermentationData();
			} else {
				SpectrumCommon.logError("A 'spectrum:ferment_randomly' loot function has set an invalid 'fermentation_recipe_id': " + this.fermentationRecipeIdentifier + " It has to match an existing Titration Barrel recipe.");
			}
		}
		if (fermentationData == null) {
			fermentationData = this.fermentationData;
		}
		if (fermentationData != null) {
			BlockPos pos = BlockPos.containing(context.getParamOrNull(LootContextParams.ORIGIN));
			Biome biome = context.getLevel().getBiome(pos).value();
			float downfall = ((BiomeAccessor)(Object) biome).getClimateSettings().downfall();
			return TitrationBarrelRecipe.getFermentedStack(fermentationData, this.thickness.getInt(context), TimeHelper.secondsFromMinecraftDays(this.daysFermented.getInt(context)), downfall, stack);
		}
		return stack;
	}
	
	public static LootItemConditionalFunction.Builder<?> builder(FermentationData fermentationData, NumberProvider daysFermented, NumberProvider thickness) {
		return simpleBuilder((conditions) -> new FermentRandomlyLootFunction(conditions, fermentationData, daysFermented, thickness));
	}
	
	public static LootItemConditionalFunction.Builder<?> builder(ResourceLocation fermentationRecipeIdentifier, NumberProvider daysFermented, NumberProvider thickness) {
		return simpleBuilder((conditions) -> new FermentRandomlyLootFunction(conditions, fermentationRecipeIdentifier, daysFermented, thickness));
	}
	
	public static class Serializer extends LootItemConditionalFunction.Serializer<FermentRandomlyLootFunction> {
		
		private static final String FERMENTATION_RECIPE_ID_STRING = "fermentation_recipe_id";
		private static final String FERMENTATION_DATA_STRING = "fermentation_data";
		private static final String DAYS_FERMENTED_STRING = "days_fermented";
		private static final String THICKNESS_STRING = "thickness";

		@Override
		public void serialize(JsonObject jsonObject, FermentRandomlyLootFunction lootFunction, JsonSerializationContext jsonSerializationContext) {
			super.serialize(jsonObject, lootFunction, jsonSerializationContext);
			
			if (lootFunction.fermentationRecipeIdentifier != null) {
				jsonObject.addProperty(FERMENTATION_RECIPE_ID_STRING, lootFunction.fermentationRecipeIdentifier.toString());
			} else {
				jsonObject.add(FERMENTATION_DATA_STRING, lootFunction.fermentationData.toJson());
			}
			jsonObject.add(DAYS_FERMENTED_STRING, jsonSerializationContext.serialize(lootFunction.daysFermented));
			jsonObject.add(THICKNESS_STRING, jsonSerializationContext.serialize(lootFunction.thickness));
		}
		
		@Override
		public FermentRandomlyLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootConditions) {
			NumberProvider daysFermented = GsonHelper.getAsObject(jsonObject, DAYS_FERMENTED_STRING, jsonDeserializationContext, NumberProvider.class);
			NumberProvider thickness = GsonHelper.getAsObject(jsonObject, THICKNESS_STRING, jsonDeserializationContext, NumberProvider.class);
			
			if (jsonObject.has(FERMENTATION_RECIPE_ID_STRING)) {
				ResourceLocation fermentationRecipeIdentifier = ResourceLocation.tryParse(jsonObject.get(FERMENTATION_RECIPE_ID_STRING).getAsString());
				return new FermentRandomlyLootFunction(lootConditions, fermentationRecipeIdentifier, daysFermented, thickness);
			} else if (jsonObject.has(FERMENTATION_DATA_STRING)) {
				FermentationData fermentationData = FermentationData.fromJson(jsonObject.get(FERMENTATION_DATA_STRING).getAsJsonObject());
				return new FermentRandomlyLootFunction(lootConditions, fermentationData, daysFermented, thickness);
			}
			
			throw new JsonParseException("A 'ferment_randomly' loot function always needs to have either 'fermentation_data' or 'fermentation_recipe_id' set.");
		}
	}
}
