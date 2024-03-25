package de.dafuqs.spectrum.api.predicate.block;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Since BlockPredicate requires world and pos as input we can not use that in BrokenBlockCriterion
 * When the predicate would be checked the block would already be broken, unable to be tested
 * here we require a block state, that can be checked against.
 * Since block entities are already destroyed at this stage the only things that can be checked is
 * block, state and block tag. Should suffice for 99 % of cases
 */
public class BrokenBlockPredicate {
	
	public static final BrokenBlockPredicate ANY = new BrokenBlockPredicate(null, null, StatePropertiesPredicate.ANY);
	
	@Nullable
	private final TagKey<Block> tag;
	@Nullable
	private final Set<Block> blocks;
	private final StatePropertiesPredicate state;
	
	public BrokenBlockPredicate(@Nullable TagKey<Block> tag, @Nullable Set<Block> blocks, StatePropertiesPredicate state) {
		this.tag = tag;
		this.blocks = blocks;
		this.state = state;
	}
	
	public static BrokenBlockPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "block");
			Set<Block> set = null;
			JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "blocks", null);
			if (jsonArray != null) {
				com.google.common.collect.ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
				
				for (JsonElement jsonElement : jsonArray) {
					ResourceLocation identifier = new ResourceLocation(GsonHelper.convertToString(jsonElement, "block"));
					builder.add(BuiltInRegistries.BLOCK.getOptional(identifier).orElseThrow(() ->
						new JsonSyntaxException("Unknown block id '" + identifier + "'")
					));
				}
				
				set = builder.build();
			}
			
			TagKey<Block> tag = null;
			if (jsonObject.has("tag")) {
				ResourceLocation identifier2 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "tag"));
				tag = TagKey.create(Registries.BLOCK, identifier2);
			}
			
			StatePropertiesPredicate statePredicate = StatePropertiesPredicate.fromJson(jsonObject.get("state"));
			return new BrokenBlockPredicate(tag, set, statePredicate);
		} else {
			return ANY;
		}
	}
	
	public boolean test(BlockState blockState) {
		if (this == ANY) {
			return true;
		} else {
			if (this.tag != null && !blockState.is(this.tag)) {
				return false;
			} else if (this.blocks != null && !this.blocks.contains(blockState.getBlock())) {
				return false;
			} else {
				return this.state.matches(blockState);
			}
		}
	}
	
	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.blocks != null) {
				JsonArray jsonArray = new JsonArray();
				
				for (Block block : this.blocks) {
					jsonArray.add(BuiltInRegistries.BLOCK.getKey(block).toString());
				}
				
				jsonObject.add("blocks", jsonArray);
			}
			
			if (this.tag != null) {
				jsonObject.addProperty("tag", this.tag.location().toString());
			}
			
			jsonObject.add("state", this.state.serializeToJson());
			return jsonObject;
		}
	}
	
	public static class Builder {
		private @Nullable Set<Block> blocks;
		private @Nullable TagKey<Block> tag;
		private StatePropertiesPredicate state;
		
		private Builder() {
			this.state = StatePropertiesPredicate.ANY;
		}
		
		public static BrokenBlockPredicate.Builder create() {
			return new BrokenBlockPredicate.Builder();
		}
		
		public BrokenBlockPredicate.Builder blocks(Block... blocks) {
			this.blocks = ImmutableSet.copyOf(blocks);
			return this;
		}
		
		public BrokenBlockPredicate.Builder blocks(Iterable<Block> blocks) {
			this.blocks = ImmutableSet.copyOf(blocks);
			return this;
		}
		
		public BrokenBlockPredicate.Builder tag(TagKey<Block> tag) {
			this.tag = tag;
			return this;
		}
		
		public BrokenBlockPredicate.Builder state(StatePropertiesPredicate state) {
			this.state = state;
			return this;
		}
		
		public BrokenBlockPredicate build() {
			return new BrokenBlockPredicate(this.tag, this.blocks, this.state);
		}
	}
}
