package de.dafuqs.spectrum.blocks;

import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BismuthClusterBlock extends AmethystClusterBlock {
	
	public static final int GROWTH_CHECK_RADIUS = 3;
	public static final int GROWTH_CHECK_TRIES = 5;
	public static final TagKey<Block> CONSUMED_TAG_TO_GROW = BlockTags.BEACON_BASE_BLOCKS;
	public static final BlockState CONSUMED_TARGET_STATE = Blocks.COBBLESTONE.defaultBlockState();
	
	public final int height;
	public final @Nullable AmethystClusterBlock grownBlock;
	
	public BismuthClusterBlock(int height, int xzOffset, @Nullable AmethystClusterBlock grownBlock, Properties settings) {
		super(height, xzOffset, settings);
		this.height = height;
		this.grownBlock = grownBlock;
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return grownBlock != null;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		super.randomTick(state, world, pos, random);
		if (!world.isClientSide && grownBlock != null && searchAndConsumeBlock(world, pos, GROWTH_CHECK_RADIUS, CONSUMED_TAG_TO_GROW, CONSUMED_TARGET_STATE, GROWTH_CHECK_TRIES, random)) {
			BlockState newState = grownBlock.defaultBlockState().setValue(FACING, state.getValue(FACING)).setValue(WATERLOGGED, state.getValue(WATERLOGGED));
			world.setBlockAndUpdate(pos, newState);
			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.8F, 0.9F + random.nextFloat() * 0.2F);
			
			Vec3 sourcePos = new Vec3(pos.getX() + 0.5D, pos.getY() + height / 16.0, pos.getZ() + 0.5D);
			Vec3 randomOffset = new Vec3(0.25, height / 32.0, 0.25);
			Vec3 randomVelocity = new Vec3(0.1, 0.1, 0.1);
			SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity(world, sourcePos, SpectrumParticleTypes.YELLOW_CRAFTING, 2, randomOffset, randomVelocity);
			SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity(world, sourcePos, SpectrumParticleTypes.LIME_CRAFTING, 2, randomOffset, randomVelocity);
			SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity(world, sourcePos, SpectrumParticleTypes.PURPLE_CRAFTING, 2, randomOffset, randomVelocity);
			SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity(world, sourcePos, SpectrumParticleTypes.ORANGE_CRAFTING, 2, randomOffset, randomVelocity);
		}
	}
	
	public static boolean searchAndConsumeBlock(Level world, BlockPos pos, int radius, TagKey<Block> tagKey, BlockState targetState, int tries, RandomSource random) {
		for (int i = 0; i < tries; i++) {
			BlockPos offsetPos = pos.offset(radius - random.nextInt(1 + radius + radius), radius - random.nextInt(1 + radius + radius), radius - random.nextInt(1 + radius + radius));
			BlockState offsetState = world.getBlockState(offsetPos);
			if (offsetState.is(tagKey)) {
				world.setBlockAndUpdate(offsetPos, targetState);
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), offsetState.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.8F, 0.9F + random.nextFloat() * 0.2F);
				return true;
			}
		}
		return false;
	}
	
}
