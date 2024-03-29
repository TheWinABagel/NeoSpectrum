package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.ColorableBlock;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.compat.claims.GenericClaimModsCompat;
import de.dafuqs.spectrum.entity.entity.InkProjectileEntity;
import de.dafuqs.spectrum.helpers.BlockVariantHelper;
import de.dafuqs.spectrum.helpers.ColorHelper;
import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.inventories.PaintbrushScreenHandler;
import de.dafuqs.spectrum.items.PigmentItem;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PaintbrushItem extends Item {
	
	public static final ResourceLocation UNLOCK_COLORING_ADVANCEMENT_ID = SpectrumCommon.locate("collect_pigment");
	public static final ResourceLocation UNLOCK_INK_SLINGING_ADVANCEMENT_ID = SpectrumCommon.locate("midgame/fill_ink_container");
	
	public static final int COOLDOWN_DURATION_TICKS = 10;
	public static final int BLOCK_COLOR_COST = 25;
	public static final int INK_FLING_COST = 100;
	
	public static final String COLOR_NBT_STRING = "Color";
	
	public PaintbrushItem(Properties settings) {
		super(settings);
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		
		Optional<InkColor> color = getColor(stack);
		boolean unlockedColoring = AdvancementHelper.hasAdvancementClient(UNLOCK_COLORING_ADVANCEMENT_ID);
		boolean unlockedSlinging = AdvancementHelper.hasAdvancementClient(UNLOCK_INK_SLINGING_ADVANCEMENT_ID);
		
		if (unlockedColoring || unlockedSlinging) {
			if (color.isPresent()) {
				tooltip.add(Component.translatable("spectrum.ink.color." + color.get()));
			} else {
				tooltip.add(Component.translatable("item.spectrum.paintbrush.tooltip.select_color"));
			}
		}
		
		tooltip.add(Component.translatable("item.spectrum.paintbrush.ability.header").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.paintbrush.ability.pedestal_triggering").withStyle(ChatFormatting.GRAY));
		if (unlockedColoring) {
			tooltip.add(Component.translatable("item.spectrum.paintbrush.ability.block_coloring").withStyle(ChatFormatting.GRAY));
		}
		if (unlockedSlinging) {
			tooltip.add(Component.translatable("item.spectrum.paintbrush.ability.ink_slinging").withStyle(ChatFormatting.GRAY));
		}
	}
	
	public static boolean canColor(Player player) {
		return AdvancementHelper.hasAdvancement(player, UNLOCK_COLORING_ADVANCEMENT_ID);
	}
	
	public static boolean canInkSling(Player player) {
		return AdvancementHelper.hasAdvancement(player, UNLOCK_INK_SLINGING_ADVANCEMENT_ID);
	}
	
	public MenuProvider createScreenHandlerFactory(ItemStack itemStack) {
		return new SimpleMenuProvider((syncId, inventory, player) ->
				new PaintbrushScreenHandler(syncId, inventory, itemStack),
				Component.translatable("item.spectrum.paintbrush")
		);
	}
	
	public static void setColor(ItemStack stack, @Nullable InkColor color) {
		CompoundTag compound = stack.getOrCreateTag();
		if (color == null) {
			compound.remove(COLOR_NBT_STRING);
		} else {
			compound.putString(COLOR_NBT_STRING, color.toString());
		}
		stack.setTag(compound);
	}
	
	public static Optional<InkColor> getColor(ItemStack stack) {
		CompoundTag compound = stack.getTag();
		if (compound != null && compound.contains(COLOR_NBT_STRING)) {
			return Optional.of(InkColor.of(compound.getString(COLOR_NBT_STRING)));
		}
		return Optional.empty();
	}

	@Override
    public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		if (canColor(context.getPlayer()) && tryColorBlock(context)) {
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		return super.useOn(context);
	}

	private boolean tryColorBlock(UseOnContext context) {
		Optional<InkColor> inkColor = getColor(context.getItemInHand());
		if (inkColor.isEmpty()) {
			return false;
		}
		DyeColor dyeColor = inkColor.get().getDyeColor();

		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof ColorableBlock colorableBlock) {
			if (!colorableBlock.isColor(state, dyeColor)) {
				if (payBlockColorCost(context.getPlayer(), inkColor.get()) && colorableBlock.color(world, pos, dyeColor)) {
					context.getLevel().playSound(null, context.getClickedPos(), SpectrumSoundEvents.PAINTBRUSH_PAINT, SoundSource.BLOCKS, 1.0F, 1.0F);
				} else {
					context.getLevel().playSound(null, context.getClickedPos(), SpectrumSoundEvents.USE_FAIL, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
			}
			return false;
		}

		return cursedColor(context);
	}
	
	private boolean cursedColor(UseOnContext context) {
		Level world = context.getLevel();
		if (context.getPlayer() == null) {
			return false;
		}
		
		Optional<InkColor> optionalInkColor = getColor(context.getItemInHand());
		if (optionalInkColor.isEmpty()) {
			return false;
		}
		
		InkColor inkColor = optionalInkColor.get();
		DyeColor dyeColor = inkColor.getDyeColor();
		
		BlockState newBlockState = BlockVariantHelper.getCursedBlockColorVariant(context.getLevel(), context.getClickedPos(), dyeColor);
		if (newBlockState.isAir()) {
			return false;
		}
		
		if (payBlockColorCost(context.getPlayer(), inkColor)) {
			if (!world.isClientSide) {
				world.setBlockAndUpdate(context.getClickedPos(), newBlockState);
				world.playSound(null, context.getClickedPos(), SpectrumSoundEvents.PAINTBRUSH_PAINT, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
			return true;
		} else {
			if (world.isClientSide) {
				context.getPlayer().playNotifySound(SpectrumSoundEvents.USE_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		}
		return false;
	}
	
	private boolean payBlockColorCost(Player player, InkColor inkColor) {
		if (player == null) {
			return false;
		}
		return player.isCreative()
				|| InkPowered.tryDrainEnergy(player, inkColor, BLOCK_COLOR_COST)
				|| InventoryHelper.removeFromInventoryWithRemainders(player, PigmentItem.byColor(inkColor.getDyeColor()).getDefaultInstance());
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		if (user.isShiftKeyDown()) {
			if (user instanceof ServerPlayer serverPlayerEntity) {
				if (canColor(serverPlayerEntity)) {
					serverPlayerEntity.openMenu(createScreenHandlerFactory(user.getItemInHand(hand)));
				}
			}
			return InteractionResultHolder.pass(user.getItemInHand(hand));
		} else if (canInkSling(user)) {
			Optional<InkColor> optionalInkColor = getColor(user.getItemInHand(hand));
			if (optionalInkColor.isPresent()) {
				
				InkColor inkColor = optionalInkColor.get();
				if (user.isCreative() || InkPowered.tryDrainEnergy(user, inkColor, INK_FLING_COST)) {
					user.getCooldowns().addCooldown(this, COOLDOWN_DURATION_TICKS);
					
					if (!world.isClientSide) {
						InkProjectileEntity.shoot(world, user, inkColor);
					}
					// cause the slightest bit of knockback
					if (!user.isCreative()) {
						causeKnockback(user, user.getYRot(), user.getXRot(), 0, 0.3F);
					}
				} else {
					if (world.isClientSide) {
						user.playNotifySound(SpectrumSoundEvents.USE_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
					}
				}
				
				return InteractionResultHolder.pass(user.getItemInHand(hand));
			}
		}
		return super.use(world, user, hand);
	}
	
	private void causeKnockback(Player user, float yaw, float pitch, float roll, float multiplier) {
		float f = Mth.sin(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F) * multiplier;
		float g = Mth.sin((pitch + roll) * 0.017453292F) * multiplier;
		float h = -Mth.cos(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F) * multiplier;
		user.push(f, g, h);
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
		Level world = user.level();
		if (canColor(user) && GenericClaimModsCompat.canInteract(entity.level(), entity, user)) {
			Optional<InkColor> color = getColor(stack);
			if (color.isPresent() && payBlockColorCost(user, color.get())) {
				boolean colored = ColorHelper.tryColorEntity(user, entity, color.get().getDyeColor());
				if (colored) {
					return InteractionResult.sidedSuccess(world.isClientSide);
				}
			}
		}
		return super.interactLivingEntity(stack, user, entity, hand);
	}
	
}
