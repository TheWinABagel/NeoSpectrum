package de.dafuqs.spectrum.recipe.anvil_crushing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class AnvilCrushingRecipeSerializer implements GatedRecipeSerializer<AnvilCrushingRecipe> {
	
	public final AnvilCrushingRecipeSerializer.RecipeFactory recipeFactory;
	
	public AnvilCrushingRecipeSerializer(AnvilCrushingRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		AnvilCrushingRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Ingredient inputIngredient, ItemStack outputItemStack, float crushedItemsPerPointOfDamage, float experience, ResourceLocation particleEffectIdentifier, int particleCount, ResourceLocation soundEventIdentifier);
	}
	
	@Override
	public AnvilCrushingRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		JsonElement jsonElement = GsonHelper.isArrayNode(jsonObject, "ingredient") ? GsonHelper.getAsJsonArray(jsonObject, "ingredient") : GsonHelper.getAsJsonObject(jsonObject, "ingredient");
		Ingredient ingredient = Ingredient.fromJson(jsonElement);
		ItemStack outputItemStack = RecipeUtils.itemStackWithNbtFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
		float crushedItemsPerPointOfDamage = GsonHelper.getAsFloat(jsonObject, "crushedItemsPerPointOfDamage");
		float experience = GsonHelper.getAsFloat(jsonObject, "experience");
		ResourceLocation particleEffectIdentifier = ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "particleEffectIdentifier"));
		
		int particleCount = 1;
		if (GsonHelper.isNumberValue(jsonObject, "particleCount")) {
			particleCount = GsonHelper.getAsInt(jsonObject, "particleCount");
		}
		
		String soundEventString = GsonHelper.getAsString(jsonObject, "soundEventIdentifier");
		ResourceLocation soundEventIdentifier = new ResourceLocation(soundEventString);
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredient, outputItemStack, crushedItemsPerPointOfDamage, experience, particleEffectIdentifier, particleCount, soundEventIdentifier);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf packetByteBuf, AnvilCrushingRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		recipe.inputIngredient.toNetwork(packetByteBuf);
		packetByteBuf.writeItem(recipe.outputItemStack);
		packetByteBuf.writeFloat(recipe.crushedItemsPerPointOfDamage);
		packetByteBuf.writeFloat(recipe.experience);
		packetByteBuf.writeResourceLocation(recipe.particleEffectIdentifier);
		packetByteBuf.writeInt(recipe.particleCount);
		packetByteBuf.writeResourceLocation(recipe.soundEvent);
	}
	
	@Override
	public AnvilCrushingRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		Ingredient ingredient = Ingredient.fromNetwork(packetByteBuf);
		ItemStack outputItemStack = packetByteBuf.readItem();
		float crushedItemsPerPointOfDamage = packetByteBuf.readFloat();
		float experience = packetByteBuf.readFloat();
		ResourceLocation particleEffectIdentifier = packetByteBuf.readResourceLocation();
		int particleCount = packetByteBuf.readInt();
		ResourceLocation soundEventIdentifier = packetByteBuf.readResourceLocation();
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredient, outputItemStack, crushedItemsPerPointOfDamage, experience, particleEffectIdentifier, particleCount, soundEventIdentifier);
	}
	
}
