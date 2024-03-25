package de.dafuqs.spectrum.mixin.compat.connectormod.present;


import de.dafuqs.spectrum.api.block.ExplosionAware;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow
    @Final
    private Level world;

    @Unique
    private BlockState blockState;
    @Unique
    private BlockPos blockPos;


    @ModifyVariable(method = "affectWorld(Z)V", at = @At(value = "STORE"), ordinal = 0)
    public BlockState snagBlockState(BlockState state){
        return blockState = state;
    }

    @ModifyVariable(method = "affectWorld(Z)V", at = @At(value = "STORE"), ordinal = 0)
    public BlockPos snagBlockPos(BlockPos pos){
        return blockPos = pos;
    }

    @Inject(method = "affectWorld(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", shift = At.Shift.BEFORE))
    public void applyExplosionEffects(boolean particles, CallbackInfo ci){
        if(blockState.getBlock() instanceof ExplosionAware explosionAware) {
            explosionAware.beforeDestroyedByExplosion(world, blockPos, blockState, (Explosion) (Object) this);
            this.world.setBlock(blockPos, explosionAware.getStateForExplosion(this.world, blockPos, blockState), 3);
        }
    }


}
