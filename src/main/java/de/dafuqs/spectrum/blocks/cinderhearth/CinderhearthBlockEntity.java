package de.dafuqs.spectrum.blocks.cinderhearth;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.MultiblockCrafter;
import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageBlockEntity;
import de.dafuqs.spectrum.api.energy.InkStorageItem;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.api.energy.storage.IndividualCappedInkStorage;
import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.api.recipe.GatedRecipe;
import de.dafuqs.spectrum.blocks.upgrade.Upgradeable;
import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.inventories.CinderhearthScreenHandler;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.recipe.cinderhearth.CinderhearthRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumItemTags;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CinderhearthBlockEntity extends BaseContainerBlockEntity implements MultiblockCrafter, WorldlyContainer, InkStorageBlockEntity<IndividualCappedInkStorage> {

	public static final int INVENTORY_SIZE = 11;
	public static final int INPUT_SLOT_ID = 0;
	public static final int INK_PROVIDER_SLOT_ID = 1;
	public static final int EXPERIENCE_STORAGE_ITEM_SLOT_ID = 2;
	public static final int FIRST_OUTPUT_SLOT_ID = 3;
	public static final int LAST_OUTPUT_SLOT_ID = 10;
	public static final int[] OUTPUT_SLOT_IDS = new int[]{3, 4, 5, 6, 7, 8, 9, 10};

	protected NonNullList<ItemStack> inventory;
	protected boolean inventoryChanged;

	public static final Set<InkColor> USED_INK_COLORS = Set.of(InkColors.ORANGE, InkColors.LIGHT_BLUE, InkColors.MAGENTA, InkColors.PURPLE, InkColors.BLACK);
	public static final long INK_STORAGE_SIZE = 64 * 100;
	protected IndividualCappedInkStorage inkStorage;

	private UUID ownerUUID;
	private UpgradeHolder upgrades;
	private Recipe<?> currentRecipe; // blasting & cinderhearth
	private int craftingTime;
	private int craftingTimeTotal;
	protected boolean canTransferInk;
	protected boolean inkDirty;

	protected CinderHearthStructureType structure = CinderHearthStructureType.NONE;

	protected final ContainerData propertyDelegate;

	@Override
	public int[] getSlotsForFace(Direction side) {
		switch (side) {
			case UP -> {
				return new int[]{INPUT_SLOT_ID};
			}
			case DOWN -> {
				return OUTPUT_SLOT_IDS;
			}
			default -> {
				return new int[]{INK_PROVIDER_SLOT_ID, EXPERIENCE_STORAGE_ITEM_SLOT_ID};
			}
		}
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
		switch (slot) {
			case INK_PROVIDER_SLOT_ID -> {
				return stack.getItem() instanceof InkStorageItem<?> inkStorageItem && (inkStorageItem.getDrainability().canDrain(false));
			}
			case EXPERIENCE_STORAGE_ITEM_SLOT_ID -> {
				return stack.getItem() instanceof ExperienceStorageItem;
			}
			default -> {
				return true;
			}
		}
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return slot >= FIRST_OUTPUT_SLOT_ID;
	}

	enum CinderHearthStructureType {
		NONE,
		WITH_LAVA,
		WITHOUT_LAVA
	}

	public CinderhearthBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.CINDERHEARTH, pos, state);
		this.inventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
		this.inkStorage = new IndividualCappedInkStorage(INK_STORAGE_SIZE, USED_INK_COLORS);
		
		this.propertyDelegate = new ContainerData() {
			@Override
			public int get(int index) {
				if (index == 0) {
					return CinderhearthBlockEntity.this.craftingTime;
				}
				return CinderhearthBlockEntity.this.craftingTimeTotal;
			}
			
			@Override
			public void set(int index, int value) {
				switch (index) {
					case 0 -> CinderhearthBlockEntity.this.craftingTime = value;
					case 1 -> CinderhearthBlockEntity.this.craftingTimeTotal = value;
				}
			}
			
			@Override
			public int getCount() {
				return 2;
			}
		};
	}
	
	@Override
	public void resetUpgrades() {
		this.upgrades = null;
		this.setChanged();
	}
	
	@Override
	public void calculateUpgrades() {
		this.upgrades = Upgradeable.calculateUpgradeMods2(level, worldPosition, Support.rotationFromDirection(level.getBlockState(worldPosition).getValue(CinderhearthBlock.FACING)), 2, 1, 1, this.ownerUUID);
		this.updateInClientWorld();
		this.setChanged();
	}
	
	public void updateInClientWorld() {
		((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}
	
	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	// Called when the chunk is first loaded to initialize this be
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbtCompound = new CompoundTag();
		this.saveAdditional(nbtCompound);
		return nbtCompound;
	}
	
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public void setOwner(Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		this.setChanged();
	}
	
	@Override
	protected Component getDefaultName() {
		return Component.translatable("block.spectrum.cinderhearth");
	}
	
	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return new CinderhearthScreenHandler(syncId, playerInventory, this.worldPosition, this.propertyDelegate);
	}
	
//	@Override
//	public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) { //todoforge hopefully not an issue
//		buf.writeBlockPos(worldPosition);
//	}



	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		ContainerHelper.loadAllItems(nbt, this.inventory);
		if (nbt.contains("InkStorage", Tag.TAG_COMPOUND)) {
			this.inkStorage = IndividualCappedInkStorage.fromNbt(nbt.getCompound("InkStorage"));
		}
		this.craftingTime = nbt.getShort("CraftingTime");
		this.craftingTimeTotal = nbt.getShort("CraftingTimeTotal");
		this.canTransferInk = nbt.getBoolean("Paused");
		this.inventoryChanged = nbt.getBoolean("InventoryChanged");
		if (nbt.contains("Structure", Tag.TAG_ANY_NUMERIC)) {
			this.structure = CinderHearthStructureType.values()[nbt.getInt("Structure")];
		} else {
			this.structure = CinderHearthStructureType.NONE;
		}
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
		if (nbt.contains("CurrentRecipe")) {
			String recipeString = nbt.getString("CurrentRecipe");
			if (!recipeString.isEmpty() && SpectrumCommon.minecraftServer != null) {
				Optional<? extends Recipe<?>> optionalRecipe = SpectrumCommon.minecraftServer.getRecipeManager().byKey(new ResourceLocation(recipeString));
				this.currentRecipe = optionalRecipe.orElse(null);
			} else {
				this.currentRecipe = null;
			}
		} else {
			this.currentRecipe = null;
		}
		if (nbt.contains("Upgrades", Tag.TAG_LIST)) {
			this.upgrades = UpgradeHolder.fromNbt(nbt.getList("Upgrades", Tag.TAG_COMPOUND));
		} else {
			this.upgrades = new UpgradeHolder();
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		ContainerHelper.saveAllItems(nbt, this.inventory);
		nbt.put("InkStorage", this.inkStorage.toNbt());
		nbt.putShort("CraftingTime", (short) this.craftingTime);
		nbt.putShort("CraftingTimeTotal", (short) this.craftingTimeTotal);
		nbt.putBoolean("Paused", this.canTransferInk);
		nbt.putBoolean("InventoryChanged", this.inventoryChanged);
		nbt.putInt("Structure", this.structure.ordinal());
		if (this.upgrades != null) {
			nbt.put("Upgrades", this.upgrades.toNbt());
		}
		
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
		if (this.currentRecipe != null) {
			nbt.putString("CurrentRecipe", this.currentRecipe.getId().toString());
		}
	}
	
	public static void serverTick(Level world, BlockPos blockPos, BlockState blockState, CinderhearthBlockEntity cinderhearthBlockEntity) {
		if (cinderhearthBlockEntity.upgrades == null) {
			cinderhearthBlockEntity.calculateUpgrades();
		}
		cinderhearthBlockEntity.inkDirty = false;
		
		if (cinderhearthBlockEntity.canTransferInk) {
			boolean didSomething = false;
			ItemStack stack = cinderhearthBlockEntity.getItem(INK_PROVIDER_SLOT_ID);
			if (stack.getItem() instanceof InkStorageItem<?> inkStorageItem) {
				InkStorage itemStorage = inkStorageItem.getEnergyStorage(stack);
				didSomething = InkStorage.transferInk(itemStorage, cinderhearthBlockEntity.inkStorage) != 0;
				if (didSomething) {
					inkStorageItem.setEnergyStorage(stack, itemStorage);
				}
			}
			if (didSomething) {
				cinderhearthBlockEntity.setChanged();
				cinderhearthBlockEntity.setInkDirty();
			} else {
				cinderhearthBlockEntity.canTransferInk = false;
			}
		}
		
		if (cinderhearthBlockEntity.inventoryChanged) {
			calculateRecipe(world, cinderhearthBlockEntity);
			cinderhearthBlockEntity.inventoryChanged = false;
			cinderhearthBlockEntity.updateInClientWorld();
		}
		
		if (cinderhearthBlockEntity.currentRecipe != null) {
			if (!canContinue(world, blockPos, cinderhearthBlockEntity)) {
				cinderhearthBlockEntity.currentRecipe = null;
				cinderhearthBlockEntity.craftingTime = 0;
				cinderhearthBlockEntity.craftingTimeTotal = 0;
				cinderhearthBlockEntity.setChanged();
				return;
			}
			cinderhearthBlockEntity.craftingTime++;


			if (cinderhearthBlockEntity.craftingTime == cinderhearthBlockEntity.craftingTimeTotal) {
				if (cinderhearthBlockEntity.currentRecipe instanceof CinderhearthRecipe cinderhearthRecipe) {
					craftCinderhearthRecipe(world, cinderhearthBlockEntity, cinderhearthRecipe);
				} else if (cinderhearthBlockEntity.currentRecipe instanceof BlastingRecipe blastingRecipe) {
					craftBlastingRecipe(world, cinderhearthBlockEntity, blastingRecipe);
				}
			}

			cinderhearthBlockEntity.setChanged();
		}
	}

	private static boolean canContinue(Level world, BlockPos blockPos, CinderhearthBlockEntity cinderhearthBlockEntity) {
		if (!canAcceptRecipeOutput(world, cinderhearthBlockEntity.currentRecipe, cinderhearthBlockEntity)) {
			return false;
		}

		if (cinderhearthBlockEntity.craftingTime % 20 == 0) {
			if (!checkRecipeRequirements(world, blockPos, cinderhearthBlockEntity)) {
				return false;
			}
			// consume orange ink
			return cinderhearthBlockEntity.drainInkForUpdatesRequired(cinderhearthBlockEntity, UpgradeType.EFFICIENCY, InkColors.ORANGE, true);
		}

		return true;
	}

	protected static boolean canAcceptRecipeOutput(Level world, Recipe<?> recipe, Container inventory) {
		if (recipe != null) {
			ItemStack outputStack = recipe.getResultItem(world.registryAccess());
			if (outputStack.isEmpty()) {
				return true;
			} else {
				int outputSpaceFound = 0;
				for (int slot : OUTPUT_SLOT_IDS) {
					ItemStack slotStack = inventory.getItem(slot);
					if (slotStack.isEmpty()) {
						return true;
					} else if (ItemStack.isSameItemSameTags(slotStack, outputStack)) {
						outputSpaceFound += outputStack.getMaxStackSize() - slotStack.getCount();
						if (outputSpaceFound >= outputStack.getCount()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private static void calculateRecipe(@NotNull Level world, @NotNull CinderhearthBlockEntity cinderhearthBlockEntity) {
		// test the cached recipe => faster
		if (cinderhearthBlockEntity.currentRecipe instanceof CinderhearthRecipe recipe) {
			if (recipe.matches(cinderhearthBlockEntity, world)) {
				float speedModifier = cinderhearthBlockEntity.drainInkForUpgrades(cinderhearthBlockEntity, UpgradeType.SPEED, InkColors.MAGENTA, true);
				cinderhearthBlockEntity.craftingTimeTotal = (int) Math.ceil(recipe.getCraftingTime() / speedModifier);
				return;
			}
		} else if (cinderhearthBlockEntity.currentRecipe instanceof BlastingRecipe recipe) {
			if (recipe.matches(cinderhearthBlockEntity, world)) {
				float speedModifier = cinderhearthBlockEntity.drainInkForUpgrades(cinderhearthBlockEntity, UpgradeType.SPEED, InkColors.MAGENTA, true);
				cinderhearthBlockEntity.craftingTimeTotal = (int) Math.ceil(recipe.getCookingTime() / speedModifier);
				return;
			}
		}
		
		cinderhearthBlockEntity.currentRecipe = null;
		cinderhearthBlockEntity.craftingTime = 0;
		cinderhearthBlockEntity.craftingTimeTotal = 0;
		
		// cached recipe did not match => calculate new
		ItemStack inputStack = cinderhearthBlockEntity.getItem(0);
		if (!inputStack.isEmpty()) {
			CinderhearthRecipe cinderhearthRecipe = world.getRecipeManager().getRecipeFor(SpectrumRecipeTypes.CINDERHEARTH, cinderhearthBlockEntity, world).orElse(null);
			if (cinderhearthRecipe == null) {
				BlastingRecipe blastingRecipe = world.getRecipeManager().getRecipeFor(RecipeType.BLASTING, cinderhearthBlockEntity, world).orElse(null);
				if (blastingRecipe != null) {
					cinderhearthBlockEntity.currentRecipe = blastingRecipe;
					float speedModifier = cinderhearthBlockEntity.drainInkForUpgrades(cinderhearthBlockEntity, UpgradeType.SPEED, InkColors.MAGENTA, true);
					cinderhearthBlockEntity.craftingTimeTotal = (int) Math.ceil(blastingRecipe.getCookingTime() / speedModifier);
				}
			} else {
				cinderhearthBlockEntity.currentRecipe = cinderhearthRecipe;
				float speedModifier = cinderhearthBlockEntity.drainInkForUpgrades(cinderhearthBlockEntity, UpgradeType.SPEED, InkColors.MAGENTA, true);
				cinderhearthBlockEntity.craftingTimeTotal = (int) Math.ceil(cinderhearthRecipe.getCraftingTime() / speedModifier);
			}
		}

	}
	
	private static boolean checkRecipeRequirements(Level world, BlockPos blockPos, @NotNull CinderhearthBlockEntity cinderhearthBlockEntity) {
		Player lastInteractedPlayer = PlayerOwned.getPlayerEntityIfOnline(cinderhearthBlockEntity.ownerUUID);
		if (lastInteractedPlayer == null) {
			return false;
		}
		
		cinderhearthBlockEntity.structure = CinderhearthBlock.verifyStructure(world, blockPos, null);
		if (cinderhearthBlockEntity.structure == CinderHearthStructureType.NONE) {
			world.playSound(null, cinderhearthBlockEntity.getBlockPos(), SpectrumSoundEvents.CRAFTING_ABORTED, SoundSource.BLOCKS, 0.9F + world.random.nextFloat() * 0.2F, 0.9F + world.random.nextFloat() * 0.2F);
			return false;
		}

		if (cinderhearthBlockEntity.currentRecipe instanceof GatedRecipe gatedRecipe) {
			return gatedRecipe.canPlayerCraft(lastInteractedPlayer);
		}
		return true;
	}

	public static void craftBlastingRecipe(Level world, @NotNull CinderhearthBlockEntity cinderhearth, @NotNull BlastingRecipe blastingRecipe) {
		// calculate outputs
		ItemStack inputStack = cinderhearth.getItem(INPUT_SLOT_ID);
		float yieldMod = inputStack.is(SpectrumItemTags.NO_CINDERHEARTH_DOUBLING) ? 1.0F : cinderhearth.drainInkForUpgrades(cinderhearth, UpgradeType.YIELD, InkColors.LIGHT_BLUE, true);
		ItemStack output = blastingRecipe.getResultItem(world.registryAccess()).copy();
		List<ItemStack> outputs = new ArrayList<>();
		if (yieldMod > 1) {
			int outputCount = Support.getIntFromDecimalWithChance(output.getCount() * yieldMod, world.random);
			while (outputCount > 0) { // if the rolled count exceeds the max stack size we need to split them (unstackable items, counts > 64, ...)
				int count = Math.min(outputCount, output.getMaxStackSize());
				ItemStack outputStack = output.copy();
				outputStack.setCount(count);
				outputs.add(outputStack);
				outputCount -= count;
			}
		} else {
			outputs.add(output.copy());
		}

		// craft
		craftRecipe(cinderhearth, inputStack, outputs, blastingRecipe.getExperience());
	}

	public static void craftCinderhearthRecipe(Level world, @NotNull CinderhearthBlockEntity cinderhearth, @NotNull CinderhearthRecipe cinderhearthRecipe) {
		// calculate outputs
		ItemStack inputStack = cinderhearth.getItem(INPUT_SLOT_ID);
		float yieldMod = inputStack.is(SpectrumItemTags.NO_CINDERHEARTH_DOUBLING) ? 1.0F : cinderhearth.drainInkForUpgrades(cinderhearth, UpgradeType.YIELD, InkColors.LIGHT_BLUE, true);
		List<ItemStack> outputs = cinderhearthRecipe.getRolledOutputs(world.random, yieldMod);

		// craft
		craftRecipe(cinderhearth, inputStack, outputs, cinderhearthRecipe.getExperience());
	}
	
	private static void craftRecipe(@NotNull CinderhearthBlockEntity cinderhearth, ItemStack inputStack, List<ItemStack> outputs, float experience) {
		NonNullList<ItemStack> backupInventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
		for (int i = 0; i < cinderhearth.inventory.size(); i++) {
			backupInventory.set(i, cinderhearth.inventory.get(i));
		}
		
		boolean couldAdd = InventoryHelper.addToInventory(cinderhearth, outputs, FIRST_OUTPUT_SLOT_ID, LAST_OUTPUT_SLOT_ID + 1);
		if (couldAdd) {
			ItemStack remainder = inputStack.getCraftingRemainingItem();
			
			// use up input ingredient
			ItemStack inputStackCopy = inputStack.copy();
			inputStack.shrink(1);
			
			if (remainder.isEmpty()) {
				boolean remainderAdded = InventoryHelper.addToInventory(cinderhearth, remainder, FIRST_OUTPUT_SLOT_ID, LAST_OUTPUT_SLOT_ID + 1);
				if (!remainderAdded) {
					cinderhearth.setItem(CinderhearthBlockEntity.INPUT_SLOT_ID, remainder);
				}
			}
			
			// effects
			playCraftingFinishedEffects(cinderhearth);
			
			// reset
			cinderhearth.craftingTime = 0;
			cinderhearth.inventoryChanged();

			// grant experience & advancements
			float experienceMod = cinderhearth.drainInkForUpgrades(cinderhearth, UpgradeType.EXPERIENCE, InkColors.PURPLE, true);
			int finalExperience = Support.getIntFromDecimalWithChance(experience * experienceMod, cinderhearth.level.random);
			ExperienceStorageItem.addStoredExperience(cinderhearth.getItem(EXPERIENCE_STORAGE_ITEM_SLOT_ID), finalExperience);
			cinderhearth.grantPlayerCinderhearthSmeltingAdvancement(inputStackCopy, outputs, finalExperience);
		} else {
			cinderhearth.inventory = backupInventory;

			// prevents trying to craft more until the inventory is freed up
			cinderhearth.craftingTimeTotal = 0;
			cinderhearth.currentRecipe = null;
			cinderhearth.inventoryChanged = false;
		}
	}

	public void grantPlayerCinderhearthSmeltingAdvancement(ItemStack input, List<ItemStack> outputs, int experience) {
		ServerPlayer serverPlayerEntity = (ServerPlayer) getOwnerIfOnline();
		if (serverPlayerEntity != null) {
			SpectrumAdvancementCriteria.CINDERHEARTH_SMELTING.trigger(serverPlayerEntity, input, outputs, experience, this.upgrades);
		}
	}

	public static void playCraftingFinishedEffects(@NotNull CinderhearthBlockEntity cinderhearthBlockEntity) {
		Direction.Axis axis = null;
		Direction direction = Direction.UP; // Give it a default so vscode will stop complaining

		for (Map.Entry<UpgradeType, Integer> entry : cinderhearthBlockEntity.upgrades.entrySet()) {
			if (entry.getValue() > 1) {
				if (axis == null) {
					BlockState state = cinderhearthBlockEntity.getLevel().getBlockState(cinderhearthBlockEntity.worldPosition);
					direction = state.getValue(CinderhearthBlock.FACING);
					axis = direction.getAxis();
				}

				double d = (double) cinderhearthBlockEntity.worldPosition.getX() + 0.5D;
				double f = (double) cinderhearthBlockEntity.worldPosition.getZ() + 0.5D;
				double g2 = -3D / 16D;
				double h2 = 4D / 16D;
				double i2 = axis == Direction.Axis.X ? (double) direction.getStepX() * g2 : h2;
				double k2 = axis == Direction.Axis.Z ? (double) direction.getStepZ() * g2 : h2;
				SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) cinderhearthBlockEntity.getLevel(),
						new Vec3(d + i2, cinderhearthBlockEntity.worldPosition.getY() + 1.1, f + k2),
						ParticleTypes.CAMPFIRE_COSY_SMOKE,
						3,
						new Vec3(0.05D, 0.00D, 0.05D),
						new Vec3(0.0D, 0.3D, 0.0D));
			}
		}
	}
	
	@Override
	public int getContainerSize() {
		return INVENTORY_SIZE;
	}
	
	@Override
	public boolean isEmpty() {
		return this.inventory.isEmpty();
	}
	
	@Override
	public ItemStack getItem(int slot) {
		return inventory.get(slot);
	}
	
	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack removedStack = ContainerHelper.removeItem(this.inventory, slot, amount);
		this.inventoryChanged();
		return removedStack;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack removedStack = ContainerHelper.takeItem(this.inventory, slot);
		this.inventoryChanged();
		return removedStack;
	}
	
	@Override
	public void setItem(int slot, @NotNull ItemStack stack) {
		this.inventory.set(slot, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		this.inventoryChanged();
	}
	
	@Override
	public boolean stillValid(Player player) {
		if (this.getLevel().getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
		}
	}
	
	public void inventoryChanged() {
		this.inventoryChanged = true;
		this.canTransferInk = true;
		this.setChanged();
	}

	@Override
	public void clearContent() {
		this.inventory.clear();
		this.inventoryChanged();
	}

	@Override
	public UpgradeHolder getUpgradeHolder() {
		return this.upgrades;
	}

	@Override
	public IndividualCappedInkStorage getEnergyStorage() {
		return this.inkStorage;
	}

	@Override
	public void setInkDirty() {
		this.inkDirty = true;
	}
	
	@Override
	public boolean getInkDirty() {
		return this.inkDirty;
	}
	
	public Recipe<?> getCurrentRecipe() {
		return currentRecipe;
	}
	
}
