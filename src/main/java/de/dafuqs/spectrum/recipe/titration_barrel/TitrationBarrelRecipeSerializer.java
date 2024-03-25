package de.dafuqs.spectrum.recipe.titration_barrel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.matchbooks.recipe.RecipeParser;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.recipe.FluidIngredient;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class TitrationBarrelRecipeSerializer implements GatedRecipeSerializer<TitrationBarrelRecipe> {
	
	public final TitrationBarrelRecipeSerializer.RecipeFactory recipeFactory;
	
	public TitrationBarrelRecipeSerializer(TitrationBarrelRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		TitrationBarrelRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, List<IngredientStack> ingredients, FluidIngredient fluid, ItemStack outputItemStack, Item tappingItem, int minTimeDays, FermentationData fermentationData);
	}
	
	@Override
	public TitrationBarrelRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		JsonArray ingredientArray = GsonHelper.getAsJsonArray(jsonObject, "ingredients");
		List<IngredientStack> ingredients = RecipeParser.ingredientStacksFromJson(ingredientArray, ingredientArray.size());

		FluidIngredient fluidInput = FluidIngredient.EMPTY;
		if (GsonHelper.isObjectNode(jsonObject, "fluid")) {
			JsonObject fluidObject = GsonHelper.getAsJsonObject(jsonObject, "fluid");
			FluidIngredient.JsonParseResult result = FluidIngredient.fromJson(fluidObject);
			fluidInput = result.result();
			if (result.malformed()) {
				// Currently handling malformed input leniently. May throw an error in the future.
				SpectrumCommon.logError("Titration Recipe " + identifier + "contains a malformed fluid input tag! This recipe will not be craftable.");
			} else if (result.result() == FluidIngredient.EMPTY) {
				if (result.isTag()) {
					SpectrumCommon.logError("Titration Recipe " + identifier + " specifies fluid tag " + result.id() + " that does not exist! This recipe will not be craftable.");
				} else {
					SpectrumCommon.logError("Titration Recipe " + identifier + " specifies fluid " + result.id() + " that does not exist! This recipe will not be craftable.");
				}
			}
		}

		ItemStack outputItemStack = RecipeUtils.itemStackWithNbtFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
		int minTimeDays = GsonHelper.getAsInt(jsonObject, "min_fermentation_time_hours", 24);
		
		Item tappingItem = Items.AIR;
		if (GsonHelper.isStringValue(jsonObject, "tapping_item")) {
			tappingItem = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "tapping_item")));
		}
		
		FermentationData fermentationData = null;
		if (GsonHelper.isObjectNode(jsonObject, "fermentation_data")) {
			fermentationData = FermentationData.fromJson(GsonHelper.getAsJsonObject(jsonObject, "fermentation_data"));
		}
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredients, fluidInput, outputItemStack, tappingItem, minTimeDays, fermentationData);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf packetByteBuf, TitrationBarrelRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		packetByteBuf.writeShort(recipe.inputStacks.size());
		for (IngredientStack ingredientStack : recipe.inputStacks) {
			ingredientStack.write(packetByteBuf);
		}
		writeFluidIngredient(packetByteBuf, recipe.fluid);
		
		packetByteBuf.writeItem(recipe.outputItemStack);
		packetByteBuf.writeUtf(BuiltInRegistries.ITEM.getKey(recipe.tappingItem).toString());
		packetByteBuf.writeInt(recipe.minFermentationTimeHours);
		
		if (recipe.fermentationData == null) {
			packetByteBuf.writeBoolean(false);
		} else {
			packetByteBuf.writeBoolean(true);
			recipe.fermentationData.write(packetByteBuf);
		}
		
	}
	
	@Override
	public TitrationBarrelRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		short craftingInputCount = packetByteBuf.readShort();
		List<IngredientStack> ingredients = IngredientStack.decodeByteBuf(packetByteBuf, craftingInputCount);

		FluidIngredient fluidInput = readFluidIngredient(packetByteBuf);
		
		ItemStack outputItemStack = packetByteBuf.readItem();
		Item tappingItem = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(packetByteBuf.readUtf()));
		int minTimeDays = packetByteBuf.readInt();
		
		FermentationData fermentationData = null;
		if (packetByteBuf.readBoolean()) {
			fermentationData = FermentationData.read(packetByteBuf);
		}
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredients, fluidInput, outputItemStack, tappingItem, minTimeDays, fermentationData);
	}
	
}
