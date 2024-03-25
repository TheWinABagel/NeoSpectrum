package de.dafuqs.spectrum.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Function;

/**
 * A copy of the old SpecialRecipeSerializer, which simply ignores any meaningful recipe serialization.
 * <p>Recipes that use this serializer do not transport any data over the network, besides their ID.
 */
public class EmptyRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
	private final Function<ResourceLocation, T> factory;
	
	public EmptyRecipeSerializer(Function<ResourceLocation, T> factory) {
		this.factory = factory;
	}
	
	@Override
	public T fromJson(ResourceLocation id, JsonObject json) {
		return this.factory.apply(id);
	}
	
	@Override
	public T fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		return this.factory.apply(id);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buf, T recipe) {
	
	}
}
