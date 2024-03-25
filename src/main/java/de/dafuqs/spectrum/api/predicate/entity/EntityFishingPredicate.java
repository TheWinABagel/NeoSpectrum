package de.dafuqs.spectrum.api.predicate.entity;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.api.predicate.block.BiomePredicate;
import de.dafuqs.spectrum.api.predicate.block.LightPredicate;
import de.dafuqs.spectrum.api.predicate.world.*;
import net.minecraft.advancements.critereon.FluidPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;

public class EntityFishingPredicate {
	private final FluidPredicate fluidPredicate;
	private final BiomePredicate biomePredicate;
	private final LightPredicate lightPredicate;
	private final DimensionPredicate dimensionPredicate;
	private final MoonPhasePredicate moonPhasePredicate;
	private final TimeOfDayPredicate timeOfDayPredicate;
	private final WeatherPredicate weatherPredicate;
	private final CommandPredicate commandPredicate;
	
	public EntityFishingPredicate(
		FluidPredicate fluidPredicate,
		BiomePredicate biomePredicate,
		LightPredicate lightPredicate,
		DimensionPredicate dimensionPredicate,
		MoonPhasePredicate moonPhasePredicate,
		TimeOfDayPredicate timeOfDayPredicate,
		WeatherPredicate weatherPredicate,
		CommandPredicate commandPredicate)
	{
		this.fluidPredicate = fluidPredicate;
		this.biomePredicate = biomePredicate;
		this.lightPredicate = lightPredicate;
		this.dimensionPredicate = dimensionPredicate;
		this.moonPhasePredicate = moonPhasePredicate;
		this.timeOfDayPredicate = timeOfDayPredicate;
		this.weatherPredicate = weatherPredicate;
		this.commandPredicate = commandPredicate;
	}
	
	public static EntityFishingPredicate fromJson(JsonObject jsonObject) {
		return new EntityFishingPredicate(
				FluidPredicate.fromJson(GsonHelper.getAsJsonObject(jsonObject, "fluid", null)),
				BiomePredicate.fromJson(GsonHelper.getAsJsonObject(jsonObject, "biome", null)),
				LightPredicate.fromJson(GsonHelper.getAsJsonObject(jsonObject, "light", null)),
				DimensionPredicate.fromJson(GsonHelper.getAsJsonObject(jsonObject, "dimension", null)),
				MoonPhasePredicate.fromJson(GsonHelper.getAsJsonObject(jsonObject, "moon_phase", null)),
				TimeOfDayPredicate.fromJson(GsonHelper.getAsJsonObject(jsonObject, "time_of_day", null)),
				WeatherPredicate.fromJson(GsonHelper.getAsJsonObject(jsonObject, "weather", null)),
				CommandPredicate.fromJson(GsonHelper.getAsJsonObject(jsonObject, "command", null))
		);
	}
	
	public boolean test(ServerLevel world, BlockPos pos) {
		return (this.fluidPredicate.matches(world, pos) &&
				this.biomePredicate.test(world, pos) &&
				this.lightPredicate.test(world, pos) &&
				this.dimensionPredicate.test(world, pos) &&
				this.moonPhasePredicate.test(world, pos) &&
				this.timeOfDayPredicate.test(world, pos) &&
				this.weatherPredicate.test(world, pos) &&
				this.commandPredicate.test(world, pos));
	}
}
