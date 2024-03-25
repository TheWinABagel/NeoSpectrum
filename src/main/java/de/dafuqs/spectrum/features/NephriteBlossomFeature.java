package de.dafuqs.spectrum.features;

import com.mojang.serialization.Codec;
import de.dafuqs.spectrum.blocks.jade_vines.NephriteBlossomLeavesBlock;
import de.dafuqs.spectrum.blocks.jade_vines.NephriteBlossomStemBlock;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.List;

public class NephriteBlossomFeature extends Feature<NephriteBlossomFeatureConfig> {

    private static final List<Direction> VALID_DIRS = List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    public NephriteBlossomFeature(Codec<NephriteBlossomFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NephriteBlossomFeatureConfig> context) {
        var world = context.level();
        var origin = context.origin();
        var random = context.random();
        var chunkGen = context.chunkGenerator();
        var floor = world.getBlockState(origin.below());
        var flowering = context.config().flowering();

        if (!floor.is(BlockTags.DIRT))
            return false;

        var stemHeight = Math.round(Mth.normal(random, 2, 1F) + 1);

        if (stemHeight + origin.getY() > chunkGen.getGenDepth() || !isReplaceable(world, origin, true))
            return false;

        generateStem(world, origin, stemHeight);
        genereateLeaves(world, origin, random, stemHeight, flowering);

        return true;
    }

    private void generateStem(LevelAccessor world, BlockPos origin, int stemHeight) {
        var stemPointer = origin.mutable();
        var topStem = false;

        for (int height = 0; height < stemHeight; height++) {

            if (height == 0) {
                this.setBlock(world, stemPointer, SpectrumBlocks.NEPHRITE_BLOSSOM_STEM.defaultBlockState());
                topStem = true;
            }
            else if (isReplaceable(world, stemPointer, true)) {
                this.setBlock(world, stemPointer, NephriteBlossomStemBlock.getStemVariant(topStem));
                topStem = !topStem;
            }
            stemPointer.move(0, 1, 0);
        }
    }

    private void genereateLeaves(LevelAccessor world, BlockPos origin, RandomSource random, int stemHeight, boolean flowering) {
        var leafHeight = Math.round(Mth.normal(random, 2.5F, 0.9F) + 1.85F);
        var leafPointer = origin.mutable().move(0, stemHeight, 0);
        var leafDirection = VALID_DIRS.get(random.nextInt(4));

        for (int i = 0; i < leafHeight; i++) {
            for(int leaf = 0; leaf < 4; leaf++) {
                leafPointer.move(leafDirection);
                setBlockStateWithoutUpdatingNeighbors(world, leafPointer, getLeafState(random, flowering));
                leafDirection = cycleDirections(leafDirection, 1);
            }

            if (i != 0 && i != leafHeight - 1) {
                leafDirection = leafDirection.getOpposite();
                for(int leaf = 0; leaf < 4; leaf++) {
                    leafPointer.move(leafDirection);
                    setBlockStateWithoutUpdatingNeighbors(world, leafPointer, getLeafState(random, flowering));
                    leafDirection = cycleDirections(leafDirection, 1);
                }
                leafDirection = leafDirection.getOpposite();
            }
    
            leafPointer.move(0, 1, 0);
            if (random.nextBoolean() ^ i % 3 == 0)
                leafDirection = cycleDirections(leafDirection, random.nextInt(3) - 1);
        }
    }
    
    private static void setBlockStateWithoutUpdatingNeighbors(LevelAccessor world, BlockPos pos, BlockState state) {
        if (isReplaceable(world, pos, false)) {
            world.setBlock(pos, state, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_ALL);
        }
    }
    
    private BlockState getLeafState(RandomSource random, boolean allowFlowering) {
        var state = SpectrumBlocks.NEPHRITE_BLOSSOM_LEAVES.defaultBlockState().setValue(NephriteBlossomLeavesBlock.DISTANCE, 1);
        if (!allowFlowering) {
            return state;
        }
        if (random.nextBoolean()) {
            return state.setValue(NephriteBlossomLeavesBlock.AGE, 1);
        }
        if (random.nextBoolean()) {
            return state.setValue(NephriteBlossomLeavesBlock.AGE, 2);
        }
        return state;
    }

    private Direction cycleDirections(Direction currentDir, int change) {
        return getDirectionFor(getDirectionOridinal(currentDir) + Math.abs(change));
    }

    private Direction getDirectionFor(int ordinal) {
        return VALID_DIRS.get(ordinal % 4);
    }

    private int getDirectionOridinal(Direction direction) {
        return VALID_DIRS.indexOf(direction);
    }

    private static boolean isReplaceable(LevelAccessor world, BlockPos pos, boolean replacePlants) {
        return world.isStateAtPosition(pos, (state) -> state.canBeReplaced() || replacePlants);
    }

}
