package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({StemBlock.class, CropBlock.class})
public abstract class CropAndStemBlockMixin {
	
	@Inject(method = "isBonemealSuccess", at = @At("HEAD"), cancellable = true)
	private void spectrum$markUnableToGrow(Level world, RandomSource random, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (world.getBlockState(pos.below()).is(SpectrumBlocks.TILLED_SHALE_CLAY)) {
			cir.setReturnValue(false);
		}
	}
	
	@Inject(method = "performBonemeal", at = @At("HEAD"), cancellable = true)
	public void spectrum$preventGrowthOnShaleClay(ServerLevel world, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (world.getBlockState(pos.below()).is(SpectrumBlocks.TILLED_SHALE_CLAY)) {
			ci.cancel();
		}
	}
	
	@Inject(method = "isValidBonemealTarget", at = @At("HEAD"), cancellable = true)
	public void spectrum$isFertilizable(LevelReader world, BlockPos pos, BlockState state, boolean isClient, CallbackInfoReturnable<Boolean> cir) {
		if (world.getBlockState(pos.below()).is(SpectrumBlocks.TILLED_SHALE_CLAY)) {
			cir.cancel();
		}
	}
	
	@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
	public void spectrum$isFertilizable(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
		if (world.getBlockState(pos.below()).is(SpectrumBlocks.TILLED_SHALE_CLAY)) {
			ci.cancel();
		}
	}

}
