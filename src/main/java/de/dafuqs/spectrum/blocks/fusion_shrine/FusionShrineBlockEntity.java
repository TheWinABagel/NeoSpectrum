package de.dafuqs.spectrum.blocks.fusion_shrine;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.api.color.ColorRegistry;
import de.dafuqs.spectrum.api.recipe.FusionShrineRecipeWorldEffect;
import de.dafuqs.spectrum.blocks.InWorldInteractionBlockEntity;
import de.dafuqs.spectrum.blocks.upgrade.Upgradeable;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.recipe.fusion_shrine.FusionShrineRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class FusionShrineBlockEntity extends InWorldInteractionBlockEntity implements PlayerOwned, Upgradeable {

    protected static final int INVENTORY_SIZE = 7;

    private UUID ownerUUID;
    private UpgradeHolder upgrades;
    private FusionShrineRecipe currentRecipe;
    private int craftingTime;
    private int craftingTimeTotal;

    private boolean inventoryChanged = true;

	protected FluidTank fluidStorage = new FluidTank(FluidType.BUCKET_VOLUME) {
		@Override
		protected void onContentsChanged() {
			super.onContentsChanged();
			setLightForFluid(level, worldPosition, this.fluid.getFluid());
			inventoryChanged();
			setChanged();
		}
	};

	private final LazyOptional<IFluidHandler> fluidStorageHolder = LazyOptional.of(() -> fluidStorage);

//    public final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<>() {
//		@Override
//		protected FluidVariant getBlankVariant() {
//			return FluidVariant.blank();
//		}
//
//		@Override
//		protected long getCapacity(FluidVariant variant) {
//			return FluidConstants.BUCKET;
//		}
//
//        @Override
//        protected void onFinalCommit() {
//            super.onFinalCommit();
//            setLightForFluid(level, worldPosition, this.variant.getFluid());
//            inventoryChanged();
//            setChanged();
//        }
//    };

    public FusionShrineBlockEntity(BlockPos pos, BlockState state) {
        super(SpectrumBlockEntities.FUSION_SHRINE, pos, state, INVENTORY_SIZE);
    }

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER)
			return fluidStorageHolder.cast();
		return super.getCapability(cap, side);
	}

	public static void clientTick(@NotNull Level world, BlockPos blockPos, BlockState blockState, FusionShrineBlockEntity fusionShrineBlockEntity) {
        if (!fusionShrineBlockEntity.isEmpty()) {
            int randomSlot = world.getRandom().nextInt(fusionShrineBlockEntity.getContainerSize());
            ItemStack randomStack = fusionShrineBlockEntity.getItem(randomSlot);
            if (!randomStack.isEmpty()) {
				Optional<DyeColor> optionalItemColor = ColorRegistry.ITEM_COLORS.getMapping(randomStack.getItem());
				if (optionalItemColor.isPresent()) {
					ParticleOptions particleEffect = SpectrumParticleTypes.getCraftingParticle(optionalItemColor.get());
					
					int particleAmount = (int) StrictMath.ceil(randomStack.getCount() / 8.0F);
					for (int i = 0; i < particleAmount; i++) {
						float randomX = 3.0F - world.getRandom().nextFloat() * 7;
						float randomZ = 3.0F - world.getRandom().nextFloat() * 7;
						world.addParticle(particleEffect, blockPos.getX() + randomX, blockPos.getY(), blockPos.getZ() + randomZ, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}
	}
	
	public void spawnCraftingParticles() {
		BlockPos blockPos = getBlockPos();
		FusionShrineRecipe recipe = this.currentRecipe;
		if (recipe != null && level != null) {
			Fluid fluid = this.getFluidVariant().getFluid();
			Optional<DyeColor> optionalFluidColor = ColorRegistry.FLUID_COLORS.getMapping(fluid);
			if (optionalFluidColor.isPresent()) {
				ParticleOptions particleEffect = SpectrumParticleTypes.getFluidRisingParticle(optionalFluidColor.get());
				
				float randomX = 0.1F + level.getRandom().nextFloat() * 0.8F;
				float randomZ = 0.1F + level.getRandom().nextFloat() * 0.8F;
				level.addParticle(particleEffect, blockPos.getX() + randomX, blockPos.getY() + 1, blockPos.getZ() + randomZ, 0.0D, 0.1D, 0.0D);
			}
		}
	}

	public void scatterContents(@NotNull Level world) {
		SpectrumS2CPacketSender.playParticleWithExactVelocity((ServerLevel) world, Vec3.atCenterOf(this.getBlockPos()), SpectrumParticleTypes.RED_CRAFTING, 1, new Vec3(0, -0.5, 0));
		world.playSound(null, this.getBlockPos(), SpectrumSoundEvents.CRAFTING_ABORTED, SoundSource.BLOCKS, 0.9F + world.random.nextFloat() * 0.2F, 0.9F + world.random.nextFloat() * 0.2F);
		world.playSound(null, this.getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.9F + world.random.nextFloat() * 0.2F, 0.5F + world.random.nextFloat() * 0.2F);
		FusionShrineBlock.scatterContents(world, this.getBlockPos());
		this.inventoryChanged();
	}

	public static void serverTick(@NotNull Level world, BlockPos blockPos, BlockState blockState, FusionShrineBlockEntity fusionShrineBlockEntity) {
		if (fusionShrineBlockEntity.upgrades == null) {
			fusionShrineBlockEntity.calculateUpgrades();
		}
		
		if (fusionShrineBlockEntity.inventoryChanged) {
			FusionShrineRecipe previousRecipe = fusionShrineBlockEntity.currentRecipe;
			fusionShrineBlockEntity.currentRecipe = calculateRecipe(world, fusionShrineBlockEntity);
			
			if (fusionShrineBlockEntity.currentRecipe != previousRecipe) {
				fusionShrineBlockEntity.craftingTime = 0;
				if (fusionShrineBlockEntity.currentRecipe == null) {
					SpectrumS2CPacketSender.sendCancelBlockBoundSoundInstance((ServerLevel) world, fusionShrineBlockEntity.worldPosition);
				} else {
					fusionShrineBlockEntity.craftingTimeTotal = (int) Math.ceil(fusionShrineBlockEntity.currentRecipe.getCraftingTime() / fusionShrineBlockEntity.upgrades.getEffectiveValue(Upgradeable.UpgradeType.SPEED));
				}
				
				fusionShrineBlockEntity.updateInClientWorld();
			}
			
			fusionShrineBlockEntity.inventoryChanged = false;
		}
		
		FusionShrineRecipe recipe = fusionShrineBlockEntity.currentRecipe;
		if (recipe == null || !recipe.getFluidInput().equals(fusionShrineBlockEntity.fluidStorage.getFluid().getFluid())) {
			return;
		}
		
		// check the crafting conditions from time to time
		// good for performance because of the many checks
		if (fusionShrineBlockEntity.craftingTime % 60 == 0) {
			Player lastInteractedPlayer = fusionShrineBlockEntity.getOwnerIfOnline();
			
			boolean recipeConditionsMet = recipe.canPlayerCraft(lastInteractedPlayer) && recipe.areConditionMetCurrently((ServerLevel) world, blockPos);
			boolean structureComplete = FusionShrineBlock.verifyStructure(world, blockPos, null);
			boolean structureCompleteWithSky = FusionShrineBlock.verifySkyAccess((ServerLevel) world, blockPos) && structureComplete;
			
			if (!recipeConditionsMet || !structureCompleteWithSky) {
				if (!structureCompleteWithSky) {
					fusionShrineBlockEntity.scatterContents(world);
				}
				fusionShrineBlockEntity.craftingTime = 0;
				return;
			}
		}
		
		// advance crafting
		++fusionShrineBlockEntity.craftingTime;
		
		if (fusionShrineBlockEntity.craftingTime == 1 && fusionShrineBlockEntity.craftingTimeTotal > 1) {
			SpectrumS2CPacketSender.sendPlayBlockBoundSoundInstance(SpectrumSoundEvents.FUSION_SHRINE_CRAFTING, (ServerLevel) world, fusionShrineBlockEntity.getBlockPos(), fusionShrineBlockEntity.craftingTimeTotal - fusionShrineBlockEntity.craftingTime);
		}
		
		// play the current crafting effect
		FusionShrineRecipeWorldEffect effect = recipe.getWorldEffectForTick(fusionShrineBlockEntity.craftingTime, fusionShrineBlockEntity.craftingTimeTotal);
		if (effect != null) {
			effect.trigger((ServerLevel) world, blockPos);
		}
		
		// craft when enough ticks have passed
		if (fusionShrineBlockEntity.craftingTime == fusionShrineBlockEntity.craftingTimeTotal) {
			craft(world, blockPos, fusionShrineBlockEntity, recipe);
			fusionShrineBlockEntity.inventoryChanged();
		} else {
			SpectrumS2CPacketSender.sendPlayFusionCraftingInProgressParticles(world, blockPos);
		}
		fusionShrineBlockEntity.setChanged();
	}
	
	@Nullable
	private static FusionShrineRecipe calculateRecipe(@NotNull Level world, FusionShrineBlockEntity fusionShrineBlockEntity) {
		if (fusionShrineBlockEntity.currentRecipe != null) {
			if (fusionShrineBlockEntity.currentRecipe.matches(fusionShrineBlockEntity, world)) {
				return fusionShrineBlockEntity.currentRecipe;
			}
		}
		return world.getRecipeManager().getRecipeFor(SpectrumRecipeTypes.FUSION_SHRINE, fusionShrineBlockEntity, world).orElse(null);
	}
	
	// calculate the max amount of items that will be crafted
	// note that we only check each ingredient once, if a match was found
	// custom recipes therefore should not use items / tags that match multiple items
	// at once, since we can not rely on positions in a grid like vanilla does
	// in its crafting table
	private static void craft(Level world, BlockPos blockPos, FusionShrineBlockEntity fusionShrineBlockEntity, FusionShrineRecipe recipe) {
		recipe.craft(world, fusionShrineBlockEntity);
		
		if (recipe.shouldPlayCraftingFinishedEffects()) {
			SpectrumS2CPacketSender.sendPlayFusionCraftingFinishedParticles(world, blockPos, recipe.getResultItem(world.registryAccess()));
			fusionShrineBlockEntity.playSound(SpectrumSoundEvents.FUSION_SHRINE_CRAFTING_FINISHED, 1.4F);
		}
		
		scatterContents(world, blockPos.above(), fusionShrineBlockEntity); // drop remaining items
		
		fusionShrineBlockEntity.fluidStorage.setFluid(FluidStack.EMPTY);
		world.setBlock(blockPos, world.getBlockState(blockPos).setValue(FusionShrineBlock.LIGHT_LEVEL, 0), 3);
		
	}

	@Override
	public UpgradeHolder getUpgradeHolder() {
		return upgrades;
	}

	public static void scatterContents(Level world, BlockPos pos, FusionShrineBlockEntity blockEntity) {
		Containers.dropContents(world, pos, blockEntity.getItems());
		world.updateNeighbourForOutputSignal(pos, world.getBlockState(pos).getBlock());
	}

	@Override
	public void load(CompoundTag nbt) {
        super.load(nbt);
		fluidStorage.readFromNBT(nbt);
//        this.fluidStorage.variant = FluidVariant.fromNbt(nbt.getCompound("FluidVariant"));
//        this.fluidStorage.amount = nbt.getLong("FluidAmount");

        this.craftingTime = nbt.getShort("CraftingTime");
        this.craftingTimeTotal = nbt.getShort("CraftingTimeTotal");
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);

        this.currentRecipe = null;
		if (nbt.contains("CurrentRecipe")) {
			String recipeString = nbt.getString("CurrentRecipe");
			if (!recipeString.isEmpty() && SpectrumCommon.minecraftServer != null) {
				Optional<? extends Recipe<?>> optionalRecipe = SpectrumCommon.minecraftServer.getRecipeManager().byKey(new ResourceLocation(recipeString));
				if (optionalRecipe.isPresent() && optionalRecipe.get() instanceof FusionShrineRecipe optionalFusionRecipe) {
					this.currentRecipe = optionalFusionRecipe;
				}
			}
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
		fluidStorage.writeToNBT(nbt);
//        nbt.put("FluidVariant", this.fluidStorage.variant.toNbt());
//        nbt.putLong("FluidAmount", this.fluidStorage.amount);
        nbt.putShort("CraftingTime", (short) this.craftingTime);
        nbt.putShort("CraftingTimeTotal", (short) this.craftingTimeTotal);
        if (this.upgrades != null) {
            nbt.put("Upgrades", this.upgrades.toNbt());
        }
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
        if (this.currentRecipe != null) {
            nbt.putString("CurrentRecipe", this.currentRecipe.getId().toString());
		}
	}
	
	public void playSound(SoundEvent soundEvent, float volume) {
		if (level != null) {
			RandomSource random = level.random;
			level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), soundEvent, SoundSource.BLOCKS, volume, 0.9F + random.nextFloat() * 0.15F);
		}
    }

    public void grantPlayerFusionCraftingAdvancement(FusionShrineRecipe recipe, int experience) {
        ServerPlayer serverPlayerEntity = (ServerPlayer) getOwnerIfOnline();
        if (serverPlayerEntity != null) {
			SpectrumAdvancementCriteria.FUSION_SHRINE_CRAFTING.trigger(serverPlayerEntity, recipe.getResultItem(serverPlayerEntity.level().registryAccess()), experience);
        }
    }

    public @NotNull FluidStack getFluidVariant() {
        if (this.fluidStorage.getFluidAmount() > 0) {
            return this.fluidStorage.getFluid();
        } else {
            return FluidStack.EMPTY;
        }
    }

    private void setLightForFluid(Level world, BlockPos blockPos, Fluid fluid) {
        if (SpectrumCommon.fluidLuminance.containsKey(fluid)) {
            int light = SpectrumCommon.fluidLuminance.get(fluid);
            world.setBlock(blockPos, world.getBlockState(blockPos).setValue(FusionShrineBlock.LIGHT_LEVEL, light), 3);
        } else {
            world.setBlock(blockPos, world.getBlockState(blockPos).setValue(FusionShrineBlock.LIGHT_LEVEL, 0), 3);
        }
    }

    // PLAYER OWNED
    // "owned" is not to be taken literally here. The owner
	// is always set to the last player interacted with to trigger advancements
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public void setOwner(Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		setChanged();
	}
	
	// UPGRADEABLE
	@Override
	public void resetUpgrades() {
		this.upgrades = null;
		this.setChanged();
	}
	
	@Override
	public void calculateUpgrades() {
		this.upgrades = Upgradeable.calculateUpgradeMods4(level, worldPosition, 2, 0, this.ownerUUID);
		this.setChanged();
	}
	
	@Override
	public void inventoryChanged() {
		super.inventoryChanged();
		this.inventoryChanged = true;
		this.craftingTime = 0;
	}
	
}
