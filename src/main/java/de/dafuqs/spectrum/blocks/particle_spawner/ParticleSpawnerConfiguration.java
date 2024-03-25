package de.dafuqs.spectrum.blocks.particle_spawner;

import de.dafuqs.spectrum.particle.effect.DynamicParticleEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

public class ParticleSpawnerConfiguration {
	
	private final ParticleType<?> particleType;
	private final Vector3i cmyColor; // 0-100 cmy
	private final boolean glowing;
	private final float particlesPerSecond; /* >1 = every xth tick */
	private final Vector3fc sourcePosition;
	private final Vector3fc sourcePositionVariance;
	private final Vector3fc velocity;
	private final Vector3fc velocityVariance;
	private final float scale;
	private final float scaleVariance;
	private final int lifetimeTicks;
	private final int lifetimeVariance;
	private final float gravity;
	private final boolean collisions;
	
	private final Vector3fc rgbColor; // 0-255 rgb
	
	public ParticleSpawnerConfiguration(ParticleType<?> particleType, Vector3i cmyColor, boolean glowing, float particlesPerSecond /* >1 = every xth tick */,
										Vector3fc sourcePosition, Vector3fc sourcePositionVariance, Vector3fc velocity, Vector3fc velocityVariance,
										float scale, float scaleVariance, int lifetimeTicks, int lifetimeVariance, float gravity, boolean collisions) {
		
		this.particleType = particleType;
		this.cmyColor = cmyColor;
		this.glowing = glowing;
		this.particlesPerSecond = particlesPerSecond;
		this.sourcePosition = sourcePosition;
		this.sourcePositionVariance = sourcePositionVariance;
		this.velocity = velocity;
		this.velocityVariance = velocityVariance;
		this.scale = scale;
		this.scaleVariance = scaleVariance;
		this.lifetimeTicks = lifetimeTicks;
		this.lifetimeVariance = lifetimeVariance;
		this.gravity = gravity;
		this.collisions = collisions;
		
		this.rgbColor = CMYtoRGB(cmyColor);
	}
	
	public static Vector3fc CMYtoRGB(Vector3i cmy) {
		float r = 1F - cmy.x() / 100F;
		float g = 1F - cmy.y() / 100F;
		float b = 1F - cmy.z() / 100F;
		return new Vector3f(r, g, b);
	}
	
	public ParticleType<?> getParticleType() {
		return particleType;
	}
	
	public Vector3i getCmyColor() {
		return cmyColor;
	}
	
	public boolean glows() {
		return glowing;
	}
	
	public float getParticlesPerSecond() {
		return particlesPerSecond;
	}
	
	public Vector3fc getSourcePosition() {
		return sourcePosition;
	}
	
	public Vector3fc getSourcePositionVariance() {
		return sourcePositionVariance;
	}
	
	public Vector3fc getVelocity() {
		return velocity;
	}
	
	public Vector3fc getVelocityVariance() {
		return velocityVariance;
	}
	
	public float getScale() {
		return scale;
	}
	
	public float getScaleVariance() {
		return scaleVariance;
	}
	
	public int getLifetimeTicks() {
		return lifetimeTicks;
	}
	
	public int getLifetimeVariance() {
		return lifetimeVariance;
	}
	
	public float getGravity() {
		return gravity;
	}
	
	public boolean hasCollisions() {
		return collisions;
	}
	
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType).toString());
		buf.writeInt(cmyColor.x());
		buf.writeInt(cmyColor.y());
		buf.writeInt(cmyColor.z());
		buf.writeBoolean(glowing);
		buf.writeFloat(particlesPerSecond);
		buf.writeFloat(sourcePosition.x());
		buf.writeFloat(sourcePosition.y());
		buf.writeFloat(sourcePosition.z());
		buf.writeFloat(sourcePositionVariance.x());
		buf.writeFloat(sourcePositionVariance.y());
		buf.writeFloat(sourcePositionVariance.z());
		buf.writeFloat(velocity.x());
		buf.writeFloat(velocity.y());
		buf.writeFloat(velocity.z());
		buf.writeFloat(velocityVariance.x());
		buf.writeFloat(velocityVariance.y());
		buf.writeFloat(velocityVariance.z());
		buf.writeFloat(scale);
		buf.writeFloat(scaleVariance);
		buf.writeInt(lifetimeTicks);
		buf.writeInt(lifetimeVariance);
		buf.writeFloat(gravity);
		buf.writeBoolean(collisions);
	}
	
	public static ParticleSpawnerConfiguration fromBuf(FriendlyByteBuf buf) {
		ResourceLocation particleIdentifier = new ResourceLocation(buf.readUtf());
		ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(particleIdentifier);
		Vector3i cmyColor = new Vector3i(buf.readInt(), buf.readInt(), buf.readInt());
		boolean glowing = buf.readBoolean();
		float particlesPerSecond = buf.readFloat();
		Vector3fc sourcePosition = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
		Vector3fc sourcePositionVariance = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
		Vector3fc velocity = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
		Vector3fc velocityVariance = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
		float scale = buf.readFloat();
		float scaleVariance = buf.readFloat();
		int lifetimeTicks = buf.readInt();
		int lifetimeVariance = buf.readInt();
		float gravity = buf.readFloat();
		boolean collisions = buf.readBoolean();
		
		return new ParticleSpawnerConfiguration(particleType, cmyColor, glowing, particlesPerSecond, sourcePosition, sourcePositionVariance,
				velocity, velocityVariance, scale, scaleVariance, lifetimeTicks, lifetimeVariance, gravity, collisions);
	}
	
	public CompoundTag toNbt() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("particle_type_identifier", BuiltInRegistries.PARTICLE_TYPE.getKey(particleType).toString());
		nbt.putFloat("particles_per_tick", particlesPerSecond);
		nbt.putBoolean("glowing", glowing);
		nbt.putFloat("source_pos_x", sourcePosition.x());
		nbt.putFloat("source_pos_y", sourcePosition.y());
		nbt.putFloat("source_pos_z", sourcePosition.z());
		nbt.putFloat("source_pos_variance_x", sourcePositionVariance.x());
		nbt.putFloat("source_pos_variance_y", sourcePositionVariance.y());
		nbt.putFloat("source_pos_variance_z", sourcePositionVariance.z());
		nbt.putFloat("source_velocity_x", velocity.x());
		nbt.putFloat("source_velocity_y", velocity.y());
		nbt.putFloat("source_velocity_z", velocity.z());
		nbt.putFloat("source_velocity_variance_x", velocityVariance.x());
		nbt.putFloat("source_velocity_variance_y", velocityVariance.y());
		nbt.putFloat("source_velocity_variance_z", velocityVariance.z());
		nbt.putInt("color_c", cmyColor.x());
		nbt.putInt("color_m", cmyColor.y());
		nbt.putInt("color_y", cmyColor.z());
		nbt.putFloat("scale", scale);
		nbt.putFloat("scale_variance", scaleVariance);
		nbt.putInt("lifetime", lifetimeTicks);
		nbt.putInt("lifetime_variance", lifetimeVariance);
		nbt.putFloat("gravity", gravity);
		nbt.putBoolean("collisions", collisions);
		return nbt;
	}
	
	public static ParticleSpawnerConfiguration fromNbt(CompoundTag tag) {
		ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(new ResourceLocation(tag.getString("particle_type_identifier")));
		float particlesPerSecond = tag.getFloat("particles_per_tick");
		boolean glowing = tag.getBoolean("glowing");
		Vector3fc particleSourcePosition = new Vector3f(tag.getFloat("source_pos_x"), tag.getFloat("source_pos_y"), tag.getFloat("source_pos_z"));
		Vector3fc particleSourcePositionVariance = new Vector3f(tag.getFloat("source_pos_variance_x"), tag.getFloat("source_pos_variance_y"), tag.getFloat("source_pos_variance_z"));
		Vector3fc velocity = new Vector3f(tag.getFloat("source_velocity_x"), tag.getFloat("source_velocity_y"), tag.getFloat("source_velocity_z"));
		Vector3fc velocityVariance = new Vector3f(tag.getFloat("source_velocity_variance_x"), tag.getFloat("source_velocity_variance_y"), tag.getFloat("source_velocity_variance_z"));
		Vector3i cmyColor = new Vector3i(tag.getInt("color_c"), tag.getInt("color_m"), tag.getInt("color_y"));
		float scale = tag.getFloat("scale");
		float scaleVariance = tag.getFloat("scale_variance");
		int lifetimeTicks = tag.getInt("lifetime");
		int lifetimeVariance = tag.getInt("lifetime_variance");
		float gravity = tag.getFloat("gravity");
		boolean collisions = tag.getBoolean("collisions");
		
		return new ParticleSpawnerConfiguration(particleType, cmyColor, glowing, particlesPerSecond, particleSourcePosition, particleSourcePositionVariance,
				velocity, velocityVariance, scale, scaleVariance, lifetimeTicks, lifetimeVariance, gravity, collisions);
	}
	
	public void spawnParticles(Level world, @NotNull BlockPos pos) {
		float particlesToSpawn = particlesPerSecond / 20F;
		while (particlesToSpawn >= 1 || world.random.nextFloat() < particlesToSpawn) {
			spawnParticle(world, pos, world.random);
			particlesToSpawn--;
		}
	}
	
	private void spawnParticle(Level world, @NotNull BlockPos pos, RandomSource random) {
		float randomScale = scaleVariance == 0 ? scale : (float) (scale + scaleVariance - random.nextDouble() * scaleVariance * 2.0D);
		int randomLifetime = lifetimeVariance == 0 ? lifetimeTicks : (int) (lifetimeTicks + lifetimeVariance - random.nextDouble() * lifetimeVariance * 2.0D);
		
		if (randomScale > 0 && randomLifetime > 0) {
			double randomOffsetX = sourcePositionVariance.x() == 0 ? 0 : sourcePositionVariance.x() - random.nextDouble() * sourcePositionVariance.x() * 2.0D;
			double randomOffsetY = sourcePositionVariance.y() == 0 ? 0 : sourcePositionVariance.y() - random.nextDouble() * sourcePositionVariance.y() * 2.0D;
			double randomOffsetZ = sourcePositionVariance.z() == 0 ? 0 : sourcePositionVariance.z() - random.nextDouble() * sourcePositionVariance.z() * 2.0D;
			
			double randomVelocityX = velocityVariance.x() == 0 ? 0 : velocityVariance.x() - random.nextDouble() * velocityVariance.x() * 2.0D;
			double randomVelocityY = velocityVariance.y() == 0 ? 0 : velocityVariance.y() - random.nextDouble() * velocityVariance.y() * 2.0D;
			double randomVelocityZ = velocityVariance.z() == 0 ? 0 : velocityVariance.z() - random.nextDouble() * velocityVariance.z() * 2.0D;
			
			world.addParticle(
					new DynamicParticleEffect(particleType, gravity, new Vector3f(rgbColor), randomScale, randomLifetime, collisions, glowing),
					(double) pos.getX() + 0.5 + sourcePosition.x() + randomOffsetX,
					(double) pos.getY() + 0.5 + sourcePosition.y() + randomOffsetY,
					(double) pos.getZ() + 0.5 + sourcePosition.z() + randomOffsetZ,
					velocity.x() + randomVelocityX,
					velocity.y() + randomVelocityY,
					velocity.z() + randomVelocityZ
			);
		}
	}
	
}
