package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.spectrum.api.energy.InkCost;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.api.interaction.NaturesStaffTriggered;
import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import de.dafuqs.spectrum.compat.claims.GenericClaimModsCompat;
import de.dafuqs.spectrum.data_loaders.NaturesStaffConversionDataLoader;
import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.sound.NaturesStaffUseSoundInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NaturesStaffItem extends Item implements ExtendedEnchantable, InkPowered {

	public static final ItemStack ITEM_COST = new ItemStack(SpectrumItems.VEGETAL, 1);
	public static final InkCost INK_COST = new InkCost(InkColors.LIME, 20);
	
	public NaturesStaffItem(Properties settings) {
		super(settings);
	}
	
	/**
	 * Near identical copy of BonemealItem.useOnFertilizable
	 * just with stack decrement removed
	 */
	public static boolean useOnFertilizable(@NotNull Level world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() instanceof BonemealableBlock fertilizable) {
			if (fertilizable.isValidBonemealTarget(world, pos, blockState, world.isClientSide)) {
				if (world instanceof ServerLevel) {
					if (fertilizable.isBonemealSuccess(world, world.random, pos, blockState)) {
						fertilizable.performBonemeal((ServerLevel) world, world.random, pos, blockState);
					}
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Near identical copy of BonemealItem.useOnGround
	 * just with stack decrement removed
	 */
	public static boolean useOnGround(@NotNull Level world, BlockPos blockPos, @Nullable Direction facing) {
		if (world.getBlockState(blockPos).is(Blocks.WATER) && world.getFluidState(blockPos).getAmount() == 8) {
			if (world instanceof ServerLevel) {
				RandomSource random = world.getRandom();
				
				label78:
				for (int i = 0; i < 128; ++i) {
					BlockPos blockPos2 = blockPos;
					BlockState grownState = Blocks.SEAGRASS.defaultBlockState();
					
					for (int j = 0; j < i / 16; ++j) {
						blockPos2 = blockPos2.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
						if (world.getBlockState(blockPos2).isCollisionShapeFullBlock(world, blockPos2)) {
							continue label78;
						}
					}
					
					Holder<Biome> biomeKey = world.getBiome(blockPos2);
					if (biomeKey.is(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL)) {
						if (i == 0 && facing != null && facing.getAxis().isHorizontal()) {
							grownState = BuiltInRegistries.BLOCK.getTag(BlockTags.WALL_CORALS)
									.flatMap((blocks) -> blocks.getRandomElement(world.random))
									.map((blockEntry) -> blockEntry.value().defaultBlockState())
									.orElse(grownState);
							if (grownState.hasProperty(BaseCoralWallFanBlock.FACING)) {
								grownState = grownState.setValue(BaseCoralWallFanBlock.FACING, facing);
							}
						} else if (random.nextInt(4) == 0) {
							grownState = BuiltInRegistries.BLOCK.getTag(BlockTags.UNDERWATER_BONEMEALS)
									.flatMap((blocks) -> blocks.getRandomElement(world.random))
									.map((blockEntry) -> blockEntry.value().defaultBlockState())
									.orElse(grownState);
						}
					}
					
					if (grownState.is(BlockTags.WALL_CORALS, (state) -> state.hasProperty(BaseCoralWallFanBlock.FACING))) {
						for (int k = 0; !grownState.canSurvive(world, blockPos2) && k < 4; ++k) {
							grownState = grownState.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
						}
					}
					
					if (grownState.canSurvive(world, blockPos2)) {
						BlockState currentState = world.getBlockState(blockPos2);
						if (currentState.is(Blocks.WATER) && world.getFluidState(blockPos2).getAmount() == 8) {
							world.setBlock(blockPos2, grownState, 3);
						} else if (currentState.is(Blocks.SEAGRASS) && random.nextInt(10) == 0) {
							((BonemealableBlock) Blocks.SEAGRASS).performBonemeal((ServerLevel) world, random, blockPos2, currentState);
						}
					}
				}
				
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, tooltip, tooltipContext);
		
		int efficiencyLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, itemStack);
		if (efficiencyLevel == 0) {
			if (InkPowered.canUseClient()) {
				tooltip.add(Component.translatable("item.spectrum.natures_staff.tooltip_with_ink"));
			} else {
				tooltip.add(Component.translatable("item.spectrum.natures_staff.tooltip"));
			}
		} else {
			int chancePercent = (int) (getInkCostMod(itemStack) * 100);
			if (InkPowered.canUseClient()) {
				tooltip.add(Component.translatable("item.spectrum.natures_staff.tooltip_with_ink_and_chance", chancePercent));
			} else {
				tooltip.add(Component.translatable("item.spectrum.natures_staff.tooltip_with_chance", chancePercent));
			}
		}
		
		tooltip.add(Component.translatable("item.spectrum.natures_staff.tooltip_lure"));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		if (canUse(user)) {
			if (world.isClientSide) {
				startSoundInstance(user);
			}
			ItemUtils.startUsingInstantly(world, user, hand);
		}
		return super.use(world, user, hand);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void startSoundInstance(Player user) {
		Minecraft.getInstance().getSoundManager().play(new NaturesStaffUseSoundInstance(user));
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 20000;
	}

	@Override
	public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		// trigger the item's usage action every x ticks
		if (remainingUseTicks % 10 != 0) {
			return;
		}

		if (!(user instanceof Player player)) {
			user.releaseUsingItem();
			return;
		}
		if (!canUse(player)) {
			user.releaseUsingItem();
		}
		
		if (world.isClientSide) {
			// Simple equality check to make sure this method doesn't execute on other clients.
			// Always true if the current player is the one wielding the staff under normal circumstances.
			if(Minecraft.getInstance().player == player) usageTickClient();
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void usageTickClient() {
		Minecraft client = Minecraft.getInstance();
		if (client.hitResult.getType() == HitResult.Type.BLOCK) {
			client.gameMode.useItemOn(
					client.player,
					client.player.getUsedItemHand(),
					(BlockHitResult) client.hitResult
			);
		}
	}
	
	public float getInkCostMod(ItemStack itemStack) {
		return 3.0F / (3.0F + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, itemStack));
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		
		Player user = context.getPlayer();
		if (world.isClientSide) {
			if (user == null) {
				return InteractionResult.FAIL;
			}
			if (canUse(user)) {
				return InteractionResult.PASS;
			} else {
				playDenySound(world, user);
				return InteractionResult.FAIL;
			}
		}
		
		if (user == null || user.getTicksUsingItem() < 2) {
			return InteractionResult.PASS;
		}
		
		if (user instanceof ServerPlayer player) {
			ItemStack stack = context.getItemInHand();
			BlockPos blockPos = context.getClickedPos();
			
			if (!GenericClaimModsCompat.canInteract(world, blockPos, user)) {
				playDenySound(world, context.getPlayer());
				return InteractionResult.FAIL;
			}
			
			if (user.getTicksUsingItem() % 10 == 0) {
				BlockState blockState = world.getBlockState(blockPos);
				
				if (blockState.getBlock() instanceof NaturesStaffTriggered naturesStaffTriggered && naturesStaffTriggered.canUseNaturesStaff(world, blockPos, blockState)) {
					if (naturesStaffTriggered.onNaturesStaffUse(world, blockPos, blockState, player)) {
						payForUse(player, stack);
						world.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, blockPos, 0);
						SpectrumAdvancementCriteria.NATURES_STAFF_USE.trigger(player, blockState, world.getBlockState(blockPos));
					}
					return InteractionResult.CONSUME;
				}
				
				// loaded as convertible? => convert
				BlockState destinationState = NaturesStaffConversionDataLoader.getConvertedBlockState(blockState.getBlock());
				if (destinationState != null) {
					if (destinationState.getBlock() instanceof SimpleWaterloggedBlock) {
						if (touchesWater(world, blockPos)) {
							destinationState = destinationState.setValue(CoralPlantBlock.WATERLOGGED, true);
						} else {
							destinationState = destinationState.setValue(CoralPlantBlock.WATERLOGGED, false);
						}
					}
					world.setBlock(blockPos, destinationState, 3);
					
					payForUse(player, stack);
					world.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, blockPos, 0);
					SpectrumAdvancementCriteria.NATURES_STAFF_USE.trigger(player, blockState, destinationState);
					
					return InteractionResult.CONSUME;
					// fertilizable? => grow
				} else if (useOnFertilizable(world, blockPos)) {
					payForUse(player, stack);
					world.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, blockPos, 0);
					return InteractionResult.CONSUME;
					// blockstate marked as stackable? => stack on top!
				} else if (blockState.is(SpectrumBlockTags.NATURES_STAFF_STACKABLE)) {
					int i = 0;
					BlockState state;
					do {
						state = world.getBlockState(context.getClickedPos().above(i));
						i++;
					} while (state.is(blockState.getBlock()));
					
					BlockPos targetPos = context.getClickedPos().above(i - 1);
					BlockState targetState = blockState.getBlock().getStateForPlacement(new DirectionalPlaceContext(world, blockPos, Direction.DOWN, null, Direction.UP));
					if (targetState != null && world.getBlockState(targetPos).isAir() && !world.isOutsideBuildHeight(targetPos.getY()) && targetState.canSurvive(world, targetPos)) {
						world.setBlockAndUpdate(targetPos, targetState);
						
						world.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, targetPos, Block.getId(targetState));
						world.playSound(null, targetPos, targetState.getSoundType().getPlaceSound(), SoundSource.PLAYERS, 1.0F, 0.9F + world.getRandom().nextFloat() * 0.2F);
						payForUse(player, stack);
						world.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, targetPos, 0);
						return InteractionResult.CONSUME;
					}
					
					// random tickable and whitelisted? => tick
					// without whitelist we would be able to tick budding blocks, ...
				} else if (blockState.isRandomlyTicking() && blockState.is(SpectrumBlockTags.NATURES_STAFF_TICKABLE)) {
					if (world instanceof ServerLevel) {
						blockState.randomTick((ServerLevel) world, blockPos, world.random);
					}
					payForUse(player, stack);
					world.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, blockPos, 0);
					return InteractionResult.CONSUME;
				} else {
					BlockPos blockPos2 = blockPos.relative(context.getClickedFace());
					boolean bl = blockState.isFaceSturdy(world, blockPos, context.getClickedFace());
					if (bl && useOnGround(world, blockPos2, context.getClickedFace())) {
						payForUse(player, stack);
						world.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, blockPos2, 0);
						return InteractionResult.CONSUME;
					}
				}
			}
		}
		
		return InteractionResult.PASS;
	}
	
	private static boolean touchesWater(Level world, BlockPos blockPos) {
		return world.getFluidState(blockPos.north()).is(FluidTags.WATER)
				|| world.getFluidState(blockPos.east()).is(FluidTags.WATER)
				|| world.getFluidState(blockPos.south()).is(FluidTags.WATER)
				|| world.getFluidState(blockPos.west()).is(FluidTags.WATER);
	}
	
	private static void spawnParticles(UseOnContext context, Level world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof NaturesStaffTriggered naturesStaffTriggered && naturesStaffTriggered.canUseNaturesStaff(world, blockPos, blockState)) {
			BoneMealItem.addGrowthParticles(world, blockPos, 3);
		} else if (blockState.is(SpectrumBlockTags.NATURES_STAFF_STACKABLE)) {
			int i = 0;
			while (world.getBlockState(context.getClickedPos().above(i)).is(blockState.getBlock())) {
				BoneMealItem.addGrowthParticles(world, context.getClickedPos().above(i), 3);
				i++;
			}
			BoneMealItem.addGrowthParticles(world, context.getClickedPos().above(i + 1), 5);
			for (int j = 1; world.getBlockState(context.getClickedPos().below(j)).is(blockState.getBlock()); j++) {
				BoneMealItem.addGrowthParticles(world, context.getClickedPos().below(j), 3);
			}
		} else {
			BoneMealItem.addGrowthParticles(world, blockPos, 15);
		}
	}
	
	private boolean payForUse(Player player, ItemStack stack) {
		boolean paid = player.isCreative(); // free for creative players
		if (!paid) { // try pay with ink
			paid = InkPowered.tryDrainEnergy(player, INK_COST, getInkCostMod(stack));
		}
		if (!paid && player.getInventory().contains(ITEM_COST)) {  // try pay with item
			int efficiencyLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack);
			if (efficiencyLevel == 0) {
				paid = InventoryHelper.removeFromInventoryWithRemainders(player, ITEM_COST);
			} else {
				paid = player.getRandom().nextFloat() > (2.0 / (2 + efficiencyLevel)) || InventoryHelper.removeFromInventoryWithRemainders(player, ITEM_COST);
			}
		}
		return paid;
	}
	
	private static boolean canUse(Player player) {
		return player.isCreative() || InkPowered.hasAvailableInk(player, INK_COST) || player.getInventory().contains(ITEM_COST);
	}
	
	private void playDenySound(@NotNull Level world, @NotNull Player playerEntity) {
		world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SpectrumSoundEvents.USE_FAIL, SoundSource.PLAYERS, 1.0F, 0.8F + playerEntity.getRandom().nextFloat() * 0.4F);
	}
	
	@Override
	public List<InkColor> getUsedColors() {
		return List.of(INK_COST.getColor());
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return stack.getCount() == 1;
	}
	
	@Override
	public boolean acceptsEnchantment(Enchantment enchantment) {
		return enchantment == Enchantments.BLOCK_EFFICIENCY;
	}
	
	@Override
	public int getEnchantmentValue() {
		return 10;
	}
	
}
