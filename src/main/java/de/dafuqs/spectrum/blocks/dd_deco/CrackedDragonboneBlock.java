package de.dafuqs.spectrum.blocks.dd_deco;

import de.dafuqs.revelationary.api.revelations.RevelationAware;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.ExplosionAware;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Map;

public class CrackedDragonboneBlock extends RotatedPillarBlock implements ExplosionAware, RevelationAware {
	
	public CrackedDragonboneBlock(Properties settings) {
		super(settings);
		RevelationAware.register(this);
	}

	@Override
	public BlockState getStateForExplosion(Level world, BlockPos blockPos, BlockState stateAtPos) {
		return stateAtPos;
	}
	
	@Override
	public ResourceLocation getCloakAdvancementIdentifier() {
		return SpectrumCommon.locate("milestones/reveal_dragonbone");
	}
	
	@Override
	public Map<BlockState, BlockState> getBlockStateCloaks() {
		Map<BlockState, BlockState> map = new Hashtable<>();
		for (Direction.Axis axis : BlockStateProperties.AXIS.getPossibleValues()) {
			map.put(this.defaultBlockState().setValue(BlockStateProperties.AXIS, axis), Blocks.BONE_BLOCK.defaultBlockState().setValue(BlockStateProperties.AXIS, axis));
		}
		return map;
	}
	
	@Override
	public @Nullable Tuple<Item, Item> getItemCloak() {
		return new Tuple<>(this.asItem(), Blocks.BONE_BLOCK.asItem());
	}
	
}
