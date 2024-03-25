package de.dafuqs.spectrum.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.dafuqs.spectrum.enchantments.InertiaEnchantment.INERTIA_BLOCK;
import static de.dafuqs.spectrum.enchantments.InertiaEnchantment.INERTIA_COUNT;

@Mixin(DiggerItem.class)
public abstract class MiningToolItemMixin {

	@Inject(at = @At("HEAD"), method = "postMine(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)Z")
	public void countInertiaBlocks(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> cir) {
		if (stack != null) { // thank you, gobber
			long inertiaAmount = 0;

			if (SpectrumEnchantmentHelper.getUsableLevel(SpectrumEnchantments.INERTIA, stack, miner) > 0) {
				CompoundTag compound = stack.getOrCreateTag();
				ResourceLocation brokenBlockIdentifier = BuiltInRegistries.BLOCK.getKey(state.getBlock());
				if (compound.getString("Inertia_LastMinedBlock").equals(brokenBlockIdentifier.toString())) {
					inertiaAmount = compound.getLong(INERTIA_COUNT) + 1;
					compound.putLong(INERTIA_COUNT, inertiaAmount);
				} else {
					compound.putString(INERTIA_BLOCK, brokenBlockIdentifier.toString());
					compound.putLong(INERTIA_COUNT, 1);
					inertiaAmount = 1;
				}
			}

			if (miner instanceof ServerPlayer serverPlayerEntity) {
				SpectrumAdvancementCriteria.INERTIA_USED.trigger(serverPlayerEntity, state, (int) inertiaAmount);
			}
			
		}
	}
	
	@ModifyReturnValue(method = "getMiningSpeedMultiplier(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/BlockState;)F", at = @At("RETURN"))
	public float applyMiningSpeedMultipliers(float original, ItemStack stack, BlockState state) {
		if (stack != null) { // thank you, gobber
			
			// INERTIA GAMING
			int inertiaLevel = EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.INERTIA, stack);
			inertiaLevel = Math.min(4, inertiaLevel); // inertia is capped at 5 levels. Higher and the formula would do weird stuff
			if (inertiaLevel > 0) {
				CompoundTag compound = stack.getOrCreateTag();
				ResourceLocation brokenBlockIdentifier = BuiltInRegistries.BLOCK.getKey(state.getBlock());
				if (compound.getString(INERTIA_BLOCK).equals(brokenBlockIdentifier.toString())) {
					long lastMinedBlockCount = compound.getLong(INERTIA_COUNT);
					double additionalSpeedPercent = 2.0 * Math.log(lastMinedBlockCount) / Math.log((6 - inertiaLevel) * (6 - inertiaLevel) + 1);
					
					original = original * (0.5F + (float) additionalSpeedPercent);
				} else {
					original = original / 4;
				}
			}
			
			// RAZING GAMING
			int razingLevel = EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.RAZING, stack);
			if (razingLevel > 0) {
				float hardness = state.getBlock().defaultDestroyTime();
				original = (float) Math.max(1 + hardness, Math.pow(2, 1 + razingLevel / 8F));
			}
			
		}
		
		return original;
	}

}
