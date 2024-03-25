package de.dafuqs.spectrum.explosion.modifier;

import de.dafuqs.spectrum.explosion.ExplosionModifier;
import de.dafuqs.spectrum.explosion.ExplosionModifierType;
import net.minecraft.core.particles.ParticleOptions;

import java.util.Optional;

public class ParticleAddingModifier extends ExplosionModifier {
	
	private final ParticleOptions particleEffect;
	
	public ParticleAddingModifier(ExplosionModifierType type, ParticleOptions particleEffect, int displayColor) {
		super(type, displayColor);
		this.particleEffect = particleEffect;
	}
	
	@Override
	public Optional<ParticleOptions> getParticleEffects() {
		return Optional.of(particleEffect);
	}
	
}
