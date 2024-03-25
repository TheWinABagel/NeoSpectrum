package de.dafuqs.spectrum.recipe.ink_converting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public class InkConvertingRecipeSerializer implements GatedRecipeSerializer<InkConvertingRecipe> {
	
	public final InkConvertingRecipeSerializer.RecipeFactory recipeFactory;
	
	public InkConvertingRecipeSerializer(InkConvertingRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		InkConvertingRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Ingredient inputIngredient, InkColor inkColor, long inkAmount);
	}
	
	@Override
	public InkConvertingRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		JsonElement jsonElement = GsonHelper.isArrayNode(jsonObject, "ingredient") ? GsonHelper.getAsJsonArray(jsonObject, "ingredient") : GsonHelper.getAsJsonObject(jsonObject, "ingredient");
		Ingredient ingredient = Ingredient.fromJson(jsonElement);
		
		InkColor inkColor = InkColor.of(GsonHelper.getAsString(jsonObject, "color"));
		long inkAmount = GsonHelper.getAsLong(jsonObject, "amount");
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredient, inkColor, inkAmount);
	}
	
	@Override
	public void write(FriendlyByteBuf packetByteBuf, InkConvertingRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		recipe.inputIngredient.toNetwork(packetByteBuf);
		packetByteBuf.writeUtf(recipe.color.toString());
		packetByteBuf.writeLong(recipe.amount);
	}
	
	@Override
	public InkConvertingRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		Ingredient ingredient = Ingredient.fromNetwork(packetByteBuf);
		InkColor inkColor = InkColor.of(packetByteBuf.readUtf());
		long inkAmount = packetByteBuf.readLong();
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredient, inkColor, inkAmount);
	}
	
}
