package de.dafuqs.spectrum.particle.effect;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public class DynamicParticleEffectAlwaysShow extends DynamicParticleEffect {
	
	public static final Codec<DynamicParticleEffectAlwaysShow> CODEC = RecordCodecBuilder.create(
			(instance) -> instance.group(
					ExtraCodecs.VECTOR3F.fieldOf("color").forGetter((effect) -> effect.color),
					Codec.STRING.fieldOf("particle_type_identifier").forGetter((effect) -> effect.particleTypeIdentifier.toString()),
					Codec.FLOAT.fieldOf("scale").forGetter((effect) -> effect.scale),
					Codec.INT.fieldOf("lifetime_ticks").forGetter((effect) -> effect.lifetimeTicks),
					Codec.FLOAT.fieldOf("gravity").forGetter((effect) -> effect.gravity),
					Codec.BOOL.fieldOf("collisions").forGetter((effect) -> effect.collisions),
					Codec.BOOL.fieldOf("glow_in_the_dark").forGetter((effect) -> effect.glowing)
			).apply(instance, DynamicParticleEffectAlwaysShow::new));
	
	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<DynamicParticleEffectAlwaysShow> FACTORY = new ParticleOptions.Deserializer<>() {
		@Override
		public DynamicParticleEffectAlwaysShow fromCommand(ParticleType<DynamicParticleEffectAlwaysShow> particleType, StringReader stringReader) throws CommandSyntaxException {
			Vector3f color = DustParticleOptionsBase.readVector3f(stringReader);
			stringReader.expect(' ');
			ResourceLocation textureIdentifier = new ResourceLocation(stringReader.readString());
			stringReader.expect(' ');
			float scale = stringReader.readFloat();
			stringReader.expect(' ');
			int lifetimeTicks = stringReader.readInt();
			stringReader.expect(' ');
			float gravity = stringReader.readFloat();
			stringReader.expect(' ');
			boolean collisions = stringReader.readBoolean();
			boolean glowInTheDark = stringReader.readBoolean();
			
			return new DynamicParticleEffectAlwaysShow(textureIdentifier, gravity, color, scale, lifetimeTicks, collisions, glowInTheDark);
		}
		
		@Override
		public DynamicParticleEffectAlwaysShow fromNetwork(ParticleType<DynamicParticleEffectAlwaysShow> particleType, FriendlyByteBuf packetByteBuf) {
			Vector3f color = DustParticleOptionsBase.readVector3f(packetByteBuf);
			ResourceLocation textureIdentifier = packetByteBuf.readResourceLocation();
			float scale = packetByteBuf.readFloat();
			int lifetimeTicks = packetByteBuf.readInt();
			float gravity = packetByteBuf.readFloat();
			boolean collisions = packetByteBuf.readBoolean();
			boolean glowInTheDark = packetByteBuf.readBoolean();
			
			return new DynamicParticleEffectAlwaysShow(textureIdentifier, gravity, color, scale, lifetimeTicks, collisions, glowInTheDark);
		}
	};
	
	public DynamicParticleEffectAlwaysShow(float gravity, Vector3f color, float scale, int lifetimeTicks, boolean collisions, boolean glowInTheDark) {
		super(gravity, color, scale, lifetimeTicks, collisions, glowInTheDark);
	}
	
	public DynamicParticleEffectAlwaysShow(Vector3f color, float scale, int lifetimeTicks, boolean collisions, boolean glowInTheDark) {
		super(color, scale, lifetimeTicks, collisions, glowInTheDark);
	}
	
	public DynamicParticleEffectAlwaysShow(ParticleType<?> particleType, Vector3f color, float scale, int lifetimeTicks, boolean collisions, boolean glowInTheDark) {
		super(particleType, 1.0, color, scale, lifetimeTicks, collisions, glowInTheDark);
	}
	
	protected DynamicParticleEffectAlwaysShow(Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
		super(o, o1, o2, o3, o4, o5, o6);
	}
	
	@Override
	public String writeToString() {
		return String.valueOf(BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()));
	}
	
	@Override
	public ParticleType<DynamicParticleEffectAlwaysShow> getType() {
		return SpectrumParticleTypes.DYNAMIC_ALWAYS_SHOW;
	}
	
}
