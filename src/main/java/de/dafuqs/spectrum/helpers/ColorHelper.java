package de.dafuqs.spectrum.helpers;

import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.entity.entity.EggLayingWoolyPigEntity;
import de.dafuqs.spectrum.items.PigmentItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Optional;

public class ColorHelper {
	
	public static Vector3f getRGBVec(DyeColor dyeColor) {
		return InkColor.of(dyeColor).getColor();
	}
	
	public static int getInt(DyeColor dyeColor) {
		Vector3f vec = getRGBVec(dyeColor);
		return new Color(vec.x(), vec.y(), vec.z()).getRGB() & 0x00FFFFFF;
	}
	
	/**
	 * Returns a nicely saturated random color based on seed
	 *
	 * @param seed the seed to base the random color on
	 * @return the color
	 */
	public static int getRandomColor(int seed) {
		return Color.getHSBColor((float) seed / Integer.MAX_VALUE, 0.7F, 0.9F).getRGB();
	}
	
	@NotNull
	public static Vector3f colorIntToVec(int color) {
		Color colorObj = new Color(color);
		float[] argb = new float[4];
		colorObj.getColorComponents(argb);
		return new Vector3f(argb[0], argb[1], argb[2]);
	}

	public static int colorVecToRGB(Vector3f color) {
		Color colorObj = new Color(color.x, color.y, color.z);
		return colorObj.getRGB();
	}

	public static Optional<DyeColor> getDyeColorOfItemStack(@NotNull ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			if (item instanceof DyeItem dyeItem) {
				return Optional.of(dyeItem.getDyeColor());
			} else if (item instanceof PigmentItem pigmentItem) {
				return Optional.of(pigmentItem.getColor());
			}
		}
		return Optional.empty();
	}
	
	public static boolean tryColorEntity(Player user, Entity entity, DyeColor dyeColor) {
		if (entity instanceof Sheep sheepEntity && sheepEntity.isAlive() && !sheepEntity.isSheared()) {
			if (sheepEntity.getColor() != dyeColor) {
				sheepEntity.level().playSound(user, sheepEntity, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
				sheepEntity.setColor(dyeColor);
				return true;
			}
		} else if (entity instanceof EggLayingWoolyPigEntity woolyPig && woolyPig.isAlive()) {
			if (woolyPig.getColor() != dyeColor) {
				woolyPig.level().playSound(user, woolyPig, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
				woolyPig.setColor(dyeColor);
				return true;
			}
		} else if (entity instanceof Shulker shulkerEntity && shulkerEntity.isAlive()) {
			if (shulkerEntity.getColor() != dyeColor) {
				shulkerEntity.level().playSound(user, shulkerEntity, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
				shulkerEntity.setVariant(Optional.of(dyeColor));
				return true;
			}
		}
		return false;
	}
	
}