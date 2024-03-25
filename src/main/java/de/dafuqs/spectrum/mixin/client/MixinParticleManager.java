package de.dafuqs.spectrum.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.dafuqs.spectrum.particle.render.EarlyRenderingParticleContainer;
import de.dafuqs.spectrum.particle.render.ExtendedParticleManager;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ParticleEngine.class)
public class MixinParticleManager implements ExtendedParticleManager {

    @Unique
    private final EarlyRenderingParticleContainer spectrum$earlyRenderingParticleContainer = new EarlyRenderingParticleContainer();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void earlyRenderingHook(final CallbackInfo ci, final Particle particle) {
        spectrum$earlyRenderingParticleContainer.add(particle);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void removeDeadHook(final CallbackInfo ci) {
        spectrum$earlyRenderingParticleContainer.removeDead();
    }

    @Override
    public void render(final PoseStack matrices, final MultiBufferSource vertexConsumers, final Camera camera, final float tickDelta) {
        spectrum$earlyRenderingParticleContainer.render(matrices, vertexConsumers, camera, tickDelta);
    }

}