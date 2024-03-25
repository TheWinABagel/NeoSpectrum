package de.dafuqs.spectrum.recipe.potion_workshop;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PotionWorkshopReactingRecipeSerializer implements GatedRecipeSerializer<PotionWorkshopReactingRecipe> {
	
	public final PotionWorkshopReactingRecipeSerializer.RecipeFactory recipeFactory;
	
	public PotionWorkshopReactingRecipeSerializer(PotionWorkshopReactingRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		PotionWorkshopReactingRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Item item, List<PotionMod> mods);
	}
	
	@Override
	public PotionWorkshopReactingRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		if (!jsonObject.has("modifiers")) {
			throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
		}
		
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		Item item = ShapedRecipe.itemFromJson(jsonObject);
		List<PotionMod> mods;
		
		if (GsonHelper.isArrayNode(jsonObject, "modifiers")) {
			JsonArray modifiers = GsonHelper.getAsJsonArray(jsonObject, "modifiers");
			mods = StreamSupport.stream(modifiers.spliterator(), false).map((jsonElement) -> PotionMod.fromJson(jsonElement.getAsJsonObject())).collect(Collectors.toList());
		} else {
			JsonObject modifiers = GsonHelper.getAsJsonObject(jsonObject, "modifiers");
			mods = Collections.singletonList(PotionMod.fromJson(modifiers));
		}
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, item, mods);
	}
	
	@Override
	public void write(FriendlyByteBuf packetByteBuf, PotionWorkshopReactingRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		packetByteBuf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.item));
		
		packetByteBuf.writeInt(recipe.modifiers.size());
		for (PotionMod mod : recipe.modifiers) {
			mod.write(packetByteBuf);
		}
	}
	
	@Override
	public PotionWorkshopReactingRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		Item item = BuiltInRegistries.ITEM.get(packetByteBuf.readResourceLocation());
		
		List<PotionMod> mods = new ArrayList<>();
		int modCount = packetByteBuf.readInt();
		for (int i = 0; i < modCount; i++) {
			mods.add(PotionMod.fromPacket(packetByteBuf));
		}
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, item, mods);
	}
	
}
