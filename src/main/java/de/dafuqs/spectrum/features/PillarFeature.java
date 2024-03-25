package de.dafuqs.spectrum.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

// a version of BasaltPillarFeature with configurable block state
public class PillarFeature extends Feature<BlockStateFeatureConfig> {
	
	public PillarFeature(Codec<BlockStateFeatureConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean place(FeaturePlaceContext<BlockStateFeatureConfig> context) {
		BlockPos blockPos = context.origin();
		WorldGenLevel structureWorldAccess = context.level();
		RandomSource random = context.random();
		if (structureWorldAccess.isEmptyBlock(blockPos) && !structureWorldAccess.isEmptyBlock(blockPos.above())) {
			BlockState blockState = context.config().blockState();
			
			BlockPos.MutableBlockPos mutable = blockPos.mutable();
			BlockPos.MutableBlockPos mutable2 = blockPos.mutable();
			boolean bl = true;
			boolean bl2 = true;
            boolean bl3 = true;
            boolean bl4 = true;

            while (structureWorldAccess.isEmptyBlock(mutable)) {
                if (structureWorldAccess.isOutsideBuildHeight(mutable)) {
                    return true;
                }

                structureWorldAccess.setBlock(mutable, blockState, 2);
                bl = bl && this.stopOrPlace(structureWorldAccess, random, mutable2.setWithOffset(mutable, Direction.NORTH), blockState);
                bl2 = bl2 && this.stopOrPlace(structureWorldAccess, random, mutable2.setWithOffset(mutable, Direction.SOUTH), blockState);
                bl3 = bl3 && this.stopOrPlace(structureWorldAccess, random, mutable2.setWithOffset(mutable, Direction.WEST), blockState);
                bl4 = bl4 && this.stopOrPlace(structureWorldAccess, random, mutable2.setWithOffset(mutable, Direction.EAST), blockState);
                mutable.move(Direction.DOWN);
            }

            mutable.move(Direction.UP);
            this.tryPlace(structureWorldAccess, random, mutable2.setWithOffset(mutable, Direction.NORTH), blockState);
            this.tryPlace(structureWorldAccess, random, mutable2.setWithOffset(mutable, Direction.SOUTH), blockState);
            this.tryPlace(structureWorldAccess, random, mutable2.setWithOffset(mutable, Direction.WEST), blockState);
            this.tryPlace(structureWorldAccess, random, mutable2.setWithOffset(mutable, Direction.EAST), blockState);
            mutable.move(Direction.DOWN);
            BlockPos.MutableBlockPos mutable3 = new BlockPos.MutableBlockPos();

            for (int x = -3; x < 4; ++x) {
                for (int z = -3; z < 4; ++z) {
                    int k = Mth.abs(x) * Mth.abs(z);
                    if (random.nextInt(10) < 10 - k) {
                        mutable3.set(mutable.offset(x, 0, z));
                        int l = 3;

                        while (structureWorldAccess.isEmptyBlock(mutable2.setWithOffset(mutable3, Direction.DOWN))) {
                            mutable3.move(Direction.DOWN);
                            --l;
                            if (l <= 0) {
                                break;
                            }
                        }

                        if (!structureWorldAccess.isEmptyBlock(mutable2.setWithOffset(mutable3, Direction.DOWN))) {
                            structureWorldAccess.setBlock(mutable3, blockState, 2);
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private void tryPlace(LevelAccessor world, @NotNull RandomSource random, BlockPos pos, BlockState blockState) {
        if (random.nextBoolean()) {
            world.setBlock(pos, blockState, 2);
        }
    }

    private boolean stopOrPlace(LevelAccessor world, @NotNull RandomSource random, BlockPos pos, BlockState blockState) {
        if (random.nextInt(10) != 0) {
            world.setBlock(pos, blockState, 2);
            return true;
        }
        return false;
    }

}
