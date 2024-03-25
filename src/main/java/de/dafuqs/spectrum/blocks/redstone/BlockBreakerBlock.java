package de.dafuqs.spectrum.blocks.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BlockBreakerBlock extends RedstoneInteractionBlock implements EntityBlock {
	
	private static final ItemStack BREAK_STACK = Items.IRON_PICKAXE.getDefaultInstance();
	
	public BlockBreakerBlock(Properties settings) {
		super(settings);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BlockBreakerBlockEntity(pos, state);
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (placer instanceof ServerPlayer serverPlayerEntity) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BlockBreakerBlockEntity blockBreakerBlockEntity) {
				blockBreakerBlockEntity.setOwner(serverPlayerEntity);
			}
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		var isTriggered = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
		boolean wasTriggered = state.getValue(TRIGGERED);
		
		if (isTriggered && !wasTriggered) {
			if (!world.isClientSide) {
				this.destroy(world, pos, state.getValue(ORIENTATION).front());
			}
			world.setBlock(pos, state.setValue(TRIGGERED, true), Block.UPDATE_INVISIBLE);
		} else if (!isTriggered && wasTriggered) {
			world.setBlock(pos, state.setValue(TRIGGERED, false), Block.UPDATE_INVISIBLE);
		}
	}
	
	protected void destroy(Level world, BlockPos breakerPos, Direction direction) {
		BlockPos breakingPos = breakerPos.relative(direction);
		BlockState blockState = world.getBlockState(breakingPos);
		
		if (blockState.isAir() || blockState.getBlock() instanceof BaseFireBlock) {
			return;
		}
		
		float hardness = blockState.getDestroySpeed(world, breakingPos);
		if (hardness < 0 || hardness > 8) {
			return;
		}
		
		BlockEntity blockEntity = world.getBlockEntity(breakerPos);
		if (!(blockEntity instanceof BlockBreakerBlockEntity blockBreakerBlockEntity)) {
			return;
		}
		Player owner = blockBreakerBlockEntity.getOwnerIfOnline();
		
		this.breakBlock(world, breakingPos, owner);
		
		Vec3 centerPos = Vec3.atCenterOf(breakingPos);
		((ServerLevel) world).sendParticles(ParticleTypes.EXPLOSION, centerPos.x(), centerPos.y(), centerPos.z(), 1, 0.0, 0.0, 0.0, 1.0);
	}
	
	public void breakBlock(Level world, BlockPos pos, Player breaker) {
		BlockState blockState = world.getBlockState(pos);
		FluidState fluidState = world.getFluidState(pos);
		
		world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(blockState));
		world.playSound(null, pos, blockState.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.2f, (1.0f + world.random.nextFloat()) * 2f);
		
		BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropResources(blockState, world, pos, blockEntity, breaker, BREAK_STACK);
		
		if (world.setBlock(pos, fluidState.createLegacyBlock(), Block.UPDATE_ALL, 512)) {
			world.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(breaker, blockState));
		}
	}
	
}
