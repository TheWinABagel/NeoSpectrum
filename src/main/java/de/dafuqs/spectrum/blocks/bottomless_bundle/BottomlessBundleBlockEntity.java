package de.dafuqs.spectrum.blocks.bottomless_bundle;

import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BottomlessBundleBlockEntity extends BlockEntity {

	protected ItemStack bottomlessBundleStack;

	public final ItemStackHandler storage = new ItemStackHandler(1) {

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			ItemStack bundledStack = BottomlessBundleItem.getFirstBundledStack(bottomlessBundleStack);
			return bundledStack.isEmpty() || ItemStack.matches(stack, bundledStack);
		}

		@Override
		protected int getStackLimit(int slot, @NotNull ItemStack stack) {
			return BottomlessBundleItem.getMaxStoredAmount(bottomlessBundleStack);
		}

		@Override
		public int getSlotLimit(int slot) {
			return BottomlessBundleItem.getMaxStoredAmount(bottomlessBundleStack);
		}
	};

	LazyOptional<IItemHandler> storageCap = LazyOptional.of(() -> this.storage);

//	public final SingleVariantStorage<ItemVariant> storage = new SingleVariantStorage<>() {
//		@Override
//		protected boolean canInsert(ItemVariant variant) {
//			ItemStack bundledStack = BottomlessBundleItem.getFirstBundledStack(bottomlessBundleStack);
//			return bundledStack.isEmpty() || variant.matches(bundledStack);
//		}
//
//		@Override
//		public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
//			long inserted = super.insert(insertedVariant, maxAmount, transaction);
//			if (EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.VOIDING, bottomlessBundleStack) > 0) {
//				return maxAmount;
//			}
//			return inserted;
//		}
//
//		@Override
//		protected ItemVariant getBlankVariant() {
//			return ItemVariant.blank();
//		}
//
//		@Override
//		protected long getCapacity(ItemVariant variant) {
//			return BottomlessBundleItem.getMaxStoredAmount(bottomlessBundleStack);
//		}
//
//		@Override
//		protected void onFinalCommit() {
//			super.onFinalCommit();
//			setChanged();
//		}
//	};

	public BottomlessBundleBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.BOTTOMLESS_BUNDLE, pos, state);
		this.bottomlessBundleStack = ItemStack.EMPTY;
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return storageCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.bottomlessBundleStack = ItemStack.of(nbt.getCompound("Bundle"));

		this.storage.deserializeNBT(nbt.getCompound("Inventory"));
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);

		CompoundTag bundleCompound = new CompoundTag();
		bottomlessBundleStack.save(bundleCompound);
		nbt.put("Bundle", bundleCompound);

		nbt.put("Inventory", this.storage.serializeNBT());
	}

	public void setBundle(@NotNull ItemStack itemStack) {
		if (itemStack.getItem() instanceof BottomlessBundleItem) {
			this.bottomlessBundleStack = itemStack;
			this.storage.setStackInSlot(0, BottomlessBundleItem.getFirstBundledStack(itemStack));
		}
	}

	public ItemStack retrieveBundle() {
		if (this.bottomlessBundleStack.isEmpty()) {
			return SpectrumItems.BOTTOMLESS_BUNDLE.getDefaultInstance();
		} else {
			BottomlessBundleItem.setBundledStack(this.bottomlessBundleStack, this.storage.getStackInSlot(0), this.storage.getStackInSlot(0).getCount());
			return this.bottomlessBundleStack;
		}
	}
	
}
