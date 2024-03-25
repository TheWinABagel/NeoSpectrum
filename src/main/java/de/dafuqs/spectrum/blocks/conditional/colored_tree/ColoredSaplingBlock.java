package de.dafuqs.spectrum.blocks.conditional.colored_tree;

import com.google.common.collect.Maps;
import de.dafuqs.revelationary.api.revelations.RevelationAware;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Hashtable;
import java.util.Map;

public class ColoredSaplingBlock extends SaplingBlock implements RevelationAware, ColoredTree {
	
	private static final Map<DyeColor, ColoredSaplingBlock> SAPLINGS = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public ColoredSaplingBlock(Properties settings, DyeColor color) {
		super(new ColoredSaplingGenerator(color), settings);
		this.color = color;
		SAPLINGS.put(color, this);
		RevelationAware.register(this);
	}
	
	@Override
	public ResourceLocation getCloakAdvancementIdentifier() {
		return ColoredTree.getTreeCloakAdvancementIdentifier(ColoredTree.TreePart.SAPLING, this.color);
	}
	
	@Override
	public Map<BlockState, BlockState> getBlockStateCloaks() {
		// Colored Logs => Oak logs
		Map<BlockState, BlockState> map = new Hashtable<>();
		for (int stage = 0; stage < 2; stage++) {
			map.put(this.defaultBlockState().setValue(SaplingBlock.STAGE, stage), Blocks.OAK_SAPLING.defaultBlockState().setValue(SaplingBlock.STAGE, stage));
		}
		return map;
	}
	
	@Override
	public Tuple<Item, Item> getItemCloak() {
		return new Tuple<>(this.asItem(), Blocks.OAK_SAPLING.asItem());
	}
	
	@Override
	public DyeColor getColor() {
		return this.color;
	}
	
	public static ColoredSaplingBlock byColor(DyeColor color) {
		return SAPLINGS.get(color);
	}
	
}
