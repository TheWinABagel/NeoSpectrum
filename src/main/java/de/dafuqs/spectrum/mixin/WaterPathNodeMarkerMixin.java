package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.registries.SpectrumFluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * WaterPathNodeMakers are hardcoded to check for the #minecraft:water fluid tag
 * To make it pick up other fluids for pathfinding we modify that check to use a
 * custom pathfinding fluid tag which includes water, but can be extended.
 */
@Mixin(SwimNodeEvaluator.class)
public class WaterPathNodeMarkerMixin {
	
	@ModifyArg(method = "getNodeType", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
	private TagKey<Fluid> spectrum$tagBasedWaterNavigation(TagKey<Fluid> tag) {
		return SpectrumFluidTags.USES_WATER_PATHFINDING;
	}
	
}
