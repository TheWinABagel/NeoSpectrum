package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.entity.entity.PhantomFrameEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

public class PhantomFrameEntityRenderer<T extends ItemFrame> extends ItemFrameRenderer<PhantomFrameEntity> {

	public static final ModelResourceLocation NORMAL_FRAME_MODEL_IDENTIFIER = ModelResourceLocation.vanilla("item_frame", "map=false");
	public static final ModelResourceLocation MAP_FRAME_MODEL_IDENTIFIER = ModelResourceLocation.vanilla("item_frame", "map=true");
	public static final ModelResourceLocation GLOW_FRAME_MODEL_IDENTIFIER = ModelResourceLocation.vanilla("glow_item_frame", "map=false");
	public static final ModelResourceLocation MAP_GLOW_FRAME_MODEL_IDENTIFIER = ModelResourceLocation.vanilla("glow_item_frame", "map=true");

	private final Minecraft client = Minecraft.getInstance();
	private final ItemRenderer itemRenderer;

	public PhantomFrameEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	protected int getBlockLightLevel(PhantomFrameEntity itemFrameEntity, BlockPos blockPos) {
		return itemFrameEntity.getType() == SpectrumEntityTypes.GLOW_PHANTOM_FRAME ? Math.max(5, super.getBlockLightLevel(itemFrameEntity, blockPos)) : super.getBlockLightLevel(itemFrameEntity, blockPos);
	}

	@Override
	public void render(PhantomFrameEntity itemFrameEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
		matrixStack.pushPose();

		Direction direction = itemFrameEntity.getDirection();
		Vec3 vec3d = this.getRenderOffset(itemFrameEntity, g);
		matrixStack.translate(-vec3d.x(), -vec3d.y(), -vec3d.z());
		double d = 0.46875D;
		matrixStack.translate((double) direction.getStepX() * d, (double) direction.getStepY() * d, (double) direction.getStepZ() * d);
		matrixStack.mulPose(Axis.XP.rotationDegrees(itemFrameEntity.getXRot()));
		matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - itemFrameEntity.getYRot()));
		boolean isInvisible = itemFrameEntity.isInvisible();
		ItemStack itemStack = itemFrameEntity.getItem();
		if (!isInvisible) {
			BlockRenderDispatcher blockRenderManager = this.client.getBlockRenderer();
			ModelManager bakedModelManager = blockRenderManager.getBlockModelShaper().getModelManager();
			ModelResourceLocation modelIdentifier = this.getModelId(itemFrameEntity, itemStack);
			matrixStack.pushPose();
			matrixStack.translate(-0.5D, -0.5D, -0.5D);
			blockRenderManager.getModelRenderer().renderModel(matrixStack.last(), vertexConsumerProvider.getBuffer(Sheets.solidBlockSheet()), null, bakedModelManager.getModel(modelIdentifier), 1.0F, 1.0F, 1.0F, light, OverlayTexture.NO_OVERLAY);
			matrixStack.popPose();
		}
		
		if (!itemStack.isEmpty()) {
			boolean isRenderingMap = itemStack.is(Items.FILLED_MAP);
			if (isInvisible) {
				matrixStack.translate(0.0D, 0.0D, 0.5D);
			} else {
				matrixStack.translate(0.0D, 0.0D, 0.4375D);
			}
			
			int renderLight = itemFrameEntity.shouldRenderAtMaxLight() ? LightTexture.FULL_BRIGHT : light;

			int bakedModelManager = isRenderingMap ? itemFrameEntity.getRotation() % 4 * 2 : itemFrameEntity.getRotation();
			matrixStack.mulPose(Axis.ZP.rotationDegrees((float) bakedModelManager * 360.0F / 8.0F));
			if (isRenderingMap) {
				matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
				float scale = 0.0078125F;
				matrixStack.scale(scale, scale, scale);
				matrixStack.translate(-64.0D, -64.0D, 0.0D);
				Integer mapId = MapItem.getMapId(itemStack);
				MapItemSavedData mapState = MapItem.getSavedData(mapId, itemFrameEntity.level());
				matrixStack.translate(0.0D, 0.0D, -1.0D);
				if (mapState != null) {
					this.client.gameRenderer.getMapRenderer().render(matrixStack, vertexConsumerProvider, mapId, mapState, true, renderLight);
				}
			} else {
				float scale = 0.75F;
				matrixStack.scale(scale, scale, scale);
				this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, renderLight, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumerProvider, itemFrameEntity.level(), itemFrameEntity.getId());
			}
		}

		matrixStack.popPose();
	}

	private ModelResourceLocation getModelId(PhantomFrameEntity entity, ItemStack stack) {
		boolean bl = entity.getType() == SpectrumEntityTypes.GLOW_PHANTOM_FRAME;
		if (stack.is(Items.FILLED_MAP)) {
			return bl ? MAP_GLOW_FRAME_MODEL_IDENTIFIER : MAP_FRAME_MODEL_IDENTIFIER;
		} else {
			return bl ? GLOW_FRAME_MODEL_IDENTIFIER : NORMAL_FRAME_MODEL_IDENTIFIER;
		}
	}


}