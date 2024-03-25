package de.dafuqs.spectrum.helpers;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

public class Support {
	
	private static final ResourceLocation PROGRESSION_FINISHED_ADVANCEMENT_IDENTIFIER = SpectrumCommon.locate("endgame/finish_progression");
	public static final DecimalFormat DF = new DecimalFormat("0");
	public static final DecimalFormat DF1 = new DecimalFormat("0.0");
	public static final DecimalFormat DF2 = new DecimalFormat("0.00");

	static {
		DF.setRoundingMode(RoundingMode.DOWN);
		DF1.setRoundingMode(RoundingMode.DOWN);
		DF2.setRoundingMode(RoundingMode.DOWN);
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
		return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
	}
	
	public static @NotNull Optional<TagKey<Block>> getFirstMatchingBlockTag(@NotNull BlockState blockState, @NotNull List<TagKey<Block>> tags) {
		return blockState.getTags().filter(tags::contains).findFirst();
	}
	
	public static String getWithOneDecimalAfterComma(float number) {
		return DF1.format(number);
	}
	
	public static String getShortenedNumberString(double number) {
		if (number > 1000000000D) {
			return DF2.format(number / 1000000000D) + "G";
		} else if (number > 1000000D) {
			return DF2.format(number / 1000000D) + "M";
		} else if (number > 1000D) {
			return DF2.format(number / 1000D) + "K";
		} else {
			return DF.format(number);
		}
	}
	
	public static String getShortenedNumberString(long number) {
		if (number > 1000000000L) {
			return DF2.format(number / 1000000000D) + "G";
		} else if (number > 1000000L) {
			return DF2.format(number / 1000000D) + "M";
		} else if (number > 1000L) {
			return DF2.format(number / 1000D) + "K";
		} else {
			return DF.format(number);
		}
	}
	
	/**
	 * Calculates the percentage of x / y from 0-100, but in a way it feels logical to players
	 * If x > 0 the result is always at least 1%,
	 * If it approaches 100%, but is not exactly 100%, returns 99
	 */
	public static String getSensiblePercent(long x, long y) {
		if (y == 0) {
			return "0";
		}
		
		double result = (double) x / y;
		if (result < 0.01 && x > 0) {
			return "1";
		} else if (result > 0.99 && x != y) {
			return "99";
		} else {
			return DF.format(Math.round(result * 100L));
		}
	}
	
	public static int getIntFromDecimalWithChance(double d, @NotNull RandomSource random) {
		boolean roundUp = (random.nextFloat() < d % 1);
		if (roundUp) {
			return ((int) d) + 1;
		} else {
			return (int) d;
		}
	}
	
	
	/**
	 * Returns a relative new BlockPos based on a facing direction and a vector
	 *
	 * @param origin           the source position
	 * @param forwardUpRight   a vector specifying the amount of blocks forward, up and right
	 * @param horizontalFacing the facing direction
	 * @return the blockpos with forwardUpRight offset from origin when facing horizontalFacing
	 */
	public static BlockPos directionalOffset(BlockPos origin, Vec3i forwardUpRight, @NotNull Direction horizontalFacing) {
		switch (horizontalFacing) {
			case NORTH -> {
				return origin.offset(forwardUpRight.getZ(), forwardUpRight.getY(), -forwardUpRight.getX());
			}
			case EAST -> {
				return origin.offset(forwardUpRight.getX(), forwardUpRight.getY(), forwardUpRight.getZ());
			}
			case SOUTH -> {
				return origin.offset(-forwardUpRight.getZ(), forwardUpRight.getY(), forwardUpRight.getX());
			}
			case WEST -> {
				return origin.offset(-forwardUpRight.getX(), forwardUpRight.getY(), -forwardUpRight.getZ());
			}
			default -> {
				SpectrumCommon.logWarning("Called directionalOffset with facing" + horizontalFacing + " this is not supported.");
				return origin;
			}
		}
	}
	
	public static void grantAdvancementCriterion(@NotNull ServerPlayer serverPlayerEntity, ResourceLocation advancementIdentifier, String criterion) {
		if (serverPlayerEntity.getServer() == null) {
			return;
		}
		ServerAdvancementManager sal = serverPlayerEntity.getServer().getAdvancements();
		PlayerAdvancements tracker = serverPlayerEntity.getAdvancements();
		
		Advancement advancement = sal.getAdvancement(advancementIdentifier);
		if (advancement == null) {
			SpectrumCommon.logError("Trying to grant a criterion \"" + criterion + "\" for an advancement that does not exist: " + advancementIdentifier);
		} else {
			if (!tracker.getOrStartProgress(advancement).isDone()) {
				tracker.award(advancement, criterion);
			}
		}
	}
	
	public static void grantAdvancementCriterion(@NotNull ServerPlayer serverPlayerEntity, String advancementString, String criterion) {
		grantAdvancementCriterion(serverPlayerEntity, SpectrumCommon.locate(advancementString), criterion);
	}
	
	public static @NotNull String getReadableDimensionString(@NotNull String dimensionKeyString) {
		switch (dimensionKeyString) {
			case "minecraft:overworld" -> {
				return "Overworld";
			}
			case "minecraft:nether" -> {
				return "Nether";
			}
			case "minecraft:end" -> {
				return "End";
			}
			case "spectrum:deeper_down" -> {
				return "Deeper Down";
			}
			default -> {
				if (dimensionKeyString.contains(":")) {
					return dimensionKeyString.substring(dimensionKeyString.indexOf(":") + 1);
				} else {
					return dimensionKeyString;
				}
			}
		}
	}
	
	@Contract(pure = true)
	public static Direction directionFromRotation(@NotNull Rotation blockRotation) {
		switch (blockRotation) {
			case NONE -> {
				return Direction.NORTH;
			}
			case CLOCKWISE_90 -> {
				return Direction.EAST;
			}
			case CLOCKWISE_180 -> {
				return Direction.SOUTH;
			}
			default -> {
				return Direction.WEST;
			}
		}
	}
	
	@Contract(pure = true)
	public static Rotation rotationFromDirection(@NotNull Direction direction) {
		switch (direction) {
			case EAST -> {
				return Rotation.CLOCKWISE_90;
			}
			case SOUTH -> {
				return Rotation.CLOCKWISE_180;
			}
			case WEST -> {
				return Rotation.COUNTERCLOCKWISE_90;
			}
			default -> {
				return Rotation.NONE;
			}
		}
	}
	
	public static boolean hasPlayerFinishedMod(Player player) {
		return AdvancementHelper.hasAdvancement(player, PROGRESSION_FINISHED_ADVANCEMENT_IDENTIFIER);
	}
	
	public static Optional<BlockPos> getNexReplaceableBlockPosUpDown(Level world, BlockPos blockPos, int maxUpDown) {
		if (world.getBlockState(blockPos).canBeReplaced()) {
			// search down
			for (int i = 0; i < maxUpDown; i++) {
				if (!world.getBlockState(blockPos.below(i + 1)).canBeReplaced()) {
					return Optional.of(blockPos.below(i));
				}
			}
		} else {
			// search up
			for (int i = 1; i <= maxUpDown; i++) {
				if (world.getBlockState(blockPos.above(i)).canBeReplaced()) {
					return Optional.of(blockPos.above(i));
				}
			}
		}
		return Optional.empty();
	}
	
	public static double logBase(double base, double logNumber) {
		return Math.log(logNumber) / Math.log(base);
	}
	
}