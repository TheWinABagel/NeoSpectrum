package de.dafuqs.spectrum.blocks.shooting_star;

import de.dafuqs.spectrum.entity.entity.ShootingStarEntity;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ShootingStarDispenserBehavior extends DefaultDispenseItemBehavior {

	@Override
	public ItemStack execute(@NotNull BlockSource pointer, @NotNull ItemStack stack) {
		Direction direction = pointer.getBlockState().getValue(DispenserBlock.FACING);

		Level world = pointer.getLevel();
		ShootingStarItem shootingStarItem = ((ShootingStarItem) stack.getItem());
		ShootingStarEntity shootingStarEntity = shootingStarItem.getEntityForStack(world,
				new Vec3(pointer.x() + direction.getStepX() * 1.125F,
						pointer.y() + direction.getStepY() * 1.13F,
						pointer.z() + direction.getStepZ() * 1.125F), stack);
		shootingStarEntity.setYRot(direction.toYRot());
		shootingStarEntity.push(direction.getStepX() * 0.4, direction.getStepY() * 0.4, direction.getStepZ() * 0.4);
		world.addFreshEntity(shootingStarEntity);

		stack.shrink(1);
		return stack;
	}

}
