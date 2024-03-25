package de.dafuqs.spectrum.blocks.titration_barrel;

import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.helpers.TimeHelper;
import de.dafuqs.spectrum.mixin.accessors.BiomeAccessor;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.recipe.titration_barrel.ITitrationBarrelRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Optional;

import static de.dafuqs.spectrum.blocks.titration_barrel.TitrationBarrelBlock.BARREL_STATE;

@SuppressWarnings("UnstableApiUsage")
public class TitrationBarrelBlockEntity extends BlockEntity {
	
	protected static final int INVENTORY_SIZE = 5;
	public static final int MAX_ITEM_COUNT = 64;
	protected SimpleContainer inventory = new SimpleContainer(INVENTORY_SIZE);
	
	public final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<>() {
		@Override
		protected FluidVariant getBlankVariant() {
			return FluidVariant.blank();
		}
		
		@Override
		protected long getCapacity(FluidVariant variant) {
			return FluidConstants.BUCKET;
		}
		
		@Override
		protected void onFinalCommit() {
			super.onFinalCommit();
			setChanged();
		}
	};
	
	// Times in milliseconds using the Date class
	protected long sealTime = -1;
	protected long tapTime = -1;
	
	protected String recipe;
	protected int extractedBottles = 0;
	
	public TitrationBarrelBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.TITRATION_BARREL, pos, state);
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("Inventory", this.inventory.createTag());
		nbt.put("FluidVariant", this.fluidStorage.variant.toNbt());
		nbt.putLong("FluidAmount", this.fluidStorage.amount);
		nbt.putLong("SealTime", this.sealTime);
		nbt.putLong("TapTime", this.tapTime);
		nbt.putInt("ExtractedBottles", this.extractedBottles);
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		this.inventory = new SimpleContainer(INVENTORY_SIZE);
		if (nbt.contains("Inventory", Tag.TAG_LIST)) {
			this.inventory.fromTag(nbt.getList("Inventory", Tag.TAG_COMPOUND));
		}
		
		this.fluidStorage.variant = FluidVariant.fromNbt(nbt.getCompound("FluidVariant"));
		this.fluidStorage.amount = nbt.getLong("FluidAmount");
		this.sealTime = nbt.contains("SealTime", Tag.TAG_LONG) ? nbt.getLong("SealTime") : -1;
		this.tapTime = nbt.contains("TapTime", Tag.TAG_LONG) ? nbt.getLong("TapTime") : -1;
		this.extractedBottles = nbt.contains("ExtractedBottles", Tag.TAG_ANY_NUMERIC) ? nbt.getInt("ExtractedBottles") : 0;
	}
	
	public Container getInventory() {
		return inventory;
	}
	
	public void seal() {
		this.sealTime = new Date().getTime();
		this.setChanged();
	}
	
	public void tap() {
		this.tapTime = new Date().getTime();
		this.setChanged();
	}
	
	public void reset(Level world, BlockPos blockPos, BlockState state) {
		this.sealTime = -1;
		this.tapTime = -1;
		this.fluidStorage.variant = FluidVariant.blank();
		this.fluidStorage.amount = 0;
		this.extractedBottles = 0;
		this.inventory.clearContent();
		
		world.setBlockAndUpdate(worldPosition, state.setValue(BARREL_STATE, TitrationBarrelBlock.BarrelState.EMPTY));
		world.playSound(null, blockPos, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
		setChanged();
	}
	
	public long getSealMilliseconds() {
		if (this.sealTime == -1) {
			return 0;
		}
		
		long tapTime;
		if (this.tapTime == -1) {
			tapTime = new Date().getTime();
		} else {
			tapTime = this.tapTime;
		}
		return tapTime - this.sealTime;
	}
	
	public long getSealSeconds() {
		return getSealMilliseconds() / 1000;
	}
	
	public int getSealMinecraftDays() {
		return (int) (getSealMilliseconds() / 1000 / 60 / 20);
	}
	
	public String getSealRealDays() {
		return Support.getWithOneDecimalAfterComma(getSealMilliseconds() / 1000F / 60 / 20 / 72);
	}
	
	private boolean isEmpty(float temperature, int extractedBottles, ITitrationBarrelRecipe recipe) {
		if (recipe.isIncomplete() || !recipe.getFluidInput().test(getFluidVariant())) {
			return true;
		}
		return extractedBottles >= recipe.getOutputCountAfterAngelsShare(this.level, temperature, getSealSeconds());
	}
	
	public void addDayOfSealTime() {
		this.sealTime -= TimeHelper.EPOCH_DAY_MILLIS;
		this.setChanged();
	}
	
	public ItemStack tryHarvest(Level world, BlockPos blockPos, BlockState blockState, ItemStack handStack, @Nullable Player player) {
		ItemStack harvestedStack = ItemStack.EMPTY;
		Biome biome = world.getBiome(blockPos).value();
		
		boolean shouldReset = false;
		Component message = null;
		
		int daysSealed = getSealMinecraftDays();
		int inventoryCount = InventoryHelper.countItemsInInventory(this.inventory);
		
		Optional<ITitrationBarrelRecipe> optionalRecipe = getRecipeForInventory(world);
		if (optionalRecipe.isEmpty()) {
			if (inventory.isEmpty() && getFluidVariant().isBlank()) {
				message = Component.translatable("block.spectrum.titration_barrel.empty_when_tapping");
			} else {
				message = Component.translatable("block.spectrum.titration_barrel.invalid_recipe_when_tapping");
			}
			shouldReset = true;
		} else {
			ITitrationBarrelRecipe recipe = optionalRecipe.get();
			if (recipe.getFluidInput().test(this.getFluidVariant())) {
				if (recipe.canPlayerCraft(player)) {
					boolean canTap = true;
					Item tappingItem = recipe.getTappingItem();
					if (tappingItem != Items.AIR) {
						if (handStack.is(tappingItem)) {
							handStack.shrink(1);
						} else {
							message = Component.translatable("block.spectrum.titration_barrel.tapping_item_required").append(tappingItem.getDescription());
							canTap = false;
						}
					}
					if (canTap) {
						long secondsFermented = (this.tapTime - this.sealTime) / 1000;
						float downfall = ((BiomeAccessor)(Object) biome).getClimateSettings().downfall();
						harvestedStack = recipe.tap(this.inventory, secondsFermented, downfall);
						
						this.extractedBottles += 1;
						shouldReset = isEmpty(biome.getBaseTemperature(), this.extractedBottles, recipe);
					}
				} else {
					message = Component.translatable("block.spectrum.titration_barrel.recipe_not_unlocked");
				}
			} else {
				if (getFluidVariant().isBlank()) {
					message = Component.translatable("block.spectrum.titration_barrel.missing_liquid_when_tapping");
				} else {
					message = Component.translatable("block.spectrum.titration_barrel.invalid_recipe_when_tapping");
				}
				shouldReset = true;
			}
		}
		
		if (player != null) {
			SpectrumAdvancementCriteria.TITRATION_BARREL_TAPPING.trigger((ServerPlayer) player, harvestedStack, daysSealed, inventoryCount);
			
			if (message != null) {
				player.displayClientMessage(message, true);
			}
		}
		
		if (shouldReset) {
			reset(world, blockPos, blockState);
		}
		
		this.setChanged();
		
		return harvestedStack;
	}
	
	public Optional<ITitrationBarrelRecipe> getRecipeForInventory(Level world) {
		return world.getRecipeManager().getRecipeFor(SpectrumRecipeTypes.TITRATION_BARREL, this.inventory, world);
	}
	
	public void giveRecipeRemainders(Player player) {
		for (ItemStack stack : this.inventory.items) {
			ItemStack remainder = stack.getRecipeRemainder();
			if (!remainder.isEmpty()) {
				player.getInventory().placeItemBackInInventory(remainder);
			}
		}
	}
	
	public @NotNull FluidVariant getFluidVariant() {
		if (this.fluidStorage.amount > 0) {
			return this.fluidStorage.variant;
		} else {
			return FluidVariant.blank();
		}
	}
	
	public boolean canBeSealed(Player player) {
		int itemCount = InventoryHelper.countItemsInInventory(inventory);
		Fluid fluid = fluidStorage.variant.getFluid();
		if (itemCount == 0 && fluid == Fluids.EMPTY) {
			return true; // tap empty barrel advancement
		}

		if (level != null) {
			Optional<ITitrationBarrelRecipe> optionalRecipe = getRecipeForInventory(level);
			return optionalRecipe.isPresent()
					&& optionalRecipe.get().canPlayerCraft(player)
					&& optionalRecipe.get().getFluidInput().test(this.getFluidVariant().getFluid());
		}

		return false;
	}
	
}
