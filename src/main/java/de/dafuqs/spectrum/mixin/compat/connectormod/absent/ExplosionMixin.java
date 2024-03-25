package de.dafuqs.spectrum.mixin.compat.connectormod.absent;

import de.dafuqs.spectrum.api.block.ExplosionAware;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow
    @Final
    private Level world;
    @ModifyArg(method = "affectWorld(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"), index = 1)
    private BlockState spectrum$modifyExplosion(BlockPos pos, BlockState state, int flags) {
        BlockState stateAtPos = world.getBlockState(pos);
        if(stateAtPos.getBlock() instanceof ExplosionAware explosionAware) {
            explosionAware.beforeDestroyedByExplosion(world, pos, stateAtPos, (Explosion) (Object) this);
            return explosionAware.getStateForExplosion(this.world, pos, stateAtPos);
        }
        return state;
    }
}
