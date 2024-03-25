package de.dafuqs.spectrum.api.block;

import de.dafuqs.spectrum.api.energy.color.InkColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface InkColorSelectedPacketReceiver {
	
	void onInkColorSelectedPacket(@Nullable InkColor inkColor);
	
	BlockEntity getBlockEntity();
	
}
