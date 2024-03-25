package de.dafuqs.spectrum.api.block;

import de.dafuqs.spectrum.helpers.ColorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * A block that can be colored by using dye, pigment, a paintbrush or other coloring actions on them
 */
public interface ColorableBlock {

    /**
     * Color the block into the specified color
     *
     * @param color the color the block should be colored in
     * @return True if coloring was successful, false if failed (like the block was this color already)
     */
    boolean color(Level world, BlockPos pos, DyeColor color);

    DyeColor getColor(BlockState state);

    default boolean isColor(BlockState state, DyeColor color) {
        return getColor(state) == color;
    }

    default boolean tryColorUsingStackInHand(Level world, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        Optional<DyeColor> itemInHandColor = ColorHelper.getDyeColorOfItemStack(handStack);
        if (itemInHandColor.isPresent()) {
            if (color(world, pos, itemInHandColor.get())) {
                if(!player.isCreative()) {
                    handStack.shrink(1);
                }
                return true;
            }
        }
        return false;
    }

}
