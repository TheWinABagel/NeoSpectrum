package de.dafuqs.spectrum.events;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.gameevent.GameEvent;

public class RedstoneTransferGameEvent extends GameEvent {
	
	private final DyeColor dyeColor;
	private final int power;
	
	public RedstoneTransferGameEvent(String id, int range, DyeColor dyeColor, int power) {
		super(id, range);
		this.dyeColor = dyeColor;
		this.power = power;
	}
	
	public DyeColor getDyeColor() {
		return dyeColor;
	}
	
	public int getPower() {
		return power;
	}
	
}
