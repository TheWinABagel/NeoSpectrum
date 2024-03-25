package de.dafuqs.spectrum.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record GilledFungusFeatureConfig(Block validBase, Block cap, Block gills, Block stem) implements FeatureConfiguration {
    
    public static final Codec<GilledFungusFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			BuiltInRegistries.BLOCK.byNameCodec().fieldOf("valid_base_block").forGetter((config) -> config.validBase),
			BuiltInRegistries.BLOCK.byNameCodec().fieldOf("cap_block").forGetter((config) -> config.cap),
			BuiltInRegistries.BLOCK.byNameCodec().fieldOf("gills_block").forGetter((config) -> config.gills),
			BuiltInRegistries.BLOCK.byNameCodec().fieldOf("stem_block").forGetter((config) -> config.stem)
	).apply(instance, GilledFungusFeatureConfig::new));
    
}
