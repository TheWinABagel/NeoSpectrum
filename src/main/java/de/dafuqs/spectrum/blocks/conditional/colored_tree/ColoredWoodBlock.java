package de.dafuqs.spectrum.blocks.conditional.colored_tree;

import com.google.common.collect.Maps;
import de.dafuqs.revelationary.api.revelations.RevelationAware;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class ColoredWoodBlock extends RotatedPillarBlock implements RevelationAware, ColoredTree {
	
	private static final Map<DyeColor, ColoredWoodBlock> WOOD = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public ColoredWoodBlock(Properties settings, DyeColor color) {
		super(settings);
		this.color = color;
		WOOD.put(color, this);
		RevelationAware.register(this);
	}
	
	@Override
	public ResourceLocation getCloakAdvancementIdentifier() {
		return ColoredTree.getTreeCloakAdvancementIdentifier(TreePart.WOOD, this.color);
	}
	
	@Override
	public Map<BlockState, BlockState> getBlockStateCloaks() {
		return Map.of(this.defaultBlockState(), Blocks.OAK_WOOD.defaultBlockState());
	}
	
	@Override
	public Tuple<Item, Item> getItemCloak() {
		return new Tuple<>(this.asItem(), Blocks.OAK_WOOD.asItem());
	}
	
	@Override
	public DyeColor getColor() {
		return this.color;
	}
	
	public static ColoredWoodBlock byColor(DyeColor color) {
		return WOOD.get(color);
	}
	
}
