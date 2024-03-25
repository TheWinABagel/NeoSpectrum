package de.dafuqs.spectrum.blocks.bottomless_bundle;

import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BottomlessBundleBlockEntity extends BlockEntity {

	protected ItemStack bottomlessBundleStack;

	public final SingleVariantStorage<ItemVariant> storage = new SingleVariantStorage<>() {
		@Override
		protected boolean canInsert(ItemVariant variant) {
			ItemStack bundledStack = BottomlessBundleItem.getFirstBundledStack(bottomlessBundleStack);
			return bundledStack.isEmpty() || variant.matches(bundledStack);
		}

		@Override
		public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
			long inserted = super.insert(insertedVariant, maxAmount, transaction);
			if (EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.VOIDING, bottomlessBundleStack) > 0) {
				return maxAmount;
			}
			return inserted;
		}

		@Override
		protected ItemVariant getBlankVariant() {
			return ItemVariant.blank();
		}

		@Override
		protected long getCapacity(ItemVariant variant) {
			return BottomlessBundleItem.getMaxStoredAmount(bottomlessBundleStack);
		}

		@Override
		protected void onFinalCommit() {
			super.onFinalCommit();
			setChanged();
		}
	};

	public BottomlessBundleBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.BOTTOMLESS_BUNDLE, pos, state);
		this.bottomlessBundleStack = ItemStack.EMPTY;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.bottomlessBundleStack = ItemStack.of(nbt.getCompound("Bundle"));

		this.storage.variant = ItemVariant.fromNbt(nbt.getCompound("StorageVariant"));
		this.storage.amount = nbt.getLong("StorageCount");
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);

		CompoundTag bundleCompound = new CompoundTag();
		bottomlessBundleStack.save(bundleCompound);
		nbt.put("Bundle", bundleCompound);

		nbt.put("StorageVariant", this.storage.variant.toNbt());
		nbt.putLong("StorageCount", this.storage.amount);
	}

	public void setBundle(@NotNull ItemStack itemStack) {
		if (itemStack.getItem() instanceof BottomlessBundleItem) {
			this.bottomlessBundleStack = itemStack;
			this.storage.variant = ItemVariant.of(BottomlessBundleItem.getFirstBundledStack(itemStack));
			this.storage.amount = BottomlessBundleItem.getStoredAmount(itemStack);
		}
	}

	public ItemStack retrieveBundle() {
		if (this.bottomlessBundleStack.isEmpty()) {
			return SpectrumItems.BOTTOMLESS_BUNDLE.getDefaultInstance();
		} else {
			BottomlessBundleItem.setBundledStack(this.bottomlessBundleStack, this.storage.getResource().toStack(), (int) this.storage.amount);
			return this.bottomlessBundleStack;
		}
	}
	
}
