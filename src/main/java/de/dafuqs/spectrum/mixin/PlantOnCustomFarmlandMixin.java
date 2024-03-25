package de.dafuqs.spectrum.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.dafuqs.spectrum.blocks.farming.SpectrumFarmlandBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({CropBlock.class, StemBlock.class, AttachedStemBlock.class})
public abstract class PlantOnCustomFarmlandMixin {
	
	@ModifyReturnValue(method = "canPlantOnTop(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("RETURN"))
	public boolean spectrum$canPlantOnTopOfCustomFarmland(boolean original, @NotNull BlockState floor, BlockGetter world, BlockPos pos) {
		return original || floor.getBlock() instanceof SpectrumFarmlandBlock;
	}
	
}
