package de.dafuqs.spectrum.blocks.mob_head;

import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import org.jetbrains.annotations.Nullable;

public class SpectrumSkullBlock extends SkullBlock {
	
	@Nullable
	private static BlockPattern witherBossPattern;
	
	public SpectrumSkullBlock(Type skullType, Properties settings) {
		super(skullType, settings);
	}
	
	private static BlockPattern getWitherSkullPattern() {
		if (witherBossPattern == null) {
			witherBossPattern = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', (pos) ->
							pos.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS))
					.where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.WITHER))
							.or(BlockStatePredicate.forBlock(SpectrumBlocks.getMobWallHead(SpectrumSkullBlockType.WITHER)))))
					.where('~', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.AIR))).build();
		}
		
		return witherBossPattern;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SpectrumSkullBlockEntity(pos, state);
	}
	
	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return null;
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.setPlacedBy(world, pos, state, placer, itemStack);
		
		// Trigger advancement if player builds a wither structure using wither skulls instead of wither skeleton skulls
		if (getType().equals(SpectrumSkullBlockType.WITHER) && placer instanceof ServerPlayer serverPlayerEntity) {
			if (pos.getY() >= world.getMinBuildHeight()) {
				BlockPattern blockPattern = getWitherSkullPattern();
				BlockPattern.BlockPatternMatch result = blockPattern.find(world, pos);
				if (result != null) {
					Support.grantAdvancementCriterion(serverPlayerEntity, "midgame/build_wither_using_wither_heads", "built_wither_using_wither_heads");
				}
			}
		}
	}
	
}
