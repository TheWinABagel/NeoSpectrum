package de.dafuqs.spectrum.blocks.decoration;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AlternatePlayerOnlyGlassBlock extends GlassBlock {
	
	private final Block alternateBlock;
	
	// used for tinted glass to make light not shine through
	private final boolean tinted;
	
	public AlternatePlayerOnlyGlassBlock(Properties settings, Block block, boolean tinted) {
		super(settings);
		this.alternateBlock = block;
		this.tinted = tinted;
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	@Override
	@Deprecated
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext entityShapeContext) {
			Entity entity = entityShapeContext.getEntity();
			if (entity instanceof Player) {
				return Shapes.empty();
			}
		}
		return state.getShape(world, pos);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
		return !tinted;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
		if (tinted) {
			return world.getMaxLightLevel();
		} else {
			return super.getLightBlock(state, world, pos);
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
		if (stateFrom.is(this) || stateFrom.getBlock() == alternateBlock) {
			return true;
		}
		
		return super.skipRendering(state, stateFrom, direction);
	}
	
}
