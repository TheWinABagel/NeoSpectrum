package de.dafuqs.spectrum.entity;

import de.dafuqs.spectrum.entity.render.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

@Environment(EnvType.CLIENT)
public class SpectrumEntityRenderers {
	
	public static void registerClient() {
		register(SpectrumEntityTypes.FLOAT_BLOCK, FloatBlockEntityRenderer::new);
		register(SpectrumEntityTypes.SEAT, SeatEntityRenderer::new);
		register(SpectrumEntityTypes.SHOOTING_STAR, ShootingStarEntityRenderer::new);
		register(SpectrumEntityTypes.PHANTOM_FRAME, PhantomFrameEntityRenderer::new);
		register(SpectrumEntityTypes.PARAMETRIC_MINING_DEVICE_ENTITY, ThrownItemRenderer::new);
		register(SpectrumEntityTypes.GLOW_PHANTOM_FRAME, PhantomFrameEntityRenderer::new);
		register(SpectrumEntityTypes.BLOCK_FLOODER_PROJECTILE, ThrownItemRenderer::new);
		register(SpectrumEntityTypes.INK_PROJECTILE, MagicProjectileEntityRenderer::new);
		register(SpectrumEntityTypes.LAGOON_FISHING_BOBBER, LagoonFishingBobberEntityRenderer::new);
		register(SpectrumEntityTypes.MOLTEN_FISHING_BOBBER, MoltenFishingBobberEntityRenderer::new);
		register(SpectrumEntityTypes.BEDROCK_FISHING_BOBBER, BedrockFishingBobberEntityRenderer::new);
		register(SpectrumEntityTypes.FIREPROOF_ITEM, ItemEntityRenderer::new);
		register(SpectrumEntityTypes.EGG_LAYING_WOOLY_PIG, EggLayingWoolyPigEntityRenderer::new);
		register(SpectrumEntityTypes.GLASS_ARROW, GlassArrowEntityRenderer::new);
		register(SpectrumEntityTypes.MINING_PROJECTILE, MagicProjectileEntityRenderer::new);
		register(SpectrumEntityTypes.BIDENT, BidentEntityRenderer::new);
		register(SpectrumEntityTypes.BIDENT_MIRROR_IMAGE, BidentEntityRenderer::new);
		register(SpectrumEntityTypes.LIGHT_SHARD, LightShardEntityRenderer::new);
		register(SpectrumEntityTypes.LIGHT_SPEAR, LightSpearEntityRenderer::new);
		register(SpectrumEntityTypes.LIGHT_MINE, LightMineEntityRenderer::new);
		register(SpectrumEntityTypes.MONSTROSITY, MonstrosityEntityRenderer::new);
		register(SpectrumEntityTypes.PRESERVATION_TURRET, PreservationTurretEntityRenderer::new);
		register(SpectrumEntityTypes.LIZARD, LizardEntityRenderer::new);
		register(SpectrumEntityTypes.KINDLING, KindlingEntityRenderer::new);
		register(SpectrumEntityTypes.KINDLING_COUGH, KindlingCoughEntityRenderer::new);
		register(SpectrumEntityTypes.ERASER, EraserEntityRenderer::new);
		register(SpectrumEntityTypes.ITEM_PROJECTILE, ThrownItemRenderer::new);
		register(SpectrumEntityTypes.DRAGON_TALON, (context) -> new BidentEntityRenderer(context, 1.5F, false));
		register(SpectrumEntityTypes.DRAGON_TWINSWORD, (context) -> new BidentEntityRenderer(context, 2.15F, true));
	}
	
	private static <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> factory) {
		EntityRendererRegistry.register(type, factory);
	}
	
}