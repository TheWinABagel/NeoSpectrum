package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.dafuqs.spectrum.entity.entity.ShootingStarEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class ShootingStarEntityRenderer extends EntityRenderer<ShootingStarEntity> {
	
	public ShootingStarEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.15F;
		this.shadowStrength = 0.75F;
	}
	
	@Override
	public void render(ShootingStarEntity shootingStarEntity, float yaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
		BlockState blockState = shootingStarEntity.getShootingStarType().getBlock().defaultBlockState();
		
		if (blockState.getRenderShape() == RenderShape.MODEL) {
			Level world = shootingStarEntity.level();
			
			if (blockState != world.getBlockState(BlockPos.containing(shootingStarEntity.position())) && blockState.getRenderShape() != RenderShape.INVISIBLE) {
				matrixStack.pushPose();
				
				BlockPos blockpos = BlockPos.containing(shootingStarEntity.getX(), shootingStarEntity.getBoundingBox().maxY, shootingStarEntity.getZ());
				matrixStack.translate(-0.5, 0.0, -0.5);
				BlockRenderDispatcher blockRenderManager = Minecraft.getInstance().getBlockRenderer();
				blockRenderManager.getModelRenderer().tesselateBlock(world, blockRenderManager.getBlockModel(blockState), blockState, blockpos, matrixStack, vertexConsumerProvider.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(blockState)), false, world.random, blockState.getSeed(shootingStarEntity.blockPosition()), OverlayTexture.NO_OVERLAY);
				matrixStack.popPose();
				super.render(shootingStarEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
			}
		}
		
		super.render(shootingStarEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
	}
	
	@Override
	public ResourceLocation getTexture(ShootingStarEntity entityIn) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
