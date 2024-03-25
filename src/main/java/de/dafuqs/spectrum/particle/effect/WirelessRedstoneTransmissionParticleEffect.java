package de.dafuqs.spectrum.particle.effect;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;

public class WirelessRedstoneTransmissionParticleEffect extends SimpleTransmissionParticleEffect {
	
	public static final Codec<WirelessRedstoneTransmissionParticleEffect> CODEC = RecordCodecBuilder.create(
			(instance) -> instance.group(
					PositionSource.CODEC.fieldOf("destination").forGetter((effect) -> effect.destination),
					Codec.INT.fieldOf("arrival_in_ticks").forGetter((vibrationParticleEffect) -> vibrationParticleEffect.arrivalInTicks)
			).apply(instance, WirelessRedstoneTransmissionParticleEffect::new));
	
	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<WirelessRedstoneTransmissionParticleEffect> FACTORY = new ParticleOptions.Deserializer<>() {
		@Override
		public WirelessRedstoneTransmissionParticleEffect fromCommand(ParticleType<WirelessRedstoneTransmissionParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			float f = (float) stringReader.readDouble();
			stringReader.expect(' ');
			float g = (float) stringReader.readDouble();
			stringReader.expect(' ');
			float h = (float) stringReader.readDouble();
			stringReader.expect(' ');
			int i = stringReader.readInt();
			BlockPos blockPos = BlockPos.containing(f, g, h);
			return new WirelessRedstoneTransmissionParticleEffect(new BlockPositionSource(blockPos), i);
		}
		
		@Override
		public WirelessRedstoneTransmissionParticleEffect fromNetwork(ParticleType<WirelessRedstoneTransmissionParticleEffect> particleType, FriendlyByteBuf packetByteBuf) {
			PositionSource positionSource = PositionSourceType.fromNetwork(packetByteBuf);
			int i = packetByteBuf.readVarInt();
			return new WirelessRedstoneTransmissionParticleEffect(positionSource, i);
		}
	};
	
	public WirelessRedstoneTransmissionParticleEffect(PositionSource positionSource, Integer arrivalInTicks) {
		super(positionSource, arrivalInTicks);
	}
	
	@Override
	public ParticleType<WirelessRedstoneTransmissionParticleEffect> getType() {
		return SpectrumParticleTypes.WIRELESS_REDSTONE_TRANSMISSION;
	}
	
}
