package de.dafuqs.spectrum.mixin.accessors;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Slime.class)
public interface SlimeEntityAccessor {
	
	@Invoker("setSize")
	void invokeSetSize(int newSize, boolean heal);
	
	@Invoker("getParticles")
	ParticleOptions invokeGetParticles();
	
	@Invoker("getSquishSound")
	SoundEvent invokeGetSquishSound();
	
	@Invoker("getSoundVolume")
	float invokeGetSoundVolume();
	
}