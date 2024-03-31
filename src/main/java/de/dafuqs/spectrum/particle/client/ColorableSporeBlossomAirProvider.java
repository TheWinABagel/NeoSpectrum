package de.dafuqs.spectrum.particle.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class ColorableSporeBlossomAirProvider extends SuspendedParticle.SporeBlossomAirProvider {
    private float red, green, blue;

    public ColorableSporeBlossomAirProvider(SpriteSet pSprites) {
        super(pSprites);
    }

    public ColorableSporeBlossomAirProvider setColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        return this;
    }

    @Override
    public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        Particle particle = super.createParticle(pType, pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        if (this.red != 0 || this.green != 0 || this.blue != 0) {
            particle.setColor(this.red, this.green, this.blue);
        }
        return particle;
    }
}
