package de.dafuqs.spectrum.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class RandomBlockProximityPatchFeature extends Feature<RandomBlockProximityPatchFeatureConfig> {
	
	public RandomBlockProximityPatchFeature(Codec<RandomBlockProximityPatchFeatureConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean place(FeaturePlaceContext<RandomBlockProximityPatchFeatureConfig> context) {
		RandomBlockProximityPatchFeatureConfig randomPatchFeatureConfig = context.config();
		RandomSource random = context.random();
		BlockPos blockPos = context.origin();
		WorldGenLevel structureWorldAccess = context.level();
		
		int placedFeatureCount = 0;
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		int xzSpreadPlus1 = randomPatchFeatureConfig.xzSpread() + 1;
		int ySpreadPlus1 = randomPatchFeatureConfig.ySpread() + 1;

        for (int l = 0; l < randomPatchFeatureConfig.tries(); ++l) {
            mutable.setWithOffset(blockPos, random.nextInt(xzSpreadPlus1) - random.nextInt(xzSpreadPlus1), random.nextInt(ySpreadPlus1) - random.nextInt(ySpreadPlus1), random.nextInt(xzSpreadPlus1) - random.nextInt(xzSpreadPlus1));
            if (closeToBlock(structureWorldAccess, mutable, randomPatchFeatureConfig.blockScanRange(), randomPatchFeatureConfig.blocksToCheckFor())) {
                if (randomPatchFeatureConfig.closeToBlockFeature().value().place(structureWorldAccess, context.chunkGenerator(), random, mutable)) {
                    ++placedFeatureCount;
                }
            } else {
                if (randomPatchFeatureConfig.fallbackFeature().value().place(structureWorldAccess, context.chunkGenerator(), random, mutable)) {
                    ++placedFeatureCount;
                }
            }

        }

        return placedFeatureCount > 0;
    }

    protected boolean closeToBlock(WorldGenLevel world, BlockPos pos, int searchRange, HolderSet<Block> blocksToSearchFor) {
        for (BlockPos currentPos : BlockPos.withinManhattan(pos, searchRange, searchRange, searchRange)) {
            if (world.getBlockState(currentPos).is(blocksToSearchFor)) {
                return true;
            }
        }
        return false;
    }

}
