package de.dafuqs.spectrum.blocks.conditional.amaranth;

import de.dafuqs.revelationary.api.revelations.RevelationAware;
import de.dafuqs.spectrum.registries.client.SpectrumColorProviders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Map;

public class AmaranthBushelBlock extends BushBlock implements RevelationAware {
	
	public AmaranthBushelBlock(Properties settings) {
		super(settings);
		RevelationAware.register(this);
	}
	
	@Override
	public ResourceLocation getCloakAdvancementIdentifier() {
		return AmaranthCropBlock.ADVANCEMENT_IDENTIFIER;
	}
	
	@Override
	public Map<BlockState, BlockState> getBlockStateCloaks() {
		Map<BlockState, BlockState> map = new Hashtable<>();
		map.put(this.defaultBlockState(), Blocks.FERN.defaultBlockState());
		return map;
	}
	
	@Override
	public @Nullable Tuple<Item, Item> getItemCloak() {
		return new Tuple<>(this.asItem(), Blocks.FERN.asItem());
	}
	
	@Override
	public void onUncloak() {
		if (SpectrumColorProviders.amaranthBushelBlockColorProvider != null && SpectrumColorProviders.amaranthBushelItemColorProvider != null) {
			SpectrumColorProviders.amaranthBushelBlockColorProvider.setShouldApply(false);
			SpectrumColorProviders.amaranthBushelItemColorProvider.setShouldApply(false);
		}
	}
	
	@Override
	public void onCloak() {
		if (SpectrumColorProviders.amaranthBushelBlockColorProvider != null && SpectrumColorProviders.amaranthBushelItemColorProvider != null) {
			SpectrumColorProviders.amaranthBushelBlockColorProvider.setShouldApply(true);
			SpectrumColorProviders.amaranthBushelItemColorProvider.setShouldApply(true);
		}
	}
	
}