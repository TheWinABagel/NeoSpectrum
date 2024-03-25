package de.dafuqs.spectrum.blocks.dd_deco;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface Dragonjag {

    enum Variant {
        YELLOW,
        RED,
        PINK,
        PURPLE,
        BLACK
    }

    Dragonjag.Variant getVariant();

    static boolean canPlantOnTop(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.isSolidRender(world, pos);
    }

}
