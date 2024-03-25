package de.dafuqs.spectrum.blocks.decoration;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class DiagonalBlock extends Block {
	
	public static final BooleanProperty DIAGONAL = BooleanProperty.create("diagonal");
	
	public DiagonalBlock(Properties settings) {
		super(settings);
		registerDefaultState(defaultBlockState().setValue(DIAGONAL, false));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		var state = super.getStateForPlacement(ctx);
		var player = ctx.getPlayer();
		
		if (player != null && state != null) {
			var yaw = player.getYRot() + 180 + 360;
			var arc = yaw % 90;
			
			return state.setValue(DIAGONAL, arc > 25 && arc < 65);
		}
		
		return super.getStateForPlacement(ctx);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(DIAGONAL);
	}
}
