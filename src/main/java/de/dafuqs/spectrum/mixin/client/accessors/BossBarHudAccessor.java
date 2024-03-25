package de.dafuqs.spectrum.mixin.client.accessors;

import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public interface BossBarHudAccessor {
	
	@Accessor(value = "bossBars")
	Map<UUID, LerpingBossEvent> getBossBars();
	
}