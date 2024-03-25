package de.dafuqs.spectrum.explosion.modifier;

import de.dafuqs.spectrum.explosion.ExplosionModifierType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class DamageChangingModifier extends ParticleAddingModifier {
	

	public DamageChangingModifier(ExplosionModifierType type, ParticleOptions effect, int color) {
		super(type, effect, color);
	}
	
	@Override
	public Optional<DamageSource> getDamageSource(@Nullable LivingEntity owner) {
		if (owner == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(owner.damageSources().generic());
	}
	
}
