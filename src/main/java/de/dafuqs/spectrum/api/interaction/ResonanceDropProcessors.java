package de.dafuqs.spectrum.api.interaction;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ResonanceDropProcessors {

	protected static Map<ResourceLocation, ResonanceDropProcessor.Serializer> PROCESSORS = new Object2ObjectOpenHashMap<>();

	public static void register(ResourceLocation id, ResonanceDropProcessor.Serializer target) {
		PROCESSORS.put(id, target);
	}

	public static @Nullable ResonanceDropProcessor.Serializer get(ResourceLocation id) {
		return PROCESSORS.getOrDefault(id, null);
	}
	
}
