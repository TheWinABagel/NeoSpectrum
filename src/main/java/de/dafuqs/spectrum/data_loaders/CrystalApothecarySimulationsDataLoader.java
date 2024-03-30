package de.dafuqs.spectrum.data_loaders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

public class CrystalApothecarySimulationsDataLoader extends SimpleJsonResourceReloadListener {
	
	public static final String ID = "crystal_apothecary_simulations";
	public static final CrystalApothecarySimulationsDataLoader INSTANCE = new CrystalApothecarySimulationsDataLoader();
	
	public static final HashMap<Block, SimulatedBlockGrowthEntry> COMPENSATIONS = new HashMap<>();
	
	public record SimulatedBlockGrowthEntry(Collection<Block> validNeighbors,
											int ticksForCompensationLootPerValidNeighbor, ItemStack compensatedStack) {
	}
	
	private CrystalApothecarySimulationsDataLoader() {
		super(new Gson(), ID);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller profiler) {
		COMPENSATIONS.clear();
		prepared.forEach((identifier, jsonElement) -> {
			JsonObject object = jsonElement.getAsJsonObject();
			
			String buddingBlockString = GsonHelper.getAsString(object, "budding_block");
			Block buddingBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(buddingBlockString));
			if (buddingBlock == Blocks.AIR) {
				SpectrumCommon.logError("Crystal Apothecary Simulation '" + identifier + "' has a non-existant 'budding_block' entry: '" + buddingBlockString + "'. Ignoring that one.");
				return;
			}
			
			Set<Block> validNeighbors = new HashSet<>();
			for (JsonElement entry : object.get("valid_neighbor_blocks").getAsJsonArray()) {
				ResourceLocation validNeighborBlockId = ResourceLocation.tryParse(entry.getAsString());
				Block validNeighborBlock = BuiltInRegistries.BLOCK.get(validNeighborBlockId);
				if (validNeighborBlock == Blocks.AIR && !validNeighborBlockId.equals(new ResourceLocation("air"))) {
					SpectrumCommon.logError("Crystal Apothecary Simulation '" + identifier + "' has a non-existant 'valid_neighbor_block' entry: '" + validNeighborBlockId + "'. Ignoring that one.");
				} else {
					validNeighbors.add(validNeighborBlock);
				}
			}
			int ticksForCompensationLootPerValidNeighbor = GsonHelper.getAsInt(object, "ticks_for_compensation_loot_per_valid_neighbor", 10000);
			
			ItemStack compensatedStack;
			try {
				compensatedStack = RecipeUtils.itemStackWithNbtFromJson(object.get("compensated_loot").getAsJsonObject());
			} catch (JsonSyntaxException e) {
				SpectrumCommon.logError("Crystal Apothecary Simulation '" + identifier + "' has an invalid 'compensated_loot' tag, perhaps with a non-existing item. Ignoring that one.");
				return;
			}
			
			COMPENSATIONS.put(buddingBlock, new SimulatedBlockGrowthEntry(validNeighbors, ticksForCompensationLootPerValidNeighbor, compensatedStack));
		});
	}
	
}