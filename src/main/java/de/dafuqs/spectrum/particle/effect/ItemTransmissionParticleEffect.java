package de.dafuqs.spectrum.particle.effect;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;

public class ItemTransmissionParticleEffect extends SimpleTransmissionParticleEffect {
	
	public static final Codec<ItemTransmissionParticleEffect> CODEC = RecordCodecBuilder.create(
			(instance) -> instance.group(
					PositionSource.CODEC.fieldOf("destination").forGetter((effect) -> effect.destination),
					Codec.INT.fieldOf("arrival_in_ticks").forGetter((vibrationParticleEffect) -> vibrationParticleEffect.arrivalInTicks)
			).apply(instance, ItemTransmissionParticleEffect::new));
	
	@SuppressWarnings("deprecation")
	public static final Deserializer<ItemTransmissionParticleEffect> FACTORY = new Deserializer<>() {
		@Override
		public ItemTransmissionParticleEffect fromCommand(ParticleType<ItemTransmissionParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			float f = (float) stringReader.readDouble();
			stringReader.expect(' ');
			float g = (float) stringReader.readDouble();
			stringReader.expect(' ');
			float h = (float) stringReader.readDouble();
			stringReader.expect(' ');
			int i = stringReader.readInt();
			BlockPos blockPos = BlockPos.containing(f, g, h);
			return new ItemTransmissionParticleEffect(new BlockPositionSource(blockPos), i);
		}
		
		@Override
		public ItemTransmissionParticleEffect fromNetwork(ParticleType<ItemTransmissionParticleEffect> particleType, FriendlyByteBuf packetByteBuf) {
			PositionSource positionSource = PositionSourceType.fromNetwork(packetByteBuf);
			int i = packetByteBuf.readVarInt();
			return new ItemTransmissionParticleEffect(positionSource, i);
		}
	};
	
	public ItemTransmissionParticleEffect(PositionSource positionSource, Integer arrivalInTicks) {
		super(positionSource, arrivalInTicks);
	}
	
	@Override
	public ParticleType<ItemTransmissionParticleEffect> getType() {
		return SpectrumParticleTypes.ITEM_TRANSMISSION;
	}
	
}
