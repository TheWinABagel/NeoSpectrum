package de.dafuqs.spectrum.blocks.spirit_sallow;

import de.dafuqs.spectrum.api.item.GemstoneColor;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class SpiritVinesBodyBlock extends GrowingPlantBodyBlock implements SpiritVine {
	
	private final GemstoneColor gemstoneColor;
	
	public SpiritVinesBodyBlock(Properties settings, GemstoneColor gemstoneColor) {
		super(settings, Direction.DOWN, SHAPE, false);
		this.registerDefaultState((this.stateDefinition.any()).setValue(YIELD, YieldType.NONE));
		this.gemstoneColor = gemstoneColor;
	}
	
	@Override
	protected GrowingPlantHeadBlock getHeadBlock() {
		switch (gemstoneColor.getDyeColor()) {
			case MAGENTA -> {
				return (GrowingPlantHeadBlock) SpectrumBlocks.MAGENTA_SPIRIT_SALLOW_VINES_BODY;
			}
			case BLACK -> {
				return (GrowingPlantHeadBlock) SpectrumBlocks.BLACK_SPIRIT_SALLOW_VINES_BODY;
			}
			case CYAN -> {
				return (GrowingPlantHeadBlock) SpectrumBlocks.CYAN_SPIRIT_SALLOW_VINES_BODY;
			}
			case WHITE -> {
				return (GrowingPlantHeadBlock) SpectrumBlocks.WHITE_SPIRIT_SALLOW_VINES_BODY;
			}
			case YELLOW -> {
				return (GrowingPlantHeadBlock) SpectrumBlocks.YELLOW_SPIRIT_SALLOW_VINES_BODY;
			}
			default -> {
				return null;
			}
		}
	}
	
	@Override
	protected BlockState updateHeadAfterConvertedFromBody(BlockState from, BlockState to) {
		return to.setValue(YIELD, from.getValue(YIELD));
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
