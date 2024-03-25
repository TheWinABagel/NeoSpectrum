package de.dafuqs.spectrum.particle.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class LitParticle extends RisingParticle {
	
	protected LitParticle(ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		super(clientWorld, x, y, z, velocityX, velocityY, velocityZ);
		this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
	}
	
	@Override
	public int getLightColor(float tint) {
		return LightTexture.FULL_BRIGHT;
	}
	
	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_LIT;
	}
	
	public static class Factory implements ParticleProvider<SimpleParticleType> {
		
		private final SpriteSet spriteProvider;
		
		public Factory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			LitParticle particle = new LitParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ);
			particle.pickSprite(this.spriteProvider);
			return particle;
		}
	}
	
}