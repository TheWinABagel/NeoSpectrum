package de.dafuqs.spectrum.mixin.client.accessors;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemOverrides.class)
public interface ModelOverrideListAccessor {
	
	@Accessor()
	ItemOverrides.BakedOverride[] getOverrides();
}
