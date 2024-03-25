package de.dafuqs.spectrum.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class GilledFungusFeature extends Feature<GilledFungusFeatureConfig> {
	
	public GilledFungusFeature(Codec<GilledFungusFeatureConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean place(FeaturePlaceContext<GilledFungusFeatureConfig> context) {
		WorldGenLevel structureWorldAccess = context.level();
		BlockPos blockPos = context.origin();
		GilledFungusFeatureConfig hugeFungusFeatureConfig = context.config();
		Block validBaseBlock = hugeFungusFeatureConfig.validBase();
		BlockState baseBlock = structureWorldAccess.getBlockState(blockPos.below());
		
		if (!baseBlock.is(validBaseBlock)) {
			return false;
		}

        RandomSource random = context.random();
        ChunkGenerator chunkGenerator = context.chunkGenerator();

        int stemHeight = Mth.nextInt(random, 4, 9);
        if (random.nextInt(12) == 0) {
            stemHeight *= 2;
        }
        if (blockPos.getY() + stemHeight + 1 >= chunkGenerator.getGenDepth()) {
            return false;
        }

        structureWorldAccess.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 4);
        this.generateStem(structureWorldAccess, hugeFungusFeatureConfig, blockPos, stemHeight);
        this.generateHat(structureWorldAccess, random, hugeFungusFeatureConfig, blockPos, stemHeight);
        return true;
    }

    private static boolean isReplaceable(LevelAccessor world, BlockPos pos, boolean replacePlants) {
        return world.isStateAtPosition(pos, (state) -> state.canBeReplaced() || replacePlants);
    }

    private void generateStem(LevelAccessor world, GilledFungusFeatureConfig config, BlockPos pos, int stemHeight) {
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		BlockState blockState = config.stem().defaultBlockState();
		int i = 0;
        for (int x = -i; x <= i; ++x) {
            for (int z = -i; z <= i; ++z) {
                for (int y = 0; y < stemHeight; ++y) {
                    mutable.setWithOffset(pos, x, y, z);
                    if (isReplaceable(world, mutable, true)) {
                        this.setBlock(world, mutable, blockState);
                    }
                }
            }
        }
    }

    private void generateHat(LevelAccessor world, RandomSource random, GilledFungusFeatureConfig config, BlockPos pos, int stemHeight) {
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		int hatWidth = Math.min(random.nextInt(2 + stemHeight / 4) + 3, 4);
		int currentHatWidth = hatWidth;
		int outerThreshold = hatWidth / 2;
	
		BlockState stemState = config.stem().defaultBlockState();
		BlockState gillsState = config.gills().defaultBlockState();
		BlockState capState = config.cap().defaultBlockState();
	
		for (int y = 0; y <= hatWidth; ++y) {
			for (int x = -currentHatWidth; x <= currentHatWidth; ++x) {
				for (int z = -currentHatWidth; z <= currentHatWidth; ++z) {
				
					boolean isCorner = Math.abs(x) == currentHatWidth && Math.abs(z) == currentHatWidth;
					if (isCorner) {
						continue;
					}

                    mutable.setWithOffset(pos, x, stemHeight + y, z);
                    if (isReplaceable(world, mutable, false)) {
                        boolean isInnerCorner = Math.abs(x) == currentHatWidth - 1 && Math.abs(z) == currentHatWidth - 1;
                        boolean isInner = Math.abs(x) < currentHatWidth && Math.abs(z) < currentHatWidth;
                        boolean isLowestLevel = y == 0;

                        if (x == 0 && z == 0) {
							this.setBlock(world, mutable, currentHatWidth < 2 ? capState : stemState);
                        } else if (isInner && !isInnerCorner) {
                            if (!isLowestLevel || Math.abs(x) > outerThreshold || Math.abs(z) > outerThreshold) {
								this.setBlock(world, mutable, gillsState.setValue(RotatedPillarBlock.AXIS, Math.abs(x) < Math.abs(z) ? Direction.Axis.X : Direction.Axis.Z));
                            }
                        } else {
							this.setBlock(world, mutable, capState);
                        }
                    }
                }
            }
            currentHatWidth -= 1;
        }
    }

}
