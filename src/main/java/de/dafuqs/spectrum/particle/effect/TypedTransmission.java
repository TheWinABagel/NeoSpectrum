package de.dafuqs.spectrum.particle.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;

public class TypedTransmission extends SimpleTransmission {
	
	public enum Variant {
		BLOCK_POS,
		ITEM,
		EXPERIENCE,
		REDSTONE,
		HUMMINGSTONE
	}
	
	public static final Codec<TypedTransmission> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Vec3.CODEC.fieldOf("origin").forGetter((itemTransfer) -> itemTransfer.origin), PositionSource.CODEC.fieldOf("destination").forGetter((itemTransfer) -> itemTransfer.destination), Codec.INT.fieldOf("arrival_in_ticks").forGetter((itemTransfer) -> itemTransfer.arrivalInTicks), Codec.INT.fieldOf("variant").forGetter((itemTransfer) -> itemTransfer.variant.ordinal())).apply(instance, TypedTransmission::new));
	
	private final Variant variant;
	
	public TypedTransmission(Vec3 origin, PositionSource destination, int arrivalInTicks, Variant variant) {
		super(origin, destination, arrivalInTicks);
		this.variant = variant;
	}
	
	public TypedTransmission(Object origin, Object destination, Object arrivalInTicks, Object variant) {
		this((Vec3) origin, (PositionSource) destination, (int) arrivalInTicks, (Variant) variant);
	}
	
	public static TypedTransmission readFromBuf(FriendlyByteBuf buf) {
		Vec3 origin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
		PositionSource positionSource = PositionSourceType.fromNetwork(buf);
		int arrivalInTicks = buf.readInt();
		Variant variant = Variant.values()[buf.readInt()];
		return new TypedTransmission(origin, positionSource, arrivalInTicks, variant);
	}
	
	public static void writeToBuf(FriendlyByteBuf buf, TypedTransmission transfer) {
		buf.writeDouble(transfer.origin.x);
		buf.writeDouble(transfer.origin.y);
		buf.writeDouble(transfer.origin.z);
		PositionSourceType.toNetwork(transfer.destination, buf);
		buf.writeInt(transfer.arrivalInTicks);
		buf.writeInt(transfer.variant.ordinal());
	}
	
	public Variant getVariant() {
		return this.variant;
	}
	
}
