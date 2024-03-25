package de.dafuqs.spectrum.mixin;

import com.google.common.collect.ImmutableList;
import de.dafuqs.spectrum.deeper_down.DDOreVeinSampler;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.NoiseChunk.BlockStateFiller;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NoiseChunk.class)
public abstract class ChunkNoiseSamplerMixin {
	
	@Inject(method = "<init>(ILnet/minecraft/world/gen/noise/NoiseConfig;IILnet/minecraft/world/gen/chunk/GenerationShapeConfig;Lnet/minecraft/world/gen/densityfunction/DensityFunctionTypes$Beardifying;Lnet/minecraft/world/gen/chunk/ChunkGeneratorSettings;Lnet/minecraft/world/gen/chunk/AquiferSampler$FluidLevelSampler;Lnet/minecraft/world/gen/chunk/Blender;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/ChunkGeneratorSettings;oreVeins()Z"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void spectrum$init(int horizontalCellCount, RandomState noiseConfig, int startX, int startZ, NoiseSettings generationShapeConfig, DensityFunctions.BeardifierOrMarker beardifying, NoiseGeneratorSettings chunkGeneratorSettings, Aquifer.FluidPicker fluidLevelSampler, Blender blender, CallbackInfo ci, NoiseRouter noiseRouter, NoiseRouter noiseRouter2, ImmutableList.Builder<BlockStateFiller> builder, DensityFunction densityFunction) {
		if (chunkGeneratorSettings.defaultBlock() == SpectrumBlocks.BLACKSLAG.defaultBlockState()) {
			builder.add(DDOreVeinSampler.create(noiseRouter.veinToggle(), noiseRouter.veinRidged(), noiseRouter.veinGap(), noiseConfig.oreRandom()));
		}
	}
	
}
