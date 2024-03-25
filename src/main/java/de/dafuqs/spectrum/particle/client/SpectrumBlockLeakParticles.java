package de.dafuqs.spectrum.particle.client;

import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumFluids;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class SpectrumBlockLeakParticles {

	public static class LandingMudFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;
		
		public LandingMudFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.DripLandParticle(clientWorld, d, e, f, SpectrumFluids.MUD);
			blockLeakParticle.setColor(SpectrumFluids.MUD_COLOR_VEC.x(), SpectrumFluids.MUD_COLOR_VEC.y(), SpectrumFluids.MUD_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}
	
	public static class FallingMudFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;
		
		public FallingMudFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.FallAndLandParticle(clientWorld, d, e, f, SpectrumFluids.MUD, SpectrumParticleTypes.LANDING_MUD);
			blockLeakParticle.setColor(SpectrumFluids.MUD_COLOR_VEC.x(), SpectrumFluids.MUD_COLOR_VEC.y(), SpectrumFluids.MUD_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}
	
	public static class DrippingMudFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;
		
		public DrippingMudFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.DripHangParticle(clientWorld, d, e, f, SpectrumFluids.MUD, SpectrumParticleTypes.FALLING_MUD);
			blockLeakParticle.setColor(SpectrumFluids.MUD_COLOR_VEC.x(), SpectrumFluids.MUD_COLOR_VEC.y(), SpectrumFluids.MUD_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	public static class LandingLiquidCrystalFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;
		
		public LandingLiquidCrystalFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.DripLandParticle(clientWorld, d, e, f, SpectrumFluids.LIQUID_CRYSTAL);
			blockLeakParticle.setColor(SpectrumFluids.LIQUID_CRYSTAL_COLOR_VEC.x(), SpectrumFluids.LIQUID_CRYSTAL_COLOR_VEC.y(), SpectrumFluids.LIQUID_CRYSTAL_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}
	
	public static class FallingLiquidCrystalFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;
		
		public FallingLiquidCrystalFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.FallAndLandParticle(clientWorld, d, e, f, SpectrumFluids.LIQUID_CRYSTAL, SpectrumParticleTypes.LANDING_LIQUID_CRYSTAL);
			blockLeakParticle.setColor(SpectrumFluids.LIQUID_CRYSTAL_COLOR_VEC.x(), SpectrumFluids.LIQUID_CRYSTAL_COLOR_VEC.y(), SpectrumFluids.LIQUID_CRYSTAL_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}
	
	public static class DrippingLiquidCrystalFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;
		
		public DrippingLiquidCrystalFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.DripHangParticle(clientWorld, d, e, f, SpectrumFluids.LIQUID_CRYSTAL, SpectrumParticleTypes.FALLING_LIQUID_CRYSTAL);
			blockLeakParticle.setColor(SpectrumFluids.LIQUID_CRYSTAL_COLOR_VEC.x(), SpectrumFluids.LIQUID_CRYSTAL_COLOR_VEC.y(), SpectrumFluids.LIQUID_CRYSTAL_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	public static class LandingMidnightSolutionFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;
		
		public LandingMidnightSolutionFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.DripLandParticle(clientWorld, d, e, f, SpectrumFluids.MIDNIGHT_SOLUTION);
			blockLeakParticle.setColor(SpectrumFluids.MIDNIGHT_SOLUTION_COLOR_VEC.x(), SpectrumFluids.MIDNIGHT_SOLUTION_COLOR_VEC.y(), SpectrumFluids.MIDNIGHT_SOLUTION_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}
	
	public static class FallingMidnightSolutionFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;
		
		public FallingMidnightSolutionFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.FallAndLandParticle(clientWorld, d, e, f, SpectrumFluids.MIDNIGHT_SOLUTION, SpectrumParticleTypes.LANDING_MIDNIGHT_SOLUTION);
			blockLeakParticle.setColor(SpectrumFluids.MIDNIGHT_SOLUTION_COLOR_VEC.x(), SpectrumFluids.MIDNIGHT_SOLUTION_COLOR_VEC.y(), SpectrumFluids.MIDNIGHT_SOLUTION_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}
	
	public static class DrippingMidnightSolutionFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;
		
		public DrippingMidnightSolutionFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.DripHangParticle(clientWorld, d, e, f, SpectrumFluids.MIDNIGHT_SOLUTION, SpectrumParticleTypes.FALLING_MIDNIGHT_SOLUTION);
			blockLeakParticle.setColor(SpectrumFluids.MIDNIGHT_SOLUTION_COLOR_VEC.x(), SpectrumFluids.MIDNIGHT_SOLUTION_COLOR_VEC.y(), SpectrumFluids.MIDNIGHT_SOLUTION_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	public static class LandingDragonrotFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;

		public LandingDragonrotFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.DripLandParticle(clientWorld, d, e, f, SpectrumFluids.DRAGONROT);
			blockLeakParticle.setColor(SpectrumFluids.DRAGONROT_COLOR_VEC.x(), SpectrumFluids.DRAGONROT_COLOR_VEC.y(), SpectrumFluids.DRAGONROT_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	public static class FallingDragonrotFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;

		public FallingDragonrotFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.FallAndLandParticle(clientWorld, d, e, f, SpectrumFluids.DRAGONROT, SpectrumParticleTypes.LANDING_DRAGONROT);
			blockLeakParticle.setColor(SpectrumFluids.DRAGONROT_COLOR_VEC.x(), SpectrumFluids.DRAGONROT_COLOR_VEC.y(), SpectrumFluids.DRAGONROT_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	public static class DrippingDragonrotFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet spriteProvider;

		public DrippingDragonrotFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			DripParticle blockLeakParticle = new DripParticle.DripHangParticle(clientWorld, d, e, f, SpectrumFluids.DRAGONROT, SpectrumParticleTypes.FALLING_DRAGONROT);
			blockLeakParticle.setColor(SpectrumFluids.DRAGONROT_COLOR_VEC.x(), SpectrumFluids.DRAGONROT_COLOR_VEC.y(), SpectrumFluids.DRAGONROT_COLOR_VEC.z());
			blockLeakParticle.pickSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

}
