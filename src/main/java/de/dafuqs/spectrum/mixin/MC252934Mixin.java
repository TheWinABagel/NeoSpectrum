package de.dafuqs.spectrum.mixin;

import net.minecraft.world.entity.decoration.HangingEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HangingEntity.class)
public abstract class MC252934Mixin {

	@Redirect(method = "readCustomDataFromNbt", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
	private void spectrum$fixMC252934(Logger thisLogger, String format, Object arg) {
	}

}