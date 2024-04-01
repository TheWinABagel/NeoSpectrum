package de.dafuqs.spectrum.blocks.pedestal;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.MultiblockCrafter;
import de.dafuqs.spectrum.api.block.PedestalVariant;
import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.api.item.GemstoneColor;
import de.dafuqs.spectrum.blocks.upgrade.Upgradeable;
import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.inventories.AutoCraftingInventory;
import de.dafuqs.spectrum.inventories.PedestalScreenHandler;
import de.dafuqs.spectrum.items.CraftingTabletItem;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipe;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipeTier;
import de.dafuqs.spectrum.recipe.pedestal.ShapedPedestalRecipe;
import de.dafuqs.spectrum.recipe.pedestal.ShapelessPedestalRecipe;
import de.dafuqs.spectrum.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IMultiblock;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PedestalBlockEntity extends BaseContainerBlockEntity implements MultiblockCrafter, StackedContentsCompatible, WorldlyContainer {

	public static final int INVENTORY_SIZE = 16; // 9 crafting, 5 gems, 1 craftingTablet, 1 output
	public static final int FIRST_POWDER_SLOT_ID = 9;
	public static final int CRAFTING_TABLET_SLOT_ID = 14;
	public static final int OUTPUT_SLOT_ID = 15;
	
	protected final AutoCraftingInventory autoCraftingInventory;
	protected final ContainerData propertyDelegate;
	protected UUID ownerUUID;
	protected PedestalVariant pedestalVariant;
	protected NonNullList<ItemStack> inventory;
	protected boolean shouldCraft;
	protected float storedXP;
	protected int craftingTime;
	protected int craftingTimeTotal;
	public @Nullable Recipe<?> currentRecipe;
	protected PedestalRecipeTier cachedMaxPedestalTier;
	protected long cachedMaxPedestalTierTick;
	protected UpgradeHolder upgrades;
	protected boolean inventoryChanged;
	
	public PedestalBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(SpectrumBlockEntities.PEDESTAL, blockPos, blockState);
		
		if (blockState.getBlock() instanceof PedestalBlock) {
			this.pedestalVariant = ((PedestalBlock) (blockState.getBlock())).getVariant();
		} else {
			this.pedestalVariant = BuiltinPedestalVariant.BASIC_AMETHYST;
		}
		autoCraftingInventory = new AutoCraftingInventory(3, 3);
		
		this.inventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
		this.propertyDelegate = new ContainerData() {
			@Override
			public int get(int index) {
				return switch (index) {
					case 0 -> PedestalBlockEntity.this.craftingTime;
					default -> PedestalBlockEntity.this.craftingTimeTotal;
				};
			}
			
			@Override
			public void set(int index, int value) {
				switch (index) {
					case 0 -> PedestalBlockEntity.this.craftingTime = value;
					case 1 -> PedestalBlockEntity.this.craftingTimeTotal = value;
				}
			}
			
			@Override
			public int getCount() {
				return 2;
			}
		};
	}
	
	public static void updateInClientWorld(PedestalBlockEntity pedestalBlockEntity) {
		((ServerLevel) pedestalBlockEntity.getLevel()).getChunkSource().blockChanged(pedestalBlockEntity.worldPosition);
	}
	
	public static void clientTick(@NotNull Level world, BlockPos blockPos, BlockState blockState, PedestalBlockEntity pedestalBlockEntity) {
		Recipe<?> currentRecipe = pedestalBlockEntity.getCurrentRecipe();
		if (currentRecipe instanceof PedestalRecipe pedestalRecipe) {
			Map<GemstoneColor, Integer> gemstonePowderInputs = pedestalRecipe.getPowderInputs();
			
			for (Map.Entry<GemstoneColor, Integer> entry : gemstonePowderInputs.entrySet()) {
				int amount = entry.getValue();
				if (amount > 0) {
					ParticleOptions particleEffect = SpectrumParticleTypes.getCraftingParticle(entry.getKey().getDyeColor());
					
					float particleAmount = Support.getIntFromDecimalWithChance(amount * 0.125, world.random);
					for (int i = 0; i < particleAmount; i++) {
						float randomX = 2.0F - world.getRandom().nextFloat() * 5;
						float randomZ = 2.0F - world.getRandom().nextFloat() * 5;
						world.addParticle(particleEffect, blockPos.getX() + randomX, blockPos.getY(), blockPos.getZ() + randomZ, 0.0D, 0.03D, 0.0D);
					}
				}
			}
		}
	}
	
	public static void spawnCraftingStartParticles(@NotNull Level world, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof PedestalBlockEntity pedestalBlockEntity) {
			Recipe<?> currentRecipe = pedestalBlockEntity.getCurrentRecipe();
			if (currentRecipe instanceof PedestalRecipe pedestalRecipe) {
				Map<GemstoneColor, Integer> gemstonePowderInputs = pedestalRecipe.getPowderInputs();
				
				for (Map.Entry<GemstoneColor, Integer> entry : gemstonePowderInputs.entrySet()) {
					int amount = entry.getValue();
					if (amount > 0) {
						ParticleOptions particleEffect = SpectrumParticleTypes.getCraftingParticle(entry.getKey().getDyeColor());
						
						amount = amount * 4;
						RandomSource random = world.random;
						for (int i = 0; i < amount; i++) {
							Direction direction = Direction.getRandom(random);
							if (direction != Direction.DOWN) {
								BlockPos offsetPos = blockPos.relative(direction);
								BlockState offsetState = world.getBlockState(offsetPos);
								if (!offsetState.isFaceSturdy(world, offsetPos, direction.getOpposite())) {
									double d = direction.getStepX() == 0 ? random.nextDouble() : 0.5D + (double) direction.getStepX() * 0.6D;
									double e = direction.getStepY() == 0 ? random.nextDouble() : 0.5D + (double) direction.getStepY() * 0.6D;
									double f = direction.getStepZ() == 0 ? random.nextDouble() : 0.5D + (double) direction.getStepZ() * 0.6D;
									world.addParticle(particleEffect, (double) blockPos.getX() + d, (double) blockPos.getY() + e, (double) blockPos.getZ() + f, 0.0D, 0.03D, 0.0D);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void serverTick(@NotNull Level world, BlockPos blockPos, BlockState blockState, PedestalBlockEntity pedestalBlockEntity) {
		if (pedestalBlockEntity.upgrades == null) {
			pedestalBlockEntity.calculateUpgrades();
		}
		
		// check recipe crafted last tick => performance
		boolean shouldMarkDirty = false;

		Recipe<?> calculatedRecipe = calculateRecipe(world, pedestalBlockEntity);
		pedestalBlockEntity.inventoryChanged = false;
		if (pedestalBlockEntity.currentRecipe != calculatedRecipe) {
			pedestalBlockEntity.shouldCraft = false;
			pedestalBlockEntity.currentRecipe = calculatedRecipe;
			pedestalBlockEntity.craftingTime = 0;
			if (calculatedRecipe instanceof PedestalRecipe calculatedPedestalRecipe) {
				pedestalBlockEntity.craftingTimeTotal = (int) Math.ceil(calculatedPedestalRecipe.getCraftingTime() / pedestalBlockEntity.upgrades.getEffectiveValue(UpgradeType.SPEED));
				
				Player player = pedestalBlockEntity.getOwnerIfOnline();
				if (player instanceof ServerPlayer serverPlayerEntity) {
					SpectrumAdvancementCriteria.PEDESTAL_RECIPE_CALCULATED.trigger(serverPlayerEntity, calculatedPedestalRecipe.assemble(pedestalBlockEntity, world.registryAccess()), (int) calculatedPedestalRecipe.getExperience(), pedestalBlockEntity.craftingTimeTotal);
				}
			} else {
				pedestalBlockEntity.craftingTimeTotal = (int) Math.ceil(SpectrumCommon.CONFIG.VanillaRecipeCraftingTimeTicks / pedestalBlockEntity.upgrades.getEffectiveValue(UpgradeType.SPEED));
			}
			pedestalBlockEntity.setChanged();
			SpectrumS2CPacketSender.sendCancelBlockBoundSoundInstance((ServerLevel) pedestalBlockEntity.getLevel(), pedestalBlockEntity.getBlockPos());
			updateInClientWorld(pedestalBlockEntity);
		}
		
		// only craft when there is redstone power
		if (pedestalBlockEntity.craftingTime == 0 && !pedestalBlockEntity.shouldCraft && !(blockState.getBlock() instanceof PedestalBlock && blockState.getValue(PedestalBlock.POWERED))) {
			return;
		}
		
		int maxCountPerStack = pedestalBlockEntity.getMaxStackSize();
		// Pedestal crafting
		boolean craftingFinished = false;
		if (calculatedRecipe instanceof PedestalRecipe pedestalRecipe && pedestalBlockEntity.canAcceptRecipeOutput(calculatedRecipe, pedestalBlockEntity, maxCountPerStack)) {
			pedestalBlockEntity.craftingTime++;
			if (pedestalBlockEntity.craftingTime == pedestalBlockEntity.craftingTimeTotal) {
				pedestalBlockEntity.craftingTime = 0;
				craftingFinished = craftPedestalRecipe(pedestalBlockEntity, pedestalRecipe, pedestalBlockEntity, maxCountPerStack);
				if (craftingFinished) {
					pedestalBlockEntity.inventoryChanged = true;
				}
				shouldMarkDirty = true;
			}
			// Vanilla crafting
		} else if (calculatedRecipe instanceof CraftingRecipe vanillaCraftingRecipe && pedestalBlockEntity.canAcceptRecipeOutput(calculatedRecipe, pedestalBlockEntity, maxCountPerStack)) {
			pedestalBlockEntity.craftingTime++;
			if (pedestalBlockEntity.craftingTime == pedestalBlockEntity.craftingTimeTotal) {
				pedestalBlockEntity.craftingTime = 0;
				craftingFinished = pedestalBlockEntity.craftVanillaRecipe(vanillaCraftingRecipe, pedestalBlockEntity, maxCountPerStack);
				if (craftingFinished) {
					pedestalBlockEntity.inventoryChanged = true;
				}
				shouldMarkDirty = true;
			}
		}
		
		if (pedestalBlockEntity.craftingTime == 1 && pedestalBlockEntity.craftingTimeTotal > 1) {
			SpectrumS2CPacketSender.sendPlayBlockBoundSoundInstance(SpectrumSoundEvents.PEDESTAL_CRAFTING, (ServerLevel) pedestalBlockEntity.getLevel(), pedestalBlockEntity.getBlockPos(), pedestalBlockEntity.craftingTimeTotal - pedestalBlockEntity.craftingTime);
		}
		
		// try to output the currently stored output stack
		ItemStack outputItemStack = pedestalBlockEntity.inventory.get(OUTPUT_SLOT_ID);
		if (outputItemStack != ItemStack.EMPTY) {
			if (world.getBlockState(blockPos.above()).getCollisionShape(world, blockPos.above()).isEmpty()) {
				spawnOutputAsItemEntity(world, blockPos, pedestalBlockEntity, outputItemStack);
				playCraftingFinishedSoundEvent(pedestalBlockEntity, calculatedRecipe);
			} else {
				BlockEntity aboveBlockEntity = world.getBlockEntity(blockPos.above());
				if (aboveBlockEntity instanceof Container aboveInventory) {
					boolean putIntoAboveInventorySuccess = tryPutOutputIntoAboveInventory(pedestalBlockEntity, aboveInventory, outputItemStack);
					if (putIntoAboveInventorySuccess) {
						playCraftingFinishedSoundEvent(pedestalBlockEntity, calculatedRecipe);
					} else {
						// play sound when the entity can not put its output anywhere
						if (craftingFinished) {
							pedestalBlockEntity.playSound(SoundEvents.LAVA_EXTINGUISH);
						}
					}
				} else {
					// play sound when the entity can not put its output anywhere
					if (craftingFinished) {
						pedestalBlockEntity.playSound(SoundEvents.LAVA_EXTINGUISH);
					}
				}
			}
		}
		
		if (shouldMarkDirty) {
			setChanged(world, blockPos, blockState);
		}
	}
	
	@Contract(pure = true)
	public static PedestalVariant getVariant(@NotNull PedestalBlockEntity pedestalBlockEntity) {
		return pedestalBlockEntity.pedestalVariant;
	}
	
	public static void spawnOutputAsItemEntity(Level world, BlockPos blockPos, @NotNull PedestalBlockEntity pedestalBlockEntity, ItemStack outputItemStack) {
		// spawn crafting output
		MultiblockCrafter.spawnItemStackAsEntitySplitViaMaxCount(world, pedestalBlockEntity.worldPosition, outputItemStack, outputItemStack.getCount(), new Vec3(0, 0.1, 0));
		pedestalBlockEntity.inventory.set(OUTPUT_SLOT_ID, ItemStack.EMPTY);
		
		// spawn XP
		MultiblockCrafter.spawnExperience(world, pedestalBlockEntity.worldPosition, pedestalBlockEntity.storedXP, world.random);
		pedestalBlockEntity.storedXP = 0;
		
		// only triggered on server side. Therefore, has to be sent to client via S2C packet
		SpectrumS2CPacketSender.sendPlayPedestalCraftingFinishedParticle(world, blockPos, outputItemStack);
	}
	
	public static boolean tryPutOutputIntoAboveInventory(PedestalBlockEntity pedestalBlockEntity, Container targetInventory, ItemStack outputItemStack) {
		if (targetInventory instanceof HopperBlockEntity) {
			return false;
		}
		
		ItemStack remainingStack = InventoryHelper.smartAddToInventory(outputItemStack, targetInventory, Direction.DOWN);
		if (remainingStack.isEmpty()) {
			pedestalBlockEntity.inventory.set(OUTPUT_SLOT_ID, ItemStack.EMPTY);
			return true;
		} else {
			pedestalBlockEntity.inventory.set(OUTPUT_SLOT_ID, remainingStack);
			return false;
		}
	}
	
	public static void playCraftingFinishedSoundEvent(PedestalBlockEntity pedestalBlockEntity, Recipe<?> craftingRecipe) {
		Level world = pedestalBlockEntity.getLevel();
		if (craftingRecipe instanceof PedestalRecipe pedestalRecipe) {
			pedestalBlockEntity.playSound(pedestalRecipe.getSoundEvent(world.random));
		} else {
			pedestalBlockEntity.playSound(SpectrumSoundEvents.PEDESTAL_CRAFTING_FINISHED_GENERIC);
		}
	}
	
	public static @Nullable Recipe<?> calculateRecipe(Level world, @NotNull PedestalBlockEntity pedestalBlockEntity) {
		if (!pedestalBlockEntity.inventoryChanged) {
			return pedestalBlockEntity.currentRecipe;
		}
		
		// unchanged pedestal recipe?
		if (pedestalBlockEntity.currentRecipe instanceof PedestalRecipe pedestalRecipe && pedestalRecipe.matches(pedestalBlockEntity, world)) {
			return pedestalBlockEntity.currentRecipe;
		}
		
		// unchanged vanilla recipe?
		if (SpectrumCommon.CONFIG.canPedestalCraftVanillaRecipes()) {
			pedestalBlockEntity.autoCraftingInventory.setInputInventory(pedestalBlockEntity.inventory.subList(0, 9));
			if (pedestalBlockEntity.currentRecipe instanceof CraftingRecipe craftingRecipe && craftingRecipe.matches(pedestalBlockEntity.autoCraftingInventory, world)) {
				return pedestalBlockEntity.currentRecipe;
			}
		}
		
		// current recipe does not match last recipe
		// => search valid recipe
		PedestalRecipe pedestalRecipe = world.getRecipeManager().getRecipeFor(SpectrumRecipeTypes.PEDESTAL, pedestalBlockEntity, world).orElse(null);
		if (pedestalRecipe == null) {
			if (SpectrumCommon.CONFIG.canPedestalCraftVanillaRecipes()) {
				return world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, pedestalBlockEntity.autoCraftingInventory, world).orElse(null);
			}
			return null;
		}
		
		if (!pedestalRecipe.canCraft(pedestalBlockEntity)) {
			return null;
		}
		return pedestalRecipe;
	}
	
	private boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe, Container inventory, int maxCountPerStack) {
		if (recipe != null) {
			ItemStack output;
			if (recipe instanceof PedestalRecipe pedestalRecipe) {
				output = pedestalRecipe.assemble(inventory, null);
			} else if (recipe instanceof CraftingRecipe craftingRecipe) {
				autoCraftingInventory.setInputInventory(inventory, 0, 9);
				output = craftingRecipe.assemble(autoCraftingInventory, null);
			} else {
				output = ItemStack.EMPTY;
			}
			
			if (output.isEmpty()) {
				return false;
			}
			
			ItemStack existingOutput = this.getItem(OUTPUT_SLOT_ID);
			if (existingOutput.isEmpty()) {
				return true;
			}
			
			if (!ItemStack.isSameItemSameTags(existingOutput, output)) {
				return false;
			}
			
			if (existingOutput.getCount() < maxCountPerStack && existingOutput.getCount() < existingOutput.getMaxStackSize()) {
				return true;
			}
			
			return existingOutput.getCount() < output.getMaxStackSize();
		}
		
		return false;
	}
	
	private static boolean craftPedestalRecipe(PedestalBlockEntity pedestalBlockEntity, @Nullable PedestalRecipe recipe, Container inventory, int maxCountPerStack) {
		if (!pedestalBlockEntity.canAcceptRecipeOutput(recipe, inventory, maxCountPerStack)) {
			return false;
		}
		ItemStack outputStack = recipe.assemble(pedestalBlockEntity, pedestalBlockEntity.getLevel().registryAccess());
		recipe.consumeIngredients(pedestalBlockEntity);

		if (!recipe.areYieldUpgradesDisabled()) {
			double yieldModifier = pedestalBlockEntity.upgrades.getEffectiveValue(UpgradeType.YIELD);
			if (yieldModifier != 1.0) {
				int modifiedCount = Support.getIntFromDecimalWithChance(outputStack.getCount() * yieldModifier, pedestalBlockEntity.level.random);
				outputStack.setCount(Math.min(outputStack.getMaxStackSize(), modifiedCount));
			}
		}

		// Add the XP for this recipe to the pedestal
		float experience = recipe.getExperience() * pedestalBlockEntity.upgrades.getEffectiveValue(UpgradeType.EXPERIENCE);
		pedestalBlockEntity.storedXP += experience;

		Player player = pedestalBlockEntity.getOwnerIfOnline();
		if (player != null) {
			outputStack.onCraftedBy(pedestalBlockEntity.level, player, outputStack.getCount());
		}

		// trigger advancements
		pedestalBlockEntity.grantPlayerPedestalCraftingAdvancement(outputStack, (int) experience, pedestalBlockEntity.craftingTimeTotal);

		// if it was a recipe to upgrade the pedestal itself
		// => upgrade
		PedestalVariant newPedestalVariant = PedestalRecipe.getUpgradedPedestalVariantForOutput(outputStack);
		if (newPedestalVariant != null && newPedestalVariant.isBetterThan(getVariant(pedestalBlockEntity))) {
			// It is an upgrade recipe (output is a pedestal block item)
			// => Upgrade
			pedestalBlockEntity.playSound(SpectrumSoundEvents.PEDESTAL_UPGRADE);
			PedestalBlock.upgradeToVariant(pedestalBlockEntity.level, pedestalBlockEntity.getBlockPos(), newPedestalVariant);
			SpectrumS2CPacketSender.spawnPedestalUpgradeParticles(pedestalBlockEntity.level, pedestalBlockEntity.worldPosition, newPedestalVariant);

			pedestalBlockEntity.pedestalVariant = newPedestalVariant;
			pedestalBlockEntity.currentRecipe = null; // reset the recipe, otherwise pedestal would remember crafting the update
		} else {
			// Not an upgrade recipe => Add output to output slot
			ItemStack existingOutput = inventory.getItem(OUTPUT_SLOT_ID);
			if (!existingOutput.isEmpty()) {
				// merge existing & newly crafted stacks
				// protection against stacks > max stack size
				outputStack.setCount(Math.min(existingOutput.getMaxStackSize(), existingOutput.getCount() + outputStack.getCount()));
			}
			inventory.setItem(OUTPUT_SLOT_ID, outputStack);
		}

		pedestalBlockEntity.setChanged();
		pedestalBlockEntity.inventoryChanged = true;
		updateInClientWorld(pedestalBlockEntity);

		return true;
	}
	
	public static Item getGemstonePowderItemForSlot(int slot) {
		return switch (slot) {
			case 9 -> SpectrumItems.TOPAZ_POWDER;
			case 10 -> SpectrumItems.AMETHYST_POWDER;
			case 11 -> SpectrumItems.CITRINE_POWDER;
			case 12 -> SpectrumItems.ONYX_POWDER;
			case 13 -> SpectrumItems.MOONSTONE_POWDER;
			default -> Items.AIR;
		};
	}
	
	public static int getSlotForGemstonePowder(GemstoneColor gemstoneColor) {
		return switch (gemstoneColor.getDyeColor()) {
			case CYAN -> 9;
			case MAGENTA -> 10;
			case YELLOW -> 11;
			case BLACK -> 12;
			case WHITE -> 13;
			default -> -1;
		};
	}
	
	public void setVariant(PedestalVariant pedestalVariant) {
		this.pedestalVariant = pedestalVariant;
		this.propertyDelegate.set(2, pedestalVariant.getRecipeTier().ordinal());
	}
	
	@Override
	public Component getDefaultName() {
		return Component.translatable("block.spectrum.pedestal");
	}
	
	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return new PedestalScreenHandler(syncId, playerInventory, this, this.propertyDelegate, this.pedestalVariant.getRecipeTier().ordinal(), this.getHighestAvailableRecipeTier().ordinal(), this.worldPosition);
	}
	
	@Override
	public int getContainerSize() {
		return this.inventory.size();
	}
	
	@Override
	public boolean isEmpty() {
		Iterator<ItemStack> var1 = this.inventory.iterator();
		
		ItemStack itemStack;
		do {
			if (!var1.hasNext()) {
				return true;
			}
			
			itemStack = var1.next();
		} while (itemStack.isEmpty());
		
		return false;
	}
	
	@Override
	public ItemStack getItem(int slot) {
		return this.inventory.get(slot);
	}
	
	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack removedStack = ContainerHelper.removeItem(this.inventory, slot, amount);
		this.inventoryChanged = true;
		this.setChanged();
		return removedStack;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack removedStack = ContainerHelper.takeItem(this.inventory, slot);
		this.inventoryChanged = true;
		this.setChanged();
		return removedStack;
	}
	
	@Override
	public void setItem(int slot, @NotNull ItemStack stack) {
		this.inventory.set(slot, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		
		this.inventoryChanged = true;
		this.setChanged();
	}
	
	@Override
	public boolean stillValid(Player player) {
		if (this.getLevel().getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
		}
	}
	
	@Override
	public void fillStackedContents(StackedContents recipeMatcher) {
		for (ItemStack itemStack : this.inventory) {
			recipeMatcher.accountStack(itemStack);
		}
	}
	
	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	// Called when the chunk is first loaded to initialize this be or manually synced via updateInClientWorld()
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbtCompound = new CompoundTag();
		this.saveAdditional(nbtCompound);
		return nbtCompound;
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		this.inventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.inventory);
		
		if (nbt.contains("StoredXP")) {
			this.storedXP = nbt.getFloat("StoredXP");
		}
		if (nbt.contains("CraftingTime")) {
			this.craftingTime = nbt.getShort("CraftingTime");
		}
		if (nbt.contains("CraftingTimeTotal")) {
			this.craftingTimeTotal = nbt.getShort("CraftingTimeTotal");
		}
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
		if (nbt.contains("Upgrades", Tag.TAG_LIST)) {
			this.upgrades = UpgradeHolder.fromNbt(nbt.getList("Upgrades", Tag.TAG_COMPOUND));
		} else {
			this.upgrades = new UpgradeHolder();
		}
		if (nbt.contains("inventory_changed")) {
			this.inventoryChanged = nbt.getBoolean("inventory_changed");
		}
		
		this.currentRecipe = null;
		if (nbt.contains("CurrentRecipe")) {
			ResourceLocation recipeIdentifier = new ResourceLocation(nbt.getString("CurrentRecipe"));
			this.currentRecipe = MultiblockCrafter.getRecipeFromId(this.getLevel(), recipeIdentifier);
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putFloat("StoredXP", this.storedXP);
		nbt.putShort("CraftingTime", (short) this.craftingTime);
		nbt.putShort("CraftingTimeTotal", (short) this.craftingTimeTotal);
		nbt.putBoolean("inventory_changed", this.inventoryChanged);
		
		if (this.upgrades != null) {
			nbt.put("Upgrades", this.upgrades.toNbt());
		}
		if (this.currentRecipe != null) {
			nbt.putString("CurrentRecipe", this.currentRecipe.getId().toString());
		}
		
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
		ContainerHelper.saveAllItems(nbt, this.inventory);
	}
	
	@Override
	public void clearContent() {
		this.inventory.clear();
	}
	
	public boolean isCrafting() {
		return this.craftingTime > 0;
	}
	
	private void playSound(SoundEvent soundEvent) {
		RandomSource random = level.random;
		level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), soundEvent, SoundSource.BLOCKS, 0.9F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F);
	}
	
	private boolean craftVanillaRecipe(@Nullable CraftingRecipe recipe, Container inventory, int maxCountPerStack) {
		if (canAcceptRecipeOutput(recipe, inventory, maxCountPerStack)) {
			autoCraftingInventory.setInputInventory(inventory, 0, 9);
			
			ItemStack recipeOutput = recipe.assemble(autoCraftingInventory, null);
			Player player = getOwnerIfOnline();
			if (player != null) { // some recipes may assume the player is never null (since this is the case in vanilla)
				recipeOutput.onCraftedBy(this.getLevel(), player, recipeOutput.getCount());
			}
			
			// -1 for all crafting inputs
			decrementInputStacks(inventory);
			
			ItemStack existingOutput = inventory.getItem(OUTPUT_SLOT_ID);
			if (existingOutput.isEmpty()) {
				inventory.setItem(OUTPUT_SLOT_ID, recipeOutput.copy());
			} else {
				existingOutput.grow(recipeOutput.getCount());
				inventory.setItem(OUTPUT_SLOT_ID, existingOutput);
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	private void decrementInputStacks(Container inventory) {
		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = inventory.getItem(i);
			if (!itemStack.isEmpty()) {
				ItemStack remainder = itemStack.getCraftingRemainingItem();
				if (remainder.isEmpty()) {
					itemStack.shrink(1);
				} else {
					if (this.inventory.get(i).getCount() == 1) {
						this.inventory.set(i, remainder);
					} else {
						this.inventory.get(i).shrink(1);
						ItemEntity itemEntity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, remainder);
						itemEntity.push(0, 0.05, 0);
						level.addFreshEntity(itemEntity);
					}
				}
			}
		}
	}
	
	private void grantPlayerPedestalCraftingAdvancement(ItemStack output, int experience, int duration) {
		ServerPlayer serverPlayerEntity = (ServerPlayer) getOwnerIfOnline();
		if (serverPlayerEntity != null) {
			SpectrumAdvancementCriteria.PEDESTAL_CRAFTING.trigger(serverPlayerEntity, output, experience, duration);
		}
	}
	
	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if (slot < 9) {
			return true;
		} else if (slot == CRAFTING_TABLET_SLOT_ID && stack.is(SpectrumItems.CRAFTING_TABLET)) {
			return true;
		} else {
			return stack.getItem().equals(getGemstonePowderItemForSlot(slot));
		}
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.DOWN) {
			return new int[]{OUTPUT_SLOT_ID};
		} else if (side == Direction.UP) {
			return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
		} else {
			switch (this.pedestalVariant.getRecipeTier()) {
				case COMPLEX -> {
					return new int[]{9, 10, 11, 12, 13};
				}
				case ADVANCED -> {
					return new int[]{9, 10, 11, 12};
				}
				default -> {
					return new int[]{9, 10, 11};
				}
			}
		}
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction dir) {
		if (stack.is(getGemstonePowderItemForSlot(slot))) {
			return true;
		}
		
		if (slot < 9 && inventory.get(CRAFTING_TABLET_SLOT_ID).is(SpectrumItems.CRAFTING_TABLET)) {
			ItemStack craftingTabletItem = inventory.get(CRAFTING_TABLET_SLOT_ID);
			
			if (inventory.get(slot).getCount() > 0) {
				return false;
			}

			Recipe<?> storedRecipe = CraftingTabletItem.getStoredRecipe(this.getLevel(), craftingTabletItem);
			
			int width = 3;
			if (storedRecipe instanceof ShapedRecipe shapedRecipe) {
				width = shapedRecipe.getWidth();
				if (slot % 3 >= width) {
					return false;
				}
			} else if (storedRecipe instanceof ShapedPedestalRecipe pedestalRecipe) {
				width = pedestalRecipe.getWidth();
				if (slot % 3 >= width) {
					return false;
				}
			} else if (!(storedRecipe instanceof ShapelessRecipe) && !(storedRecipe instanceof ShapelessPedestalRecipe)) {
				return false;
			}
			
			int resultRecipeSlot = getCraftingRecipeSlotDependingOnWidth(slot, width);
			if (resultRecipeSlot < storedRecipe.getIngredients().size()) {
				Ingredient ingredient = storedRecipe.getIngredients().get(resultRecipeSlot);
				return ingredient.test(stack);
			} else {
				return false;
			}
		} else {
			return slot < CRAFTING_TABLET_SLOT_ID;
		}
	}
	
	private int getCraftingRecipeSlotDependingOnWidth(int slot, int recipeWidth) {
		int line = slot / 3;
		int posInLine = slot % 3;
		return line * recipeWidth + posInLine;
	}
	
	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return slot == OUTPUT_SLOT_ID;
	}
	
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	public @Nullable Recipe<?> getCurrentRecipe() {
		return this.currentRecipe;
	}
	
	public ItemStack getCurrentCraftingRecipeOutput() {
		if (this.currentRecipe == null) {
			return ItemStack.EMPTY;
		}
		
		if (this.currentRecipe instanceof PedestalRecipe pedestalRecipe) {
			return pedestalRecipe.assemble(this, this.level.registryAccess());
		}
		
		if (this.currentRecipe instanceof CraftingRecipe craftingRecipe) {
			autoCraftingInventory.setInputInventory(this, 0, 9);
			return craftingRecipe.assemble(autoCraftingInventory, null);
		}
		
		return ItemStack.EMPTY;
	}

//	todoforge extended screen handler stuff
//	@Override
	public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
		buf.writeInt(this.pedestalVariant.getRecipeTier().ordinal());
		buf.writeInt(this.getHighestAvailableRecipeTier().ordinal());
		buf.writeBlockPos(this.worldPosition);
	}
	
	public PedestalRecipeTier getHighestAvailableRecipeTier() {
		if (this.getLevel().getGameTime() <= this.cachedMaxPedestalTierTick + 20) {
			return cachedMaxPedestalTier;
		} else {
			PedestalRecipeTier pedestalTier = getPedestalTier();
			PedestalRecipeTier structureTier = getStructureTier();
			
			PedestalRecipeTier denominator = PedestalRecipeTier.values()[Math.min(pedestalTier.ordinal(), structureTier.ordinal())];
			this.cachedMaxPedestalTier = denominator;
			this.cachedMaxPedestalTierTick = level.getGameTime();

			return denominator;
		}
	}

	private PedestalRecipeTier getPedestalTier() {
		return this.pedestalVariant.getRecipeTier();
	}


	@NotNull
	private PedestalRecipeTier getStructureTier() {
		IMultiblock multiblock;

		multiblock = SpectrumMultiblocks.MULTIBLOCKS.get(SpectrumMultiblocks.PEDESTAL_COMPLEX_STRUCTURE_IDENTIFIER_CHECK);
		if (multiblock.validate(level, worldPosition.below(), Rotation.NONE)) {
			SpectrumAdvancementCriteria.COMPLETED_MULTIBLOCK.trigger((ServerPlayer) this.getOwnerIfOnline(), multiblock);
			return PedestalRecipeTier.COMPLEX;
		}

		multiblock = SpectrumMultiblocks.MULTIBLOCKS.get(SpectrumMultiblocks.PEDESTAL_COMPLEX_STRUCTURE_WITHOUT_MOONSTONE_IDENTIFIER_CHECK);
		if (multiblock.validate(level, worldPosition.below(), Rotation.NONE)) {
			SpectrumAdvancementCriteria.COMPLETED_MULTIBLOCK.trigger((ServerPlayer) this.getOwnerIfOnline(), multiblock);
			return PedestalRecipeTier.ADVANCED;
		}

		multiblock = SpectrumMultiblocks.MULTIBLOCKS.get(SpectrumMultiblocks.PEDESTAL_ADVANCED_STRUCTURE_IDENTIFIER_CHECK);
		if (multiblock.validate(level, worldPosition.below(), Rotation.NONE)) {
			SpectrumAdvancementCriteria.COMPLETED_MULTIBLOCK.trigger((ServerPlayer) this.getOwnerIfOnline(), multiblock);
			return PedestalRecipeTier.ADVANCED;
		}

		multiblock = SpectrumMultiblocks.MULTIBLOCKS.get(SpectrumMultiblocks.PEDESTAL_SIMPLE_STRUCTURE_IDENTIFIER_CHECK);
		if (multiblock.validate(level, worldPosition.below(), Rotation.NONE)) {
			SpectrumAdvancementCriteria.COMPLETED_MULTIBLOCK.trigger((ServerPlayer) this.getOwnerIfOnline(), multiblock);
			return PedestalRecipeTier.SIMPLE;
		}

		return PedestalRecipeTier.BASIC;
	}

	@Override
	public void resetUpgrades() {
		this.upgrades = null;
		this.setChanged();
	}

	/**
	 * Search for upgrades at valid positions and apply
	 */
	@Override
	public void calculateUpgrades() {
		this.upgrades = Upgradeable.calculateUpgradeMods4(level, worldPosition, 3, 2, this.ownerUUID);
		this.setChanged();
	}

	@Override
	public UpgradeHolder getUpgradeHolder() {
		return this.upgrades;
	}

	@Override
	public void setOwner(Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		setChanged();
	}
	
	public void setInventoryChanged() {
		this.inventoryChanged = true;
	}
	
}
