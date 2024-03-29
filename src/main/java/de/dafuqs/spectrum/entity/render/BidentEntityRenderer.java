package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.dafuqs.spectrum.entity.entity.BidentBaseEntity;
import de.dafuqs.spectrum.registries.client.SpectrumModelPredicateProviders;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
public class BidentEntityRenderer extends EntityRenderer<BidentBaseEntity> {
	
	private final ItemRenderer itemRenderer;
	private final float scale;
	private final boolean center;
	
	public BidentEntityRenderer(EntityRendererProvider.Context context) {
		this(context, 2F, false);
	}

	public BidentEntityRenderer(EntityRendererProvider.Context context, float scale, boolean center) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
		this.scale = scale;
		this.center = center;
	}
	
	@Override
	public void render(BidentBaseEntity bidentBaseEntity, float yaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
		ItemStack itemStack = bidentBaseEntity.getTrackedStack();
		renderAsItemStack(bidentBaseEntity, tickDelta, matrixStack, vertexConsumerProvider, light, itemStack);
		super.render(bidentBaseEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
	}
	
	private void renderAsItemStack(BidentBaseEntity entity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, ItemStack itemStack) {
		SpectrumModelPredicateProviders.currentItemRenderMode = ItemDisplayContext.NONE;
		BakedModel bakedModel = this.itemRenderer.getModel(itemStack, entity.level(), null, entity.getId());
		
		matrixStack.pushPose();
		matrixStack.translate(0, entity.makeBoundingBox().getSize() / 2, 0);
		matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(tickDelta, entity.yRotO, entity.getYRot()) - 90.0F));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(-135 + Mth.lerp(tickDelta, entity.xRotO, entity.getXRot()) + 90.0F));

		matrixStack.scale(scale, scale, scale);

		this.itemRenderer.render(itemStack, ItemDisplayContext.NONE, false, matrixStack, vertexConsumerProvider, light, OverlayTexture.NO_OVERLAY, bakedModel);

		matrixStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(BidentBaseEntity entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}

}
