package de.dafuqs.spectrum.mixin.compat.connectormod.absent;

import de.dafuqs.spectrum.api.entity.TouchingWaterAware;
import de.dafuqs.spectrum.blocks.fluid.SpectrumFluid;
import de.dafuqs.spectrum.registries.SpectrumFluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public class EntityApplyFluidsMixinNoSinytra {

    // 25.12.2023: Lithium's mixin cancel makes this code not run, making fluids not swimmable
    // https://github.com/CaffeineMC/lithium-fabric/blob/300f430d7b8618ac3b0862892b36696dcfab5a85/src/main/java/me/jellysquid/mods/lithium/mixin/entity/collisions/fluid/EntityMixin.java#L46
    // we therefore disable that mixin in our fabric.mod.json like documented in
    // https://github.com/CaffeineMC/lithium-fabric/wiki/Disabling-Lithium's-Mixins-using-your-mod's-fabric-mod.json
    @Redirect(method = "updateFluidHeightAndDoFluidPushing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    public boolean spectrum$updateMovementInFluid(FluidState fluidState, TagKey<Fluid> tag) {
        if (tag == FluidTags.WATER) {
            return (fluidState.is(FluidTags.WATER) || fluidState.is(SpectrumFluidTags.SWIMMABLE_FLUID));
        } else {
            return fluidState.is(tag);
        }
    }

    @ModifyArg(method = "doWaterSplashEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), index = 0)
    private ParticleOptions spectrum$modifySwimmingStartParticles(ParticleOptions particleEffect) {
        Fluid fluid = ((Entity) (Object) this).level().getFluidState(((Entity) (Object) this).blockPosition()).getType();
        if (fluid instanceof SpectrumFluid spectrumFluid) {
            return spectrumFluid.getSplashParticle();
        }
        return particleEffect;
    }

    @Inject(method = "updateFluidHeightAndDoFluidPushing", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(DD)D"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void spectrum$updateMovementInFluid(TagKey<Fluid> tag, double speed, CallbackInfoReturnable<Boolean> info, AABB box, int i, int j, int k, int l, int m, int n, double d, boolean bl, boolean bl2, Vec3 vec3d, int o, BlockPos.MutableBlockPos mutable, int p, int q, int r, FluidState fluidState) {
        ((TouchingWaterAware) this).spectrum$setActuallyTouchingWater(fluidState.is(FluidTags.WATER));
    }
}
