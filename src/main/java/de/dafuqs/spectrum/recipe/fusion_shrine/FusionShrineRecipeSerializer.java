package de.dafuqs.spectrum.recipe.fusion_shrine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.matchbooks.recipe.RecipeParser;
import de.dafuqs.spectrum.api.predicate.world.WorldConditionPredicate;
import de.dafuqs.spectrum.api.recipe.FusionShrineRecipeWorldEffect;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public class FusionShrineRecipeSerializer implements GatedRecipeSerializer<FusionShrineRecipe> {
	
	public final FusionShrineRecipeSerializer.RecipeFactory recipeFactory;
	
	public FusionShrineRecipeSerializer(FusionShrineRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}

	public interface RecipeFactory {
		FusionShrineRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier,
								  List<IngredientStack> craftingInputs, Fluid fluidInput, ItemStack output, float experience, int craftingTime, boolean noBenefitsFromYieldUpgrades, boolean playCraftingFinishedEffects, boolean copyNbt,
								  List<WorldConditionPredicate> worldConditions, FusionShrineRecipeWorldEffect startWorldEffect, List<FusionShrineRecipeWorldEffect> duringWorldEffects, FusionShrineRecipeWorldEffect finishWorldEffect, Component description);
	}
	
	@Override
	public FusionShrineRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);

		JsonArray ingredientArray = GsonHelper.getAsJsonArray(jsonObject, "ingredients");
		List<IngredientStack> craftingInputs = RecipeParser.ingredientStacksFromJson(ingredientArray, ingredientArray.size());
		if (craftingInputs.size() > 7) {
			throw new JsonParseException("Recipe cannot have more than 7 ingredients. Has " + craftingInputs.size());
		}

		Fluid fluid = Fluids.EMPTY;
		if (GsonHelper.isStringValue(jsonObject, "fluid")) {
			ResourceLocation fluidIdentifier = ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "fluid"));
			fluid = BuiltInRegistries.FLUID.get(fluidIdentifier);
			if (fluid.defaultFluidState().isEmpty()) {
				throw new JsonParseException("Recipe specifies fluid " + fluidIdentifier + " that does not exist! This recipe will not be craftable.");
			}
		}
		
		ItemStack output;
		if (GsonHelper.isObjectNode(jsonObject, "result")) {
			output = RecipeUtils.itemStackWithNbtFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
		} else {
			output = ItemStack.EMPTY;
		}
		float experience = GsonHelper.getAsFloat(jsonObject, "experience", 0);
		int craftingTime = GsonHelper.getAsInt(jsonObject, "time", 200);
		boolean yieldUpgradesDisabled = GsonHelper.getAsBoolean(jsonObject, "disable_yield_upgrades", false);
		boolean playCraftingFinishedEffects = GsonHelper.getAsBoolean(jsonObject, "play_crafting_finished_effects", true);
		
		List<WorldConditionPredicate> worldConditions = new ArrayList<>();
		if (GsonHelper.isArrayNode(jsonObject, "world_conditions")) {
			for (JsonElement element : GsonHelper.getAsJsonArray(jsonObject, "world_conditions")) {
				worldConditions.add(WorldConditionPredicate.fromJson(element));
			}
		}
		
		FusionShrineRecipeWorldEffect startWorldEffect = FusionShrineRecipeWorldEffect.fromString(GsonHelper.getAsString(jsonObject, "start_crafting_effect", null));
		List<FusionShrineRecipeWorldEffect> duringWorldEffects = new ArrayList<>();
		if (GsonHelper.isArrayNode(jsonObject, "during_crafting_effects")) {
			JsonArray worldEffectsArray = GsonHelper.getAsJsonArray(jsonObject, "during_crafting_effects");
			for (int i = 0; i < worldEffectsArray.size(); i++) {
				duringWorldEffects.add(FusionShrineRecipeWorldEffect.fromString(worldEffectsArray.get(i).getAsString()));
			}
		}
		FusionShrineRecipeWorldEffect finishWorldEffect = FusionShrineRecipeWorldEffect.fromString(GsonHelper.getAsString(jsonObject, "finish_crafting_effect", null));
		
		Component description;
		if (GsonHelper.isStringValue(jsonObject, "description")) {
			description = Component.translatable(GsonHelper.getAsString(jsonObject, "description"));
		} else {
			description = null;
		}
		boolean copyNbt = GsonHelper.getAsBoolean(jsonObject, "copy_nbt", false);
		if (copyNbt && output.isEmpty()) {
			throw new JsonParseException("Recipe does have copy_nbt set, but has no output!");
		}

		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier,
				craftingInputs, fluid, output, experience, craftingTime, yieldUpgradesDisabled, playCraftingFinishedEffects, copyNbt,
				worldConditions, startWorldEffect, duringWorldEffects, finishWorldEffect, description);
	}
	
	
	@Override
	public void write(FriendlyByteBuf packetByteBuf, FusionShrineRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		packetByteBuf.writeShort(recipe.craftingInputs.size());
		for (IngredientStack ingredientStack : recipe.craftingInputs) {
			ingredientStack.write(packetByteBuf);
		}
		
		packetByteBuf.writeResourceLocation(BuiltInRegistries.FLUID.getKey(recipe.fluidInput));
		packetByteBuf.writeItem(recipe.output);
		packetByteBuf.writeFloat(recipe.experience);
		packetByteBuf.writeInt(recipe.craftingTime);
		packetByteBuf.writeBoolean(recipe.yieldUpgradesDisabled);
		packetByteBuf.writeBoolean(recipe.playCraftingFinishedEffects);
		
		if (recipe.getDescription().isEmpty()) {
			packetByteBuf.writeComponent(Component.literal(""));
		} else {
			packetByteBuf.writeComponent(recipe.getDescription().get());
		}
		packetByteBuf.writeBoolean(recipe.copyNbt);
	}
	
	
	@Override
	public FusionShrineRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		short craftingInputCount = packetByteBuf.readShort();
		List<IngredientStack> ingredients = IngredientStack.decodeByteBuf(packetByteBuf, craftingInputCount);
		
		Fluid fluid = BuiltInRegistries.FLUID.get(packetByteBuf.readResourceLocation());
		ItemStack output = packetByteBuf.readItem();
		float experience = packetByteBuf.readFloat();
		int craftingTime = packetByteBuf.readInt();
		boolean yieldUpgradesDisabled = packetByteBuf.readBoolean();
		boolean playCraftingFinishedEffects = packetByteBuf.readBoolean();

		Component description = packetByteBuf.readComponent();
		boolean copyNbt = packetByteBuf.readBoolean();
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier,
				ingredients, fluid, output, experience, craftingTime, yieldUpgradesDisabled, playCraftingFinishedEffects, copyNbt,
				List.of(), FusionShrineRecipeWorldEffect.NOTHING, List.of(), FusionShrineRecipeWorldEffect.NOTHING, description);
	}
	
}
