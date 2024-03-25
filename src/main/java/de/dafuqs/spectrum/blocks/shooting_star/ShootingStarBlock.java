package de.dafuqs.spectrum.blocks.shooting_star;

import de.dafuqs.spectrum.blocks.PlacedItemBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShootingStarBlock extends PlacedItemBlock implements ShootingStar {
	
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
	public final Type shootingStarType;
	
	public ShootingStarBlock(Properties settings, Type shootingStarType) {
		super(settings);
		this.shootingStarType = shootingStarType;
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public ShootingStar.Type getShootingStarType() {
		return this.shootingStarType;
	}

}
