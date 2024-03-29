package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.compat.claims.GenericClaimModsCompat;
import de.dafuqs.spectrum.helpers.BuildingHelper;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipeTier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Triplet;

import java.util.List;
import java.util.Optional;

public class ConstructorsStaffItem extends BuildingStaffItem {

	public static final int INK_COST_PER_BLOCK = 1;
	public static final int CREATIVE_RANGE = 10;
	
	public ConstructorsStaffItem(Properties settings) {
		super(settings);
	}
	
	// The range grows with the players progression
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
						return 10;
					}
					case ADVANCED -> {
						return 7;
					}
					default -> {
						return 4;
					}
				}
			} else {
				return 3;
			}
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
		Minecraft client = Minecraft.getInstance();
		super.appendHoverText(stack, world, tooltip, context);
		addInkPoweredTooltip(tooltip);
		tooltip.add(Component.translatable("item.spectrum.constructors_staff.tooltip.range", getRange(client.player)).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.constructors_staff.tooltip.crouch").withStyle(ChatFormatting.GRAY));
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState targetBlockState = world.getBlockState(pos);

		if ((player != null && this.canInteractWith(targetBlockState, context.getLevel(), context.getClickedPos(), context.getPlayer()))) {
			Block blockToPlace = targetBlockState.getBlock();
			Item itemToConsume;

			long count;
			if (player.isCreative()) {
				itemToConsume = blockToPlace.asItem();
				count = Integer.MAX_VALUE;
			} else {
				Triplet<Block, Item, Integer> replaceData = countSuitableReplacementItems(player, blockToPlace, false, INK_COST_PER_BLOCK);
				blockToPlace = replaceData.getA();
				itemToConsume = replaceData.getB();
				count = replaceData.getC();
			}

			if (count > 0) {
				Direction side = context.getClickedFace();
				int maxRange = getRange(player);
				int range = (int) Math.min(maxRange, player.isCreative() ? maxRange : count);
				boolean sneaking = player.isShiftKeyDown();
				List<BlockPos> targetPositions = BuildingHelper.calculateBuildingStaffSelection(world, pos, side, count, range, !sneaking);
				if (targetPositions.isEmpty()) {
					return InteractionResult.FAIL;
				}

				if (!world.isClientSide) {
					placeBlocksAndDecrementInventory(player, world, blockToPlace, itemToConsume, side, targetPositions);
				}

				return InteractionResult.SUCCESS;
			}
		} else {
			if (player != null) {
				world.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		}
		
		return InteractionResult.FAIL;
	}
	
	protected static void placeBlocksAndDecrementInventory(Player player, Level world, Block blockToPlace, Item itemToConsume, Direction side, List<BlockPos> targetPositions) {
		int placedBlocks = 0;
		for (BlockPos position : targetPositions) {
			// Only place blocks where you are allowed to do so
			if (!GenericClaimModsCompat.canPlaceBlock(world, position, player))
				continue;
			
			BlockState originalState = world.getBlockState(position);
			if (originalState.isAir() || originalState.getBlock() instanceof LiquidBlock || (originalState.canBeReplaced() && originalState.getCollisionShape(world, position).isEmpty())) {
				BlockState stateToPlace = blockToPlace.getStateForPlacement(new BuildingStaffPlacementContext(world, player, new BlockHitResult(Vec3.atBottomCenterOf(position), side, position, false)));
				if (stateToPlace != null && stateToPlace.canSurvive(world, position)) {
					if (world.setBlockAndUpdate(position, stateToPlace)) {
						if (placedBlocks == 0) {
							world.playSound(null, player.blockPosition(), stateToPlace.getSoundType().getPlaceSound(), SoundSource.PLAYERS, stateToPlace.getSoundType().getVolume(), stateToPlace.getSoundType().getPitch());
						}
						placedBlocks++;
					}
				}
			}
		}
		
		if (!player.isCreative()) {
			player.getInventory().clearOrCountMatchingItems(stack -> stack.getItem().equals(itemToConsume), placedBlocks, player.getInventory());
			InkPowered.tryDrainEnergy(player, USED_COLOR, (long) targetPositions.size() * ConstructorsStaffItem.INK_COST_PER_BLOCK);
		}
	}
	
	@Override
	public List<InkColor> getUsedColors() {
		return List.of(USED_COLOR);
	}
	
}
