package de.dafuqs.spectrum.compat.create;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.compat.SpectrumIntegrationPacks;
import de.dafuqs.spectrum.registries.SpectrumBiomeTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;

public class CreateCompat extends SpectrumIntegrationPacks.ModIntegrationPack {
	
	@Override
	public void register() {
		BiomeModifications.addFeature(BiomeSelectors.tag(SpectrumBiomeTags.DD_BIOMES), GenerationStep.Decoration.UNDERGROUND_ORES, ResourceKey.create(Registries.PLACED_FEATURE, SpectrumCommon.locate("mod_integration/create/dd_zinc_ore")));
	}
	
	@Override
	public void registerClient() {
	
	}
}
