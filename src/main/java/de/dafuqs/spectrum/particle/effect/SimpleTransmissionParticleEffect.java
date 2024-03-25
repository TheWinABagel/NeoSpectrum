package de.dafuqs.spectrum.particle.effect;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;
import java.util.Optional;

public abstract class SimpleTransmissionParticleEffect implements ParticleOptions {
	
	protected final PositionSource destination;
	protected final int arrivalInTicks;
	
	public SimpleTransmissionParticleEffect(PositionSource positionSource, int arrivalInTicks) {
		this.destination = positionSource;
		this.arrivalInTicks = arrivalInTicks;
	}
	
	@Override
	public void writeToNetwork(FriendlyByteBuf buf) {
		PositionSourceType.toNetwork(this.destination, buf);
		buf.writeVarInt(this.arrivalInTicks);
	}
	
	@Override
	public String writeToString() {
		Optional<Vec3> pos = this.destination.getPosition(null);
		if (pos.isPresent()) {
			double d = pos.get().x();
			double e = pos.get().y();
			double f = pos.get().z();
			return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), d, e, f, this.arrivalInTicks);
		}
		return String.format(Locale.ROOT, "%s <no destination> %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.arrivalInTicks);
	}
	
	public PositionSource getDestination() {
		return this.destination;
	}
	
	public int getArrivalInTicks() {
		return this.arrivalInTicks;
	}
	
}
