package de.dafuqs.spectrum.compat.claims;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class GenericClaimModsCompat {
	
	public static final boolean IS_COMMON_PROTECTION_API_PRESENT = ModList.get().isLoaded("common-protection-api");
	
	/**
	 * Call this for all kinds of world modifications
	 * For each supported protection mod add a single check here
	 * instead of spreading individual protection mods over the whole codebase
	 * <p>
	 * This also means we do not need any kind of hard compat
	 *
	 * @param world the world that should get modified
	 * @param pos   the pos that should get modified
	 * @return if modification is allowed
	 */
	public static boolean canBreak(Level world, BlockPos pos, @Nullable Entity cause) {
		if (IS_COMMON_PROTECTION_API_PRESENT) {
			return CommonProtectionApiCompat.canBreak(world, pos, cause);
		}
		return true;
	}
	
	public static boolean canInteract(Level world, Entity entity, @Nullable Entity cause) {
		if (IS_COMMON_PROTECTION_API_PRESENT) {
			return CommonProtectionApiCompat.canInteract(world, entity, cause);
		}
		return true;
	}

	public static boolean canInteract(Level world, BlockPos pos, @Nullable Entity cause) {
		if (IS_COMMON_PROTECTION_API_PRESENT) {
			return CommonProtectionApiCompat.canInteract(world, pos, cause);
		}
		return true;
	}

	/**
	 * Used to determine whether you can break and place blocks in this area, which is useful
	 * for swapping blocks
	 *
	 * @param world the world that should get modified
	 * @param pos   the pos that should get modified
	 * @return if modification is allowed
	 */
	public static boolean canModify(Level world, BlockPos pos, @Nullable Entity cause) {
		if (IS_COMMON_PROTECTION_API_PRESENT) {
			return CommonProtectionApiCompat.canModify(world, pos, cause);
		}
		return true;
	}

	public static boolean canPlaceBlock(Level world, BlockPos pos, @Nullable Entity cause) {
		if (IS_COMMON_PROTECTION_API_PRESENT) {
			return CommonProtectionApiCompat.canPlaceBlock(world, pos, cause);
		}
		return true;
	}
	
}
