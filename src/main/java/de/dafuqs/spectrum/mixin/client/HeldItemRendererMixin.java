package de.dafuqs.spectrum.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin {

    @Shadow
    private ItemStack offHand;

    @Shadow
    private void renderMapInOneHand(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float equipProgress, HumanoidArm arm, float swingProgress, ItemStack stack) { }

    @Shadow
    private void renderMapInBothHands(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float pitch, float equipProgress, float swingProgress) { }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 1), cancellable = true)
    private void spectrum$renderFirstPersonItem(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (item.is(SpectrumItems.ARTISANS_ATLAS)) {
            boolean isInMainHand = hand == InteractionHand.MAIN_HAND;
            if (isInMainHand && this.offHand.isEmpty()) {
                this.renderMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
            } else {
                HumanoidArm arm = isInMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
                this.renderMapInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
            }
            matrices.popPose();
            ci.cancel();
        }
    }

}
