package de.dafuqs.spectrum.particle.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;

public class ColoredTransmission extends SimpleTransmission {
	
	public static final Codec<ColoredTransmission> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Vec3.CODEC.fieldOf("origin").forGetter((coloredTransmission) -> coloredTransmission.origin), PositionSource.CODEC.fieldOf("destination").forGetter((coloredTransmission) -> coloredTransmission.destination), Codec.INT.fieldOf("dye_color").forGetter((coloredTransmission) -> coloredTransmission.dyeColor.getId()), Codec.INT.fieldOf("arrival_in_ticks").forGetter((coloredTransmission) -> coloredTransmission.arrivalInTicks)).apply(instance, ColoredTransmission::new));
	
	protected final DyeColor dyeColor;
	
	public ColoredTransmission(Vec3 origin, PositionSource destination, int arrivalInTicks, DyeColor dyeColor) {
		super(origin, destination, arrivalInTicks);
		this.dyeColor = dyeColor;
	}
	
	public ColoredTransmission(Object origin, Object destination, Object arrivalInTicks, Object dyeColor) {
		this((Vec3) origin, (PositionSource) destination, (int) arrivalInTicks, DyeColor.values()[(int) dyeColor]);
	}
	
	public static ColoredTransmission readFromBuf(FriendlyByteBuf buf) {
		Vec3 origin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
		PositionSource destination = PositionSourceType.fromNetwork(buf);
		int arrivalInTicks = buf.readVarInt();
		DyeColor dyeColor = DyeColor.byId(buf.readVarInt());
		return new ColoredTransmission(origin, destination, arrivalInTicks, dyeColor);
	}
	
	public static void writeToBuf(FriendlyByteBuf buf, ColoredTransmission transfer) {
		buf.writeDouble(transfer.origin.x);
		buf.writeDouble(transfer.origin.y);
		buf.writeDouble(transfer.origin.z);
		PositionSourceType.toNetwork(transfer.destination, buf);
		buf.writeVarInt(transfer.arrivalInTicks);
		buf.writeVarInt(transfer.dyeColor.getId());
	}
	
	public DyeColor getDyeColor() {
		return this.dyeColor;
	}
	
}
