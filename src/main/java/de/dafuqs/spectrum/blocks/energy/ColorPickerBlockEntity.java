package de.dafuqs.spectrum.blocks.energy;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageBlockEntity;
import de.dafuqs.spectrum.api.energy.InkStorageItem;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.storage.TotalCappedInkStorage;
import de.dafuqs.spectrum.inventories.ColorPickerScreenHandler;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.recipe.ink_converting.InkConvertingRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ColorPickerBlockEntity extends RandomizableContainerBlockEntity implements ExtendedScreenHandlerFactory, PlayerOwned, InkStorageBlockEntity<TotalCappedInkStorage> {
	
	public static final int INVENTORY_SIZE = 2; // input & output slots
	public static final int INPUT_SLOT_ID = 0;
	public static final int OUTPUT_SLOT_ID = 1;
	public static final long TICKS_PER_CONVERSION = 5;
	public static final long STORAGE_AMOUNT = 64 * 64 * 64 * 100;
	public NonNullList<ItemStack> inventory;
	protected TotalCappedInkStorage inkStorage;
	protected boolean paused;
	protected boolean inkDirty;
	protected @Nullable InkConvertingRecipe cachedRecipe;
	protected @Nullable InkColor selectedColor;
	private UUID ownerUUID;
	
	public ColorPickerBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(SpectrumBlockEntities.COLOR_PICKER, blockPos, blockState);
		
		this.inventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
		this.inkStorage = new TotalCappedInkStorage(STORAGE_AMOUNT);
		this.selectedColor = null;
	}
	
	public static void tick(Level world, BlockPos pos, BlockState state, ColorPickerBlockEntity blockEntity) {
		if (!world.isClientSide) {
			blockEntity.inkDirty = false;
			if (!blockEntity.paused) {
				boolean convertedPigment = false;
				boolean shouldPause = true;
				if (world.getGameTime() % TICKS_PER_CONVERSION == 0) {
					convertedPigment = blockEntity.tryConvertPigmentToEnergy((ServerLevel) world);
				} else {
					shouldPause = false;
				}
				boolean filledContainer = blockEntity.tryFillInkContainer(); // that's an OR
				
				if (convertedPigment || filledContainer) {
					blockEntity.updateInClientWorld();
					blockEntity.setInkDirty();
					blockEntity.setChanged();
				} else if (shouldPause) {
					blockEntity.paused = true;
				}
			}
		}
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(nbt)) {
			ContainerHelper.loadAllItems(nbt, this.inventory);
		}
		if (nbt.contains("InkStorage", Tag.TAG_COMPOUND)) {
			this.inkStorage = TotalCappedInkStorage.fromNbt(nbt.getCompound("InkStorage"));
		}
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
		if (nbt.contains("SelectedColor", Tag.TAG_STRING)) {
			this.selectedColor = InkColor.of(nbt.getString("SelectedColor"));
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (!this.trySaveLootTable(nbt)) {
			ContainerHelper.saveAllItems(nbt, this.inventory);
		}
		nbt.put("InkStorage", this.inkStorage.toNbt());
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
		if (this.selectedColor != null) {
			nbt.putString("SelectedColor", this.selectedColor.toString());
		}
	}
	
	@Override
	protected Component getDefaultName() {
		return Component.translatable("block.spectrum.color_picker");
	}
	
	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return new ColorPickerScreenHandler(syncId, playerInventory, this.worldPosition, this.selectedColor);
	}
	
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public void setOwner(Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		setChanged();
	}
	
	@Override
	public TotalCappedInkStorage getEnergyStorage() {
		return inkStorage;
	}
	
	@Override
	public void setInkDirty() {
		this.inkDirty = true;
	}
	
	@Override
	public boolean getInkDirty() {
		return inkDirty;
	}
	
	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.inventory;
	}
	
	@Override
	protected void setItems(NonNullList<ItemStack> list) {
		this.inventory = list;
		this.paused = false;
		updateInClientWorld();
	}
	
	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack itemStack = super.removeItem(slot, amount);
		this.paused = false;
		updateInClientWorld();
		return itemStack;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack itemStack = super.removeItemNoUpdate(slot);
		this.paused = false;
		updateInClientWorld();
		return itemStack;
	}
	
	@Override
	public void setItem(int slot, ItemStack stack) {
		super.setItem(slot, stack);
		this.paused = false;
		updateInClientWorld();
	}
	
	@Override
	public int getContainerSize() {
		return INVENTORY_SIZE;
	}
	
	@Override
	public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
		buf.writeBlockPos(worldPosition);
		if (this.selectedColor == null) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeUtf(selectedColor.toString());
		}
	}
	
	protected boolean tryConvertPigmentToEnergy(ServerLevel world) {
		InkConvertingRecipe recipe = getInkConvertingRecipe(world);
		if (recipe != null) {
			InkColor color = recipe.getInkColor();
			long amount = recipe.getInkAmount();
			if (amount <= this.inkStorage.getRoom(color)) {
				inventory.get(INPUT_SLOT_ID).shrink(1);
				this.inkStorage.addEnergy(color, amount);
				
				if (SpectrumCommon.CONFIG.BlockSoundVolume > 0) {
					world.playSound(null, worldPosition, SpectrumSoundEvents.COLOR_PICKER_PROCESSING, SoundSource.BLOCKS, SpectrumCommon.CONFIG.BlockSoundVolume / 3, 1.0F);
				}
				SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity(world,
						new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.7, worldPosition.getZ() + 0.5),
						SpectrumParticleTypes.getFluidRisingParticle(color.getDyeColor()),
						5,
						new Vec3(0.22, 0.0, 0.22),
						new Vec3(0.0, 0.1, 0.0)
				);
				
				return true;
			}
		}
		return false;
	}
	
	protected @Nullable InkConvertingRecipe getInkConvertingRecipe(Level world) {
		// is the current stack empty?
		ItemStack inputStack = inventory.get(INPUT_SLOT_ID);
		if (inputStack.isEmpty()) {
			this.cachedRecipe = null;
			return null;
		}
		
		// does the cached recipe match?
		if (this.cachedRecipe != null) {
			if (this.cachedRecipe.getIngredients().get(0).test(inputStack)) {
				return this.cachedRecipe;
			}
		}
		
		// search matching recipe
		Optional<InkConvertingRecipe> recipe = world.getRecipeManager().getRecipeFor(SpectrumRecipeTypes.INK_CONVERTING, this, world);
		if (recipe.isPresent()) {
			this.cachedRecipe = recipe.get();
			return this.cachedRecipe;
		} else {
			this.cachedRecipe = null;
			return null;
		}
	}
	
	protected boolean tryFillInkContainer() {
		long transferredAmount = 0;
		
		ItemStack stack = inventory.get(OUTPUT_SLOT_ID);
		if (stack.getItem() instanceof InkStorageItem<?> inkStorageItem) {
			InkStorage itemStorage = inkStorageItem.getEnergyStorage(stack);

			ServerPlayer owner = null;
			if (getOwnerIfOnline() instanceof ServerPlayer serverPlayerEntity) {
				owner = serverPlayerEntity;
			}

			if (this.selectedColor == null) {
				for (InkColor color : InkColor.all()) {
					transferredAmount += tryTransferInk(owner, stack, itemStorage, color);
				}
			} else {
				transferredAmount = tryTransferInk(owner, stack, itemStorage, this.selectedColor);
			}
			
			if (transferredAmount > 0) {
				inkStorageItem.setEnergyStorage(stack, itemStorage);
			}
		}
		
		return transferredAmount > 0;
	}

	private long tryTransferInk(ServerPlayer owner, ItemStack stack, InkStorage itemStorage, InkColor color) {
		long amount = InkStorage.transferInk(this.inkStorage, itemStorage, color);
		if (amount > 0 && owner != null) {
			SpectrumAdvancementCriteria.INK_CONTAINER_INTERACTION.trigger(owner, stack, itemStorage, color, amount);
		}
		return amount;
	}

	public void setSelectedColor(InkColor inkColor) {
		this.selectedColor = inkColor;
		this.paused = false;
		this.setChanged();
	}
	
	public @Nullable InkColor getSelectedColor() {
		return this.selectedColor;
	}
	
	// Called when the chunk is first loaded to initialize this be
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbtCompound = new CompoundTag();
		this.saveAdditional(nbtCompound);
		return nbtCompound;
	}
	
	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	public void updateInClientWorld() {
		if (level != null) {
			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), Block.UPDATE_INVISIBLE);
		}
	}
	
	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if (slot == INPUT_SLOT_ID) {
			return InkConvertingRecipe.isInput(stack.getItem());
		}
		if (slot == OUTPUT_SLOT_ID) {
			return stack.getItem() instanceof InkStorageItem<?>;
		}
		return true;
	}
	
}
