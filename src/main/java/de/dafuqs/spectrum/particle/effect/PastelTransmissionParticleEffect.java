package de.dafuqs.spectrum.particle.effect;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PastelTransmissionParticleEffect implements ParticleOptions {

	public static final Codec<PastelTransmissionParticleEffect> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(BlockPos.CODEC).fieldOf("positions").forGetter((particleEffect) -> particleEffect.nodePositions),
            ItemStack.CODEC.fieldOf("stack").forGetter((effect) -> effect.stack),
            Codec.INT.fieldOf("travel_time").forGetter((particleEffect) -> particleEffect.travelTime),
            Codec.INT.fieldOf("color").forGetter((particleEffect) -> particleEffect.color)
    ).apply(instance, PastelTransmissionParticleEffect::new));

	@SuppressWarnings("deprecation")
	public static final Deserializer<PastelTransmissionParticleEffect> FACTORY = new Deserializer<>() {
		@Override
		public PastelTransmissionParticleEffect fromCommand(ParticleType<PastelTransmissionParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			List<BlockPos> posList = new ArrayList<>();
			
			stringReader.expect(' ');
			int travelTime = stringReader.readInt();
			
			// TODO I don't care, really
			stringReader.expect(' ');
			int x1 = stringReader.readInt();
			stringReader.expect(' ');
			int y1 = stringReader.readInt();
			stringReader.expect(' ');
			int z1 = stringReader.readInt();
			
			stringReader.expect(' ');
			int x2 = stringReader.readInt();
			stringReader.expect(' ');
			int y2 = stringReader.readInt();
			stringReader.expect(' ');
			int z2 = stringReader.readInt();
			
			stringReader.expect(' ');
			int color = stringReader.readInt();
			
			BlockPos sourcePos = new BlockPos(x1, y1, z1);
			BlockPos destinationPos = new BlockPos(x2, y2, z2);
			posList.add(sourcePos);
			posList.add(destinationPos);
			return new PastelTransmissionParticleEffect(posList, Items.STONE.getDefaultInstance(), travelTime, color);
		}
		
		@Override
		public PastelTransmissionParticleEffect fromNetwork(ParticleType<PastelTransmissionParticleEffect> particleType, FriendlyByteBuf buf) {
			int posCount = buf.readInt();
			List<BlockPos> posList = new ArrayList<>();
			for (int i = 0; i < posCount; i++) {
				posList.add(buf.readBlockPos());
			}
			ItemStack stack = buf.readItem();
			int travelTime = buf.readInt();
			int color = buf.readInt();
			return new PastelTransmissionParticleEffect(posList, stack, travelTime, color);
		}
    };

    private final List<BlockPos> nodePositions;
    private final ItemStack stack;
	private final int travelTime;
	private final int color;
	
	public PastelTransmissionParticleEffect(List<BlockPos> nodePositions, ItemStack stack, Integer travelTime, Integer color) {
		this.nodePositions = nodePositions;
		this.stack = stack;
		this.travelTime = travelTime;
		this.color = color;
	}
	
	@Override
	public ParticleType<PastelTransmissionParticleEffect> getType() {
		return SpectrumParticleTypes.PASTEL_TRANSMISSION;
	}
	
	@Override
	public String writeToString() {
		int nodeCount = this.nodePositions.size();
		BlockPos source = this.nodePositions.get(0);
		BlockPos destination = this.nodePositions.get(this.nodePositions.size() - 1);
		int d = source.getX();
		int e = source.getY();
		int f = source.getZ();
		int g = destination.getX();
		int h = destination.getY();
		int i = destination.getZ();
		return String.format(Locale.ROOT, "%s %d %d %d %d %d %d %d %d %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.travelTime, nodeCount, d, e, f, g, h, i, this.color);
	}

	@Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeInt(nodePositions.size());
        for (BlockPos pos : nodePositions) {
            buf.writeBlockPos(pos);
        }
        buf.writeItem(stack);
        buf.writeInt(travelTime);
        buf.writeInt(color);
    }

    public List<BlockPos> getNodePositions() {
        return this.nodePositions;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public int getTravelTime() {
        return this.travelTime;
    }

    public int getColor() {
        return this.color;
    }

}
