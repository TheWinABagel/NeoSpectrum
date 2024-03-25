package de.dafuqs.spectrum.blocks.conditional.colored_tree;

import com.google.common.collect.Maps;
import de.dafuqs.revelationary.api.revelations.RevelationAware;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Hashtable;
import java.util.Map;

public class ColoredStrippedLogBlock extends RotatedPillarBlock implements RevelationAware, ColoredTree {
	
	private static final Map<DyeColor, ColoredStrippedLogBlock> LOGS = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public ColoredStrippedLogBlock(Properties settings, DyeColor color) {
		super(settings);
		this.color = color;
		LOGS.put(color, this);
		RevelationAware.register(this);
	}
	
	@Override
	public ResourceLocation getCloakAdvancementIdentifier() {
		return ColoredTree.getTreeCloakAdvancementIdentifier(TreePart.STRIPPED_LOG, this.color);
	}
	
	@Override
	public Map<BlockState, BlockState> getBlockStateCloaks() {
		Map<BlockState, BlockState> map = new Hashtable<>();
		for (Direction.Axis axis : RotatedPillarBlock.AXIS.getPossibleValues()) {
			map.put(this.defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis), Blocks.STRIPPED_OAK_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis));
		}
		return map;
	}
	
	@Override
	public Tuple<Item, Item> getItemCloak() {
		return new Tuple<>(this.asItem(), Blocks.STRIPPED_OAK_LOG.asItem());
	}
	
	@Override
	public DyeColor getColor() {
		return this.color;
	}
	
	public static ColoredStrippedLogBlock byColor(DyeColor color) {
		return LOGS.get(color);
	}
	
}
