package de.dafuqs.spectrum.api.energy;

import de.dafuqs.spectrum.api.energy.color.InkColor;
import net.minecraft.nbt.CompoundTag;

public class InkCost {
	
	private final InkColor color;
	private final long cost;
	
	public InkCost(InkColor color, long cost) {
		this.color = color;
		this.cost = cost;
	}
	
	public InkColor getColor() {
		return color;
	}
	
	public long getCost() {
		return cost;
	}
	
	public void writeNbt(CompoundTag nbt) {
		nbt.putString("InkColor", color.toString());
		nbt.putLong("InkCost", cost);
	}
	
	public static InkCost fromNbt(CompoundTag nbt) {
		InkColor inkColor = InkColor.of(nbt.getString("InkColor"));
		long inkCost = nbt.getLong("InkCost");
		return new InkCost(inkColor, inkCost);
	}
	
}