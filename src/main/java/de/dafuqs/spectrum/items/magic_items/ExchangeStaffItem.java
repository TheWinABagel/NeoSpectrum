package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import de.dafuqs.spectrum.compat.claims.GenericClaimModsCompat;
import de.dafuqs.spectrum.helpers.BuildingHelper;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipeTier;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeStaffItem extends BuildingStaffItem implements ExtendedEnchantable {

	public static final int INK_COST_PER_BLOCK = 5;
	public static final int CREATIVE_RANGE = 5;
	
	public ExchangeStaffItem(Properties settings) {
		super(settings);
	}
	
	// The range grows with the players' progression
	// this way the item is not overpowered at the start
	// but not useless at the end
	// this way the player does not need to craft 5 tiers
	// of staffs that each do basically feel the same
	public static int getRange(Player playerEntity) {
		if (playerEntity == null || playerEntity.isCreative()) {
			return CREATIVE_RANGE;
		} else {
			Optional<PedestalRecipeTier> highestUnlockedRecipeTier = PedestalRecipeTier.getHighestUnlockedRecipeTier(playerEntity);
			if (highestUnlockedRecipeTier.isPresent()) {
				switch (highestUnlockedRecipeTier.get()) {
					case COMPLEX -> {
						return 5;
					}
					case ADVANCED -> {
						return 4;
					}
					default -> {
						return 3;
					}
				}
			} else {
				return 2;
			}
		}
	}
	
	@Override
	public boolean canInteractWith(BlockState state, BlockGetter world, BlockPos pos, Player player) {
		return super.canInteractWith(state, world, pos, player) && state.getDestroySpeed(world, pos) < 20;
	}
	
	public static Optional<Block> getStoredBlock(@NotNull ItemStack exchangeStaffItemStack) {
		CompoundTag compound = exchangeStaffItemStack.getOrCreateTag();
		if (compound.contains("TargetBlock")) {
			String targetBlockString = compound.getString("TargetBlock");
			Block targetBlock = BuiltInRegistries.BLOCK.get(new ResourceLocation(targetBlockString));
			if (targetBlock != Blocks.AIR) {
				return Optional.of(targetBlock);
			}
		}
		return Optional.empty();
	}
	
	public static boolean exchange(Level world, BlockPos pos, @NotNull Player player, @NotNull Block targetBlock, ItemStack exchangeStaffItemStack, Direction side) {
		return exchange(world, pos, player, targetBlock, exchangeStaffItemStack, false, side);
	}
	
	public static boolean exchange(Level world, BlockPos pos, @NotNull Player player, @NotNull Block targetBlock, ItemStack exchangeStaffItemStack, boolean single, Direction side) {
		Triplet<Block, Item, Integer> replaceData = countSuitableReplacementItems(player, targetBlock, single, INK_COST_PER_BLOCK);
		
		long blocksToReplaceCount = replaceData.getC();
		if (blocksToReplaceCount == 0) {
			return false;
		}
		targetBlock = replaceData.getA();
		Item consumedItem = replaceData.getB();

		int range = getRange(player);
		List<BlockPos> targetPositions = BuildingHelper.getConnectedBlocks(world, pos, blocksToReplaceCount, range);
		if (targetPositions.isEmpty()) {
			return false;
		}

		int blocksReplaced = 0;
		if (!world.isClientSide) {
			List<ItemStack> stacks = new ArrayList<>();
			BlockState stateToPlace;
			for (BlockPos targetPosition : targetPositions) {

				// Require both place and break permissions in order to swap blocks
				if (!GenericClaimModsCompat.canModify(world, pos, player))
					continue;

				if (!player.isCreative()) {
					BlockState droppedStacks = world.getBlockState(targetPosition);
					stacks.addAll(Block.getDrops(droppedStacks, (ServerLevel) world, targetPosition, world.getBlockEntity(targetPosition), player, exchangeStaffItemStack));
				}
				world.setBlockAndUpdate(targetPosition, Blocks.AIR.defaultBlockState());

				stateToPlace = targetBlock.getStateForPlacement(new BuildingStaffPlacementContext(world, player, new BlockHitResult(Vec3.atBottomCenterOf(targetPosition), side, targetPosition, false)));
				if (stateToPlace != null && stateToPlace.canSurvive(world, targetPosition)) {
					if (blocksReplaced == 0) {
						world.playSound(null, player.blockPosition(), stateToPlace.getSoundType().getPlaceSound(), SoundSource.PLAYERS, stateToPlace.getSoundType().getVolume(), stateToPlace.getSoundType().getPitch());
					}

					if (!world.setBlockAndUpdate(targetPosition, stateToPlace)) {
						ItemEntity itemEntity = new ItemEntity(world, targetPosition.getX(), targetPosition.getY(), targetPosition.getZ(), new ItemStack(consumedItem));
						itemEntity.setTarget(player.getUUID());
						itemEntity.setNoPickUpDelay();
						world.addFreshEntity(itemEntity);
					}
				}

				blocksReplaced++;
			}

			if (!player.isCreative()) {
				player.getInventory().clearOrCountMatchingItems(stack -> stack.getItem().equals(consumedItem), targetPositions.size(), player.getInventory());
				for (ItemStack stack : stacks) {
					player.getInventory().placeItemBackInInventory(stack);
				}
				InkPowered.tryDrainEnergy(player, USED_COLOR, (long) targetPositions.size() * INK_COST_PER_BLOCK);
			}

		}

		return true;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
		Minecraft client = Minecraft.getInstance();
		super.appendHoverText(stack, world, tooltip, context);
		addInkPoweredTooltip(tooltip);
		tooltip.add(Component.translatable("item.spectrum.exchanging_staff.tooltip.range", getRange(client.player)).withStyle(ChatFormatting.GRAY));
		
		Optional<Block> optionalBlock = getStoredBlock(stack);
		if (optionalBlock.isPresent()) {
			tooltip.add(Component.translatable("item.spectrum.exchanging_staff.tooltip.target", optionalBlock.get().getName()).withStyle(ChatFormatting.GRAY));
		}
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();

		if (player == null) {
			return InteractionResult.FAIL;
		}

		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState targetBlockState = world.getBlockState(pos);
		Block targetBlock = targetBlockState.getBlock();

		if (!canInteractWith(targetBlockState, context.getLevel(), context.getClickedPos(), context.getPlayer())) {
			world.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
			return InteractionResult.FAIL;
		}

		ItemStack staffStack = context.getItemInHand();

		if (player.isShiftKeyDown()) {
			// storing that block as target
			if (world instanceof ServerLevel serverWorld) {
				storeBlockAsTarget(staffStack, targetBlock);

				Direction side = context.getClickedFace();
				Vec3 sourcePos = new Vec3(context.getClickLocation().x() + side.getStepX() * 0.1, context.getClickLocation().y() + side.getStepY() * 0.1, context.getClickLocation().z() + side.getStepZ() * 0.1);
				SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity(serverWorld, sourcePos, SpectrumParticleTypes.SHIMMERSTONE_SPARKLE_SMALL, 15, Vec3.ZERO, new Vec3(0.25, 0.25, 0.25));
				world.playSound(null, player.blockPosition(), SpectrumSoundEvents.EXCHANGING_STAFF_SELECT, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
			return InteractionResult.sidedSuccess(world.isClientSide);
		} else {
			// exchanging
			Optional<Block> storedBlock = getStoredBlock(staffStack);
			if (storedBlock.isPresent()
					&& storedBlock.get() != targetBlock
					&& storedBlock.get().asItem() != Items.AIR
					&& exchange(world, pos, player, storedBlock.get(), staffStack, context.getClickedFace())) {

				return InteractionResult.sidedSuccess(world.isClientSide);
			}
		}
		
		world.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
		return InteractionResult.FAIL;
	}
	
	public void storeBlockAsTarget(@NotNull ItemStack exchangeStaffItemStack, Block block) {
		CompoundTag compound = exchangeStaffItemStack.getOrCreateTag();
		ResourceLocation blockIdentifier = BuiltInRegistries.BLOCK.getKey(block);
		compound.putString("TargetBlock", blockIdentifier.toString());
		exchangeStaffItemStack.setTag(compound);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return stack.getCount() == 1;
	}
	
	@Override
	public boolean acceptsEnchantment(Enchantment enchantment) {
		return enchantment == Enchantments.BLOCK_FORTUNE || enchantment == Enchantments.SILK_TOUCH || enchantment == SpectrumEnchantments.RESONANCE;
	}
	
	@Override
	public int getEnchantmentValue() {
		return 3;
	}

	@Override
	public List<InkColor> getUsedColors() {
		return List.of(USED_COLOR);
	}
	
}
