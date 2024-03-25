package de.dafuqs.spectrum.recipe.fusion_shrine;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.MultiblockCrafter;
import de.dafuqs.spectrum.api.predicate.world.WorldConditionPredicate;
import de.dafuqs.spectrum.api.recipe.FusionShrineRecipeWorldEffect;
import de.dafuqs.spectrum.blocks.fusion_shrine.FusionShrineBlockEntity;
import de.dafuqs.spectrum.blocks.upgrade.Upgradeable;
import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.recipe.GatedStackSpectrumRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class FusionShrineRecipe extends GatedStackSpectrumRecipe {
	
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("build_fusion_shrine");
	
	protected final List<IngredientStack> craftingInputs;
	protected final Fluid fluidInput;
	protected final ItemStack output;
	protected final float experience;
	protected final int craftingTime;
	// since there are a few recipes that are basically compacting recipes
	// they could be crafted ingots>block and block>ingots back
	// In that case:
	// - the player should not get XP
	// - Yield upgrades disabled (item multiplication)
	protected final boolean yieldUpgradesDisabled;
	protected final boolean playCraftingFinishedEffects;
	
	protected final List<WorldConditionPredicate> worldConditions;
	@NotNull
	protected final FusionShrineRecipeWorldEffect startWorldEffect;
	@NotNull
	protected final List<FusionShrineRecipeWorldEffect> duringWorldEffects;
	@NotNull
	protected final FusionShrineRecipeWorldEffect finishWorldEffect;
	@Nullable
	protected final Component description;
	// copy all nbt data from the first stack in the ingredients to the output stack
	protected final boolean copyNbt;
	
	public FusionShrineRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier,
							  List<IngredientStack> craftingInputs, Fluid fluidInput, ItemStack output, float experience, int craftingTime, boolean yieldUpgradesDisabled, boolean playCraftingFinishedEffects, boolean copyNbt,
							  List<WorldConditionPredicate> worldConditions, @NotNull FusionShrineRecipeWorldEffect startWorldEffect, @NotNull List<FusionShrineRecipeWorldEffect> duringWorldEffects, @NotNull FusionShrineRecipeWorldEffect finishWorldEffect, @Nullable Component description) {
		super(id, group, secret, requiredAdvancementIdentifier);
		
		this.craftingInputs = craftingInputs;
		this.fluidInput = fluidInput;
		this.output = output;
		this.experience = experience;
		this.craftingTime = craftingTime;
		this.yieldUpgradesDisabled = yieldUpgradesDisabled;
		this.playCraftingFinishedEffects = playCraftingFinishedEffects;
		
		this.worldConditions = worldConditions;
		this.startWorldEffect = startWorldEffect;
		this.duringWorldEffects = duringWorldEffects;
		this.finishWorldEffect = finishWorldEffect;
		this.description = description;
		this.copyNbt = copyNbt;

		registerInToastManager(getType(), this);
	}
	
	/**
	 * Only tests the items. The required fluid has to be tested manually by the crafting block
	 */
	@Override
	public boolean matches(Container inv, Level world) {
		return matchIngredientStacksExclusively(inv, getIngredientStacks());
	}
	
	@Override
	public ItemStack assemble(Container inv, RegistryAccess drm) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess registryManager) {
		return output;
	}
	
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(SpectrumBlocks.FUSION_SHRINE_BASALT);
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.FUSION_SHRINE_RECIPE_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.FUSION_SHRINE;
	}
	
	@Override
	public List<IngredientStack> getIngredientStacks() {
		return this.craftingInputs;
	}
	
	public float getExperience() {
		return experience;
	}
	
	/**
	 * Returns a boolean depending on if the recipes condition is set
	 * This can be always true, a specific day or moon phase, or weather.
	 */
	public boolean areConditionMetCurrently(ServerLevel world, BlockPos pos) {
		for (WorldConditionPredicate worldCondition : this.worldConditions) {
			if (!worldCondition.test(world, pos)) {
				return false;
			}
		}
		return true;
	}
	
	public Fluid getFluidInput() {
		return this.fluidInput;
	}
	
	public int getCraftingTime() {
		return this.craftingTime;
	}
	
	/**
	 * @param tick The crafting tick if the fusion shrine recipe
	 * @return The effect that should be played for the given recipe tick
	 */
	public FusionShrineRecipeWorldEffect getWorldEffectForTick(int tick, int totalTicks) {
		if (tick == 1) {
			return this.startWorldEffect;
		} else if (tick == totalTicks) {
			return this.finishWorldEffect;
		} else {
			if (this.duringWorldEffects.size() == 0) {
				return null;
			} else if (this.duringWorldEffects.size() == 1) {
				return this.duringWorldEffects.get(0);
			} else {
				// we really have to calculate the current effect, huh?
				float parts = (float) totalTicks / this.duringWorldEffects.size();
				int index = (int) (tick / (parts));
				FusionShrineRecipeWorldEffect effect = this.duringWorldEffects.get(index);
				if (effect.isOneTimeEffect()) {
					if (index != (int) parts) {
						return null;
					}
				}
				return effect;
			}
		}
	}
	
	public Optional<Component> getDescription() {
		if (this.description == null) {
			return Optional.empty();
		} else {
			return Optional.of(this.description);
		}
	}

	@Override
	public ResourceLocation getRecipeTypeUnlockIdentifier() {
		return UNLOCK_IDENTIFIER;
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.FUSION_SHRINE_ID;
	}
	
	public void craft(Level world, FusionShrineBlockEntity fusionShrineBlockEntity) {
		ItemStack firstStack = ItemStack.EMPTY;
		
		int maxAmount = 1;
		if (!getResultItem(world.registryAccess()).isEmpty()) {
			maxAmount = getResultItem(world.registryAccess()).getMaxStackSize();
			for (IngredientStack ingredientStack : getIngredientStacks()) {
				for (int i = 0; i < fusionShrineBlockEntity.getContainerSize(); i++) {
					ItemStack currentStack = fusionShrineBlockEntity.getItem(i);
					if (ingredientStack.test(currentStack)) {
						if (firstStack.isEmpty()) {
							firstStack = currentStack;
						}
						int ingredientStackAmount = ingredientStack.getCount();
						maxAmount = Math.min(maxAmount, currentStack.getCount() / ingredientStackAmount);
						break;
					}
				}
			}

			if (maxAmount > 0) {
				double efficiencyModifier = fusionShrineBlockEntity.getUpgradeHolder().getEffectiveValue(Upgradeable.UpgradeType.EFFICIENCY);
				decrementIngredients(world, fusionShrineBlockEntity, maxAmount, efficiencyModifier);
			}
		} else {
			for (IngredientStack ingredientStack : getIngredientStacks()) {
				double efficiencyModifier = fusionShrineBlockEntity.getUpgradeHolder().getEffectiveValue(Upgradeable.UpgradeType.EFFICIENCY);

				for (int i = 0; i < fusionShrineBlockEntity.getContainerSize(); i++) {
					ItemStack currentStack = fusionShrineBlockEntity.getItem(i);
					if (ingredientStack.test(currentStack)) {
						int reducedAmountAfterMod = Support.getIntFromDecimalWithChance(ingredientStack.getCount() / efficiencyModifier, world.random);
						currentStack.shrink(reducedAmountAfterMod);
						break;
					}
				}
			}
		}
		
		ItemStack output = getResultItem(world.registryAccess()).copy();
		if (this.copyNbt) {
			// this overrides all nbt data, that are not nested compounds (like lists)
			CompoundTag sourceNbt = firstStack.getTag();
			if (sourceNbt != null) {
				sourceNbt = sourceNbt.copy();
				sourceNbt.remove(ItemStack.TAG_DAMAGE);
				output.setTag(sourceNbt);
				// so we need to restore all previous enchantments that the original item had and are still applicable to the new item
				output = SpectrumEnchantmentHelper.clearAndCombineEnchantments(output, false, false, getResultItem(world.registryAccess()), firstStack);
			}
		}
		
		spawnCraftingResultAndXP(world, fusionShrineBlockEntity, output, maxAmount); // spawn results
	}
	
	private void decrementIngredients(Level world, FusionShrineBlockEntity fusionShrineBlockEntity, int recipesCrafted, double efficiencyModifier) {
		for (IngredientStack ingredientStack : getIngredientStacks()) {
			for (int i = 0; i < fusionShrineBlockEntity.getContainerSize(); i++) {
				ItemStack currentStack = fusionShrineBlockEntity.getItem(i);
				if (ingredientStack.test(currentStack)) {
					int reducedAmount = recipesCrafted * ingredientStack.getCount();
					int reducedAmountAfterMod = efficiencyModifier == 1 ? reducedAmount : Support.getIntFromDecimalWithChance(reducedAmount / efficiencyModifier, world.random);
					
					ItemStack currentRemainder = currentStack.getRecipeRemainder();
					currentStack.shrink(reducedAmountAfterMod);
					
					if (!currentRemainder.isEmpty()) {
						currentRemainder = currentRemainder.copy();
						currentRemainder.setCount(reducedAmountAfterMod);
						InventoryHelper.smartAddToInventory(currentRemainder, fusionShrineBlockEntity, null);
					}
					
					break;
				}
			}
		}
	}
	
	protected void spawnCraftingResultAndXP(@NotNull Level world, @NotNull FusionShrineBlockEntity fusionShrineBlockEntity, @NotNull ItemStack stack, int recipeCount) {
		int resultAmountBeforeMod = recipeCount * stack.getCount();
		double yieldModifier = yieldUpgradesDisabled ? 1.0 : fusionShrineBlockEntity.getUpgradeHolder().getEffectiveValue(Upgradeable.UpgradeType.YIELD);
		int resultAmountAfterMod = Support.getIntFromDecimalWithChance(resultAmountBeforeMod * yieldModifier, world.random);
		
		int intExperience = Support.getIntFromDecimalWithChance(recipeCount * experience, world.random);
		MultiblockCrafter.spawnItemStackAsEntitySplitViaMaxCount(world, fusionShrineBlockEntity.getBlockPos().above(2), stack, resultAmountAfterMod, MultiblockCrafter.RECIPE_STACK_VELOCITY);
		
		if (experience > 0) {
			MultiblockCrafter.spawnExperience(world, fusionShrineBlockEntity.getBlockPos(), intExperience);
		}
		
		//only triggered on server side. Therefore, has to be sent to client via S2C packet
		fusionShrineBlockEntity.grantPlayerFusionCraftingAdvancement(this, intExperience);
	}
	
	public boolean shouldPlayCraftingFinishedEffects() {
		return this.playCraftingFinishedEffects;
	}
	
}
