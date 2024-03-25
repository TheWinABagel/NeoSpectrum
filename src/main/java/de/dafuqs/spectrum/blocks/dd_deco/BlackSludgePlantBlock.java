package de.dafuqs.spectrum.blocks.dd_deco;

import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlackSludgePlantBlock extends BushBlock {

	protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 6.0, 12.0);

	public BlackSludgePlantBlock(Properties settings) {
		super(settings);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
		return floor.is(SpectrumBlockTags.BLACK_SLUDGE_BLOCKS) || super.mayPlaceOn(floor, world, pos);
	}

	@Override
	public float getMaxHorizontalOffset() {
		return 0.08F;
	}

}
