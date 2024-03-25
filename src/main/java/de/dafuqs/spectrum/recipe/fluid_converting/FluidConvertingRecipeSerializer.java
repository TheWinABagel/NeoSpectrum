package de.dafuqs.spectrum.recipe.fluid_converting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class FluidConvertingRecipeSerializer<R extends FluidConvertingRecipe> implements GatedRecipeSerializer<R> {
	
	public final FluidConvertingRecipeSerializer.RecipeFactory<R> recipeFactory;
	
	public FluidConvertingRecipeSerializer(FluidConvertingRecipeSerializer.RecipeFactory<R> recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory<R> {
		R create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Ingredient inputIngredient, ItemStack outputItemStack);
	}
	
	@Override
	public R fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		JsonElement jsonElement = GsonHelper.getAsJsonObject(jsonObject, "ingredient");
		Ingredient ingredient = Ingredient.fromJson(jsonElement);
		ItemStack outputItemStack = RecipeUtils.itemStackWithNbtFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredient, outputItemStack);
	}
	
	@Override
	public void write(FriendlyByteBuf packetByteBuf, R recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		recipe.inputIngredient.toNetwork(packetByteBuf);
		packetByteBuf.writeItem(recipe.outputItemStack);
	}
	
	@Override
	public R fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		Ingredient ingredient = Ingredient.fromNetwork(packetByteBuf);
		ItemStack outputItemStack = packetByteBuf.readItem();
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredient, outputItemStack);
	}
	
}
