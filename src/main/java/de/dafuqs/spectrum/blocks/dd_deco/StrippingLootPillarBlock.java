package de.dafuqs.spectrum.blocks.dd_deco;

import de.dafuqs.spectrum.api.block.StrippableDrop;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StrippingLootPillarBlock extends RotatedPillarBlock implements StrippableDrop {
    
    private final Block sourceBlock;
    private final ResourceLocation strippingLootTableIdentifier;
    
    public StrippingLootPillarBlock(Properties settings, Block sourceBlock, ResourceLocation strippingLootTableIdentifier) {
        super(settings);
        this.sourceBlock = sourceBlock;
        this.strippingLootTableIdentifier = strippingLootTableIdentifier;
    }
    
    @Override
    public Block getStrippedBlock() {
        return sourceBlock;
    }
    
    @Override
    public ResourceLocation getStrippingLootTableIdentifier() {
        return strippingLootTableIdentifier;
    }
    
    @Override
	@SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        checkAndDropStrippedLoot(state, world, pos, newState, moved);
        super.onRemove(state, world, pos, newState, moved);
    }
    
}
