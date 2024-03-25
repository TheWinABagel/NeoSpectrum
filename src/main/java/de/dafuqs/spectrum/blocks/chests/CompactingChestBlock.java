package de.dafuqs.spectrum.blocks.chests;

import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CompactingChestBlock extends SpectrumChestBlock {
	
	public CompactingChestBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CompactingChestBlockEntity(pos, state);
	}
	
	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SpectrumBlockEntities.COMPACTING_CHEST, CompactingChestBlockEntity::tick);
	}
	
	@Override
	public void openScreen(Level world, BlockPos pos, Player player) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof CompactingChestBlockEntity compactingChestBlockEntity) {
			if (!isChestBlocked(world, pos)) {
				player.openMenu(compactingChestBlockEntity);
			}
		}
	}
	
}
