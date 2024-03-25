package de.dafuqs.spectrum.api.recipe;

import com.google.gson.JsonObject;
import de.dafuqs.matchbooks.recipe.RegistryHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class FluidIngredient {
    private final @Nullable Fluid fluid;
    private final @Nullable TagKey<Fluid> tag;
    // Compare against EMPTY to check if empty.
    // In order to represent an empty value, specifically use this field.
    public static FluidIngredient EMPTY = new FluidIngredient(null, null);

    // Can't be both fluid and tag, so ONLY use the provided of() methods
    // NOTE: ALL FluidIngredient-related code assumes that either:
    // 1. there are always EITHER the fluid OR the fluid tag, NOT both
    // 2. the object is empty AND the object is EQUAL TO FluidIngredient.EMPTY.
    // Violation of either of those results in either an AssertionError or
    // undefined behavior. As such, don't even allow creation of invalid obj.
    // FluidIngredient objects with unknown/invalid tags are considered valid.
    private FluidIngredient(@Nullable Fluid fluid, @Nullable TagKey<Fluid> tag) {
        this.fluid = fluid;
        this.tag = tag;
    }

    // NOTE: This is for testing. Doesn't explicitly handle invalid FluidInput.
    @Override
    public String toString() {
        if (this == EMPTY)
            return "FluidIngredient.EMPTY";
        if (this.fluid != null)
            return String.format("FluidIngredient[fluid=%s]", this.fluid);
        // must contain either or be FluidInput.EMPTY(as per FluidInput doc)
        assert this.tag != null;
        return String.format("FluidIngredient[tag=%s]", this.tag);
    }

    public static FluidIngredient of(@NotNull Fluid fluid) {
        Objects.requireNonNull(fluid);
        return new FluidIngredient(fluid, null);
    }

    public static FluidIngredient of(@NotNull TagKey<Fluid> tag) {
        Objects.requireNonNull(tag);
        return new FluidIngredient(null, tag);
    }

    public Optional<Fluid> fluid() {
        return Optional.ofNullable(this.fluid);
    }
    public Optional<TagKey<Fluid>> tag() {
        return Optional.ofNullable(this.tag);
    }
    public boolean isTag() {
        return this.tag != null;
    }

    public ResourceLocation id() {
        return fluid != null ? BuiltInRegistries.FLUID.getKey(fluid)
                             : tag != null ? tag.location() : null;
    }

    // Vanilla-friendly compatibility method.
    // Represents this FluidIngredient as bucket stack(s).
    public @NotNull Ingredient into() {
        if (this == EMPTY) return Ingredient.of();
        if (this.fluid != null)
            return Ingredient.of(this.fluid.getBucket()
                                                 .getDefaultInstance());
        if (this.tag != null) {
            // Handle custom fluid registries
            // in the case of FluidIngredient objects created by other mods.
            Registry<Fluid> registry = RegistryHelper.getRegistryOf(this.tag);
            if(registry == null) return Ingredient.of();
            Optional<HolderSet.Named<Fluid>> optional =
                    registry.getTag(this.tag);
            if(optional.isEmpty()) return Ingredient.of();
            HolderSet.Named<Fluid> list = optional.get();
            Stream<ItemStack> stacks = list.stream().map(
                    (entry) -> entry.value().getBucket().getDefaultInstance()
            );
            return Ingredient.of(stacks);
        }

        // UNREACHABLE under normal circumstances!
        throw new AssertionError("Invalid FluidIngredient object");
    }

    public boolean test(@NotNull Fluid fluid) {
        Objects.requireNonNull(fluid);
        if (this == EMPTY) return fluid == Fluids.EMPTY;
        if (this.fluid != null) return this.fluid == fluid;
        if (this.tag != null) return fluid.defaultFluidState().is(this.tag);

        // UNREACHABLE under normal circumstances!
        throw new AssertionError("Invalid FluidIngredient object");
    }

    @SuppressWarnings("UnstableApiUsage")
    public boolean test(@NotNull FluidVariant variant) {
        Objects.requireNonNull(variant);
        return test(variant.getFluid());
    }

    public static @NotNull FluidIngredient fromIdentifier(@Nullable ResourceLocation id, boolean isTag) {
        if (isTag) {
            Optional<TagKey<Fluid>> tag = RegistryHelper.tryGetTagKey(BuiltInRegistries.FLUID, id);
            if (tag.isEmpty()) return FluidIngredient.EMPTY;
            else return FluidIngredient.of(tag.get());
        } else {
            Fluid fluid = BuiltInRegistries.FLUID.get(id);
            if (fluid.defaultFluidState().isEmpty()) return FluidIngredient.EMPTY;
            else return FluidIngredient.of(fluid);
        }
    }

    // Interpret FluidIngredient.EMPTY as an unknown ID error.
    public record JsonParseResult(
            @NotNull FluidIngredient result,
            boolean malformed,
            boolean isTag,
            @Nullable ResourceLocation id
    ) {}

    public static @NotNull JsonParseResult fromJson(JsonObject fluidObject) {
        boolean hasFluid = GsonHelper.isStringValue(fluidObject, "fluid");
        boolean isTag = GsonHelper.isStringValue(fluidObject, "tag");

        if ((hasFluid && isTag) || !(hasFluid || isTag)) {
            return new JsonParseResult(FluidIngredient.EMPTY, true, isTag, null);
        } else {
            ResourceLocation id = ResourceLocation.tryParse(GsonHelper.getAsString(fluidObject, isTag ? "tag" : "fluid"));
            return new JsonParseResult(fromIdentifier(id, isTag), false, isTag, id);
        }
    }
}
