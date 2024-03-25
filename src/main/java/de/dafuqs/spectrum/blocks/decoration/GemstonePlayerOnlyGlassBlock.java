package de.dafuqs.spectrum.blocks.decoration;

import de.dafuqs.spectrum.api.item.GemstoneColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GemstonePlayerOnlyGlassBlock extends GemstoneGlassBlock {
	
	public GemstonePlayerOnlyGlassBlock(Properties settings, GemstoneColor gemstoneColor) {
		super(settings, gemstoneColor);
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
	
}
