package de.dafuqs.spectrum.recipe.cinderhearth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class CinderhearthRecipeSerializer implements GatedRecipeSerializer<CinderhearthRecipe> {
	
	public final RecipeFactory recipeFactory;
	
	public CinderhearthRecipeSerializer(RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		CinderhearthRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Ingredient inputIngredient, int time, float experience, List<Tuple<ItemStack, Float>> outputsWithChance);
	}
	
	@Override
	public CinderhearthRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		Ingredient inputIngredient = Ingredient.fromJson(GsonHelper.isArrayNode(jsonObject, "ingredient") ? GsonHelper.getAsJsonArray(jsonObject, "ingredient") : GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
		int time = GsonHelper.getAsInt(jsonObject, "time");
		float experience = GsonHelper.getAsFloat(jsonObject, "experience");
		
		List<Tuple<ItemStack, Float>> outputsWithChance = new ArrayList<>();
		for (JsonElement outputEntry : GsonHelper.getAsJsonArray(jsonObject, "results")) {
			JsonObject outputObject = outputEntry.getAsJsonObject();
			ItemStack outputStack = RecipeUtils.itemStackWithNbtFromJson(outputObject);
			float outputChance = 1.0F;
			if (GsonHelper.isNumberValue(outputObject, "chance")) {
				outputChance = GsonHelper.getAsFloat(outputObject, "chance");
			}
			outputsWithChance.add(new Tuple<>(outputStack, outputChance));
		}
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, inputIngredient, time, experience, outputsWithChance);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf packetByteBuf, CinderhearthRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		recipe.inputIngredient.toNetwork(packetByteBuf);
		packetByteBuf.writeInt(recipe.time);
		packetByteBuf.writeFloat(recipe.experience);
		
		packetByteBuf.writeInt(recipe.outputsWithChance.size());
		for (Tuple<ItemStack, Float> output : recipe.outputsWithChance) {
			packetByteBuf.writeItem(output.getA());
			packetByteBuf.writeFloat(output.getB());
		}
	}
	
	@Override
	public CinderhearthRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		Ingredient inputIngredient = Ingredient.fromNetwork(packetByteBuf);
		int time = packetByteBuf.readInt();
		float experience = packetByteBuf.readFloat();
		
		int outputCount = packetByteBuf.readInt();
		List<Tuple<ItemStack, Float>> outputsWithChance = new ArrayList<>(outputCount);
		for (int i = 0; i < outputCount; i++) {
			outputsWithChance.add(new Tuple<>(packetByteBuf.readItem(), packetByteBuf.readFloat()));
		}
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, inputIngredient, time, experience, outputsWithChance);
	}
	
}
