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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;

public class ColoredTransmissionParticleEffect extends SimpleTransmissionParticleEffect {
	
	public static final Codec<ColoredTransmissionParticleEffect> CODEC = RecordCodecBuilder.create(
			(instance) -> instance.group(
					PositionSource.CODEC.fieldOf("destination").forGetter((effect) -> effect.destination),
					Codec.INT.fieldOf("arrival_in_ticks").forGetter((effect) -> effect.arrivalInTicks),
					Codec.INT.fieldOf("dye_color").forGetter((effect) -> effect.dyeColor.getId())
			).apply(instance, ColoredTransmissionParticleEffect::new));
	
	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<ColoredTransmissionParticleEffect> FACTORY = new ParticleOptions.Deserializer<>() {
		@Override
		public ColoredTransmissionParticleEffect fromCommand(ParticleType<ColoredTransmissionParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			float f = (float) stringReader.readDouble();
			stringReader.expect(' ');
			float g = (float) stringReader.readDouble();
			stringReader.expect(' ');
			float h = (float) stringReader.readDouble();
			stringReader.expect(' ');
			int i = stringReader.readInt();
			int dyeColorId = stringReader.readInt();
			BlockPos blockPos = BlockPos.containing(f, g, h);
			return new ColoredTransmissionParticleEffect(new BlockPositionSource(blockPos), i, dyeColorId);
		}
		
		@Override
		public ColoredTransmissionParticleEffect fromNetwork(ParticleType<ColoredTransmissionParticleEffect> particleType, FriendlyByteBuf packetByteBuf) {
			PositionSource positionSource = PositionSourceType.fromNetwork(packetByteBuf);
			int i = packetByteBuf.readVarInt();
			int dyeColorId = packetByteBuf.readVarInt();
			return new ColoredTransmissionParticleEffect(positionSource, i, dyeColorId);
		}
	};
	
	public final DyeColor dyeColor;
	
	public ColoredTransmissionParticleEffect(PositionSource positionSource, Integer arrivalInTicks, Integer dyeColorId) {
		super(positionSource, arrivalInTicks);
		this.dyeColor = DyeColor.byId(dyeColorId);
	}
	
	public ColoredTransmissionParticleEffect(PositionSource positionSource, Integer arrivalInTicks, DyeColor dyeColor) {
		super(positionSource, arrivalInTicks);
		this.dyeColor = dyeColor;
	}
	
	@Override
	public ParticleType<ColoredTransmissionParticleEffect> getType() {
		return SpectrumParticleTypes.COLORED_TRANSMISSION;
	}
	
	public DyeColor getDyeColor() {
		return dyeColor;
	}
	
}
