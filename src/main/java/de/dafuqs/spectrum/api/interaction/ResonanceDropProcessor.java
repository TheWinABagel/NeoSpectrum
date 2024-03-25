package de.dafuqs.spectrum.api.interaction;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.api.predicate.block.BrokenBlockPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class ResonanceDropProcessor {
	
	public BrokenBlockPredicate blockPredicate;
	
	public ResonanceDropProcessor(BrokenBlockPredicate blockPredicate) throws Exception {
		this.blockPredicate = blockPredicate;

		if(blockPredicate.test(Blocks.AIR.defaultBlockState())) {
			throw new Exception("Registering a Resonance Drop that matches on everything!");
		}
	}
	
	public abstract boolean process(BlockState state, BlockEntity blockEntity, List<ItemStack> droppedStacks);
	
	public interface Serializer {
		ResonanceDropProcessor fromJson(JsonObject json) throws Exception;
	}
	
}
