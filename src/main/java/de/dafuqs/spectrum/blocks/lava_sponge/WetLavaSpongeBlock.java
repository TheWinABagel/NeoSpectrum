package de.dafuqs.spectrum.blocks.lava_sponge;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.WetSpongeBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WetLavaSpongeBlock extends WetSpongeBlock {
	
	public WetLavaSpongeBlock(Properties settings) {
		super(settings);
	}
	
	// faster than fire (30+ 0-10)
	// even more in the nether
	private static int getRandomTickTime(Level world) {
		if (world.dimensionType().ultraWarm()) {
			return 10 + world.random.nextInt(5);
		} else {
			return 20 + world.random.nextInt(10);
		}
	}
	
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		world.scheduleTick(pos, this, getRandomTickTime(world));
		
		if (world.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
			int xOffset = 2 - random.nextInt(5);
			int yOffset = 1 - random.nextInt(3);
			int zOffset = 2 - random.nextInt(5);
			
			BlockPos targetPos = pos.offset(xOffset, yOffset, zOffset);
			if (BaseFireBlock.canBePlacedAt(world, targetPos, Direction.UP)) {
				world.setBlockAndUpdate(targetPos, BaseFireBlock.getState(world, targetPos));
			}
		}
	}
	
	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		world.scheduleTick(pos, this, getRandomTickTime(world));
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		Direction direction = Direction.getRandom(random);
		if (direction != Direction.UP) {
			BlockPos blockPos = pos.relative(direction);
			BlockState blockState = world.getBlockState(blockPos);
			if (!state.canOcclude() || !blockState.isFaceSturdy(world, blockPos, direction.getOpposite())) {
				double d = pos.getX();
				double e = pos.getY();
				double f = pos.getZ();
				if (direction == Direction.DOWN) {
					e -= 0.05D;
					d += random.nextDouble();
					f += random.nextDouble();
				} else {
					e += random.nextDouble() * 0.8D;
					if (direction.getAxis() == Direction.Axis.X) {
						f += random.nextDouble();
						if (direction == Direction.EAST) {
							++d;
						} else {
							d += 0.05D;
						}
					} else {
						d += random.nextDouble();
						if (direction == Direction.SOUTH) {
							++f;
						} else {
							f += 0.05D;
						}
					}
				}
				
				world.addParticle(ParticleTypes.DRIPPING_LAVA, d, e, f, 0.0D, 0.0D, 0.0D);
			}
		}
	}
	
}
