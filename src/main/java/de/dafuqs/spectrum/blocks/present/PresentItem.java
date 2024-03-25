package de.dafuqs.spectrum.blocks.present;

import de.dafuqs.spectrum.items.tooltip.PresentTooltipData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.stream.Stream;

public class PresentItem extends BlockItem {
	
	private static final String ITEMS_KEY = "Items";
	public static final int MAX_STORAGE_STACKS = 5;
	private static final int ITEM_BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
	
	public PresentItem(Block block, Properties settings) {
		super(block, settings);
	}
	
	
	@Override
	protected boolean canPlace(BlockPlaceContext context, BlockState state) {
		return isWrapped(context.getItemInHand()) && super.canPlace(context, state);
	}
	
	public static boolean isWrapped(ItemStack itemStack) {
		return isWrapped(itemStack.getTag());
	}
	
	public static boolean isWrapped(CompoundTag compound) {
		return compound != null && compound.getBoolean("Wrapped");
	}
	
	public static void setWrapper(ItemStack itemStack, Player giver) {
		setWrapper(itemStack, giver.getUUID(), giver.getName().getString());
	}
	
	public static void setWrapper(ItemStack itemStack, UUID uuid, String name) {
		CompoundTag compound = itemStack.getOrCreateTag();
		compound.putUUID("GiverUUID", uuid);
		compound.putString("Giver", name);
		itemStack.setTag(compound);
	}
	
	public static Optional<Tuple<UUID, String>> getWrapper(ItemStack itemStack) {
		return getWrapper(itemStack.getTag());
	}
	
	public static Optional<Tuple<UUID, String>> getWrapper(CompoundTag compound) {
		if (compound != null && compound.contains("GiverUUID") && compound.contains("Giver", Tag.TAG_STRING)) {
			return Optional.of(new Tuple<>(compound.getUUID("GiverUUID"), compound.getString("Giver")));
		}
		return Optional.empty();
	}
	
	public static Map<DyeColor, Integer> getColors(ItemStack itemStack) {
		return getColors(itemStack.getTag());
	}
	
	public static Map<DyeColor, Integer> getColors(CompoundTag compound) {
		Map<DyeColor, Integer> colors = new HashMap<>();
		if (compound != null && compound.contains("Colors", Tag.TAG_LIST)) {
			for (Tag e : compound.getList("Colors", Tag.TAG_COMPOUND)) {
				CompoundTag c = (CompoundTag) e;
				colors.put(DyeColor.valueOf(c.getString("Color").toUpperCase(Locale.ROOT)), c.getInt("Amount"));
			}
		}
		return colors;
	}
	
	public static void wrap(ItemStack itemStack, PresentBlock.WrappingPaper wrappingPaper, Map<DyeColor, Integer> colors) {
		CompoundTag compound = itemStack.getOrCreateTag();
		setWrapped(compound);
		setVariant(compound, wrappingPaper);
		setColors(compound, colors);
		itemStack.setTag(compound);
	}
	
	public static void setWrapped(CompoundTag compound) {
		compound.putBoolean("Wrapped", true);
	}
	
	public static void setColors(CompoundTag compound, Map<DyeColor, Integer> colors) {
		if (!colors.isEmpty()) {
			ListTag colorList = new ListTag();
			for (Map.Entry<DyeColor, Integer> colorEntry : colors.entrySet()) {
				CompoundTag colorCompound = new CompoundTag();
				colorCompound.putString("Color", colorEntry.getKey().getName());
				colorCompound.putInt("Amount", colorEntry.getValue());
				colorList.add(colorCompound);
			}
			compound.put("Colors", colorList);
		}
	}
	
	public static void setVariant(CompoundTag compound, PresentBlock.WrappingPaper wrappingPaper) {
		compound.putString("Variant", wrappingPaper.getSerializedName());
	}
	
	public static PresentBlock.WrappingPaper getVariant(CompoundTag compound) {
		if (compound != null && compound.contains("Variant", Tag.TAG_STRING)) {
			return PresentBlock.WrappingPaper.valueOf(compound.getString("Variant").toUpperCase(Locale.ROOT));
		}
		return PresentBlock.WrappingPaper.RED;
	}
	
	@Override
	public boolean overrideStackedOnOther(ItemStack present, Slot slot, ClickAction clickType, Player player) {
		if (clickType != ClickAction.SECONDARY) {
			return false;
		} else {
			ItemStack itemStack = slot.getItem();
			if (itemStack.isEmpty()) {
				this.playRemoveOneSound(player);
				removeFirstStack(present).ifPresent((removedStack) -> addToPresent(present, slot.safeInsert(removedStack)));
			} else if (itemStack.getItem().canFitInsideContainerItems()) {
				ItemStack slotStack = slot.safeTake(itemStack.getCount(), 64, player);
				int acceptedStacks = addToPresent(present, slotStack);
				slotStack.shrink(acceptedStacks);
				if (!slotStack.isEmpty()) {
					slot.setByPlayer(slotStack);
				}
				if (acceptedStacks > 0) {
					this.playInsertSound(player);
				}
			}
			
			return true;
		}
	}
	
	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference) {
		if (clickType == ClickAction.SECONDARY && slot.allowModification(player)) {
			if (otherStack.isEmpty()) {
				removeFirstStack(stack).ifPresent((itemStack) -> {
					this.playRemoveOneSound(player);
					cursorStackReference.set(itemStack);
				});
			} else {
				int i = addToPresent(stack, otherStack);
				if (i > 0) {
					this.playInsertSound(player);
					otherStack.shrink(i);
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void onCraftedBy(ItemStack stack, Level world, Player player) {
		super.onCraftedBy(stack, world, player);
		if (player != null) {
			setWrapper(stack, player);
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
		if (isWrapped(itemStack)) {
			super.use(world, user, hand);
		}
		return InteractionResultHolder.pass(itemStack);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return !isWrapped(stack) && getBundledStacks(stack).findAny().isPresent();
	}
	
	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.min(1 + (int) (12 * (getBundledStacks(stack).count() / (float) MAX_STORAGE_STACKS)), 13);
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return ITEM_BAR_COLOR;
	}

	public static int addToPresent(ItemStack present, ItemStack stackToAdd) {
		if (!stackToAdd.isEmpty() && stackToAdd.getItem().canFitInsideContainerItems()) {
			CompoundTag nbt = present.getOrCreateTag();
			if (!nbt.contains(ITEMS_KEY)) {
				nbt.put(ITEMS_KEY, new ListTag());
			}
			
			ListTag nbtList = nbt.getList(ITEMS_KEY, 10);
			
			int originalCount = stackToAdd.getCount();
			for (int i = 0; i < MAX_STORAGE_STACKS; i++) {
				ItemStack storedStack = ItemStack.of(nbtList.getCompound(i));
				if (storedStack.isEmpty()) {
					CompoundTag leftoverCompound = new CompoundTag();
					stackToAdd.save(leftoverCompound);
					nbtList.add(leftoverCompound);
					present.setTag(nbt);
					return originalCount;
				}
				if (ItemStack.isSameItemSameTags(stackToAdd, storedStack)) {
					int additionalAmount = Math.min(stackToAdd.getCount(), storedStack.getMaxStackSize() - storedStack.getCount());
					if (additionalAmount > 0) {
						stackToAdd.shrink(additionalAmount);
						storedStack.grow(additionalAmount);
						
						CompoundTag newCompound = new CompoundTag();
						storedStack.save(newCompound);
						nbtList.set(i, newCompound);
						if (stackToAdd.isEmpty()) {
							present.setTag(nbt);
							return originalCount;
						}
					}
				}
			}
			
			return originalCount - stackToAdd.getCount();
		}
		return 0;
	}
	
	private static Optional<ItemStack> removeFirstStack(ItemStack stack) {
		CompoundTag nbt = stack.getOrCreateTag();
		if (!nbt.contains(ITEMS_KEY)) {
			return Optional.empty();
		} else {
			ListTag nbtList = nbt.getList(ITEMS_KEY, 10);
			if (nbtList.isEmpty()) {
				return Optional.empty();
			} else {
				int i = 0;
				CompoundTag nbtCompound2 = nbtList.getCompound(i);
				ItemStack itemStack = ItemStack.of(nbtCompound2);
				nbtList.remove(i);
				if (nbtList.isEmpty()) {
					stack.removeTagKey(ITEMS_KEY);
				}
				
				return Optional.of(itemStack);
			}
		}
	}
	
	public static Stream<ItemStack> getBundledStacks(ItemStack stack) {
		return getBundledStacks(stack.getTag());
	}
	
	public static Stream<ItemStack> getBundledStacks(CompoundTag nbtCompound) {
		if (nbtCompound == null) {
			return Stream.empty();
		} else {
			ListTag nbtList = nbtCompound.getList(ITEMS_KEY, 10);
			Stream<Tag> stream = nbtList.stream();
			Objects.requireNonNull(CompoundTag.class);
			return stream.map(CompoundTag.class::cast).map(ItemStack::of);
		}
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		boolean wrapped = isWrapped(stack);
		if (wrapped) {
			return Optional.empty();
		}
		
		List<ItemStack> list = new ArrayList<>(MAX_STORAGE_STACKS);
		getBundledStacks(stack).forEachOrdered(list::add);
		while (list.size() < MAX_STORAGE_STACKS) {
			list.add(ItemStack.EMPTY);
		}
		return Optional.of(new PresentTooltipData(list));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
		boolean wrapped = isWrapped(stack);
		if (wrapped) {
			Optional<Tuple<UUID, String>> giver = getWrapper(stack);
			if (giver.isPresent()) {
				tooltip.add((Component.translatable("block.spectrum.present.tooltip.wrapped.giver", giver.get().getB()).withStyle(ChatFormatting.GRAY)));
				if (context.isAdvanced()) {
					tooltip.add((Component.literal("UUID: " + giver.get().getA().toString()).withStyle(ChatFormatting.GRAY)));
				}
			} else {
				tooltip.add((Component.translatable("block.spectrum.present.tooltip.wrapped").withStyle(ChatFormatting.GRAY)));
			}
		} else {
			tooltip.add((Component.translatable("block.spectrum.present.tooltip.description").withStyle(ChatFormatting.GRAY)));
			tooltip.add((Component.translatable("block.spectrum.present.tooltip.description2").withStyle(ChatFormatting.GRAY)));
			
			NonNullList<ItemStack> defaultedList = NonNullList.create();
			Stream<ItemStack> bundledStacks = getBundledStacks(stack);
			bundledStacks.forEach(defaultedList::add);
			tooltip.add((Component.translatable("item.minecraft.bundle.fullness", defaultedList.size(), MAX_STORAGE_STACKS)).withStyle(ChatFormatting.GRAY));
		}
	}
	
	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
	
	@Override
	public void onDestroyed(ItemEntity entity) {
		ItemUtils.onContainerDestroyed(entity, getBundledStacks(entity.getItem()));
	}
	
	private void playRemoveOneSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}
	
	private void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}
	
}
