package de.dafuqs.spectrum.compat.claims;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CommonProtectionApiCompat { //todoforge is there some sort of protection system in forge already?
	
	public static boolean canBreak(Level world, BlockPos pos, Entity cause) {
//		if (cause instanceof Player playerEntity) {
//			return CommonProtection.canBreakBlock(world, pos, playerEntity.getGameProfile(), playerEntity);
//		} else {
//			return CommonProtection.canBreakBlock(world, pos, ProtectionProvider.UNKNOWN, null);
//		}
		return true;
	}
	
	public static boolean canInteract(Level world, Entity entityToInteractWith, Entity cause) {
//		if (cause instanceof Player playerEntity) {
//			return CommonProtection.canInteractEntity(world, entityToInteractWith, playerEntity.getGameProfile(), playerEntity);
//		} else {
//			return CommonProtection.canInteractEntity(world, entityToInteractWith, ProtectionProvider.UNKNOWN, null);
//		}
		return true;
	}

	public static boolean canInteract(Level world, BlockPos pos, Entity cause) {
//		if (cause instanceof Player playerEntity) {
//			return CommonProtection.canInteractBlock(world, pos, playerEntity.getGameProfile(), playerEntity);
//		} else {
//			return CommonProtection.canInteractBlock(world, pos, ProtectionProvider.UNKNOWN, null);
//		}
		return true;
	}

	public static boolean canModify(Level world, BlockPos pos, Entity cause) {
//		if (cause instanceof Player playerEntity) {
//			return CommonProtection.canPlaceBlock(world, pos, playerEntity.getGameProfile(), playerEntity) &&
//					CommonProtection.canBreakBlock(world, pos, playerEntity.getGameProfile(), playerEntity);
//		} else {
//			return CommonProtection.canPlaceBlock(world, pos, ProtectionProvider.UNKNOWN, null) &&
//					CommonProtection.canBreakBlock(world, pos, ProtectionProvider.UNKNOWN, null);
//		}
		return true;
	}

	public static boolean canPlaceBlock(Level world, BlockPos pos, Entity cause) {
//		if (cause instanceof Player playerEntity) {
//			return CommonProtection.canPlaceBlock(world, pos, playerEntity.getGameProfile(), playerEntity);
//		} else {
//			return CommonProtection.canPlaceBlock(world, pos, ProtectionProvider.UNKNOWN, null);
//		}
		return true;
	}
	
}
