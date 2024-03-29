package de.dafuqs.spectrum.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlock;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlockType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@OnlyIn(Dist.CLIENT)
@Mixin(CustomHeadLayer.class)
public abstract class HeadFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> {

	@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;translateAndRotate(Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.AFTER),
			cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void spectrum$renderSkull(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, T livingEntity, float animationProgress, float h, float j, float k, float l, float m, CallbackInfo ci, ItemStack itemStack, Item item, boolean bl) {
		if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SpectrumSkullBlock spectrumSkullBlock) {
			m = 1.1875F;
			matrixStack.scale(m, -m, -m);
			if (bl) {
				matrixStack.translate(0.0D, 0.0625D, 0.0D);
			}
			
			matrixStack.translate(-0.5D, 0.0D, -0.5D);
			
			SpectrumSkullBlockType skullType = (SpectrumSkullBlockType) spectrumSkullBlock.getType();
			RenderType renderLayer = SpectrumSkullBlockEntityRenderer.getRenderLayer(skullType);
			SkullModelBase model = SpectrumSkullBlockEntityRenderer.getModel(skullType);
			SpectrumSkullBlockEntityRenderer.renderSkull(null, 180.0F, animationProgress, matrixStack, vertexConsumerProvider, light, model, renderLayer);
			matrixStack.popPose();
			ci.cancel();
		}
	}
	
}
