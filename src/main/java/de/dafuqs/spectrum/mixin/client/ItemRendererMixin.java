package de.dafuqs.spectrum.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.dafuqs.spectrum.api.render.DynamicItemRenderer;
import de.dafuqs.spectrum.api.render.DynamicRenderModel;
import de.dafuqs.spectrum.registries.client.SpectrumModelPredicateProviders;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	
	@Inject(at = @At("HEAD"), method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V")
	private void spectrum$storeItemRenderMode1(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, Level world, int light, int overlay, int seed, CallbackInfo ci) {
		SpectrumModelPredicateProviders.currentItemRenderMode = renderMode;
	}
	
	@Inject(at = @At("HEAD"), method = "render")
	private void spectrum$storeItemRenderMode2(ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
		SpectrumModelPredicateProviders.currentItemRenderMode = renderMode;
	}

	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	private void spectrum$dynRender(ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
		// if model is a dynamic one, check the renderers
		if (model instanceof DynamicRenderModel dm) {
			DynamicItemRenderer renderer = DynamicItemRenderer.RENDERERS.get(stack.getItem());
			// shouldn't happen normally but check anyway
			if (renderer != null) {
				// unwrap the model here so that the custom renderer doesn't have to do it
				renderer.render((ItemRenderer) (Object) this, stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, dm.getWrappedModel());
				ci.cancel();
			}
		}
	}

	// workaround for REIs batched item render mode
	@Inject(at = @At("HEAD"), method = "renderQuadList")
	private void spectrum$storeItemRenderMode3(PoseStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay, CallbackInfo ci) {
		SpectrumModelPredicateProviders.currentItemRenderMode = ItemDisplayContext.GUI;
	}
	
}
