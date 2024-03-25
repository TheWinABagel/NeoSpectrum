package de.dafuqs.spectrum.blocks.weathering;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringStairsBlock extends StairBlock implements Weathering {
	
	private final Weathering.WeatheringLevel weatheringLevel;
	
	public WeatheringStairsBlock(Weathering.WeatheringLevel weatheringLevel, BlockState baseBlockState, BlockBehaviour.Properties settings) {
		super(baseBlockState, settings);
		this.weatheringLevel = weatheringLevel;
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (shouldTryWeather(world, pos)) {
			this.onRandomTick(state, world, pos, random);
		}
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return Weathering.getIncreasedWeatheredBlock(state.getBlock()).isPresent();
	}
	
	@Override
	public Weathering.WeatheringLevel getAge() {
		return this.weatheringLevel;
	}
	
}
