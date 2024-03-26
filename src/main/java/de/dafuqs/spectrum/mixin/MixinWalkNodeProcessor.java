package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WalkNodeEvaluator.class)
public class MixinWalkNodeProcessor {
	
	@Inject(method = "isBurningBlock", at = @At("HEAD"), cancellable = true)
	private static void spectrum$burningBlockPathfinding(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (state.is(SpectrumBlockTags.FIRE_LAND_NODE_MARKERS)) {
			cir.setReturnValue(true);
		}
	}
	
	@Inject(method = "getBlockPathTypeRaw", at = @At("RETURN"), cancellable = true)
	private static void spectrum$addBlockNodeTypes(BlockGetter world, BlockPos pos, CallbackInfoReturnable<BlockPathTypes> cir) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.is(SpectrumBlockTags.DAMAGING_LAND_NODE_MARKERS)) {
			cir.setReturnValue(BlockPathTypes.DAMAGE_OTHER);
		}
	}
	
}