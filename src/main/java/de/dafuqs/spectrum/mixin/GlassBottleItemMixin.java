package de.dafuqs.spectrum.mixin;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(BottleItem.class)
public abstract class GlassBottleItemMixin {
	
	@Shadow
	protected abstract ItemStack fill(ItemStack stack, Player player, ItemStack outputStack);
	
	@Inject(method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"),
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void onUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, List<AreaEffectCloud> list, ItemStack handStack, BlockHitResult areaEffectCloudEntity, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		
		if (blockState.is(SpectrumBlocks.FADING)
				&& SpectrumCommon.CONFIG.CanBottleUpFading
				&& AdvancementHelper.hasAdvancement(user, SpectrumCommon.locate("unlocks/items/bottle_of_fading"))) {
			
			world.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
			cir.setReturnValue(InteractionResultHolder.sidedSuccess(this.fill(handStack, user, SpectrumItems.BOTTLE_OF_FADING.getDefaultInstance()), world.isClientSide()));
			
		} else if (blockState.is(SpectrumBlocks.FAILING)
				&& SpectrumCommon.CONFIG.CanBottleUpFailing
				&& AdvancementHelper.hasAdvancement(user, SpectrumCommon.locate("unlocks/items/bottle_of_failing"))) {
			
			world.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
			cir.setReturnValue(InteractionResultHolder.sidedSuccess(this.fill(handStack, user, SpectrumItems.BOTTLE_OF_FAILING.getDefaultInstance()), world.isClientSide()));
			
		} else if (blockState.is(SpectrumBlocks.RUIN)
				&& SpectrumCommon.CONFIG.CanBottleUpRuin
				&& AdvancementHelper.hasAdvancement(user, SpectrumCommon.locate("unlocks/items/bottle_of_ruin"))) {
			
			world.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
			cir.setReturnValue(InteractionResultHolder.sidedSuccess(this.fill(handStack, user, SpectrumItems.BOTTLE_OF_RUIN.getDefaultInstance()), world.isClientSide()));
			
		} else if (blockState.is(SpectrumBlocks.FORFEITURE)
				&& SpectrumCommon.CONFIG.CanBottleUpForfeiture
				&& AdvancementHelper.hasAdvancement(user, SpectrumCommon.locate("unlocks/items/bottle_of_forfeiture"))) {
			
			world.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
			cir.setReturnValue(InteractionResultHolder.sidedSuccess(this.fill(handStack, user, SpectrumItems.BOTTLE_OF_FORFEITURE.getDefaultInstance()), world.isClientSide()));
		}
	}
	
}
