package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class SpectrumBiomeTags {
	
	public static final TagKey<Biome> COLORED_TREES_GENERATING_IN = getReference("colored_trees_generating_in");
	public static final TagKey<Biome> DD_BIOMES = getReference("in_deeper_down");
	
	private static TagKey<Biome> getReference(String id) {
		return TagKey.create(Registries.BIOME, SpectrumCommon.locate(id));
	}
	
}
