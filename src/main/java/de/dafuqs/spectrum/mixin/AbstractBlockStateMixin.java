package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.data_loaders.ResonanceDropsDataLoader;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class AbstractBlockStateMixin {
	
	@ModifyVariable(method = "onStacksDropped", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	public boolean spectrum$preventXPDropsWhenUsingResonance(boolean dropExperience, ServerLevel world, BlockPos pos, ItemStack stack) {
		if (ResonanceDropsDataLoader.preventNextXPDrop && EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.RESONANCE, stack) > 0) {
			ResonanceDropsDataLoader.preventNextXPDrop = false;
			return false;
		}
		return dropExperience;
	}
	
}
