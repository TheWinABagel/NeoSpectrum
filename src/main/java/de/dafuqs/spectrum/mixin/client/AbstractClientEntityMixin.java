package de.dafuqs.spectrum.mixin.client;

import de.dafuqs.spectrum.render.capes.WorthinessChecker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@OnlyIn(Dist.CLIENT)
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientEntityMixin {
    @Inject(
            method = "getCloakTextureLocation",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getCapeTexture(CallbackInfoReturnable<ResourceLocation> cir) {
        var cape = WorthinessChecker.getCapeType(((Entity) (Object) (this)).getUUID());
        if (cape.render) {
            cir.setReturnValue(cape.capePath);
            cir.cancel();
        }
    }
}
