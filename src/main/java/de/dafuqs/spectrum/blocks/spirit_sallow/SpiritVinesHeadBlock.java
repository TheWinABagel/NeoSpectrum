package de.dafuqs.spectrum.blocks.spirit_sallow;

import de.dafuqs.spectrum.api.item.GemstoneColor;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class SpiritVinesHeadBlock extends GrowingPlantHeadBlock implements SpiritVine {
	
	private final GemstoneColor gemstoneColor;
	
	public SpiritVinesHeadBlock(Properties settings, GemstoneColor gemstoneColor) {
		super(settings, Direction.DOWN, SHAPE, false, 0.0D);
		this.registerDefaultState((this.stateDefinition.any()).setValue(YIELD, YieldType.NONE));
		this.gemstoneColor = gemstoneColor;
	}
	
	@Override
	protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
		return 1;
	}
	
	@Override
	protected boolean canGrowInto(BlockState state) {
		return state.isAir();
	}
	
	@Override
	protected Block getBodyBlock() {
		switch (gemstoneColor.getDyeColor()) {
			case MAGENTA -> {
				return SpectrumBlocks.MAGENTA_SPIRIT_SALLOW_VINES_HEAD;
			}
			case BLACK -> {
				return SpectrumBlocks.BLACK_SPIRIT_SALLOW_VINES_HEAD;
			}
			case CYAN -> {
				return SpectrumBlocks.CYAN_SPIRIT_SALLOW_VINES_HEAD;
			}
			case WHITE -> {
				return SpectrumBlocks.WHITE_SPIRIT_SALLOW_VINES_HEAD;
			}
			case YELLOW -> {
				return SpectrumBlocks.YELLOW_SPIRIT_SALLOW_VINES_HEAD;
			}
			default -> {
				return null;
			}
		}
	}
	
	@Override
	protected BlockState updateBodyAfterConvertedFromHead(BlockState from, BlockState to) {
		return to.setValue(YIELD, from.getValue(YIELD));
	}
	
	@Override
	protected BlockState getGrowIntoState(BlockState state, RandomSource random) {
		return super.getGrowIntoState(state, random).setValue(YIELD, YieldType.NONE);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return new ItemStack(SpiritVine.getYieldItem(state, true));
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return SpiritVine.pick(state, world, pos);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(YIELD);
	}
	
	@Override
	public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
		return false;
	}
	
	@Override
	public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
		return true;
	}
	
	@Override
	public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
		world.setBlock(pos, state.setValue(YIELD, YieldType.NONE), 2);
	}
}
