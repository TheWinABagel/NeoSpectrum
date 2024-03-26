package de.dafuqs.spectrum.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import de.dafuqs.spectrum.api.render.ExtendedItemBars;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphics.class)
public abstract class DrawContextMixin {

    @Shadow public abstract void fill(RenderType layer, int x1, int y1, int x2, int y2, int color);

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "FIELD", target = "net/minecraft/client/gui/GuiGraphics.minecraft : Lnet/minecraft/client/Minecraft;", ordinal = 0, shift = At.Shift.BEFORE))
    protected void spectrum$appendBars(Font textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        if (!(stack.getItem() instanceof ExtendedItemBars extendedItemBars)) {
            return;
        }

        for (int i = 0; i < extendedItemBars.barCount(stack); i++) {
            var signature =  extendedItemBars.getSignature(Minecraft.getInstance().player, stack, i);

            if (signature == ExtendedItemBars.PASS)
                continue;

            int k = x + signature.xPos();
            int l = y + signature.yPos();
            this.fill(RenderType.guiOverlay(), k, l, k + signature.length(), l + signature.backgroundHeight(), signature.backgroundColor());
            this.fill(RenderType.guiOverlay(), k, l, k + signature.fill(), l + signature.fillHeight(), signature.fillColor());
        }
    }

    @WrapWithCondition(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.fill (Lnet/minecraft/client/renderer/RenderType;IIIII)V", ordinal = 0))
    protected boolean spectrum$disableVanillaBackground(GuiGraphics instance, RenderType layer, int x1, int y1, int x2, int y2, int color, @Local(argsOnly = true) ItemStack stack) {
        if (stack.getItem() instanceof ExtendedItemBars extendedItemBars) {
            return extendedItemBars.allowVanillaDurabilityBarRendering(Minecraft.getInstance().player, stack);
        }
        return true;
    }

    @WrapWithCondition(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.fill (Lnet/minecraft/client/renderer/RenderType;IIIII)V", ordinal = 1))
    protected boolean spectrum$disableVanillaBar(GuiGraphics instance, RenderType layer, int x1, int y1, int x2, int y2, int color, @Local(argsOnly = true) ItemStack stack) {
        if (stack.getItem() instanceof ExtendedItemBars extendedItemBars) {
            return extendedItemBars.allowVanillaDurabilityBarRendering(Minecraft.getInstance().player, stack);
        }
        return true;
    }
}
