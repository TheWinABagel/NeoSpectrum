package de.dafuqs.spectrum.blocks.crystallarieum;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
public class CrystallarieumBlockEntityRenderer<T extends CrystallarieumBlockEntity> implements BlockEntityRenderer<T> {

	public CrystallarieumBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {

	}
	
	@Override
	public void render(CrystallarieumBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		Minecraft client = Minecraft.getInstance();
		ItemStack inkStorageStack = entity.getItem(CrystallarieumBlockEntity.INK_STORAGE_STACK_SLOT_ID);
		if(!inkStorageStack.isEmpty()) {
			matrices.pushPose();
			
			float time = entity.getLevel().getGameTime() % 50000 + tickDelta;
			double height = 1 + Math.sin((time) / 8.0) / 6.0; // item height
			
			matrices.translate(0.5, 1.0 + height, 0.5);
			matrices.mulPose(client.getBlockEntityRenderDispatcher().camera.rotation());
			matrices.mulPose(Axis.YP.rotationDegrees(180.0F));
			Minecraft.getInstance().getItemRenderer().renderStatic(inkStorageStack, ItemDisplayContext.GROUND, light, overlay, matrices, vertexConsumers, entity.getLevel(), 0);
			matrices.popPose();
		}
		
		ItemStack catalystStack = entity.getItem(CrystallarieumBlockEntity.CATALYST_SLOT_ID);
		if (!catalystStack.isEmpty()) {
			matrices.pushPose();
			
			int count = catalystStack.getCount();
			if (count > 0) {
				matrices.translate(0.65, 0.95, 0.65);
				matrices.mulPose(Axis.XP.rotationDegrees(270));
				matrices.mulPose(Axis.YP.rotationDegrees(180));
				matrices.mulPose(Axis.ZP.rotationDegrees(70));
				Minecraft.getInstance().getItemRenderer().renderStatic(catalystStack, ItemDisplayContext.GROUND, light, overlay, matrices, vertexConsumers, entity.getLevel(), 0);
				
				if (count > 4) {
					matrices.translate(0.45, 0.0, 0.01);
					matrices.mulPose(Axis.ZP.rotationDegrees(140));
					Minecraft.getInstance().getItemRenderer().renderStatic(catalystStack, ItemDisplayContext.GROUND, light, overlay, matrices, vertexConsumers, entity.getLevel(), 0);
					
					if (count > 16) {
						matrices.translate(0.2, 0.5, 0.01);
						matrices.mulPose(Axis.ZP.rotationDegrees(100));
						Minecraft.getInstance().getItemRenderer().renderStatic(catalystStack, ItemDisplayContext.GROUND, light, overlay, matrices, vertexConsumers, entity.getLevel(), 0);
						
						if (count > 32) {
							matrices.translate(-0.55, 0.0, 0.01);
							matrices.mulPose(Axis.ZP.rotationDegrees(40));
							Minecraft.getInstance().getItemRenderer().renderStatic(catalystStack, ItemDisplayContext.GROUND, light, overlay, matrices, vertexConsumers, entity.getLevel(), 0);
							
							if (count > 48) {
								matrices.translate(0.6, 0.0, 0.01);
								matrices.mulPose(Axis.ZP.rotationDegrees(170));
								Minecraft.getInstance().getItemRenderer().renderStatic(catalystStack, ItemDisplayContext.GROUND, light, overlay, matrices, vertexConsumers, entity.getLevel(), 0);
							}
						}
					}
				}
			}
			
			matrices.popPose();
		}
	}
	
	@Override
	public int getViewDistance() {
		return 16;
	}
	
}
