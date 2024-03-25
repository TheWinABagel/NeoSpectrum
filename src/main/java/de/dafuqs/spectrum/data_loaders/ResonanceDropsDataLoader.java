package de.dafuqs.spectrum.data_loaders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.interaction.ResonanceDropProcessor;
import de.dafuqs.spectrum.api.interaction.ResonanceDropProcessors;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResonanceDropsDataLoader extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
	
	public static final String ID = "resonance_drops";
	public static final ResonanceDropsDataLoader INSTANCE = new ResonanceDropsDataLoader();
	
	protected static final List<ResonanceDropProcessor> RESONANCE_DROPS = new ArrayList<>();
	
	public static boolean preventNextXPDrop;
	
	private ResonanceDropsDataLoader() {
		super(new Gson(), ID);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller profiler) {
		RESONANCE_DROPS.clear();
		prepared.forEach((identifier, jsonElement) -> {
			JsonObject json = jsonElement.getAsJsonObject();
			
			ResourceLocation processorType = ResourceLocation.tryParse(GsonHelper.getAsString(json, "type"));
			ResonanceDropProcessor.Serializer serializer = ResonanceDropProcessors.get(processorType);
			if (serializer == null) {
				SpectrumCommon.logError("Unknown ResonanceDropProcessor " + processorType + " in file " + identifier);
				return;
			}
			try {
				RESONANCE_DROPS.add(serializer.fromJson(json));
			} catch (Exception e) {
				SpectrumCommon.logError("Error parsing ResonanceDropProcessor " + identifier + ": " + e.getLocalizedMessage());
			}
		});
	}
	
	@Override
	public ResourceLocation getFabricId() {
		return SpectrumCommon.locate(ID);
	}
	
	public static void applyResonance(BlockState minedState, BlockEntity blockEntity, List<ItemStack> droppedStacks) {
		for (ResonanceDropProcessor entry : RESONANCE_DROPS) {
			if (entry.process(minedState, blockEntity, droppedStacks)) {
				return;
			}
		}
	}
	
}