package de.dafuqs.spectrum.compat.mythic_metals;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.compat.SpectrumIntegrationPacks;
import de.dafuqs.spectrum.registries.SpectrumBiomeTags;
import de.dafuqs.spectrum.registries.SpectrumBiomes;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;

import java.util.function.Predicate;

public class MythicMetalsCompat extends SpectrumIntegrationPacks.ModIntegrationPack {
	
	@Override
	public void register() {
		addOre(BiomeSelectors.tag(SpectrumBiomeTags.DD_BIOMES), "mod_integration/mythicmetals/dd_ore_adamantite");
		addOre(BiomeSelectors.includeByKey(SpectrumBiomes.DEEP_DRIPSTONE_CAVES), "mod_integration/mythicmetals/dd_ore_aquarium");
		addOre(BiomeSelectors.tag(SpectrumBiomeTags.DD_BIOMES), "mod_integration/mythicmetals/dd_ore_calcite_kyber");
		addOre(BiomeSelectors.tag(SpectrumBiomeTags.DD_BIOMES), "mod_integration/mythicmetals/dd_ore_deepslate_runite");
		addOre(BiomeSelectors.tag(SpectrumBiomeTags.DD_BIOMES), "mod_integration/mythicmetals/dd_ore_mythril");
		addOre(BiomeSelectors.tag(SpectrumBiomeTags.DD_BIOMES), "mod_integration/mythicmetals/dd_ore_orichalcum");
		addOre(BiomeSelectors.tag(SpectrumBiomeTags.DD_BIOMES), "mod_integration/mythicmetals/dd_ore_unobtainium");
	}
	
	private void addOre(Predicate<BiomeSelectionContext> biomeSelector, String placedFeatureName) {
		BiomeModifications.addFeature(biomeSelector, GenerationStep.Decoration.UNDERGROUND_ORES, ResourceKey.create(Registries.PLACED_FEATURE, SpectrumCommon.locate(placedFeatureName)));
	}
	
	@Override
	public void registerClient() {
	
	}
	
}
