package de.dafuqs.spectrum.helpers;

import de.dafuqs.spectrum.particle.VectorPattern;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ParticleHelper {
	
	public static void playParticleWithPatternAndVelocityClient(Level world, Vec3 position, ParticleOptions particleEffect, @NotNull VectorPattern pattern, double velocity) {
		for (Vec3 vec3d : pattern.getVectors()) {
			world.addParticle(particleEffect, position.x(), position.y(), position.z(), vec3d.x * velocity, vec3d.y * velocity, vec3d.z * velocity);
		}
	}

	public static void playParticleWithRotation(Level world, Vec3 position, double longitude, double latitude, ParticleOptions particleEffect, @NotNull VectorPattern pattern, double velocity) {
		for (Vec3 vec3d : pattern.getVectors()) {
			var length = vec3d.length();
			var orientation = Orientation.getVectorOrientation(vec3d).add(longitude, latitude);
			vec3d = orientation.toVector(length);

			world.addParticle(particleEffect, position.x(), position.y(), position.z(), vec3d.x * velocity, vec3d.y * velocity, vec3d.z * velocity);
		}
	}
	
}
