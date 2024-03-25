package de.dafuqs.spectrum.api.predicate.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class BiomePredicate {
    public static final BiomePredicate ANY = new BiomePredicate(null, null);
    @Nullable
    private final TagKey<Biome> tag;
    @Nullable
    private final Biome biome;

    public BiomePredicate(@Nullable TagKey<Biome> tag, @Nullable Biome biome) {
        this.tag = tag;
        this.biome = biome;
    }

    public boolean test(ServerLevel world, BlockPos pos) {
        if (this == ANY) {
            return true;
        }
        if (this.tag != null && world.getBiome(pos).is(this.tag)) {
            return true;
        }
        if (this.biome != null && world.getBiome(pos).value() == this.biome) {
            return true;
        }
        return false;
    }

    public static BiomePredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        
        JsonObject biomeObject = GsonHelper.convertToJsonObject(json, "biome");
        
        Biome biome = null;
        if (biomeObject.has("biome")) {
            ResourceLocation biomeId = new ResourceLocation(GsonHelper.getAsString(biomeObject, "biome"));
            biome = VanillaRegistries.createLookup().lookupOrThrow(Registries.BIOME).getOrThrow(ResourceKey.create(Registries.BIOME, biomeId)).value();
        }
        
        TagKey<Biome> tagKey = null;
        if (biomeObject.has("tag")) {
            ResourceLocation tagId = new ResourceLocation(GsonHelper.getAsString(biomeObject, "tag"));
            tagKey = TagKey.create(Registries.BIOME, tagId);
        }
        
        return new BiomePredicate(tagKey, biome);
    }
}
