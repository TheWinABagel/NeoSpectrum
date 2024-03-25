package de.dafuqs.spectrum.blocks.spirit_sallow;

import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface SpiritVine {

	VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
	EnumProperty<YieldType> YIELD = EnumProperty.create("yield", YieldType.class);

	static InteractionResult pick(BlockState blockState, Level world, BlockPos blockPos) {
		if (canBeHarvested(blockState)) {
			Block.popResource(world, blockPos, new ItemStack(getYieldItem(blockState, false), 1));
			float f = Mth.randomBetween(world.random, 0.8F, 1.2F);
			world.playSound(null, blockPos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, f);
			world.setBlock(blockPos, blockState.setValue(YIELD, YieldType.NONE), 2);
			return InteractionResult.sidedSuccess(world.isClientSide);
		} else {
			return InteractionResult.PASS;
		}
	}

	static boolean canBeHarvested(BlockState state) {
		return state.hasProperty(YIELD) && !state.getValue(YIELD).equals(YieldType.NONE);
	}

	static Item getYieldItem(BlockState blockState, boolean pickStack) {
		Comparable<YieldType> yield = blockState.getValue(YIELD);

		if (yield.equals(YieldType.NORMAL)) {
			if (blockState.is(SpectrumBlocks.CYAN_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.CYAN_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.VIBRANT_CYAN_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.MAGENTA_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.MAGENTA_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.VIBRANT_MAGENTA_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.YELLOW_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.YELLOW_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.VIBRANT_YELLOW_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.BLACK_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.BLACK_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.VIBRANT_BLACK_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.WHITE_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.WHITE_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.VIBRANT_WHITE_CATKIN;
			}
		} else if (yield.equals(YieldType.LUCID)) {
			if (blockState.is(SpectrumBlocks.CYAN_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.CYAN_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.LUCID_CYAN_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.MAGENTA_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.MAGENTA_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.LUCID_MAGENTA_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.YELLOW_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.YELLOW_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.LUCID_YELLOW_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.BLACK_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.BLACK_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.LUCID_BLACK_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.WHITE_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.WHITE_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.LUCID_WHITE_CATKIN;
			}
		} else if (yield.equals(YieldType.NONE) && pickStack) {
			if (blockState.is(SpectrumBlocks.CYAN_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.CYAN_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.VIBRANT_CYAN_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.MAGENTA_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.MAGENTA_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.VIBRANT_MAGENTA_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.YELLOW_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.YELLOW_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.VIBRANT_YELLOW_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.BLACK_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.BLACK_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.VIBRANT_BLACK_CATKIN;
			}
			if (blockState.is(SpectrumBlocks.WHITE_SPIRIT_SALLOW_VINES_HEAD) || blockState.is(SpectrumBlocks.WHITE_SPIRIT_SALLOW_VINES_BODY)) {
				return SpectrumItems.VIBRANT_WHITE_CATKIN;
			}
		}
		return null;
	}

	enum YieldType implements StringRepresentable {
		NONE("none"),
		NORMAL("normal"),
		LUCID("lucid");

		private final String name;

		YieldType(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}

}
