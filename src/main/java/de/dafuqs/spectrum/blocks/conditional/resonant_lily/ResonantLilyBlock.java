package de.dafuqs.spectrum.blocks.conditional.resonant_lily;

import de.dafuqs.revelationary.api.revelations.RevelationAware;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class ResonantLilyBlock extends FlowerBlock implements RevelationAware {
	
	public static final ResourceLocation ADVANCEMENT_IDENTIFIER = SpectrumCommon.locate("midgame/collect_resonant_lily");
	
	public ResonantLilyBlock(MobEffect suspiciousStewEffect, int effectDuration, Properties settings) {
		super(suspiciousStewEffect, effectDuration, settings);
		RevelationAware.register(this);
	}
	
	@Override
	public ResourceLocation getCloakAdvancementIdentifier() {
		return ADVANCEMENT_IDENTIFIER;
	}
	
	@Override
	public Map<BlockState, BlockState> getBlockStateCloaks() {
		return Map.of(this.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState());
	}
	
	@Override
	public Tuple<Item, Item> getItemCloak() {
		return new Tuple<>(this.asItem(), Items.WHITE_TULIP);
	}
	
}
