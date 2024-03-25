package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InfestedBlock.class)
public abstract class InfestedBlockMixin {
	
	/*
	 * Do not spawn silverfish when block is broken with Resonance Tool
	 */
	@Inject(at = @At("HEAD"), method = "onStacksDropped(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;Z)V", cancellable = true)
	public void onStacksDropped(BlockState state, ServerLevel world, BlockPos pos, ItemStack stack, boolean dropExperience, CallbackInfo ci) {
		if (EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.PEST_CONTROL, stack) > 0) {
			Silverfish silverfishEntity = EntityType.SILVERFISH.create(world);
			if (silverfishEntity != null) {
				silverfishEntity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
				world.addFreshEntity(silverfishEntity);
				silverfishEntity.spawnAnim();
				silverfishEntity.kill();
				
				ExperienceOrb experienceOrbEntity = new ExperienceOrb(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 10);
				world.addFreshEntity(experienceOrbEntity);
			}
			ci.cancel();
		}
	}
	
}
