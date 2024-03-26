package de.dafuqs.spectrum.mixin.client.accessors;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public interface InGameHudAccessor {
    @Accessor(value = "screenWidth")
    int getWidth();

    @Accessor(value = "screenHeight")
    int getHeight();
}
