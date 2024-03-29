package de.dafuqs.spectrum.mixin.compat.colorful_hearts.present;

import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import terrails.colorfulhearts.heart.Heart;

@OnlyIn(Dist.CLIENT)
@Mixin(Heart.class)
public abstract class HeartMixin {

    @ModifyVariable(method = "draw", at = @At("STORE"), ordinal = 1)
    private boolean heartRendererRenderPlayerHeartsGetHealthInjector(boolean hardcore) {
        Minecraft client = Minecraft.getInstance();
        if (!hardcore && client.player != null && client.player.hasEffect(SpectrumStatusEffects.DIVINITY)) {
            return true;
        }
        return hardcore;
    }
    
}
