package de.dafuqs.spectrum.blocks.amphora;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AmphoraBlock extends BarrelBlock {
	
	public AmphoraBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AmphoraBlockEntity amphoraBlockEntity) {
				player.openMenu(amphoraBlockEntity);
				player.awardStat(Stats.OPEN_BARREL);
				PiglinAi.angerNearbyPiglins(player, true);
			}
			
			return InteractionResult.CONSUME;
		}
	}
	
	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AmphoraBlockEntity(pos, state);
	}
	
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof AmphoraBlockEntity amphoraBlockEntity) {
			amphoraBlockEntity.tick();
		}
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomHoverName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AmphoraBlockEntity amphoraBlockEntity) {
				amphoraBlockEntity.setCustomName(itemStack.getHoverName());
			}
		}
	}
	
}
