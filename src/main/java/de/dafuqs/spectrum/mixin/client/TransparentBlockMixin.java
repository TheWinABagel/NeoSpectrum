package de.dafuqs.spectrum.mixin.client;

import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(HalfTransparentBlock.class)
public abstract class TransparentBlockMixin extends Block {

	public TransparentBlockMixin(Properties settings) {
		super(settings);
	}

	@Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
	public void dontRenderVanillaPlayerOnlyGlass(BlockState state, BlockState stateFrom, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		Block thisBlock = (Block) (Object) this;
		if (thisBlock == Blocks.GLASS && stateFrom.getBlock() == SpectrumBlocks.SEMI_PERMEABLE_GLASS ||
				thisBlock == Blocks.TINTED_GLASS && stateFrom.getBlock() == SpectrumBlocks.TINTED_SEMI_PERMEABLE_GLASS) {
			callbackInfoReturnable.setReturnValue(true);
		}
	}
	
}