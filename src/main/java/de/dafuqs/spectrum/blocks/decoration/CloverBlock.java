package de.dafuqs.spectrum.blocks.decoration;

import de.dafuqs.spectrum.registries.SpectrumConfiguredFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CloverBlock extends TallGrassBlock {

	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 1.0D, 14.0D, 2.0D, 14.0D);

	public CloverBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
		world.registryAccess()
				.registryOrThrow(Registries.CONFIGURED_FEATURE)
				.get(SpectrumConfiguredFeatures.CLOVER_PATCH)
				.place(world, world.getChunkSource().getGenerator(), random, pos);
	}

}
