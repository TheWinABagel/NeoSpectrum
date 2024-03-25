package de.dafuqs.spectrum.recipe.potion_workshop;

import com.google.gson.JsonObject;
import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.matchbooks.recipe.RecipeParser;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class PotionWorkshopCraftingRecipeSerializer implements GatedRecipeSerializer<PotionWorkshopCraftingRecipe> {
	
	public final PotionWorkshopCraftingRecipeSerializer.RecipeFactory recipeFactory;
	
	public PotionWorkshopCraftingRecipeSerializer(PotionWorkshopCraftingRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		PotionWorkshopCraftingRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, IngredientStack baseIngredient, boolean consumeBaseIngredient, int requiredExperience, IngredientStack ingredient1, IngredientStack ingredient2, IngredientStack ingredient3, ItemStack output, int craftingTime, int color);
	}
	
	@Override
	public PotionWorkshopCraftingRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		IngredientStack baseIngredient = RecipeParser.ingredientStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "base_ingredient"));
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
		
		int requiredExperience = GsonHelper.getAsInt(jsonObject, "required_experience", 0);
		int craftingTime = GsonHelper.getAsInt(jsonObject, "time", 200);
		int color = GsonHelper.getAsInt(jsonObject, "color", 0xc03058);
		boolean consumeBaseIngredient = GsonHelper.getAsBoolean(jsonObject, "use_up_base_ingredient", true);
		ItemStack output = RecipeUtils.itemStackWithNbtFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, baseIngredient, consumeBaseIngredient, requiredExperience, ingredient1, ingredient2, ingredient3, output, craftingTime, color);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf packetByteBuf, PotionWorkshopCraftingRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		recipe.baseIngredient.write(packetByteBuf);
		packetByteBuf.writeBoolean(recipe.consumeBaseIngredient);
		packetByteBuf.writeInt(recipe.requiredExperience);
		recipe.ingredient1.write(packetByteBuf);
		recipe.ingredient2.write(packetByteBuf);
		recipe.ingredient3.write(packetByteBuf);
		packetByteBuf.writeItem(recipe.output);
		packetByteBuf.writeInt(recipe.craftingTime);
		packetByteBuf.writeInt(recipe.color);
	}
	
	@Override
	public PotionWorkshopCraftingRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		IngredientStack baseIngredient = IngredientStack.fromByteBuf(packetByteBuf);
		boolean consumeBaseIngredient = packetByteBuf.readBoolean();
		int requiredExperience = packetByteBuf.readInt();
		IngredientStack ingredient1 = IngredientStack.fromByteBuf(packetByteBuf);
		IngredientStack ingredient2 = IngredientStack.fromByteBuf(packetByteBuf);
		IngredientStack ingredient3 = IngredientStack.fromByteBuf(packetByteBuf);
		ItemStack output = packetByteBuf.readItem();
		int craftingTime = packetByteBuf.readInt();
		int color = packetByteBuf.readInt();
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, baseIngredient, consumeBaseIngredient, requiredExperience, ingredient1, ingredient2, ingredient3, output, craftingTime, color);
	}
	
}
