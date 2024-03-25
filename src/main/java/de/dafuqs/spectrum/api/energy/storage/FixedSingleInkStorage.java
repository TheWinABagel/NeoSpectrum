package de.dafuqs.spectrum.api.energy.storage;

import de.dafuqs.spectrum.api.energy.color.InkColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FixedSingleInkStorage extends SingleInkStorage {
	
	public FixedSingleInkStorage(long maxEnergy, InkColor color) {
		super(maxEnergy);
		this.storedColor = color;
	}
	
	public FixedSingleInkStorage(long maxEnergy, InkColor color, long amount) {
		super(maxEnergy, color, amount);
	}
	
	public static @Nullable FixedSingleInkStorage fromNbt(@NotNull CompoundTag compound) {
		if (compound.contains("MaxEnergyTotal", Tag.TAG_LONG)) {
			long maxEnergyTotal = compound.getLong("MaxEnergyTotal");
			InkColor color = InkColor.of(compound.getString("Color"));
			long amount = compound.getLong("Amount");
			return new FixedSingleInkStorage(maxEnergyTotal, color, amount);
		}
		return null;
	}

	@Override
	public boolean accepts(InkColor color) {
		return this.storedColor == color;
	}
	
	@Override
	public long getRoom(InkColor color) {
		if (this.storedColor == color) {
			return this.maxEnergy - this.storedEnergy;
		} else {
			return 0;
		}
	}
	
}