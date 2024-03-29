package de.dafuqs.spectrum.blocks.mob_head;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.mob_head.models.*;
import de.dafuqs.spectrum.entity.render.EggLayingWoolyPigEntityRenderer;
import de.dafuqs.spectrum.entity.render.KindlingEntityRenderer;
import de.dafuqs.spectrum.entity.render.MonstrosityEntityRenderer;
import de.dafuqs.spectrum.entity.render.PreservationTurretEntityRenderer;
import de.dafuqs.spectrum.registries.client.SpectrumModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class SpectrumSkullBlockEntityRenderer implements BlockEntityRenderer<SpectrumSkullBlockEntity> {
	
	private static Map<SkullBlock.Type, SkullModelBase> MODELS = new HashMap<>();
	
	public SpectrumSkullBlockEntityRenderer(BlockEntityRendererProvider.Context renderContext) {
		MODELS = getModels(renderContext.getModelSet());
	}
	
	public static Map<SkullBlock.Type, SkullModelBase> getModels(EntityModelSet modelLoader) {
		ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder = ImmutableMap.builder();
		
		builder.put(SkullBlock.Types.PLAYER, new SkullModel(modelLoader.bakeLayer(ModelLayers.PLAYER_HEAD)));
		
		builder.put(SpectrumSkullBlockType.EGG_LAYING_WOOLY_PIG, new EggLayingWoolyPigHeadModel(modelLoader.bakeLayer(SpectrumModelLayers.EGG_LAYING_WOOLY_PIG_HEAD)));
		builder.put(SpectrumSkullBlockType.MONSTROSITY, new MonstrosityHeadModel(modelLoader.bakeLayer(SpectrumModelLayers.MONSTROSITY_HEAD)));
		builder.put(SpectrumSkullBlockType.KINDLING, new KindlingHeadModel(modelLoader.bakeLayer(SpectrumModelLayers.KINDLING_HEAD)));
		builder.put(SpectrumSkullBlockType.ERASER, new EraserHeadModel(modelLoader.bakeLayer(SpectrumModelLayers.ERASER_HEAD)));
		builder.put(SpectrumSkullBlockType.LIZARD, new LizardHeadModel(modelLoader.bakeLayer(SpectrumModelLayers.LIZARD_HEAD)));
		builder.put(SpectrumSkullBlockType.PRESERVATION_TURRET, new GuardianTurretHeadModel(modelLoader.bakeLayer(SpectrumModelLayers.PRESERVATION_TURRET_HEAD)));
		builder.put(SpectrumSkullBlockType.WARDEN, new WardenHeadModel(modelLoader.bakeLayer(SpectrumModelLayers.WARDEN_HEAD)));
		
		return builder.build();
	}
	
	public static SkullModelBase getModel(SkullBlock.Type skullType) {
		if (MODELS.containsKey(skullType)) {
			return MODELS.get(skullType);
		} else {
			return MODELS.get(SkullBlock.Types.PLAYER);
		}
	}
	
	@Override
	public void render(SpectrumSkullBlockEntity spectrumSkullBlockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int j) {
		BlockState blockState = spectrumSkullBlockEntity.getBlockState();
		Direction direction = null;
		float yaw = 22.5F;
		if (blockState.getBlock() instanceof WallSkullBlock) {
			direction = blockState.getValue(WallSkullBlock.FACING);
			yaw *= (2 + direction.get2DDataValue()) * 4;
		} else {
			yaw *= blockState.getValue(SkullBlock.ROTATION);
		}
		SpectrumSkullBlockType skullType = spectrumSkullBlockEntity.getSkullType();
		if (skullType == null) {
			skullType = SpectrumSkullBlockType.PIG;
		}
		SkullModelBase skullBlockEntityModel = MODELS.get(skullType.getModelType());
		RenderType renderLayer = getRenderLayer(skullType);
		renderSkull(direction, yaw, 0, matrixStack, vertexConsumerProvider, light, skullBlockEntityModel, renderLayer);
	}
	
	public static void renderSkull(@Nullable Direction direction, float yaw, float animationProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, SkullModelBase model, RenderType renderLayer) {
		matrices.pushPose();
		if (direction == null) {
			matrices.translate(0.5D, 0.0D, 0.5D);
		} else {
			matrices.translate((0.5F - (float) direction.getStepX() * 0.25F), 0.25D, (0.5F - (float) direction.getStepZ() * 0.25F));
		}
		
		matrices.scale(-1.0F, -1.0F, 1.0F);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
		model.setupAnim(animationProgress, yaw, 0.0F);
		model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		matrices.popPose();
	}
	
	public static RenderType getRenderLayer(SpectrumSkullBlockType type) {
		ResourceLocation identifier = getTextureIdentifier(type);
		RenderType renderLayer = RenderType.entityCutoutNoCullZOffset(identifier);
		if (renderLayer == null) {
			return RenderType.entityCutoutNoCullZOffset(new ResourceLocation("textures/entity/zombie/zombie.png"));
		} else {
			return renderLayer;
		}
	}
	
	protected static ResourceLocation getTextureIdentifier(SpectrumSkullBlockType type) {
		switch (type) {
			case EGG_LAYING_WOOLY_PIG -> {
				return EggLayingWoolyPigEntityRenderer.TEXTURE;
			}
			case PRESERVATION_TURRET -> {
				return PreservationTurretEntityRenderer.TEXTURE;
			}
			case MONSTROSITY -> {
				return MonstrosityEntityRenderer.TEXTURE;
			}
			case LIZARD -> {
				return SpectrumCommon.locate("textures/entity/lizard/lizard_head.png");
			}
			case KINDLING -> {
				return KindlingEntityRenderer.TEXTURE;
			}
			case ERASER -> {
				return SpectrumCommon.locate("textures/entity/eraser/eraser_combined.png");
			}
			case WARDEN -> {
				return new ResourceLocation("textures/entity/warden/warden.png");
			}
			default -> {
				return SpectrumCommon.locate("textures/entity/mob_head/" + type.toString().toLowerCase(Locale.ROOT) + ".png");
			}
		}
	}
	
}