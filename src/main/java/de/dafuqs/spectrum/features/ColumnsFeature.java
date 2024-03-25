package de.dafuqs.spectrum.features;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * a BasaltColumnsFeature with configurable block state
 */
public class ColumnsFeature extends Feature<ColumnsFeatureConfig> {
	
	private static final ImmutableList<Block> CANNOT_REPLACE_BLOCKS = ImmutableList.of(Blocks.LAVA, Blocks.BEDROCK, Blocks.MAGMA_BLOCK, Blocks.SOUL_SAND, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);
	private static final int field_31495 = 5;
	private static final int field_31496 = 50;
	private static final int field_31497 = 8;
	private static final int field_31498 = 15;
	
	public ColumnsFeature(Codec<ColumnsFeatureConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean place(FeaturePlaceContext<ColumnsFeatureConfig> context) {
		int i = context.chunkGenerator().getSeaLevel();
		BlockPos blockPos = context.origin();
		WorldGenLevel structureWorldAccess = context.level();
		RandomSource random = context.random();
		ColumnsFeatureConfig config = context.config();
		if (!canPlaceAt(structureWorldAccess, i, blockPos.mutable())) {
			return false;
		} else {
			int j = config.height().sample(random);
			boolean bl = random.nextFloat() < 0.9F;
            int k = Math.min(j, bl ? field_31495 : field_31497);
            int l = bl ? field_31496 : field_31498;
            boolean bl2 = false;

            for (BlockPos blockPos2 : BlockPos.randomBetweenClosed(random, l, blockPos.getX() - k, blockPos.getY(), blockPos.getZ() - k, blockPos.getX() + k, blockPos.getY(), blockPos.getZ() + k)) {
                int m = j - blockPos2.distManhattan(blockPos);
                if (m >= 0) {
                    bl2 |= this.placeColumn(structureWorldAccess, i, blockPos2, m, config.reach().sample(random), config.blockState());
                }
            }

            return bl2;
        }
    }

    private boolean placeColumn(LevelAccessor world, int seaLevel, BlockPos pos, int height, int reach, BlockState blockState) {
        boolean bl = false;
        Iterator<BlockPos> var7 = BlockPos.betweenClosed(pos.getX() - reach, pos.getY(), pos.getZ() - reach, pos.getX() + reach, pos.getY(), pos.getZ() + reach).iterator();

        while (true) {
            int i;
            BlockPos blockPos2;
            do {
                if (!var7.hasNext()) {
                    return bl;
                }

                BlockPos blockPos = var7.next();
                i = blockPos.distManhattan(pos);
                blockPos2 = isAirOrFluid(world, seaLevel, blockPos) ? moveDownToGround(world, seaLevel, blockPos.mutable(), i) : moveUpToAir(world, blockPos.mutable(), i);
            } while (blockPos2 == null);

            int j = height - i / 2;

            for (BlockPos.MutableBlockPos mutable = blockPos2.mutable(); j >= 0; --j) {
                if (isAirOrFluid(world, seaLevel, mutable)) {
                    this.setBlock(world, mutable, blockState);
                    mutable.move(Direction.UP);
                    bl = true;
                } else {
                    if (!world.getBlockState(mutable).is(blockState.getBlock())) {
                        break;
                    }
                    mutable.move(Direction.UP);
                }
            }
        }
    }

    @Nullable
    private static BlockPos moveDownToGround(LevelAccessor world, int seaLevel, BlockPos.MutableBlockPos mutablePos, int distance) {
        while (mutablePos.getY() > world.getMinBuildHeight() + 1 && distance > 0) {
            --distance;
            if (canPlaceAt(world, seaLevel, mutablePos)) {
                return mutablePos;
            }

            mutablePos.move(Direction.DOWN);
        }

        return null;
    }

    private static boolean canPlaceAt(LevelAccessor world, int seaLevel, BlockPos.MutableBlockPos mutablePos) {
        if (!isAirOrFluid(world, seaLevel, mutablePos)) {
            return false;
        } else {
            BlockState blockState = world.getBlockState(mutablePos.move(Direction.DOWN));
            mutablePos.move(Direction.UP);
            return !blockState.isAir() && !CANNOT_REPLACE_BLOCKS.contains(blockState.getBlock());
        }
    }

    @Nullable
    private static BlockPos moveUpToAir(LevelAccessor world, BlockPos.MutableBlockPos mutablePos, int distance) {
        while (mutablePos.getY() < world.getMaxBuildHeight() && distance > 0) {
            --distance;
            BlockState blockState = world.getBlockState(mutablePos);
            if (CANNOT_REPLACE_BLOCKS.contains(blockState.getBlock())) {
                return null;
            }

            if (blockState.isAir()) {
                return mutablePos;
            }

            mutablePos.move(Direction.UP);
        }

        return null;
    }

    private static boolean isAirOrFluid(LevelAccessor world, int seaLevel, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isAir() || !blockState.getFluidState().isEmpty() && pos.getY() <= seaLevel;
    }

}
