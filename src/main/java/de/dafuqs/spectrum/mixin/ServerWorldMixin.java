package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.events.SpectrumGameEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {

	@Inject(at = @At("TAIL"), method = "spawnEntity")
	private void spectrum$emitSpawnEntityEvent(Entity entity, final CallbackInfoReturnable<Boolean> info) {
		entity.gameEvent(SpectrumGameEvents.ENTITY_SPAWNED);
	}

	@Inject(method = "onBlockChanged(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V", at = @At("HEAD"))
	private void spectrum$emitBlockChangedEvent(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci) {
		((ServerLevel) (Object) this).gameEvent(SpectrumGameEvents.BLOCK_CHANGED, pos, GameEvent.Context.of(newBlock));
	}
	
}