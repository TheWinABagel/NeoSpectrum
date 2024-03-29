package de.dafuqs.spectrum.particle.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class LightTrailparticle extends SimpleAnimatedParticle {
    protected LightTrailparticle(ClientLevel world, double x, double y, double z,  double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, 0);
        this.xd = velocityX;
        this.yd = velocityY;
        this.zd = velocityZ;
        this.quadSize = 0.2F;
        this.lifetime = 25;
        this.setSpriteFromAge(spriteProvider);
        setAlpha(0.8f);
        setFadeColor(0xa8baff);
    }

    @Override
    public void tick() {
        super.tick();
        var fadeProgress = Mth.clamp((age + Minecraft.getInstance().getFrameTime()) / lifetime, 0, 1);
        setAlpha(Mth.lerp(fadeProgress, 0.8F, 0F));
        quadSize = Mth.lerp(fadeProgress, 0.2F, 0.1F);
    }
	
	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;
		
		public Factory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			return new LightTrailparticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
