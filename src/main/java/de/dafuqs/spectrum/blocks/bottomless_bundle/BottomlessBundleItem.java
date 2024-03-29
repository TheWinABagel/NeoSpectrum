package de.dafuqs.spectrum.blocks.bottomless_bundle;

import com.mojang.blaze3d.vertex.PoseStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import de.dafuqs.spectrum.api.item.InventoryInsertionAcceptor;
import de.dafuqs.spectrum.api.render.DynamicItemRenderer;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.items.tooltip.BottomlessBundleTooltipData;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BottomlessBundleItem extends BundleItem implements InventoryInsertionAcceptor, ExtendedEnchantable {
	
	private static final int MAX_STORED_AMOUNT_BASE = 20000;
	
	public BottomlessBundleItem(Item.Properties settings) {
		super(settings);
	}
	
	public static ItemStack getWithBlockAndCount(ItemStack itemStack, int amount) {
		ItemStack bottomlessBundleStack = new ItemStack(SpectrumItems.BOTTOMLESS_BUNDLE);
		BottomlessBundleItem.bundleStack(bottomlessBundleStack, itemStack, amount);
		return bottomlessBundleStack;
	}
	
	public static int getMaxStoredAmount(ItemStack itemStack) {
		int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, itemStack);
		return MAX_STORED_AMOUNT_BASE * (int) Math.pow(10, Math.min(5, powerLevel)); // to not exceed int max
	}
	
	/**
	 * @return The amount of items put into the bundle
	 */
	public static int add(ItemStack voidBundleStack, ItemStack stackToBundle) {
		if (!stackToBundle.isEmpty() && stackToBundle.getItem().canFitInsideContainerItems()) {
			int storedAmount = getStoredAmount(voidBundleStack);
			int amountAbleToStore = Math.min(stackToBundle.getCount(), (getMaxStoredAmount(voidBundleStack) - getStoredAmount(voidBundleStack)));
			if (amountAbleToStore > 0) {
				ItemStack stackInBundle = getFirstBundledStack(voidBundleStack);
				if (stackInBundle.isEmpty()) {
					stackInBundle = stackToBundle.copy();
					stackInBundle.setCount(amountAbleToStore);
					bundleStack(voidBundleStack, stackInBundle);
					return amountAbleToStore;
				} else if (ItemStack.isSameItemSameTags(stackInBundle, stackToBundle)) {
					stackInBundle.grow(amountAbleToStore);
					bundleStack(voidBundleStack, stackInBundle, storedAmount + amountAbleToStore);
					return amountAbleToStore;
				}
			}
		}
		return 0;
	}
	
	public static Optional<ItemStack> removeFirstBundledStack(ItemStack voidBundleStack) {
		ItemStack removedStack = getFirstBundledStack(voidBundleStack);
		if (removedStack.isEmpty()) {
			return Optional.empty();
		} else {
			removeBundledStackAmount(voidBundleStack, removedStack.getCount());
			return Optional.of(removedStack);
		}
	}
	
	private static boolean dropOneBundledStack(ItemStack voidBundleStack, Player player) {
		ItemStack bundledStack = getFirstBundledStack(voidBundleStack);
		int storedAmount = getStoredAmount(voidBundleStack);
		
		int droppedAmount = Math.min(bundledStack.getMaxStackSize(), storedAmount);
		if (droppedAmount > 0) {
			ItemStack stackToDrop = bundledStack.copy();
			stackToDrop.setCount(droppedAmount);
			player.drop(stackToDrop, true);
			removeBundledStackAmount(voidBundleStack, droppedAmount);
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isLocked(ItemStack itemStack) {
		CompoundTag compound = itemStack.getTag();
		if (compound != null) {
			return compound.getBoolean("Locked");
		}
		return false;
	}
	
	public static ItemStack getFirstBundledStack(ItemStack voidBundleStack) {
		CompoundTag nbtCompound = voidBundleStack.getTag();
		if (nbtCompound == null) {
			return ItemStack.EMPTY;
		} else {
			return getFirstBundledStack(nbtCompound);
		}
	}
	
	private static ItemStack getFirstBundledStack(CompoundTag nbtCompound) {
		CompoundTag storedItemCompound = nbtCompound.getCompound("StoredStack");
		
		int storedAmount = storedItemCompound.getInt("Count");
		ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(storedItemCompound.getString("ID"))));
		int stackAmount = Math.min(storedAmount, itemStack.getMaxStackSize());
		itemStack.setCount(stackAmount);
		
		if (storedItemCompound.contains("Tag", 10)) {
			itemStack.setTag(storedItemCompound.getCompound("Tag"));
		}
		if (itemStack.getItem().canBeDepleted()) {
			itemStack.setDamageValue(itemStack.getDamageValue());
		}
		return itemStack;
	}
	
	private static void bundleStack(ItemStack voidBundleStack, ItemStack stackToBundle) {
		bundleStack(voidBundleStack, stackToBundle, stackToBundle.getCount());
	}
	
	private static int bundleStack(ItemStack voidBundleStack, ItemStack stackToBundle, int amount) {
		CompoundTag voidBundleCompound = voidBundleStack.getOrCreateTag();
		CompoundTag storedItemCompound = new CompoundTag();
		
		boolean hasVoiding = EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.VOIDING, voidBundleStack) > 0;
		int maxStoredAmount = getMaxStoredAmount(voidBundleStack);
		int newAmount = Math.min(maxStoredAmount, storedItemCompound.getInt("Count") + amount);
		int overflowAmount = hasVoiding ? 0 : Math.max(0, amount - maxStoredAmount);

		ResourceLocation identifier = BuiltInRegistries.ITEM.getKey(stackToBundle.getItem());
		storedItemCompound.putString("ID", identifier.toString());
		storedItemCompound.putInt("Count", newAmount);
		if (stackToBundle.getTag() != null) {
			storedItemCompound.put("Tag", stackToBundle.getTag().copy());
		}

		voidBundleCompound.put("StoredStack", storedItemCompound);
		voidBundleStack.setTag(voidBundleCompound);

		return overflowAmount;
	}
	
	protected static void setBundledStack(ItemStack voidBundleStack, ItemStack stackToBundle, int amount) {
		if (stackToBundle.isEmpty() || amount <= 0) {
			voidBundleStack.removeTagKey("StoredStack");
		} else {
			CompoundTag voidBundleCompound = voidBundleStack.getOrCreateTag();
			CompoundTag storedItemCompound = new CompoundTag();
			int maxStoredAmount = getMaxStoredAmount(voidBundleStack);
			int newAmount = Math.min(maxStoredAmount, amount);

			ResourceLocation identifier = BuiltInRegistries.ITEM.getKey(stackToBundle.getItem());
			storedItemCompound.putString("ID", identifier.toString());
			storedItemCompound.putInt("Count", newAmount);
			if (stackToBundle.getTag() != null) {
				storedItemCompound.put("Tag", stackToBundle.getTag().copy());
			}

			voidBundleCompound.put("StoredStack", storedItemCompound);
			voidBundleStack.setTag(voidBundleCompound);
			
		}
	}

	public static int getStoredAmount(ItemStack voidBundleStack) {
		CompoundTag voidBundleCompound = voidBundleStack.getOrCreateTag();
		if (voidBundleCompound.contains("StoredStack")) {
			CompoundTag storedStackCompound = voidBundleCompound.getCompound("StoredStack");
			if (storedStackCompound.contains("Count")) {
				return storedStackCompound.getInt("Count");
			}
		}
		return 0;
	}
	
	private static void removeBundledStackAmount(ItemStack voidBundleStack, int amount) {
		int storedAmount = getStoredAmount(voidBundleStack);
		
		CompoundTag voidBundleCompound = voidBundleStack.getOrCreateTag();
		if (voidBundleCompound.contains("StoredStack")) {
			int remainingCount = storedAmount - amount;
			if (remainingCount > 0) {
				CompoundTag storedStackCompound = voidBundleCompound.getCompound("StoredStack");
				storedStackCompound.putInt("Count", remainingCount);
				voidBundleCompound.put("StoredStack", storedStackCompound);
			} else {
				voidBundleCompound.remove("StoredStack");
			}
			voidBundleStack.setTag(voidBundleCompound);
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
		if (user.isShiftKeyDown()) {
			ItemStack handStack = user.getItemInHand(hand);
			CompoundTag compound = handStack.getOrCreateTag();
			if (compound.contains("Locked")) {
				compound.remove("Locked");
				if (world.isClientSide) {
					playZipSound(user, 0.8F);
				}
			} else {
				compound.putBoolean("Locked", true);
				if (world.isClientSide) {
					playZipSound(user, 1.0F);
				}
			}
			return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
		} else if (dropOneBundledStack(itemStack, user)) {
			this.playDropContentsSound(user);
			user.awardStat(Stats.ITEM_USED.get(this));
			return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
		} else {
			return InteractionResultHolder.fail(itemStack);
		}
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getPlayer().isShiftKeyDown()) {
			// place as block
			return this.place(new BlockPlaceContext(context));
		}
		return super.useOn(context);
	}
	
	public InteractionResult place(BlockPlaceContext itemPlacementContext) {
		if (!itemPlacementContext.canPlace()) {
			return InteractionResult.FAIL;
		} else {
			BlockState blockState = this.getPlacementState(itemPlacementContext);
			if (blockState == null) {
				return InteractionResult.FAIL;
			} else if (!this.place(itemPlacementContext, blockState)) {
				return InteractionResult.FAIL;
			} else {
				BlockPos blockPos = itemPlacementContext.getClickedPos();
				Level world = itemPlacementContext.getLevel();
				Player playerEntity = itemPlacementContext.getPlayer();
				ItemStack itemStack = itemPlacementContext.getItemInHand();
				BlockState blockState2 = world.getBlockState(blockPos);
				if (blockState2.is(blockState.getBlock())) {
					blockState2.getBlock().setPlacedBy(world, blockPos, blockState2, playerEntity, itemStack);
					if (playerEntity instanceof ServerPlayer) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerEntity, blockPos, itemStack);
					}
				}
				
				SoundType blockSoundGroup = blockState2.getSoundType();
				world.playSound(playerEntity, blockPos, this.getPlaceSound(blockState2), SoundSource.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
				world.gameEvent(playerEntity, GameEvent.BLOCK_PLACE, blockPos);
				if (playerEntity == null || !playerEntity.getAbilities().instabuild) {
					itemStack.shrink(1);
				}
				
				return InteractionResult.sidedSuccess(world.isClientSide);
			}
		}
	}
	
	protected boolean place(BlockPlaceContext context, BlockState state) {
		return context.getLevel().setBlock(context.getClickedPos(), state, 11);
	}
	
	protected SoundEvent getPlaceSound(BlockState state) {
		return state.getSoundType().getPlaceSound();
	}
	
	@Nullable
	protected BlockState getPlacementState(BlockPlaceContext context) {
		BlockState blockState = SpectrumBlocks.BOTTOMLESS_BUNDLE.getStateForPlacement(context);
		return blockState != null && this.canPlace(context, blockState) ? blockState : null;
	}
	
	protected boolean canPlace(BlockPlaceContext context, BlockState state) {
		Player playerEntity = context.getPlayer();
		CollisionContext shapeContext = playerEntity == null ? CollisionContext.empty() : CollisionContext.of(playerEntity);
		return state.canSurvive(context.getLevel(), context.getClickedPos()) && context.getLevel().isUnobstructed(state, context.getClickedPos(), shapeContext);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getStoredAmount(stack) > 0;
	}
	
	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.min(1 + (int) Math.round(12 * ((double) getStoredAmount(stack) / getMaxStoredAmount(stack))), 13);
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return super.getBarColor(stack);
	}
	
	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack voidBundleStack) {
		ItemStack itemStack = getFirstBundledStack(voidBundleStack);
		int storedAmount = getStoredAmount(voidBundleStack);
		
		return Optional.of(new BottomlessBundleTooltipData(itemStack, storedAmount));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
		boolean locked = isLocked(stack);
		int storedAmount = getStoredAmount(stack);
		if (storedAmount == 0) {
			tooltip.add(Component.translatable("item.spectrum.bottomless_bundle.tooltip.empty").withStyle(ChatFormatting.GRAY));
			if (locked) {
				tooltip.add(Component.translatable("item.spectrum.bottomless_bundle.tooltip.locked").withStyle(ChatFormatting.GRAY));
			}
		} else {
			ItemStack firstStack = getFirstBundledStack(stack);
			String totalStacks = Support.getShortenedNumberString(storedAmount / (float) firstStack.getMaxStackSize());
			tooltip.add(Component.translatable("item.spectrum.bottomless_bundle.tooltip.count", storedAmount, getMaxStoredAmount(stack), totalStacks).withStyle(ChatFormatting.GRAY));
			if (locked) {
				tooltip.add(Component.translatable("item.spectrum.bottomless_bundle.tooltip.locked").withStyle(ChatFormatting.GRAY));
			} else {
				tooltip.add(Component.translatable("item.spectrum.bottomless_bundle.tooltip.enter_inventory", firstStack.getHoverName().getString()).withStyle(ChatFormatting.GRAY));
			}
		}
		if (EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.VOIDING, stack) > 0) {
			tooltip.add(Component.translatable("item.spectrum.bottomless_bundle.tooltip.voiding"));
		}
	}
	
	@Override
	public void onDestroyed(ItemEntity entity) {
		Level world = entity.level();
		if (!world.isClientSide) {
			ItemStack voidBundleItemStack = entity.getItem();
			int currentAmount = getStoredAmount(voidBundleItemStack);
			if (currentAmount > 0) {
				ItemStack storedStack = getFirstBundledStack(voidBundleItemStack);
				while (currentAmount > 0) {
					int stackCount = Math.min(currentAmount, storedStack.getMaxStackSize());
					
					ItemStack entityStack = storedStack.copy();
					entityStack.setCount(stackCount);
					world.addFreshEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), entityStack));
					
					currentAmount -= stackCount;
				}
			}
		}
	}
	
	/**
	 * When the bundle is clicked onto another stack
	 */
	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickType, Player player) {
		if (clickType != ClickAction.SECONDARY) {
			return false;
		} else {
			ItemStack itemStack = slot.getItem();
			if (itemStack.isEmpty()) {
				playRemoveOneSound(player);
				removeFirstBundledStack(stack).ifPresent((removedStack) -> add(stack, slot.safeInsert(removedStack)));
			} else if (itemStack.getItem().canFitInsideContainerItems()) {
				ItemStack firstStack = getFirstBundledStack(stack);
				if (firstStack.isEmpty() || ItemStack.isSameItemSameTags(firstStack, itemStack)) {
					boolean hasVoiding = EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.VOIDING, stack) > 0;
					int amountAbleToStore = hasVoiding ? itemStack.getCount() : Math.min(itemStack.getCount(), (getMaxStoredAmount(stack) - getStoredAmount(stack)));
					if (amountAbleToStore > 0) {
						add(stack, slot.safeTake(itemStack.getCount(), amountAbleToStore, player));
						this.playInsertSound(player);
					}
				}
			}
			
			return true;
		}
	}
	
	/**
	 * When an itemStack is right-clicked onto the bundle
	 */
	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference) {
		if (clickType == ClickAction.SECONDARY && slot.allowModification(player)) {
			if (otherStack.isEmpty()) {
				removeFirstBundledStack(stack).ifPresent((itemStack) -> {
					this.playRemoveOneSound(player);
					cursorStackReference.set(itemStack);
				});
			} else {
				int storedAmount = add(stack, otherStack);
				if (storedAmount > 0) {
					this.playInsertSound(player);
					otherStack.shrink(storedAmount);
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		
		// tick stack inside the bundle. I hope that kinda wrong slot reference does not break anything
		ItemStack bundledStack = BottomlessBundleItem.getFirstBundledStack(stack);
		if (!bundledStack.isEmpty()) {
			int amount = BottomlessBundleItem.getStoredAmount(stack);
			bundledStack.setCount(amount);
			bundledStack.getItem().inventoryTick(bundledStack, world, entity, slot, selected);
			BottomlessBundleItem.bundleStack(stack, bundledStack);
		}
	}
	
	@Override
	public boolean acceptsItemStack(ItemStack inventoryInsertionAcceptorStack, ItemStack itemStackToAccept) {
		ItemStack storedStack = getFirstBundledStack(inventoryInsertionAcceptorStack);
		if (storedStack.isEmpty()) {
			return false;
		} else {
			return ItemStack.isSameItemSameTags(storedStack, itemStackToAccept);
		}
	}
	
	@Override
	public int acceptItemStack(ItemStack inventoryInsertionAcceptorStack, ItemStack itemStackToAccept, Player playerEntity) {
		if (isLocked(inventoryInsertionAcceptorStack)) {
			return itemStackToAccept.getCount();
		}
		
		int storedAmount = getStoredAmount(inventoryInsertionAcceptorStack);
		return bundleStack(inventoryInsertionAcceptorStack, itemStackToAccept, itemStackToAccept.getCount() + storedAmount);
	}
	
	private void playRemoveOneSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}
	
	private void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}
	
	private void playDropContentsSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}
	
	private void playZipSound(Entity entity, float basePitch) {
		entity.playSound(SpectrumSoundEvents.BOTTOMLESS_BUNDLE_ZIP, 0.8F, basePitch + entity.level().getRandom().nextFloat() * 0.4F);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return stack.getCount() == 1;
	}
	
	@Override
	public boolean acceptsEnchantment(Enchantment enchantment) {
		return enchantment == Enchantments.POWER_ARROWS || enchantment == SpectrumEnchantments.VOIDING;
	}
	
	@Override
	public int getEnchantmentValue() {
		return 5;
	}

	public static class BottomlessBundlePlacementDispenserBehavior extends OptionalDispenseItemBehavior {
		
		@Override
		protected ItemStack execute(BlockSource pointer, ItemStack stack) {
			this.setSuccess(false);
			Item item = stack.getItem();
			if (item instanceof BottomlessBundleItem bottomlessBundleItem) {
				Direction direction = pointer.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos blockPos = pointer.getPos().relative(direction);
				Direction direction2 = pointer.getLevel().isEmptyBlock(blockPos.below()) ? direction : Direction.UP;
				
				try {
					this.setSuccess(bottomlessBundleItem.place(new DirectionalPlaceContext(pointer.getLevel(), blockPos, direction, stack, direction2)).consumesAction());
				} catch (Exception e) {
					SpectrumCommon.logError("Error trying to place bottomless bundle at " + blockPos + " : " + e);
				}
			}
			
			return stack;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Renderer implements DynamicItemRenderer {
		public Renderer() {}
		@Override
		public void render(ItemRenderer renderer, ItemStack stack, ItemDisplayContext mode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel model) {
			renderer.render(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, model);
			if(mode != ItemDisplayContext.GUI
					|| getStoredAmount(stack) <= 0) return;
			ItemStack bundledStack = BottomlessBundleItem.getFirstBundledStack(stack);
			Minecraft client = Minecraft.getInstance();
			BakedModel bundledModel = renderer.getModel(bundledStack, client.level, client.player, 0);

			matrices.pushPose();
			matrices.scale(0.5F, 0.5F, 0.5F);
			matrices.translate(0.5F, 0.5F, 0.5F);
			renderer.render(bundledStack, mode, leftHanded, matrices, vertexConsumers, light, overlay, bundledModel);
			matrices.popPose();
		}
	}
}
