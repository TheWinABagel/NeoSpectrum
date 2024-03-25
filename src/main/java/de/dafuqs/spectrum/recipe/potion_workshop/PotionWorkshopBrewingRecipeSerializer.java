package de.dafuqs.spectrum.recipe.potion_workshop;

import com.google.gson.JsonObject;
import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.matchbooks.recipe.RecipeParser;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class PotionWorkshopBrewingRecipeSerializer implements GatedRecipeSerializer<PotionWorkshopBrewingRecipe> {
	
	public final PotionWorkshopBrewingRecipeSerializer.RecipeFactory recipeFactory;
	
	public PotionWorkshopBrewingRecipeSerializer(PotionWorkshopBrewingRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		PotionWorkshopBrewingRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, int craftingTime, IngredientStack ingredient1, IngredientStack ingredient2, IngredientStack ingredient3, PotionRecipeEffect recipeData);
	}
	
	@Override
	public PotionWorkshopBrewingRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		IngredientStack ingredient1 = RecipeParser.ingredientStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient1"));
		IngredientStack ingredient2;
		if (GsonHelper.isObjectNode(jsonObject, "ingredient2")) {
			ingredient2 = RecipeParser.ingredientStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient2"));
		} else {
			ingredient2 = IngredientStack.EMPTY;
		}
		IngredientStack ingredient3;
		if (GsonHelper.isObjectNode(jsonObject, "ingredient3")) {
			ingredient3 = RecipeParser.ingredientStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient3"));
		} else {
			ingredient3 = IngredientStack.EMPTY;
		}

		int craftingTime = GsonHelper.getAsInt(jsonObject, "time", 200);
		
		PotionRecipeEffect recipeData = PotionRecipeEffect.read(jsonObject);
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, craftingTime, ingredient1, ingredient2, ingredient3, recipeData);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf packetByteBuf, PotionWorkshopBrewingRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		packetByteBuf.writeInt(recipe.craftingTime);
		recipe.ingredient1.write(packetByteBuf);
		recipe.ingredient2.write(packetByteBuf);
		recipe.ingredient3.write(packetByteBuf);
		recipe.recipeData.write(packetByteBuf);
	}
	
	@Override
	public PotionWorkshopBrewingRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		int craftingTime = packetByteBuf.readInt();
		IngredientStack ingredient1 = IngredientStack.fromByteBuf(packetByteBuf);
		IngredientStack ingredient2 = IngredientStack.fromByteBuf(packetByteBuf);
		IngredientStack ingredient3 = IngredientStack.fromByteBuf(packetByteBuf);
		
		PotionRecipeEffect recipeData = PotionRecipeEffect.read(packetByteBuf);
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, craftingTime, ingredient1, ingredient2, ingredient3, recipeData);
	}
	
}
