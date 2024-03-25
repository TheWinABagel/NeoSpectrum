package de.dafuqs.spectrum.blocks.shooting_star;

import de.dafuqs.spectrum.entity.entity.ShootingStarEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShootingStarItem extends BlockItem implements ShootingStar {
	
	private final Type shootingStarType;
	
	public ShootingStarItem(ShootingStarBlock block, Properties settings) {
		super(block, settings);
		this.shootingStarType = block.shootingStarType;
	}
	
	public static int getRemainingHits(@NotNull ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getTag();
		if (nbtCompound == null || !nbtCompound.contains("remaining_hits", Tag.TAG_ANY_NUMERIC)) {
			return 5;
		} else {
			return nbtCompound.getInt("remaining_hits");
		}
	}
	
	public static @NotNull ItemStack getWithRemainingHits(@NotNull ShootingStarItem shootingStarItem, int remainingHits, boolean hardened) {
		return getWithRemainingHits(shootingStarItem.getDefaultInstance(), remainingHits, hardened);
	}
	
	public static @NotNull ItemStack getWithRemainingHits(@NotNull ItemStack stack, int remainingHits, boolean hardened) {
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.putInt("remaining_hits", remainingHits);
		if (hardened) {
			nbt.putBoolean("Hardened", true);
		}
		return stack;
	}
	
	@Override
	public InteractionResult useOn(@NotNull UseOnContext context) {
		if (context.getPlayer().isShiftKeyDown()) {
			// place as block
			return super.useOn(context);
		} else {
			// place as entity
			Level world = context.getLevel();
			
			if (!world.isClientSide) {
				ItemStack itemStack = context.getItemInHand();
				Vec3 hitPos = context.getClickLocation();
				Player user = context.getPlayer();

				ShootingStarEntity shootingStarEntity = getEntityForStack(context.getLevel(), hitPos, itemStack);
				shootingStarEntity.setYRot(user.getYRot());
				if (!world.noCollision(shootingStarEntity, shootingStarEntity.getBoundingBox())) {
					return InteractionResult.FAIL;
				} else {
					world.addFreshEntity(shootingStarEntity);
					world.gameEvent(user, GameEvent.ENTITY_PLACE, context.getClickedPos());
					if (!user.getAbilities().instabuild) {
						itemStack.shrink(1);
					}
					
					user.awardStat(Stats.ITEM_USED.get(this));
				}
			}
			
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
	}

	@NotNull
	public ShootingStarEntity getEntityForStack(@NotNull Level world, Vec3 pos, ItemStack stack) {
		ShootingStarEntity shootingStarEntity = new ShootingStarEntity(world, pos.x, pos.y, pos.z);
		shootingStarEntity.setShootingStarType(this.shootingStarType, true, isHardened(stack));
		shootingStarEntity.setAvailableHits(getRemainingHits(stack));
		return shootingStarEntity;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		if (isHardened(stack)) {
			tooltip.add(Component.translatable("item.spectrum.shooting_star.tooltip.hardened").withStyle(ChatFormatting.GRAY));
		}
	}
	
	public ShootingStar.Type getShootingStarType() {
		return this.shootingStarType;
	}
	
	public static boolean isHardened(ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getTag();
		return nbtCompound != null && nbtCompound.getBoolean("Hardened");
	}
	
	public static void setHardened(ItemStack itemStack) {
		CompoundTag nbt = itemStack.getOrCreateTag();
		nbt.putBoolean("Hardened", true);
		itemStack.setTag(nbt);
	}

}
