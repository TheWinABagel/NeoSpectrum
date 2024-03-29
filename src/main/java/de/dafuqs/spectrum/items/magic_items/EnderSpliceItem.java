package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.networking.SpectrumC2SPacketSender;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.sound.EnderSpliceChargingSoundInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EnderSpliceItem extends Item implements ExtendedEnchantable {
	
	public EnderSpliceItem(Properties settings) {
		super(settings);
	}
	
	public static boolean isSameWorld(Level world1, Level world2) {
		return world1.dimension().location().toString().equals(world2.dimension().location().toString());
	}
	
	public static void setTeleportTargetPos(@NotNull ItemStack itemStack, Level world, Vec3 pos) {
		CompoundTag nbtCompound = itemStack.getOrCreateTag();
		
		// Remove player tags, if present
		if (nbtCompound.contains("TargetPlayerName")) {
			nbtCompound.remove("TargetPlayerName");
		}
		if (nbtCompound.contains("TargetPlayerUUID")) {
			nbtCompound.remove("TargetPlayerUUID");
		}
		
		// Add pos
		nbtCompound.putDouble("PosX", pos.x());
		nbtCompound.putDouble("PosY", pos.y());
		nbtCompound.putDouble("PosZ", pos.z());
		nbtCompound.putString("Dimension", world.dimension().location().toString());
		itemStack.setTag(nbtCompound);
	}
	
	public static void setTeleportTargetPlayer(@NotNull ItemStack itemStack, ServerPlayer player) {
		CompoundTag nbtCompound = itemStack.getOrCreateTag();
		
		// Override target pos, if present
		if (nbtCompound.contains("PosX")) {
			nbtCompound.remove("PosX");
		}
		if (nbtCompound.contains("PosY")) {
			nbtCompound.remove("PosY");
		}
		if (nbtCompound.contains("PosZ")) {
			nbtCompound.remove("PosZ");
		}
		if (nbtCompound.contains("Dimension")) {
			nbtCompound.remove("Dimension");
		}
		
		// Add player
		nbtCompound.putString("TargetPlayerName", player.getName().getString());
		nbtCompound.putUUID("TargetPlayerUUID", player.getUUID());
		itemStack.setTag(nbtCompound);
	}
	
	public static boolean hasTeleportTarget(ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getTag();
		if (nbtCompound == null) {
			return false;
		}
		
		return nbtCompound.contains("PosX") || nbtCompound.contains("TargetPlayerName");
	}
	
	public static void clearTeleportTarget(ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getOrCreateTag();
		
		if (nbtCompound.contains("PosX")) {
			nbtCompound.remove("PosX");
		}
		if (nbtCompound.contains("PosY")) {
			nbtCompound.remove("PosY");
		}
		if (nbtCompound.contains("PosZ")) {
			nbtCompound.remove("PosZ");
		}
		if (nbtCompound.contains("Dimension")) {
			nbtCompound.remove("Dimension");
		}
		if (nbtCompound.contains("TargetPlayerName")) {
			nbtCompound.remove("TargetPlayerName");
		}
		if (nbtCompound.contains("TargetPlayerUUID")) {
			nbtCompound.remove("TargetPlayerUUID");
		}
		
		itemStack.setTag(nbtCompound);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemStack, Level world, LivingEntity user) {
		if (world.isClientSide) {
			if (getTeleportTargetPos(itemStack).isEmpty() && getTeleportTargetPlayerUUID(itemStack).isEmpty()) {
				interactWithEntityClient();
			}
		} else if (user instanceof ServerPlayer playerEntity) {
			CriteriaTriggers.CONSUME_ITEM.trigger(playerEntity, itemStack);
			
			boolean resonance = EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.RESONANCE, itemStack) > 0;
			
			// If Dimension & Pos stored => Teleport to that position
			Optional<Tuple<String, Vec3>> teleportTargetPos = getTeleportTargetPos(itemStack);
			if (teleportTargetPos.isPresent()) {
				ResourceKey<Level> targetWorldKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(teleportTargetPos.get().getA()));
				Level targetWorld = world.getServer().getLevel(targetWorldKey);
				if (teleportPlayerToPos(world, user, playerEntity, targetWorld, teleportTargetPos.get().getB(), resonance)) {
					decrementWithChance(itemStack, world, playerEntity);
				}
			} else {
				// If UUID stored => Teleport to player, if online
				Optional<UUID> teleportTargetPlayerUUID = getTeleportTargetPlayerUUID(itemStack);
				if (teleportTargetPlayerUUID.isPresent()) {
					if (teleportPlayerToPlayerWithUUID(world, user, playerEntity, teleportTargetPlayerUUID.get(), resonance)) {
						decrementWithChance(itemStack, world, playerEntity);
					}
				} else {
					// Nothing stored => Store current position
					setTeleportTargetPos(itemStack, playerEntity.getCommandSenderWorld(), playerEntity.position());
					world.playSound(null, playerEntity.blockPosition(), SpectrumSoundEvents.ENDER_SPLICE_BOUND, SoundSource.PLAYERS, 1.0F, 1.0F);
				}
			}
			playerEntity.awardStat(Stats.ITEM_USED.get(this));
		}
		
		return itemStack;
	}
	
	private static void decrementWithChance(ItemStack itemStack, Level world, ServerPlayer playerEntity) {
		if (EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.INDESTRUCTIBLE, itemStack) > 0) {
			return;
		}
		if (!playerEntity.getAbilities().instabuild) {
			int unbreakingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, itemStack);
			if (unbreakingLevel == 0) {
				itemStack.shrink(1);
			} else {
				itemStack.shrink(Support.getIntFromDecimalWithChance(1.0 / (1 + unbreakingLevel), world.random));
			}
		}
	}
	
	@Environment(EnvType.CLIENT)
    public void interactWithEntityClient() {
		// If aiming at an entity: trigger entity interaction
		Minecraft client = Minecraft.getInstance();
		HitResult hitResult = client.hitResult;
		if (hitResult.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityHitResult = (EntityHitResult) hitResult;
			if (entityHitResult.getEntity() instanceof Player playerEntity) {
				SpectrumC2SPacketSender.sendBindEnderSpliceToPlayer(playerEntity);
			}
		}
	}
	
	private boolean teleportPlayerToPlayerWithUUID(Level world, LivingEntity user, Player playerEntity, UUID targetPlayerUUID, boolean hasResonance) {
		Player targetPlayer = PlayerOwned.getPlayerEntityIfOnline(targetPlayerUUID);
		if (targetPlayer != null) {
			return teleportPlayerToPos(targetPlayer.getCommandSenderWorld(), user, playerEntity, targetPlayer.getCommandSenderWorld(), targetPlayer.position(), hasResonance);
		}
		return false;
	}
	
	private boolean teleportPlayerToPos(Level world, LivingEntity user, Player playerEntity, Level targetWorld, Vec3 targetPos, boolean hasResonance) {
		boolean isSameWorld = isSameWorld(user.getCommandSenderWorld(), targetWorld);
		Vec3 currentPos = playerEntity.position();
		if (hasResonance || isSameWorld) {
			world.playSound(playerEntity, currentPos.x(), currentPos.y(), currentPos.z(), SpectrumSoundEvents.PLAYER_TELEPORTS, SoundSource.PLAYERS, 1.0F, 1.0F);
			
			if (!isSameWorld) {
				FabricDimensions.teleport(user, (ServerLevel) targetWorld, new PortalInfo(targetPos.add(0, 0.25, 0), new Vec3(0, 0, 0), user.getYRot(), user.getXRot()));
			} else {
				user.teleportTo(targetPos.x(), targetPos.y + 0.25, targetPos.z); // +0.25 makes it look way more lively
			}
			world.playSound(playerEntity, targetPos.x(), targetPos.y, targetPos.z, SpectrumSoundEvents.PLAYER_TELEPORTS, SoundSource.PLAYERS, 1.0F, 1.0F);
			
			// make sure the sound plays even when the player currently teleports
			if (playerEntity instanceof ServerPlayer) {
				world.playSound(null, playerEntity.blockPosition(), SpectrumSoundEvents.PLAYER_TELEPORTS, SoundSource.PLAYERS, 1.0F, 1.0F);
				world.playSound(null, playerEntity.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
			return true;
		} else {
			user.releaseUsingItem();
			world.playSound(null, currentPos.x(), currentPos.y(), currentPos.z(), SpectrumSoundEvents.USE_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
			return false;
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		if (world.isClientSide) {
			startSoundInstance(user);
		}
		return ItemUtils.startUsingInstantly(world, user, hand);
	}
	
	@Environment(EnvType.CLIENT)
	public void startSoundInstance(Player user) {
		Minecraft.getInstance().getSoundManager().play(new EnderSpliceChargingSoundInstance(user));
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return 48;
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		// If Dimension & Pos stored => Teleport to that position
		Optional<Tuple<String, Vec3>> teleportTargetPos = getTeleportTargetPos(itemStack);
		if (teleportTargetPos.isPresent()) {
			String dimensionDisplayString = Support.getReadableDimensionString(teleportTargetPos.get().getA());
			Vec3 pos = teleportTargetPos.get().getB();
			tooltip.add(Component.translatable("item.spectrum.ender_splice.tooltip.bound_pos", (int) pos.x, (int) pos.y, (int) pos.z, dimensionDisplayString));
			return;
		} else {
			// If UUID stored => Teleport to player, if online
			Optional<UUID> teleportTargetPlayerUUID = getTeleportTargetPlayerUUID(itemStack);
			if (teleportTargetPlayerUUID.isPresent()) {
				Optional<String> teleportTargetPlayerName = getTeleportTargetPlayerName(itemStack);
				if (teleportTargetPlayerName.isPresent()) {
					tooltip.add(Component.translatable("item.spectrum.ender_splice.tooltip.bound_player", teleportTargetPlayerName.get()));
				} else {
					tooltip.add(Component.translatable("item.spectrum.ender_splice.tooltip.bound_player", "???"));
				}
				return;
			}
		}
		
		tooltip.add(Component.translatable("item.spectrum.ender_splice.tooltip.unbound"));
	}
	
	public Optional<Tuple<String, Vec3>> getTeleportTargetPos(@NotNull ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getTag();
		if (nbtCompound != null && nbtCompound.contains("PosX") && nbtCompound.contains("PosY") && nbtCompound.contains("PosZ") && nbtCompound.contains("Dimension")) {
			String dimensionKeyString = nbtCompound.getString("Dimension");
			double x = nbtCompound.getDouble("PosX");
			double y = nbtCompound.getDouble("PosY");
			double z = nbtCompound.getDouble("PosZ");
			Vec3 pos = new Vec3(x, y, z);
			
			return Optional.of(new Tuple<>(dimensionKeyString, pos));
		}
		return Optional.empty();
	}
	
	public Optional<UUID> getTeleportTargetPlayerUUID(@NotNull ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getTag();
		if (nbtCompound != null && nbtCompound.contains("TargetPlayerUUID")) {
			return Optional.of(nbtCompound.getUUID("TargetPlayerUUID"));
		}
		return Optional.empty();
	}
	
	public Optional<String> getTeleportTargetPlayerName(@NotNull ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getTag();
		if (nbtCompound != null && nbtCompound.contains("TargetPlayerName")) {
			return Optional.of(nbtCompound.getString("TargetPlayerName"));
		}
		return Optional.empty();
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return stack.getCount() == 1;
	}
	
	@Override
	public boolean acceptsEnchantment(Enchantment enchantment) {
		return enchantment == SpectrumEnchantments.RESONANCE || enchantment == SpectrumEnchantments.INDESTRUCTIBLE || enchantment == Enchantments.UNBREAKING;
	}
	
	@Override
	public int getEnchantmentValue() {
		return 50;
	}
	
}
