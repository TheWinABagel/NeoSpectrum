package de.dafuqs.spectrum.items.food.beverages;

import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.items.food.beverages.properties.BeverageProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;
import java.util.Optional;

public class RepriseItem extends BeverageItem {
	
	public RepriseItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public BeverageProperties getBeverageProperties(ItemStack itemStack) {
		return new RepriseProperties(itemStack.getTag());
	}
	
	public static class RepriseProperties extends BeverageProperties {
		
		public RepriseProperties(CompoundTag nbtCompound) {
			super(nbtCompound);
		}
		
		@Override
		public void addTooltip(ItemStack itemStack, List<Component> tooltip) {
			super.addTooltip(itemStack, tooltip);
			long teleportRange = getTeleportRange(itemStack);
			tooltip.add(Component.translatable("item.spectrum.reprise.tooltip.teleport", teleportRange).withStyle(ChatFormatting.GRAY));
		}
		
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
		if (world instanceof ServerLevel serverWorld) {
			randomTeleport(serverWorld, user, getTeleportRange(stack));
		}
		return super.finishUsingItem(stack, world, user);
	}

	public static long getTeleportRange(ItemStack itemStack) {
		BeverageProperties properties = BeverageProperties.getFromStack(itemStack);
		return (long) Math.ceil(Math.pow(2, properties.alcPercent));
	}

	public void randomTeleport(ServerLevel world, LivingEntity user, long maxRange) {
		double d = user.getX();
		double e = user.getY();
		double f = user.getZ();

		for (int i = 0; i < 16; ++i) {
			double newX = user.getX() + (user.getRandom().nextDouble() - 0.5D) * maxRange;
			double newY = user.getY();
			double newZ = user.getZ() + (user.getRandom().nextDouble() - 0.5D) * maxRange;
			BlockPos destination = world.getWorldBorder().clampToBounds(newX, newY, newZ);

			Optional<BlockPos> safeDestination = Support.getNexReplaceableBlockPosUpDown(world, destination, 20);
			if (safeDestination.isPresent()) {
				destination = safeDestination.get();

				world.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(destination), 1, user.getId());
				if (user.isSleeping()) {
					user.stopSleeping();
				}
				if (user.isPassenger()) {
					user.stopRiding();
				}

				user.teleportTo(destination.getX(), destination.getY(), destination.getZ());

				world.gameEvent(GameEvent.TELEPORT, user.position(), GameEvent.Context.of(user));
				SoundEvent soundEvent = user instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
				world.playSound(null, d, e, f, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
				user.playSound(soundEvent, 1.0F, 1.0F);
				break;
			}
		}

		if (user instanceof Player) {
			((Player) user).getCooldowns().addCooldown(this, 20);
		}
	}
	
}
