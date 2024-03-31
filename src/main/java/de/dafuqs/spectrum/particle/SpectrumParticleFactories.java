package de.dafuqs.spectrum.particle;

import de.dafuqs.spectrum.blocks.pastel_network.PastelRenderHelper;
import de.dafuqs.spectrum.particle.client.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// See ParticleManager for vanilla
@OnlyIn(Dist.CLIENT)
public class SpectrumParticleFactories { //todoforge what am i even doing with my life

	@SubscribeEvent
	public static void register(RegisterParticleProvidersEvent e) {
		e.registerSprite(SpectrumParticleTypes.ITEM_TRANSMISSION, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
			TransmissionParticle particle = new TransmissionParticle(world, x, y, z, parameters.getDestination(), parameters.getArrivalInTicks());
			return particle;
		});
		
		e.registerSprite(SpectrumParticleTypes.EXPERIENCE_TRANSMISSION, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
			TransmissionParticle particle = new TransmissionParticle(world, x, y, z, parameters.getDestination(), parameters.getArrivalInTicks());

			return particle;
		});
		
		e.registerSprite(SpectrumParticleTypes.WIRELESS_REDSTONE_TRANSMISSION, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
			TransmissionParticle particle = new TransmissionParticle(world, x, y, z, parameters.getDestination(), parameters.getArrivalInTicks());

			return particle;
		});
		
		e.registerSprite(SpectrumParticleTypes.COLORED_TRANSMISSION, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
            ColoredTransmissionParticle particle = new ColoredTransmissionParticle(world, x, y, z, parameters.getDestination(), parameters.getArrivalInTicks(), parameters.getDyeColor());

            return particle;
        });

        e.registerSprite(SpectrumParticleTypes.BLOCK_POS_EVENT_TRANSMISSION, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
            TransmissionParticle particle = new TransmissionParticle(world, x, y, z, parameters.getDestination(), parameters.getArrivalInTicks());

            return particle;
		});
		
		e.registerSprite(SpectrumParticleTypes.PASTEL_TRANSMISSION, (pastelTransmissionParticleEffect, world, x, y, z, velocityX, velocityY, velocityZ) -> {
			PastelTransmissionParticle particle = new PastelTransmissionParticle(Minecraft.getInstance().getItemRenderer(), world, x, y, z, pastelTransmissionParticleEffect.getNodePositions(), pastelTransmissionParticleEffect.getStack(), pastelTransmissionParticleEffect.getTravelTime());

			float[] color = PastelRenderHelper.unpackNormalizedColor(pastelTransmissionParticleEffect.getColor());
			particle.setColor(color[1], color[2], color[3]);
			return particle;
		});
		
		e.registerSprite(SpectrumParticleTypes.HUMMINGSTONE_TRANSMISSION, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
			TransmissionParticle particle = new TransmissionParticle(world, x, y, z, parameters.getDestination(), parameters.getArrivalInTicks());

			return particle;
		});
		
		e.registerSprite(SpectrumParticleTypes.MOONSTONE_STRIKE, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
			MoonstoneStrikeParticle.Factory factory = new MoonstoneStrikeParticle.Factory();
			return (TextureSheetParticle) factory.createParticle(SpectrumParticleTypes.MOONSTONE_STRIKE, world, x, y, z, velocityX, velocityY, velocityZ);
		});

		e.registerSpriteSet(SpectrumParticleTypes.PRIMORDIAL_COSY_SMOKE, LargePrimordialSmokeParticle.CosySmokeFactory::new);
		e.registerSpriteSet(SpectrumParticleTypes.PRIMORDIAL_SIGNAL_SMOKE, LargePrimordialSmokeParticle.SignalSmokeFactory::new);
		e.registerSpriteSet(SpectrumParticleTypes.PRIMORDIAL_SMOKE, PrimordialSmokeParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.PRIMORDIAL_FLAME, FlameParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.PRIMORDIAL_FLAME_SMALL, FlameParticle.SmallFlameProvider::new);

		e.registerSpriteSet(SpectrumParticleTypes.DIVINITY, HardcoreParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.SHOOTING_STAR, LitParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.SHIMMERSTONE_SPARKLE, LitParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.SHIMMERSTONE_SPARKLE_SMALL, LitParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.SHIMMERSTONE_SPARKLE_TINY, LitParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.LIQUID_CRYSTAL_SPARKLE, LitParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.DRAGONROT, BubblePopParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.VOID_FOG, VoidFogParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.MUD_POP, BubblePopParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.BLUE_BUBBLE_POP, BubblePopParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.GREEN_BUBBLE_POP, BubblePopParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.SPIRIT_SALLOW, WindParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.DECAY_PLACE, CraftingParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.JADE_VINES, ZigZagParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.JADE_VINES_BLOOM, ZigZagParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.MIRROR_IMAGE, LitParticle.Factory::new);
		
		// Runes / Dike
		e.registerSpriteSet(SpectrumParticleTypes.RUNES, LitParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.AZURE_DIKE_RUNES, LitParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.AZURE_DIKE_RUNES_MAJOR, LitParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.DRAKEBLOOD_DIKE_RUNES, LitParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.DRAKEBLOOD_DIKE_RUNES_MAJOR, LitParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.MALACHITE_DIKE_RUNES, LitParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.MALACHITE_DIKE_RUNES_MAJOR, LitParticle.Factory::new);
		
		// Fluid Splash
		e.registerSpriteSet(SpectrumParticleTypes.MUD_SPLASH, SplashParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.LIQUID_CRYSTAL_SPLASH, SplashParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.MIDNIGHT_SOLUTION_SPLASH, SplashParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.DRAGONROT_SPLASH, SplashParticle.Provider::new);
		
		// Fluid Dripping
		e.registerSpriteSet(SpectrumParticleTypes.DRIPPING_MUD, SpectrumBlockLeakParticles.DrippingMudFactory::new);
		e.registerSpriteSet(SpectrumParticleTypes.DRIPPING_LIQUID_CRYSTAL, SpectrumBlockLeakParticles.DrippingLiquidCrystalFactory::new);
		e.registerSpriteSet(SpectrumParticleTypes.DRIPPING_MIDNIGHT_SOLUTION, SpectrumBlockLeakParticles.DrippingMidnightSolutionFactory::new);
		e.registerSpriteSet(SpectrumParticleTypes.DRIPPING_DRAGONROT, SpectrumBlockLeakParticles.DrippingDragonrotFactory::new);

		// Fluid Falling
		e.registerSpriteSet(SpectrumParticleTypes.FALLING_MUD, SpectrumBlockLeakParticles.FallingMudFactory::new);
		e.registerSpriteSet(SpectrumParticleTypes.FALLING_LIQUID_CRYSTAL, SpectrumBlockLeakParticles.FallingLiquidCrystalFactory::new);
		e.registerSpriteSet(SpectrumParticleTypes.FALLING_MIDNIGHT_SOLUTION, SpectrumBlockLeakParticles.FallingMidnightSolutionFactory::new);
		e.registerSpriteSet(SpectrumParticleTypes.FALLING_DRAGONROT, SpectrumBlockLeakParticles.FallingDragonrotFactory::new);

		// Fluid Landing
		e.registerSpriteSet(SpectrumParticleTypes.LANDING_MUD, SpectrumBlockLeakParticles.LandingMudFactory::new);
		e.registerSpriteSet(SpectrumParticleTypes.LANDING_LIQUID_CRYSTAL, SpectrumBlockLeakParticles.LandingLiquidCrystalFactory::new);
		e.registerSpriteSet(SpectrumParticleTypes.LANDING_MIDNIGHT_SOLUTION, SpectrumBlockLeakParticles.LandingMidnightSolutionFactory::new);
		e.registerSpriteSet(SpectrumParticleTypes.LANDING_DRAGONROT, SpectrumBlockLeakParticles.LandingDragonrotFactory::new);

		// Fluid Fishing
		e.registerSpriteSet(SpectrumParticleTypes.LAVA_FISHING, WakeParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.MUD_FISHING, WakeParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.LIQUID_CRYSTAL_FISHING, WakeParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.MIDNIGHT_SOLUTION_FISHING, WakeParticle.Provider::new);
		e.registerSpriteSet(SpectrumParticleTypes.DRAGONROT_FISHING, WakeParticle.Provider::new);

		// Used for the colored spore blossoms
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.BLACK_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.BLACK_SPORE_BLOSSOM_AIR, 0.1F, 0.1F, 0.1F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.BLUE_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.BLUE_SPORE_BLOSSOM_AIR, 0.05F, 0.011F, 0.95F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.BROWN_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.BROWN_SPORE_BLOSSOM_AIR, 0.31F, 0.16F, 0.05F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.CYAN_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.CYAN_SPORE_BLOSSOM_AIR, 0.0F, 1.0F, 1.0F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.GRAY_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.GRAY_SPORE_BLOSSOM_AIR, 0.3F, 0.3F, 0.3F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.GREEN_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.GREEN_SPORE_BLOSSOM_AIR, 0.14F, 0.24F, 0.0F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.LIGHT_BLUE_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.LIGHT_BLUE_SPORE_BLOSSOM_AIR, 0.0F, 0.75F, 0.95F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.LIGHT_GRAY_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.LIGHT_GRAY_SPORE_BLOSSOM_AIR, 0.68F, 0.68F, 0.68F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.LIME_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.LIME_SPORE_BLOSSOM_AIR, 0.0F, 0.86F, 0.0F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.MAGENTA_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.MAGENTA_SPORE_BLOSSOM_AIR, 1.0F, 0.0F, 1.0F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.ORANGE_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.ORANGE_SPORE_BLOSSOM_AIR, 0.93F, 0.39F, 0.0F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.PINK_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.PINK_SPORE_BLOSSOM_AIR, 1.0F, 0.78F, 0.87F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.PURPLE_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.PURPLE_SPORE_BLOSSOM_AIR, 0.43F, 0.0F, 0.68F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.RED_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.RED_SPORE_BLOSSOM_AIR, 0.95F, 0.0F, 0.0F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.WHITE_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.WHITE_SPORE_BLOSSOM_AIR, 0.97F, 0.97F, 0.97F, e);
		registerColoredSporeBlossomParticles(SpectrumParticleTypes.YELLOW_FALLING_SPORE_BLOSSOM, SpectrumParticleTypes.YELLOW_SPORE_BLOSSOM_AIR, 0.93F, 0.93F, 0.0F, e);
		
		registerColoredCraftingParticle(SpectrumParticleTypes.BLACK_CRAFTING, 0.1F, 0.1F, 0.1F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.BLUE_CRAFTING, 0.05F, 0.011F, 0.95F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.BROWN_CRAFTING, 0.31F, 0.16F, 0.05F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.CYAN_CRAFTING, 0.0F, 1.0F, 1.0F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.GRAY_CRAFTING, 0.3F, 0.3F, 0.3F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.GREEN_CRAFTING, 0.14F, 0.24F, 0.0F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.LIGHT_BLUE_CRAFTING, 0.0F, 0.75F, 0.95F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.LIGHT_GRAY_CRAFTING, 0.68F, 0.68F, 0.68F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.LIME_CRAFTING, 0.0F, 0.86F, 0.0F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.MAGENTA_CRAFTING, 1.0F, 0.0F, 1.0F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.ORANGE_CRAFTING, 0.93F, 0.39F, 0.0F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.PINK_CRAFTING, 1.0F, 0.78F, 0.87F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.PURPLE_CRAFTING, 0.43F, 0.0F, 0.68F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.RED_CRAFTING, 0.95F, 0.0F, 0.0F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.WHITE_CRAFTING, 0.97F, 0.97F, 0.97F, e);
		registerColoredCraftingParticle(SpectrumParticleTypes.YELLOW_CRAFTING, 0.93F, 0.93F, 0.0F, e);
		
		registerColoredRisingParticle(SpectrumParticleTypes.BLACK_FLUID_RISING, 0.1F, 0.1F, 0.1F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.BLUE_FLUID_RISING, 0.05F, 0.011F, 0.95F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.BROWN_FLUID_RISING, 0.31F, 0.16F, 0.05F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.CYAN_FLUID_RISING, 0.0F, 1.0F, 1.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.GRAY_FLUID_RISING, 0.3F, 0.3F, 0.3F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.GREEN_FLUID_RISING, 0.14F, 0.24F, 0.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.LIGHT_BLUE_FLUID_RISING, 0.0F, 0.75F, 0.95F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.LIGHT_GRAY_FLUID_RISING, 0.68F, 0.68F, 0.68F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.LIME_FLUID_RISING, 0.0F, 0.86F, 0.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.MAGENTA_FLUID_RISING, 1.0F, 0.0F, 1.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.ORANGE_FLUID_RISING, 0.93F, 0.39F, 0.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.PINK_FLUID_RISING, 1.0F, 0.78F, 0.87F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.PURPLE_FLUID_RISING, 0.43F, 0.0F, 0.68F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.RED_FLUID_RISING, 0.95F, 0.0F, 0.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.WHITE_FLUID_RISING, 0.97F, 0.97F, 0.97F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.YELLOW_FLUID_RISING, 0.93F, 0.93F, 0.0F, e);
		
		registerColoredRisingParticle(SpectrumParticleTypes.BLACK_SPARKLE_RISING, 0.1F, 0.1F, 0.1F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.BLUE_SPARKLE_RISING, 0.05F, 0.011F, 0.95F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.BROWN_SPARKLE_RISING, 0.31F, 0.16F, 0.05F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.CYAN_SPARKLE_RISING, 0.0F, 1.0F, 1.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.GRAY_SPARKLE_RISING, 0.3F, 0.3F, 0.3F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.GREEN_SPARKLE_RISING, 0.14F, 0.24F, 0.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.LIGHT_BLUE_SPARKLE_RISING, 0.0F, 0.75F, 0.95F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.LIGHT_GRAY_SPARKLE_RISING, 0.68F, 0.68F, 0.68F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.LIME_SPARKLE_RISING, 0.0F, 0.86F, 0.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.MAGENTA_SPARKLE_RISING, 1.0F, 0.0F, 1.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.ORANGE_SPARKLE_RISING, 0.93F, 0.39F, 0.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.PINK_SPARKLE_RISING, 1.0F, 0.78F, 0.87F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.PURPLE_SPARKLE_RISING, 0.43F, 0.0F, 0.68F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.RED_SPARKLE_RISING, 0.95F, 0.0F, 0.0F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.WHITE_SPARKLE_RISING, 0.97F, 0.97F, 0.97F, e);
		registerColoredRisingParticle(SpectrumParticleTypes.YELLOW_SPARKLE_RISING, 0.93F, 0.93F, 0.0F, e);
		
		registerColoredExplosionParticle(SpectrumParticleTypes.BLACK_EXPLOSION, 0.1F, 0.1F, 0.1F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.BLUE_EXPLOSION, 0.05F, 0.011F, 0.95F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.BROWN_EXPLOSION, 0.31F, 0.16F, 0.05F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.CYAN_EXPLOSION, 0.0F, 1.0F, 1.0F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.GRAY_EXPLOSION, 0.3F, 0.3F, 0.3F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.GREEN_EXPLOSION, 0.14F, 0.24F, 0.0F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.LIGHT_BLUE_EXPLOSION, 0.0F, 0.75F, 0.95F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.LIGHT_GRAY_EXPLOSION, 0.68F, 0.68F, 0.68F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.LIME_EXPLOSION, 0.0F, 0.86F, 0.0F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.MAGENTA_EXPLOSION, 1.0F, 0.0F, 1.0F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.ORANGE_EXPLOSION, 0.93F, 0.39F, 0.0F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.PINK_EXPLOSION, 1.0F, 0.78F, 0.87F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.PURPLE_EXPLOSION, 0.43F, 0.0F, 0.68F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.RED_EXPLOSION, 0.95F, 0.0F, 0.0F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.WHITE_EXPLOSION, 0.97F, 0.97F, 0.97F, e);
		registerColoredExplosionParticle(SpectrumParticleTypes.YELLOW_EXPLOSION, 0.93F, 0.93F, 0.0F, e);
		
		//Azzyy sucked cock here
		//Can confirm this sucks ~Daf
		e.registerSpriteSet(SpectrumParticleTypes.LIGHT_TRAIL, LightTrailparticle.Factory::new);
		
		// Since these can reference other particle types, they should always come last
		e.registerSpriteSet(SpectrumParticleTypes.DYNAMIC, DynamicParticle.Factory::new);
		e.registerSpriteSet(SpectrumParticleTypes.DYNAMIC_ALWAYS_SHOW, DynamicParticle.Factory::new);
	}

	@SubscribeEvent
	public void registerr(RegisterParticleProvidersEvent e) {
		e.registerSprite(SpectrumParticleTypes.ITEM_TRANSMISSION, (parameters, pLevel, x, y, z, pXSpeed, pYSpeed, pZSpeed) -> {
			return new TransmissionParticle(pLevel, x, y, z, parameters.getDestination(), parameters.getArrivalInTicks());
        });
	}

	public static void registerColoredExplosionParticle(SimpleParticleType particleType, float red, float green, float blue, RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(particleType, pSprites -> {
			var factory = new ColorableHugeExplosionParticleProvider(pSprites);
			factory.setColor(red, green, blue);
			return factory;
		});
//		event.registerSprite(particleType, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
//			HugeExplosionParticle.Provider factory = new HugeExplosionParticle.Provider(provider);
//			Particle particle = factory.createParticle(particleType, world, x, y, z, velocityX, velocityY, velocityZ);
//			particle.setColor(red, green, blue);
//			return particle;
//		});
	}
	
	public static void registerColoredCraftingParticle(SimpleParticleType particleType, float red, float green, float blue, RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(particleType, pSprites -> {
			CraftingParticle.Factory factory = new CraftingParticle.Factory(pSprites);
			factory.setColor(red, green, blue);
			return factory;
		});
//		event.registerSprite(particleType, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
//			CraftingParticle.Factory factory = new CraftingParticle.Factory(provider);
//			Particle particle = factory.createParticle(particleType, world, x, y, z, velocityX, velocityY, velocityZ);
//			particle.setColor(red, green, blue);
//			return particle;
//		});
	}
	
	public static void registerColoredRisingParticle(SimpleParticleType particleType, float red, float green, float blue, RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(particleType, pSprites -> {
			FixedVelocityParticle.Factory factory = new FixedVelocityParticle.Factory(pSprites);
			factory.setColor(red, green, blue);
			return factory;
		});
//		event.registerSprite(particleType, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
//			FixedVelocityParticle.Factory factory = new FixedVelocityParticle.Factory(provider);
//			Particle particle = factory.createParticle(particleType, world, x, y, z, velocityX, velocityY, velocityZ);
//			particle.setColor(red, green, blue);
//			return particle;
//		});
	}
	
	public static void registerColoredSporeBlossomParticles(SimpleParticleType fallingParticleType, SimpleParticleType airParticleType, float red, float green, float blue, RegisterParticleProvidersEvent event) {
		event.registerSprite(fallingParticleType, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
			TextureSheetParticle particle = DripParticle.createSporeBlossomFallParticle(fallingParticleType, world, x, y, z, velocityX, velocityY, velocityZ);
			particle.setColor(red, green, blue);
			return particle;
		});

		event.registerSpriteSet(airParticleType, pSprites -> {
			var factory = new ColorableSporeBlossomAirProvider(pSprites);
			factory.setColor(red, green, blue);
			return factory;
		});

//		event.registerSprite(airParticleType, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
//			SuspendedParticle.SporeBlossomAirProvider factory = new SuspendedParticle.SporeBlossomAirProvider(provider);
//			Particle particle = factory.createParticle(airParticleType, world, x, y, z, velocityX, velocityY, velocityZ);
//			particle.setColor(red, green, blue);
//			return particle;
//		});
	}
	
}