package de.dafuqs.spectrum.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ExactPositionSource implements PositionSource {
	
	public static final Codec<ExactPositionSource> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Vec3.CODEC.fieldOf("pos").forGetter((positionSource) -> positionSource.pos)).apply(instance, ExactPositionSource::new));
	
	final Vec3 pos;
	
	public ExactPositionSource(Vec3 pos) {
		this.pos = pos;
	}
	
	@Override
	public Optional<Vec3> getPosition(Level world) {
		return Optional.of(this.pos);
	}
	
	@Override
	public PositionSourceType<?> getType() {
		return SpectrumPositionSources.EXACT;
	}
	
	public static class Type implements PositionSourceType<ExactPositionSource> {
		public Type() {
		}
		
		@Override
		public ExactPositionSource read(FriendlyByteBuf packetByteBuf) {
			return new ExactPositionSource(new Vec3(packetByteBuf.readDouble(), packetByteBuf.readDouble(), packetByteBuf.readDouble()));
		}
		
		@Override
		public void write(FriendlyByteBuf packetByteBuf, ExactPositionSource blockPositionSource) {
			packetByteBuf.writeDouble(blockPositionSource.pos.x);
			packetByteBuf.writeDouble(blockPositionSource.pos.y);
			packetByteBuf.writeDouble(blockPositionSource.pos.z);
		}
		
		@Override
		public Codec<ExactPositionSource> codec() {
			return ExactPositionSource.CODEC;
		}
	}
	
}
