package de.dafuqs.spectrum.mixin.client.accessors;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@OnlyIn(Dist.CLIENT)
@Mixin(Gui.class)
public interface InGameHudAccessor {
    @Accessor(value = "screenWidth")
    int getWidth();

    @Accessor(value = "screenHeight")
    int getHeight();
}
