package de.dafuqs.spectrum.mixin.client;

import de.dafuqs.spectrum.render.HudRenderers;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public class InGameHudMixin {

    @Inject(method = "renderStatusBars(Lnet/minecraft/client/gui/DrawContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getArmor()I"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void spectrum$renderHealthBar(GuiGraphics context, CallbackInfo ci, Player cameraPlayer, int lastHealth, boolean blinking, long timeStart, int health, FoodData hungerManager, int foodLevel, int x, int foodX, int y, float maxHealth, int absorption, int heartRows, int rowHeight, int armorY) {
        HudRenderers.renderAzureDike(context, cameraPlayer, x, armorY);
    }

}
