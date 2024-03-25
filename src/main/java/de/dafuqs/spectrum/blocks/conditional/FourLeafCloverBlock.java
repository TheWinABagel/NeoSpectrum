package de.dafuqs.spectrum.blocks.conditional;

import de.dafuqs.revelationary.api.revelations.RevelationAware;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.decoration.CloverBlock;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Hashtable;
import java.util.Map;

public class FourLeafCloverBlock extends CloverBlock implements RevelationAware {
	
	public FourLeafCloverBlock(Properties settings) {
		super(settings);
		RevelationAware.register(this);
	}
	
	@Override
	public ResourceLocation getCloakAdvancementIdentifier() {
		return SpectrumCommon.locate("milestones/reveal_four_leaf_clover");
	}
	
	@Override
	public Map<BlockState, BlockState> getBlockStateCloaks() {
		Map<BlockState, BlockState> map = new Hashtable<>();
		map.put(this.defaultBlockState(), SpectrumBlocks.CLOVER.defaultBlockState());
		return map;
	}
	
	@Override
	public Tuple<Item, Item> getItemCloak() {
		return new Tuple<>(this.asItem(), SpectrumBlocks.CLOVER.asItem());
	}
	
}
