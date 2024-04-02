package de.dafuqs.spectrum.entity;

import de.dafuqs.spectrum.entity.render.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class SpectrumEntityRenderers {

	@SubscribeEvent
	public static void registerClient(EntityRenderersEvent.RegisterRenderers e) {
		e.registerEntityRenderer(SpectrumEntityTypes.FLOAT_BLOCK, FloatBlockEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.SEAT, SeatEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.SHOOTING_STAR, ShootingStarEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.PHANTOM_FRAME, PhantomFrameEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.PARAMETRIC_MINING_DEVICE_ENTITY, ThrownItemRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.GLOW_PHANTOM_FRAME, PhantomFrameEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.BLOCK_FLOODER_PROJECTILE, ThrownItemRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.INK_PROJECTILE, MagicProjectileEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.LAGOON_FISHING_BOBBER, LagoonFishingBobberEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.MOLTEN_FISHING_BOBBER, MoltenFishingBobberEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.BEDROCK_FISHING_BOBBER, BedrockFishingBobberEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.FIREPROOF_ITEM, ItemEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.EGG_LAYING_WOOLY_PIG, EggLayingWoolyPigEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.GLASS_ARROW, GlassArrowEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.MINING_PROJECTILE, MagicProjectileEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.BIDENT, BidentEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.BIDENT_MIRROR_IMAGE, BidentEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.LIGHT_SHARD, LightShardEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.LIGHT_SPEAR, LightSpearEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.LIGHT_MINE, LightMineEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.MONSTROSITY, MonstrosityEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.PRESERVATION_TURRET, PreservationTurretEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.LIZARD, LizardEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.KINDLING, KindlingEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.KINDLING_COUGH, KindlingCoughEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.ERASER, EraserEntityRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.ITEM_PROJECTILE, ThrownItemRenderer::new);
		e.registerEntityRenderer(SpectrumEntityTypes.DRAGON_TALON, (context) -> new BidentEntityRenderer(context, 1.5F, false));
		e.registerEntityRenderer(SpectrumEntityTypes.DRAGON_TWINSWORD, (context) -> new BidentEntityRenderer(context, 2.15F, true));
	}
	
}