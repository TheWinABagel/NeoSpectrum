package de.dafuqs.spectrum.blocks.crystallarieum;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageBlockEntity;
import de.dafuqs.spectrum.api.energy.InkStorageItem;
import de.dafuqs.spectrum.api.energy.storage.IndividualCappedInkStorage;
import de.dafuqs.spectrum.blocks.InWorldInteractionBlockEntity;
import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.helpers.NullableDyeColor;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.helpers.TickLooper;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.recipe.crystallarieum.CrystallarieumCatalyst;
import de.dafuqs.spectrum.recipe.crystallarieum.CrystallarieumRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

public class CrystallarieumBlockEntity extends InWorldInteractionBlockEntity implements PlayerOwned, InkStorageBlockEntity<IndividualCappedInkStorage> {
	
	protected final static int CATALYST_SLOT_ID = 0;
	protected final static int INK_STORAGE_STACK_SLOT_ID = 1;
	protected final static int INVENTORY_SIZE = 2;
	
	public static final long INK_STORAGE_SIZE = 64 * 64 * 100;
	
	protected IndividualCappedInkStorage inkStorage;
	protected boolean inkDirty;
	
	@Nullable
	protected UUID ownerUUID;
	
	@Nullable
	protected CrystallarieumRecipe currentRecipe;
	protected CrystallarieumCatalyst currentCatalyst = CrystallarieumCatalyst.EMPTY;
	
	// for performance reasons, the crystallarieum only processes recipe logic all 20 ticks
	public static final int SECOND = 20;
	protected TickLooper tickLooper = new TickLooper(SECOND);
	
	protected int currentGrowthStageTicks;
	protected boolean canWork;
	
	public CrystallarieumBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.CRYSTALLARIEUM, pos, state, INVENTORY_SIZE);
		this.inkStorage = new IndividualCappedInkStorage(INK_STORAGE_SIZE);
		this.canWork = true;
	}
	
	public static void clientTick(@NotNull Level world, BlockPos blockPos, BlockState blockState, CrystallarieumBlockEntity crystallarieum) {
		if (crystallarieum.canWork && crystallarieum.currentRecipe != null) {
			ParticleOptions particleEffect = SpectrumParticleTypes.getSparkleRisingParticle(crystallarieum.currentRecipe.getInkColor().getDyeColor());
			
			int amount = 1 + crystallarieum.currentRecipe.getInkPerSecond();
			if (Support.getIntFromDecimalWithChance(amount / 80.0, world.random) > 0) {
				double randomX = world.getRandom().nextDouble() * 0.8;
				double randomZ = world.getRandom().nextDouble() * 0.8;
				world.addAlwaysVisibleParticle(particleEffect, blockPos.getX() + 0.1 + randomX, blockPos.getY() + 1, blockPos.getZ() + 0.1 + randomZ, 0.0D, 0.03D, 0.0D);
			}
		}
	}
	
	public static void serverTick(@NotNull Level world, BlockPos blockPos, BlockState blockState, CrystallarieumBlockEntity crystallarieum) {
		if (crystallarieum.canWork) {
			transferInk(crystallarieum);
			
			if (crystallarieum.currentRecipe != null) {
				crystallarieum.tickLooper.tick();
				if (crystallarieum.tickLooper.reachedCap()) {
					tickRecipe(world, blockPos, crystallarieum);
					crystallarieum.tickLooper.reset();
				}
			}
		}
	}
	
	/**
	 * Progress the recipe
	 * gets called 1/second
	 */
	private static void tickRecipe(@NotNull Level world, BlockPos blockPos, CrystallarieumBlockEntity crystallarieum) {
		if (crystallarieum.currentCatalyst == CrystallarieumCatalyst.EMPTY && !crystallarieum.currentRecipe.growsWithoutCatalyst()) {
			return;
		}
		
		// advance growing
		int consumedInk = (int) (crystallarieum.currentRecipe.getInkPerSecond() * crystallarieum.currentCatalyst.growthAccelerationMod * crystallarieum.currentCatalyst.inkConsumptionMod);
		if (crystallarieum.inkStorage.drainEnergy(crystallarieum.currentRecipe.getInkColor(), consumedInk) < consumedInk) {
			crystallarieum.canWork = false;
			crystallarieum.setInkDirty();
			crystallarieum.updateInClientWorld();
			return;
		}
		
		crystallarieum.setInkDirty();
		crystallarieum.currentGrowthStageTicks += SECOND * crystallarieum.currentCatalyst.growthAccelerationMod;
		
		// check if a catalyst should get used up
		if (world.random.nextFloat() < crystallarieum.currentCatalyst.consumeChancePerSecond) {
			ItemStack catalystStack = crystallarieum.getItem(CATALYST_SLOT_ID);
			catalystStack.shrink(1);
			crystallarieum.updateInClientWorld();
			if (catalystStack.isEmpty()) {
				crystallarieum.currentCatalyst = CrystallarieumCatalyst.EMPTY;
				if (!crystallarieum.currentRecipe.growsWithoutCatalyst()) {
					crystallarieum.canWork = false;
				}
			}
		}
		
		// advanced enough? grow!
		if (crystallarieum.currentGrowthStageTicks >= crystallarieum.currentRecipe.getSecondsPerGrowthStage() * SECOND) {
			BlockPos topPos = blockPos.above();
			BlockState topState = world.getBlockState(topPos);
			for (Iterator<BlockState> it = crystallarieum.currentRecipe.getGrowthStages().iterator(); it.hasNext(); ) {
				BlockState state = it.next();
				if (state.equals(topState)) {
					if (it.hasNext()) {
						BlockState targetState = it.next();
						world.setBlockAndUpdate(topPos, targetState);
						
						// if the stone on top can not grow any further: pause
						if (!it.hasNext()) {
							crystallarieum.canWork = false;
						}
						
						ServerPlayer owner = (ServerPlayer) crystallarieum.getOwnerIfOnline();
						if (owner != null) {
							SpectrumAdvancementCriteria.CRYSTALLARIEUM_GROWING.trigger(owner, (ServerLevel) world, topPos, crystallarieum.getItem(CATALYST_SLOT_ID));
						}
					}
				}
			}
			crystallarieum.currentGrowthStageTicks = 0;
		}
	}
	
	private static void transferInk(CrystallarieumBlockEntity crystallarieum) {
		ItemStack inkStorageStack = crystallarieum.getItem(INK_STORAGE_STACK_SLOT_ID);
		if (inkStorageStack.getItem() instanceof InkStorageItem<?> inkStorageItem) {
			InkStorage itemInkStorage = inkStorageItem.getEnergyStorage(inkStorageStack);
			long transferredAmount = InkStorage.transferInk(itemInkStorage, crystallarieum.inkStorage);
			if (transferredAmount > 0) {
				inkStorageItem.setEnergyStorage(inkStorageStack, itemInkStorage);
			}
		}
	}
	
	@Override
	public void inventoryChanged() {
		this.currentCatalyst = this.currentRecipe == null ? CrystallarieumCatalyst.EMPTY : this.currentRecipe.getCatalyst(getItem(CATALYST_SLOT_ID));
		this.canWork = true;
		super.inventoryChanged();
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		if (nbt.contains("InkStorage", Tag.TAG_COMPOUND)) {
			this.inkStorage = IndividualCappedInkStorage.fromNbt(nbt.getCompound("InkStorage"));
		}
		if (nbt.contains("Looper", Tag.TAG_COMPOUND)) {
			this.tickLooper.readNbt(nbt.getCompound("Looper"));
		}
		this.canWork = nbt.getBoolean("CanWork");
		
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
		
		this.currentRecipe = null;
		this.currentCatalyst = CrystallarieumCatalyst.EMPTY;
		if (nbt.contains("CurrentRecipe")) {
			this.currentGrowthStageTicks = nbt.getInt("CurrentGrowthStageDuration");
			String recipeString = nbt.getString("CurrentRecipe");
			if (!recipeString.isEmpty() && SpectrumCommon.minecraftServer != null) {
				Optional<? extends Recipe<?>> optionalRecipe = SpectrumCommon.minecraftServer.getRecipeManager().byKey(new ResourceLocation(recipeString));
				if (optionalRecipe.isPresent() && (optionalRecipe.get() instanceof CrystallarieumRecipe crystallarieumRecipe)) {
					this.currentRecipe = crystallarieumRecipe;
					this.currentCatalyst = this.currentRecipe.getCatalyst(getItem(CATALYST_SLOT_ID));
				}
			}
		} else {
			this.currentGrowthStageTicks = 0;
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("InkStorage", this.inkStorage.toNbt());
		nbt.put("Looper", this.tickLooper.toNbt());
		
		nbt.putBoolean("CanWork", this.canWork);
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
		if (this.currentRecipe != null) {
			nbt.putString("CurrentRecipe", this.currentRecipe.getId().toString());
			nbt.putInt("CurrentGrowthStageDuration", this.currentGrowthStageTicks);
		}
	}
	
	@Override
	public @Nullable UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public void setOwner(Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		setChanged();
	}
	
	/**
	 * Searches recipes for a valid one using itemStack and plants the first block of that recipe on top
	 *
	 * @param itemStack stack that is tried to plant on top, if a valid recipe
	 */
	public void acceptStack(ItemStack itemStack, boolean creative, @Nullable UUID player) {
		boolean changed = false;
		
		if (itemStack.getItem() instanceof InkStorageItem<?> inkStorageItem && inkStorageItem.getDrainability().canDrain(false)) {
			ItemStack currentInkStorageStack = getItem(INK_STORAGE_STACK_SLOT_ID);
			if (currentInkStorageStack.isEmpty()) {
				setItem(INK_STORAGE_STACK_SLOT_ID, itemStack.copy());
				if (!creative) {
					itemStack.setCount(0);
				}
				changed = true;
			}
		} else if (level.getBlockState(worldPosition.above()).isAir()) {
			CrystallarieumRecipe recipe = CrystallarieumRecipe.getRecipeForStack(itemStack);
			if (recipe != null) {
				if (!creative) {
					itemStack.shrink(1);
				}
				BlockState placedState = recipe.getGrowthStages().get(0);
				level.setBlockAndUpdate(worldPosition.above(), placedState);
				onTopBlockChange(placedState, recipe);
				changed = true;
			}
		} else if (this.currentRecipe != null) {
			ItemStack currentCatalystStack = getItem(CATALYST_SLOT_ID);
			if (currentCatalystStack.isEmpty()) {
				CrystallarieumCatalyst catalyst = this.currentRecipe.getCatalyst(itemStack);
				if (catalyst != CrystallarieumCatalyst.EMPTY) {
					setItem(CATALYST_SLOT_ID, itemStack.copy());
					if (!creative) {
						itemStack.setCount(0);
					}
					this.currentCatalyst = catalyst;
					changed = true;
				}
			} else if (ItemStack.isSameItemSameTags(currentCatalystStack, itemStack)) {
				InventoryHelper.combineStacks(currentCatalystStack, itemStack);
				changed = true;
			}
		}
		
		if (changed) {
			level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 0.8F + level.random.nextFloat() * 0.6F);
			if (player != null) {
				this.ownerUUID = player;
			}
			inventoryChanged();
		}
	}
	
	/**
	 * Triggered when the block on top of the crystallarieum has changed
	 * Sets the new recipe matching that block state
	 *
	 * @param newState the new block state on top
	 * @param recipe   optionally the matching CrystallarieumRecipe. If null is passed it will be calculated
	 */
	public void onTopBlockChange(BlockState newState, @Nullable CrystallarieumRecipe recipe) {
		if (newState.isAir()) { // fast fail
			this.currentRecipe = null;
			this.canWork = false;
			setChanged();
			updateInClientWorld();
			
			level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(CrystallarieumBlock.COLOR, NullableDyeColor.NONE));
		} else {
			this.currentRecipe = recipe == null ? CrystallarieumRecipe.getRecipeForState(newState) : recipe;
			
			if (this.currentRecipe == null) {
				level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(CrystallarieumBlock.COLOR, NullableDyeColor.NONE));
			} else {
				level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(CrystallarieumBlock.COLOR, NullableDyeColor.get(this.currentRecipe.getInkColor().getDyeColor())));
				
				ItemStack catalystStack = getItem(CATALYST_SLOT_ID);
				if (!catalystStack.isEmpty()) {
					this.currentCatalyst = this.currentRecipe.getCatalyst(catalystStack);
					if (this.currentCatalyst == CrystallarieumCatalyst.EMPTY) {
						ItemEntity itemEntity = new ItemEntity(level, this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + 0.5, catalystStack);
						this.setItem(CATALYST_SLOT_ID, ItemStack.EMPTY);
						level.addFreshEntity(itemEntity);
					}
				}
			}
			
			inventoryChanged();
		}
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
	
	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if (slot == INK_STORAGE_STACK_SLOT_ID) {
			return stack.getItem() instanceof InkStorageItem;
		} else if (this.currentRecipe != null) {
			return this.currentRecipe.getCatalyst(stack) != CrystallarieumCatalyst.EMPTY;
		}
		return false;
	}
	
}
