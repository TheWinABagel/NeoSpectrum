package de.dafuqs.spectrum.data_loaders.resonance_processors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.dafuqs.spectrum.api.interaction.ResonanceDropProcessor;
import de.dafuqs.spectrum.api.predicate.block.BrokenBlockPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifyDropsResonanceProcessor extends ResonanceDropProcessor {
	
	public static class Serializer implements ResonanceDropProcessor.Serializer {
		
		@Override
		public ResonanceDropProcessor fromJson(JsonObject json) throws Exception {
			BrokenBlockPredicate blockTarget = BrokenBlockPredicate.fromJson(json.get("block"));
			
			Map<Ingredient, Item> modifiedDrops = new HashMap<>();
			JsonArray modifyDropsArray = GsonHelper.getAsJsonArray(json, "modify_drops");
			for (JsonElement entry : modifyDropsArray) {
				if (!(entry instanceof JsonObject entryObject)) {
					throw new JsonSyntaxException("modify_drops is not an json object");
				}
				Ingredient ingredient = Ingredient.fromJson(entryObject.get("input"));
				Item output = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(GsonHelper.getAsString(entryObject, "output")));
				modifiedDrops.put(ingredient, output);
			}
			
			return new ModifyDropsResonanceProcessor(blockTarget, modifiedDrops);
		}
		
	}
	
	public Map<Ingredient, Item> modifiedDrops;
	
	public ModifyDropsResonanceProcessor(BrokenBlockPredicate blockTarget, Map<Ingredient, Item> modifiedDrops) throws Exception {
		super(blockTarget);
		this.modifiedDrops = modifiedDrops;
	}
	
	@Override
	public boolean process(BlockState state, BlockEntity blockEntity, List<ItemStack> droppedStacks) {
		if (blockPredicate.test(state)) {
			modifyDrops(droppedStacks);
			return true;
		}
		return false;
	}
	
	private void modifyDrops(List<ItemStack> droppedStacks) {
		for (ItemStack stack : droppedStacks) {
			for (Map.Entry<Ingredient, Item> modifiedDrop : modifiedDrops.entrySet()) {
				if (modifiedDrop.getKey().test(stack)) {
					ItemStack convertedStack;
					convertedStack = modifiedDrop.getValue().getDefaultInstance();
					convertedStack.setCount(stack.getCount());
					
					droppedStacks.remove(stack);
					droppedStacks.add(convertedStack);
					break;
				}
			}
		}
	}
}
