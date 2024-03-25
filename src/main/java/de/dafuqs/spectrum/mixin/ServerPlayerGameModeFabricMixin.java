package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.api.item.AoEBreakingTool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeFabricMixin {
	
	@Shadow
	@Final
	protected ServerPlayer player;

	@Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"))
	private void spectrum$tryBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		ServerPlayer player = this.player;
		ItemStack stack = player.getMainHandItem();
		if(stack.getItem() instanceof AoEBreakingTool tool) {
			tool.onTryBreakBlock(stack, pos, player);
		}
	}
}