package de.dafuqs.spectrum.blocks.jade_vines;

import de.dafuqs.spectrum.blocks.decoration.SpectrumFacingBlock;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;

public class JadeiteFlowerBlock extends SpectrumFacingBlock {

    public JadeiteFlowerBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState());
    }
    
    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        var facing = state.getValue(FACING);
        var root = pos.relative(facing.getOpposite());
        var supportBlock = world.getBlockState(root);
        return (facing.getAxis().isVertical() && supportBlock.is(SpectrumBlocks.JADEITE_LOTUS_STEM)) || supportBlock.isFaceSturdy(world, root, facing, SupportType.CENTER);
    }
    
    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return SpectrumItems.JADEITE_LOTUS_BULB.getDefaultInstance();
    }
    
    @Override
	@SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1);
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        var amount = random.nextInt(18) + 9;
        for (int i = 0; i < amount; i++) {
            var xOffset = Mth.clamp(Mth.normal(random, 0.5F, 5.85F), -9F, 9F) + 0.5F;
            var yOffset = Mth.clamp(Mth.normal(random, 0.5F, 5.85F), -9F, 9F) + 0.5F;
            var zOffset = Mth.clamp(Mth.normal(random, 0.5F, 5.85F), -9F, 9F) + 0.5F;
            world.addAlwaysVisibleParticle(ParticleTypes.END_ROD, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, random.nextFloat() * 0.05 - 0.025, random.nextFloat() * 0.05 - 0.025, random.nextFloat() * 0.05 - 0.025);
        }
    }

    @Override
	@SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (!state.canSurvive(world, pos)) {
            world.destroyBlock(pos, true);
        }
    }

}
