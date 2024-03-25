package de.dafuqs.spectrum.api.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface GatedRecipeSerializer<T extends Recipe<?>> extends RecipeSerializer<T> {
	
	default String readGroup(JsonObject jsonObject) {
		return GsonHelper.getAsString(jsonObject, "group", "");
	}
	
	default boolean readSecret(JsonObject jsonObject) {
		return GsonHelper.getAsBoolean(jsonObject, "secret", false);
	}
	
	default ResourceLocation readRequiredAdvancementIdentifier(JsonObject jsonObject) {
		if (GsonHelper.isStringValue(jsonObject, "required_advancement")) {
			return new ResourceLocation(GsonHelper.getAsString(jsonObject, "required_advancement"));
		}
		return null;
	}

	// NOTE: All 4 of these methods could be static, as they are not overridden, nor does it make sense to override them.
	default void writeNullableIdentifier(FriendlyByteBuf buf, @Nullable ResourceLocation identifier) {
		if (identifier == null) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeResourceLocation(identifier);
		}
	}
	
	default @Nullable ResourceLocation readNullableIdentifier(FriendlyByteBuf buf) {
		boolean notNull = buf.readBoolean();
		if (notNull) {
			return buf.readResourceLocation();
		}
		return null;
	}

	default @NotNull FluidIngredient readFluidIngredient(FriendlyByteBuf buf) {
		boolean isTag = buf.readBoolean();
		ResourceLocation id = readNullableIdentifier(buf);
		return FluidIngredient.fromIdentifier(id, isTag);
	}

	default void writeFluidIngredient(FriendlyByteBuf buf, @NotNull FluidIngredient ingredient) {
		Objects.requireNonNull(ingredient);
		buf.writeBoolean(ingredient.isTag());
		writeNullableIdentifier(buf, ingredient.id());
	}

}
