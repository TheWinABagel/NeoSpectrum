package de.dafuqs.spectrum.mixin.client;

import de.dafuqs.spectrum.cca.hardcore_death.HardcoreDeathComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@OnlyIn(Dist.CLIENT)
@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin {
	
	@ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private static boolean spectrum$isHardcore(boolean isHardcore) {
		if (!isHardcore && (HardcoreDeathComponent.isInHardcore(Minecraft.getInstance().player) || HardcoreDeathComponent.hasHardcoreDeath(Minecraft.getInstance().player.getGameProfile()))) {
			return true;
		}
		return isHardcore;
	}
	
}
