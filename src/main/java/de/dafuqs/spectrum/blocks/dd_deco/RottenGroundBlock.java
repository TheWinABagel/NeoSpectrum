package de.dafuqs.spectrum.blocks.dd_deco;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MudBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RottenGroundBlock extends MudBlock {
    
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 11, 16);
    
    public RottenGroundBlock(Properties settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
}
