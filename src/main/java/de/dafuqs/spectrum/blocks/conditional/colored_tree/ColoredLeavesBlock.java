package de.dafuqs.spectrum.blocks.conditional.colored_tree;

import com.google.common.collect.Maps;
import de.dafuqs.revelationary.api.revelations.RevelationAware;
import de.dafuqs.spectrum.registries.client.SpectrumColorProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Hashtable;
import java.util.Map;

public class ColoredLeavesBlock extends LeavesBlock implements RevelationAware, ColoredTree {
	
	private static final Map<DyeColor, ColoredLeavesBlock> LEAVES = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public ColoredLeavesBlock(Properties settings, DyeColor color) {
		super(settings);
		this.color = color;
		LEAVES.put(color, this);
		RevelationAware.register(this);
	}
	
	@Override
	public ResourceLocation getCloakAdvancementIdentifier() {
		return ColoredTree.getTreeCloakAdvancementIdentifier(TreePart.LEAVES, this.color);
	}
	
	@Override
	public Map<BlockState, BlockState> getBlockStateCloaks() {
		Map<BlockState, BlockState> map = new Hashtable<>();
		for (int distance = 1; distance < 8; distance++) {
			map.put(this.defaultBlockState().setValue(LeavesBlock.DISTANCE, distance).setValue(LeavesBlock.PERSISTENT, false), Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.DISTANCE, distance).setValue(LeavesBlock.PERSISTENT, false));
			map.put(this.defaultBlockState().setValue(LeavesBlock.DISTANCE, distance).setValue(LeavesBlock.PERSISTENT, true), Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.DISTANCE, distance).setValue(LeavesBlock.PERSISTENT, true));
		}
		return map;
	}
	
	@Override
	public Tuple<Item, Item> getItemCloak() {
		return new Tuple<>(this.asItem(), Blocks.OAK_LEAVES.asItem());
	}
	
	@Override
	public void onUncloak() {
		if (SpectrumColorProviders.coloredLeavesBlockColorProvider != null && SpectrumColorProviders.coloredLeavesItemColorProvider != null) {
			SpectrumColorProviders.coloredLeavesBlockColorProvider.setShouldApply(false);
			SpectrumColorProviders.coloredLeavesItemColorProvider.setShouldApply(false);
		}
	}
	
	@Override
	public void onCloak() {
		if (SpectrumColorProviders.coloredLeavesBlockColorProvider != null && SpectrumColorProviders.coloredLeavesItemColorProvider != null) {
			SpectrumColorProviders.coloredLeavesBlockColorProvider.setShouldApply(true);
			SpectrumColorProviders.coloredLeavesItemColorProvider.setShouldApply(true);
		}
	}
	
	@Override
	public DyeColor getColor() {
		return this.color;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 30;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 60;
	}

	public static ColoredLeavesBlock byColor(DyeColor color) {
		return LEAVES.get(color);
	}
	
}
