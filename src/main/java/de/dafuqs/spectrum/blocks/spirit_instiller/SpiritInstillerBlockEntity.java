package de.dafuqs.spectrum.blocks.spirit_instiller;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.MultiblockCrafter;
import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.api.color.ItemColors;
import de.dafuqs.spectrum.blocks.InWorldInteractionBlockEntity;
import de.dafuqs.spectrum.blocks.decoration.GemstoneChimeBlock;
import de.dafuqs.spectrum.blocks.item_bowl.ItemBowlBlockEntity;
import de.dafuqs.spectrum.blocks.upgrade.Upgradeable;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.recipe.spirit_instiller.SpiritInstillerRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SpiritInstillerBlockEntity extends InWorldInteractionBlockEntity implements MultiblockCrafter {
	
	protected static final int INVENTORY_SIZE = 1;
	public static final List<Vec3i> itemBowlOffsetsHorizontal = new ArrayList<>() {{
		add(new Vec3i(0, 0, 2));
		add(new Vec3i(0, 0, -2));
	}};
	
	public static final List<Vec3i> itemBowlOffsetsVertical = new ArrayList<>() {{
		add(new Vec3i(2, 0, 0));
		add(new Vec3i(-2, 0, 0));
	}};
	
	private final Container autoCraftingInventory; // 0: instiller stack; 1-2: item bowl stacks
	private boolean inventoryChanged;
	private UUID ownerUUID;
	private UpgradeHolder upgrades;
	private Rotation multiblockRotation = Rotation.NONE;
	private SpiritInstillerRecipe currentRecipe;
	private int craftingTime;
	private int craftingTimeTotal;
	
	public SpiritInstillerBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.SPIRIT_INSTILLER, pos, state, INVENTORY_SIZE);
		this.autoCraftingInventory = new SimpleContainer(INVENTORY_SIZE + 2); // 2 item bowls
	}
	
	public static void clientTick(Level world, BlockPos blockPos, BlockState blockState, @NotNull SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
		if (spiritInstillerBlockEntity.currentRecipe != null) {
			spiritInstillerBlockEntity.doInstillerParticles(world);
			if (world.getGameTime() % 40 == 0) {
				spiritInstillerBlockEntity.doChimeParticles(world);
			}
		}
	}
	
	public static void serverTick(Level world, BlockPos blockPos, BlockState blockState, SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
		if (spiritInstillerBlockEntity.upgrades == null) {
			spiritInstillerBlockEntity.calculateUpgrades();
		}
		
		if (spiritInstillerBlockEntity.inventoryChanged) {
			SpiritInstillerRecipe previousRecipe = spiritInstillerBlockEntity.currentRecipe;
			calculateCurrentRecipe(world, spiritInstillerBlockEntity);
			
			if (spiritInstillerBlockEntity.currentRecipe != previousRecipe) {
				spiritInstillerBlockEntity.craftingTime = 0;
				if (spiritInstillerBlockEntity.currentRecipe == null) {
					SpectrumS2CPacketSender.sendCancelBlockBoundSoundInstance((ServerLevel) world, spiritInstillerBlockEntity.worldPosition);
				} else {
					spiritInstillerBlockEntity.craftingTimeTotal = (int) Math.ceil(spiritInstillerBlockEntity.currentRecipe.getCraftingTime() / spiritInstillerBlockEntity.upgrades.getEffectiveValue(Upgradeable.UpgradeType.SPEED));
				}
				spiritInstillerBlockEntity.updateInClientWorld();
			}
			spiritInstillerBlockEntity.inventoryChanged = false;
		}
		
		if (spiritInstillerBlockEntity.currentRecipe == null) {
			return;
		}
		
		if (spiritInstillerBlockEntity.craftingTime % 60 == 0) {
			if (!checkRecipeRequirements(world, blockPos, spiritInstillerBlockEntity)) {
				spiritInstillerBlockEntity.craftingTime = 0;
				return;
			}
		}
		
		if (spiritInstillerBlockEntity.currentRecipe != null) {
			spiritInstillerBlockEntity.craftingTime++;
			
			if (spiritInstillerBlockEntity.craftingTime == 1) {
				SpectrumS2CPacketSender.sendPlayBlockBoundSoundInstance(SpectrumSoundEvents.SPIRIT_INSTILLER_CRAFTING, (ServerLevel) world, spiritInstillerBlockEntity.worldPosition, Integer.MAX_VALUE);
			} else if (spiritInstillerBlockEntity.craftingTime == spiritInstillerBlockEntity.craftingTimeTotal * 0.01
					|| spiritInstillerBlockEntity.craftingTime == Math.floor(spiritInstillerBlockEntity.craftingTimeTotal * 0.25)
					|| spiritInstillerBlockEntity.craftingTime == Math.floor(spiritInstillerBlockEntity.craftingTimeTotal * 0.5)
					|| spiritInstillerBlockEntity.craftingTime == Math.floor(spiritInstillerBlockEntity.craftingTimeTotal * 0.75)
					|| spiritInstillerBlockEntity.craftingTime == Math.floor(spiritInstillerBlockEntity.craftingTimeTotal * 0.83)
					|| spiritInstillerBlockEntity.craftingTime == Math.floor(spiritInstillerBlockEntity.craftingTimeTotal * 0.90)
					|| spiritInstillerBlockEntity.craftingTime == Math.floor(spiritInstillerBlockEntity.craftingTimeTotal * 0.95)
					|| spiritInstillerBlockEntity.craftingTime == Math.floor(spiritInstillerBlockEntity.craftingTimeTotal * 0.98)
					|| spiritInstillerBlockEntity.craftingTime == Math.floor(spiritInstillerBlockEntity.craftingTimeTotal * 0.99)) {
				spiritInstillerBlockEntity.doItemBowlOrbs(world);
			} else if (spiritInstillerBlockEntity.craftingTime == spiritInstillerBlockEntity.craftingTimeTotal) {
				craftSpiritInstillerRecipe(world, spiritInstillerBlockEntity, spiritInstillerBlockEntity.currentRecipe);
			}
			
			spiritInstillerBlockEntity.setChanged();
		}
	}
	
	private static void calculateCurrentRecipe(@NotNull Level world, @NotNull SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
		// test the cached recipe => faster
		if (spiritInstillerBlockEntity.currentRecipe != null && !spiritInstillerBlockEntity.autoCraftingInventory.isEmpty()) {
			if (spiritInstillerBlockEntity.currentRecipe.matches(spiritInstillerBlockEntity.autoCraftingInventory, world)) {
				return;
			}
		}
		
		// cached recipe did not match => calculate new
		spiritInstillerBlockEntity.craftingTime = 0;
		spiritInstillerBlockEntity.currentRecipe = null;
		
		ItemStack instillerStack = spiritInstillerBlockEntity.getItem(SpiritInstillerRecipe.CENTER_INGREDIENT);
		if (!instillerStack.isEmpty()) {
			spiritInstillerBlockEntity.autoCraftingInventory.setItem(SpiritInstillerRecipe.CENTER_INGREDIENT, instillerStack);
			
			// left item bowl
			if (world.getBlockEntity(getItemBowlPos(spiritInstillerBlockEntity, false)) instanceof ItemBowlBlockEntity itemBowlBlockEntity) {
				spiritInstillerBlockEntity.autoCraftingInventory.setItem(SpiritInstillerRecipe.FIRST_INGREDIENT, itemBowlBlockEntity.getItem(0));
			} else {
				spiritInstillerBlockEntity.autoCraftingInventory.setItem(SpiritInstillerRecipe.FIRST_INGREDIENT, ItemStack.EMPTY);
			}
			// right item bowl
			if (world.getBlockEntity(getItemBowlPos(spiritInstillerBlockEntity, true)) instanceof ItemBowlBlockEntity itemBowlBlockEntity) {
				spiritInstillerBlockEntity.autoCraftingInventory.setItem(SpiritInstillerRecipe.SECOND_INGREDIENT, itemBowlBlockEntity.getItem(0));
			} else {
				spiritInstillerBlockEntity.autoCraftingInventory.setItem(SpiritInstillerRecipe.SECOND_INGREDIENT, ItemStack.EMPTY);
			}
			
			SpiritInstillerRecipe spiritInstillerRecipe = world.getRecipeManager().getRecipeFor(SpectrumRecipeTypes.SPIRIT_INSTILLING, spiritInstillerBlockEntity.autoCraftingInventory, world).orElse(null);
			if (spiritInstillerRecipe != null) {
				spiritInstillerBlockEntity.currentRecipe = spiritInstillerRecipe;
				spiritInstillerBlockEntity.craftingTimeTotal = (int) Math.ceil(spiritInstillerRecipe.getCraftingTime() / spiritInstillerBlockEntity.upgrades.getEffectiveValue(Upgradeable.UpgradeType.SPEED));
			}
		}
		
		spiritInstillerBlockEntity.updateInClientWorld();
	}
	
	public static BlockPos getItemBowlPos(@NotNull SpiritInstillerBlockEntity spiritInstillerBlockEntity, boolean right) {
		BlockPos blockPos = spiritInstillerBlockEntity.worldPosition;
		switch (spiritInstillerBlockEntity.multiblockRotation) {
			case NONE, CLOCKWISE_180 -> {
				if (right) {
					return blockPos.above().east(2);
				} else {
					return blockPos.above().west(2);
				}
			}
			default -> {
				if (right) {
					return blockPos.above().north(2);
				} else {
					return blockPos.above().south(2);
				}
			}
		}
	}
	
	private static boolean checkRecipeRequirements(Level world, BlockPos blockPos, @NotNull SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
		Player lastInteractedPlayer = PlayerOwned.getPlayerEntityIfOnline(spiritInstillerBlockEntity.ownerUUID);
		if (lastInteractedPlayer == null) {
			return false;
		}
		
		boolean playerCanCraft = true;
		if (spiritInstillerBlockEntity.currentRecipe != null) {
			playerCanCraft = spiritInstillerBlockEntity.currentRecipe.canPlayerCraft(lastInteractedPlayer);
		}
		
		boolean structureComplete = SpiritInstillerBlock.verifyStructure(world, blockPos, null, spiritInstillerBlockEntity);
		boolean canCraft = true;
		if (!playerCanCraft || !structureComplete) {
			if (!structureComplete) {
				world.playSound(null, spiritInstillerBlockEntity.getBlockPos(), SpectrumSoundEvents.CRAFTING_ABORTED, SoundSource.BLOCKS, 0.9F + world.random.nextFloat() * 0.2F, 0.9F + world.random.nextFloat() * 0.2F);
			}
			
			canCraft = false;
		}
		
		if (lastInteractedPlayer instanceof ServerPlayer serverPlayerEntity) {
			testAndUnlockUnlockBossMemoryAdvancement(serverPlayerEntity, spiritInstillerBlockEntity.currentRecipe, canCraft);
		}
		
		return canCraft & spiritInstillerBlockEntity.currentRecipe.canPlayerCraft(lastInteractedPlayer) && spiritInstillerBlockEntity.currentRecipe.canCraftWithStacks(spiritInstillerBlockEntity.autoCraftingInventory);
	}
	
	public static void testAndUnlockUnlockBossMemoryAdvancement(ServerPlayer player, SpiritInstillerRecipe spiritInstillerRecipe, boolean canActuallyCraft) {
		boolean isBossMemory = spiritInstillerRecipe.getGroup() != null && spiritInstillerRecipe.getGroup().equals("boss_memories");
		if (isBossMemory) {
			if (canActuallyCraft) {
				Support.grantAdvancementCriterion(player, "midgame/craft_blacklisted_memory_success", "succeed_crafting_boss_memory");
			} else {
				Support.grantAdvancementCriterion(player, "midgame/craft_blacklisted_memory_fail", "fail_to_craft_boss_memory");
			}
		}
	}
	
	public static void craftSpiritInstillerRecipe(Level world, @NotNull SpiritInstillerBlockEntity spiritInstillerBlockEntity, @NotNull SpiritInstillerRecipe spiritInstillerRecipe) {
		ItemStack resultStack = spiritInstillerRecipe.assemble(spiritInstillerBlockEntity, world.registryAccess());
		decrementItemsInInstillerAndBowls(spiritInstillerBlockEntity);
		if (!resultStack.isEmpty()) {
			if (spiritInstillerBlockEntity.getItem(0).isEmpty()) {
				// keep it on the Instiller
				spiritInstillerBlockEntity.setItem(0, resultStack);
			} else {
				// spawn the result stack in world
				MultiblockCrafter.spawnItemStackAsEntitySplitViaMaxCount(world, spiritInstillerBlockEntity.worldPosition, resultStack, resultStack.getCount(), MultiblockCrafter.RECIPE_STACK_VELOCITY);
			}
		}
		
		playCraftingFinishedEffects(spiritInstillerBlockEntity);
		spiritInstillerBlockEntity.craftingTime = 0;
		spiritInstillerBlockEntity.inventoryChanged();
	}
	
	public static void decrementItemsInInstillerAndBowls(@NotNull SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
		Level world = spiritInstillerBlockEntity.getLevel();
		SpiritInstillerRecipe recipe = spiritInstillerBlockEntity.currentRecipe;
		
		double efficiencyModifier = 1.0;
		if (!recipe.areYieldAndEfficiencyUpgradesDisabled() && spiritInstillerBlockEntity.upgrades.getEffectiveValue(UpgradeType.EFFICIENCY) != 1.0) {
			efficiencyModifier = 1.0 / spiritInstillerBlockEntity.upgrades.getEffectiveValue(UpgradeType.EFFICIENCY);
		}
		
		BlockEntity leftBowlBlockEntity = world.getBlockEntity(getItemBowlPos(spiritInstillerBlockEntity, false));
		BlockEntity rightBowlBlockEntity = world.getBlockEntity(getItemBowlPos(spiritInstillerBlockEntity, true));
		if (leftBowlBlockEntity instanceof ItemBowlBlockEntity leftBowl && rightBowlBlockEntity instanceof ItemBowlBlockEntity rightBowl) {
			// center ingredient
			int decreasedAmountAfterEfficiencyMod = Support.getIntFromDecimalWithChance(recipe.getIngredientStacks().get(SpiritInstillerRecipe.CENTER_INGREDIENT).getCount() * efficiencyModifier, world.random);
			if (decreasedAmountAfterEfficiencyMod > 0) {
				spiritInstillerBlockEntity.getItem(0).shrink(decreasedAmountAfterEfficiencyMod);
			}
			
			List<IngredientStack> ingredientStacks = recipe.getIngredientStacks();
			
			// first side ingredient
			int amountAfterEfficiencyModFirst = Support.getIntFromDecimalWithChance(ingredientStacks.get(SpiritInstillerRecipe.FIRST_INGREDIENT).getCount() * efficiencyModifier, world.random);
			int amountAfterEfficiencyModSecond = Support.getIntFromDecimalWithChance(ingredientStacks.get(SpiritInstillerRecipe.SECOND_INGREDIENT).getCount() * efficiencyModifier, world.random);
			boolean leftIsFirstIngredient = ingredientStacks.get(SpiritInstillerRecipe.FIRST_INGREDIENT).test(leftBowl.getItem(0));
			Vec3 particlePos = new Vec3(spiritInstillerBlockEntity.worldPosition.getX() + 0.5, spiritInstillerBlockEntity.worldPosition.getY() + 1, spiritInstillerBlockEntity.worldPosition.getZ() + 0.5);
			if (leftIsFirstIngredient) {
				if (amountAfterEfficiencyModFirst > 0) {
					leftBowl.decrementBowlStack(particlePos, amountAfterEfficiencyModFirst, true);
				}
				if (amountAfterEfficiencyModSecond > 0) {
					rightBowl.decrementBowlStack(particlePos, amountAfterEfficiencyModSecond, true);
				}
			} else {
				if (amountAfterEfficiencyModFirst > 0) {
					rightBowl.decrementBowlStack(particlePos, amountAfterEfficiencyModFirst, true);
				}
				if (amountAfterEfficiencyModSecond > 0) {
					leftBowl.decrementBowlStack(particlePos, amountAfterEfficiencyModSecond, true);
				}
			}
		}
	}
	
	public static void playCraftingFinishedEffects(@NotNull SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
		Level world = spiritInstillerBlockEntity.getLevel();
		world.playSound(null, spiritInstillerBlockEntity.worldPosition, SpectrumSoundEvents.SPIRIT_INSTILLER_CRAFTING_FINISHED, SoundSource.BLOCKS, 1.0F, 1.0F);
		SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) world,
				new Vec3(spiritInstillerBlockEntity.worldPosition.getX() + 0.5D, spiritInstillerBlockEntity.worldPosition.getY() + 0.5, spiritInstillerBlockEntity.worldPosition.getZ() + 0.5D),
				SpectrumParticleTypes.LIGHT_BLUE_CRAFTING, 75, new Vec3(0.5D, 0.5D, 0.5D),
				new Vec3(0.1D, -0.1D, 0.1D));
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.craftingTime = nbt.getShort("CraftingTime");
		this.craftingTimeTotal = nbt.getShort("CraftingTimeTotal");
		this.inventoryChanged = true;
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
		if (nbt.contains("MultiblockRotation")) {
			try {
				this.multiblockRotation = Rotation.valueOf(nbt.getString("MultiblockRotation").toUpperCase(Locale.ROOT));
			} catch (Exception e) {
				this.multiblockRotation = Rotation.NONE;
			}
		}
		
		this.currentRecipe = null;
		if (nbt.contains("CurrentRecipe")) {
			String recipeString = nbt.getString("CurrentRecipe");
			if (!recipeString.isEmpty() && SpectrumCommon.minecraftServer != null) {
				Optional<? extends Recipe<?>> optionalRecipe = SpectrumCommon.minecraftServer.getRecipeManager().byKey(new ResourceLocation(recipeString));
				if (optionalRecipe.isPresent() && optionalRecipe.get() instanceof SpiritInstillerRecipe spiritInstillerRecipe) {
					this.currentRecipe = spiritInstillerRecipe;
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
		nbt.putShort("CraftingTime", (short) this.craftingTime);
		nbt.putShort("CraftingTimeTotal", (short) this.craftingTimeTotal);
		nbt.putString("MultiblockRotation", this.multiblockRotation.toString());
		if (this.upgrades != null) {
			nbt.put("Upgrades", this.upgrades.toNbt());
		}
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
		if (this.currentRecipe != null) {
			nbt.putString("CurrentRecipe", this.currentRecipe.getId().toString());
		}
	}
	
	
	// Called when the chunk is first loaded to initialize this on the clients
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbtCompound = new CompoundTag();
		ContainerHelper.saveAllItems(nbtCompound, this.getItems());
		nbtCompound.putShort("CraftingTime", (short) this.craftingTime);
		nbtCompound.putShort("CraftingTimeTotal", (short) this.craftingTimeTotal);
		nbtCompound.putString("MultiblockRotation", this.multiblockRotation.toString());
		if (this.currentRecipe != null && checkRecipeRequirements(level, this.worldPosition, this)) {
			nbtCompound.putString("CurrentRecipe", this.currentRecipe.getId().toString());
		}
		return nbtCompound;
	}
	
	private void doInstillerParticles(@NotNull Level world) {
		Optional<DyeColor> stackColor = ItemColors.ITEM_COLORS.getMapping(this.getItem(0).getItem());
		
		if (stackColor.isPresent()) {
			RandomSource random = world.random;
			ParticleOptions particleEffect = SpectrumParticleTypes.getSparkleRisingParticle(stackColor.get());
			world.addParticle(particleEffect,
					worldPosition.getX() + 0.25 + random.nextDouble() * 0.5,
					worldPosition.getY() + 0.75,
					worldPosition.getZ() + 0.25 + random.nextDouble() * 0.5,
					0.02 - random.nextDouble() * 0.04,
					0.01 + random.nextDouble() * 0.05,
					0.02 - random.nextDouble() * 0.04);
		}
	}
	
	private void doChimeParticles(@NotNull Level world) {
		doChimeInstillingParticles(world, worldPosition.offset(getItemBowlHorizontalPositionOffset(false).above(3)));
		doChimeInstillingParticles(world, worldPosition.offset(getItemBowlHorizontalPositionOffset(true).above(3)));
	}
	
	public void doChimeInstillingParticles(@NotNull Level world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() instanceof GemstoneChimeBlock gemstoneChimeBlock) {
			RandomSource random = world.random;
			ParticleOptions particleEffect = gemstoneChimeBlock.getParticleEffect();
			for (int i = 0; i < 16; i++) {
				world.addParticle(particleEffect,
						pos.getX() + 0.25 + random.nextDouble() * 0.5,
						pos.getY() + 0.15 + random.nextDouble() * 0.5,
						pos.getZ() + 0.25 + random.nextDouble() * 0.5,
						0.06 - random.nextDouble() * 0.12,
						-0.1 - random.nextDouble() * 0.05,
						0.06 - random.nextDouble() * 0.12);
			}
		}
	}
	
	private void doItemBowlOrbs(@NotNull Level world) {
		BlockPos itemBowlPos = worldPosition.offset(getItemBowlHorizontalPositionOffset(false).above());
		BlockEntity blockEntity = world.getBlockEntity(itemBowlPos);
		if (blockEntity instanceof ItemBowlBlockEntity itemBowlBlockEntity) {
			itemBowlBlockEntity.spawnOrbParticles(new Vec3(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + 0.5));
		}
		
		itemBowlPos = worldPosition.offset(getItemBowlHorizontalPositionOffset(true).above());
		blockEntity = world.getBlockEntity(itemBowlPos);
		if (blockEntity instanceof ItemBowlBlockEntity itemBowlBlockEntity) {
			itemBowlBlockEntity.spawnOrbParticles(new Vec3(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + 0.5));
		}
	}
	
	public Vec3i getItemBowlHorizontalPositionOffset(boolean right) {
		if (this.multiblockRotation == Rotation.NONE || this.multiblockRotation == Rotation.CLOCKWISE_180) {
			return itemBowlOffsetsVertical.get(right ? 1 : 0);
		} else {
			return itemBowlOffsetsHorizontal.get(right ? 1 : 0);
		}
	}
	
	// UPGRADEABLE
	@Override
	public void resetUpgrades() {
		this.upgrades = null;
		this.setChanged();
	}
	
	@Override
	public void calculateUpgrades() {
		this.upgrades = Upgradeable.calculateUpgradeMods2(level, worldPosition, multiblockRotation, 4, 1, this.ownerUUID);
		this.setChanged();
	}
	
	@Override
	public UpgradeHolder getUpgradeHolder() {
		return this.upgrades;
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
		this.setChanged();
	}
	
	public Rotation getMultiblockRotation() {
		return multiblockRotation;
	}
	
	public void setMultiblockRotation(Rotation blockRotation) {
		this.multiblockRotation = blockRotation;
		this.upgrades = null;
		this.setChanged();
	}
	
	@Override
	public void inventoryChanged() {
		super.inventoryChanged();
		this.inventoryChanged = true;
		this.autoCraftingInventory.clearContent();
		setChanged();
	}
	
}
