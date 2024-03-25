package de.dafuqs.spectrum.data_loaders.resonance_processors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.spectrum.api.interaction.ResonanceDropProcessor;
import de.dafuqs.spectrum.api.predicate.block.BrokenBlockPredicate;
import de.dafuqs.spectrum.data_loaders.ResonanceDropsDataLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;

public class DropSelfResonanceProcessor extends ResonanceDropProcessor {
	
	public static class Serializer implements ResonanceDropProcessor.Serializer {
		
		@Override
		public ResonanceDropProcessor fromJson(JsonObject json) throws Exception {
			BrokenBlockPredicate blockTarget = BrokenBlockPredicate.fromJson(json.get("block"));
			
			List<String> statePropertiesToCopy = new ArrayList<>();
			if (json.has("state_properties_to_copy")) {
				for (JsonElement e : GsonHelper.getAsJsonArray(json, "state_properties_to_copy")) {
					statePropertiesToCopy.add(e.getAsString());
				}
			}
			
			List<String> nbtToCopy = new ArrayList<>();
			if (json.has("nbt_to_copy")) {
				for (JsonElement e : GsonHelper.getAsJsonArray(json, "nbt_to_copy")) {
					nbtToCopy.add(e.getAsString());
				}
			}
			boolean includeDefaultStateProperties = GsonHelper.getAsBoolean(json, "include_default_state_properties", false);
			
			return new DropSelfResonanceProcessor(blockTarget, nbtToCopy, statePropertiesToCopy, includeDefaultStateProperties);
		}
		
	}
	
	public List<String> nbtToCopy;
	public List<String> statePropertiesToCopy;
	public boolean includeDefaultStateProperties;
	
	public DropSelfResonanceProcessor(BrokenBlockPredicate blockTarget, List<String> nbtToCopy, List<String> statePropertiesToCopy, boolean includeDefaultStateProperties) throws Exception {
		super(blockTarget);
		this.nbtToCopy = nbtToCopy;
		this.statePropertiesToCopy = statePropertiesToCopy;
		this.includeDefaultStateProperties = includeDefaultStateProperties;
	}
	
	@Override
	public boolean process(BlockState state, BlockEntity blockEntity, List<ItemStack> droppedStacks) {
		if (blockPredicate.test(state)) {
			dropSelf(state, blockEntity, droppedStacks);
			ResonanceDropsDataLoader.preventNextXPDrop = true;
			return true;
		}
		return false;
	}
	
	public void copyBlockStateTags(BlockState minedState, ItemStack convertedStack) {
		for (Property<?> blockProperty : minedState.getProperties()) {
			if (statePropertiesToCopy.contains(blockProperty.getName())) {
				if (!includeDefaultStateProperties && minedState.getBlock().defaultBlockState().getValue(blockProperty) == minedState.getValue(blockProperty)) {
					// do not copy if the value matches the default to make it stack with others
					continue;
				}
				
				CompoundTag nbt = convertedStack.getOrCreateTagElement("BlockStateTag");
				nbt.putString(blockProperty.getName(), getPropertyName(minedState, blockProperty));
			}
		}
	}
	
	private static <T extends Comparable<T>> String getPropertyName(BlockState state, Property<T> property) {
		return property.getName(state.getValue(property));
	}
	
	public void copyNbt(BlockEntity blockEntity, ItemStack convertedStack) {
		CompoundTag newNbt = new CompoundTag();
		
		CompoundTag beNbt = blockEntity.saveWithoutMetadata();
		for (String s : nbtToCopy) {
			if (beNbt.contains(s)) {
				newNbt.put(s, beNbt.get(s));
			}
		}
		
		if (!newNbt.isEmpty()) {
			convertedStack.getOrCreateTagElement("BlockEntityTag").merge(newNbt);
		}
	}
	
	private void dropSelf(BlockState minedState, BlockEntity blockEntity, List<ItemStack> droppedStacks) {
		ItemStack selfStack = minedState.getBlock().asItem().getDefaultInstance();
		if (!statePropertiesToCopy.isEmpty()) {
			copyBlockStateTags(minedState, selfStack);
		}
		if (!nbtToCopy.isEmpty()) {
			copyNbt(blockEntity, selfStack);
		}
		
		droppedStacks.clear();
		droppedStacks.add(selfStack);
	}
	
}
