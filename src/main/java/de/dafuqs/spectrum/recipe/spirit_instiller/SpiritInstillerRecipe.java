package de.dafuqs.spectrum.recipe.spirit_instiller;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.MultiblockCrafter;
import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.blocks.memory.MemoryItem;
import de.dafuqs.spectrum.blocks.spirit_instiller.SpiritInstillerBlockEntity;
import de.dafuqs.spectrum.blocks.upgrade.Upgradeable;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.recipe.GatedStackSpectrumRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItemTags;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class SpiritInstillerRecipe extends GatedStackSpectrumRecipe {
	
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("midgame/build_spirit_instiller_structure");
	
	public static final int CENTER_INGREDIENT = 0;
	public static final int FIRST_INGREDIENT = 1;
	public static final int SECOND_INGREDIENT = 2;
	
	protected final IngredientStack centerIngredient;
	protected final IngredientStack bowlIngredient1;
	protected final IngredientStack bowlIngredient2;
	protected final ItemStack outputItemStack;
	
	protected final int craftingTime;
	protected final float experience;
	protected final boolean noBenefitsFromYieldAndEfficiencyUpgrades;
	
	public SpiritInstillerRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier,
	                             IngredientStack centerIngredient, IngredientStack bowlIngredient1, IngredientStack bowlIngredient2, ItemStack outputItemStack, int craftingTime, float experience, boolean noBenefitsFromYieldAndEfficiencyUpgrades) {
		
		super(id, group, secret, requiredAdvancementIdentifier);
		
		this.centerIngredient = centerIngredient;
		this.bowlIngredient1 = bowlIngredient1;
		this.bowlIngredient2 = bowlIngredient2;
		this.outputItemStack = outputItemStack;
		this.craftingTime = craftingTime;
		this.experience = experience;
		this.noBenefitsFromYieldAndEfficiencyUpgrades = noBenefitsFromYieldAndEfficiencyUpgrades;
		
		registerInToastManager(getType(), this);
	}
	
	@Override
	public boolean matches(Container inv, Level world) {
		List<IngredientStack> ingredientStacks = getIngredientStacks();
		if (inv.getContainerSize() > 2) {
			if (ingredientStacks.get(CENTER_INGREDIENT).test(inv.getItem(CENTER_INGREDIENT))) {
				if (ingredientStacks.get(FIRST_INGREDIENT).test(inv.getItem(FIRST_INGREDIENT))) {
					return ingredientStacks.get(SECOND_INGREDIENT).test(inv.getItem(SECOND_INGREDIENT));
				} else if (ingredientStacks.get(FIRST_INGREDIENT).test(inv.getItem(SECOND_INGREDIENT))) {
					return ingredientStacks.get(SECOND_INGREDIENT).test(inv.getItem(FIRST_INGREDIENT));
				}
			}
		}
		return false;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess registryManager) {
		return outputItemStack.copy();
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.SPIRIT_INSTILLING_SERIALIZER;
	}
	
	@Override
	public List<IngredientStack> getIngredientStacks() {
		NonNullList<IngredientStack> defaultedList = NonNullList.create();
		defaultedList.add(this.centerIngredient);
		defaultedList.add(this.bowlIngredient1);
		defaultedList.add(this.bowlIngredient2);
		return defaultedList;
	}
	
	@Override
	public ItemStack assemble(Container inv, RegistryAccess drm) {
		ItemStack resultStack = ItemStack.EMPTY;
		if (inv instanceof SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
			Upgradeable.UpgradeHolder upgradeHolder = spiritInstillerBlockEntity.getUpgradeHolder();
			Level world = spiritInstillerBlockEntity.getLevel();
			BlockPos pos = spiritInstillerBlockEntity.getBlockPos();
			
			resultStack = getResultItem(drm).copy();
			
			// Yield upgrade
			if (!areYieldAndEfficiencyUpgradesDisabled() && upgradeHolder.getEffectiveValue(Upgradeable.UpgradeType.YIELD) != 1.0) {
				int resultCountMod = Support.getIntFromDecimalWithChance(resultStack.getCount() * upgradeHolder.getEffectiveValue(Upgradeable.UpgradeType.YIELD), world.random);
				resultStack.setCount(resultCountMod);
			}

			if (resultStack.is(SpectrumBlocks.MEMORY.asItem())) {
				boolean makeUnrecognizable = spiritInstillerBlockEntity.getItem(0).is(SpectrumItemTags.MEMORY_BONDING_AGENTS_CONCEALABLE);
				if (makeUnrecognizable) {
					MemoryItem.makeUnrecognizable(resultStack);
				}
			}
			
			spawnXPAndGrantAdvancements(resultStack, spiritInstillerBlockEntity, upgradeHolder, world, pos);
		}
		
		return resultStack;
	}
	
	// Calculate and spawn experience
	protected void spawnXPAndGrantAdvancements(ItemStack resultStack, SpiritInstillerBlockEntity spiritInstillerBlockEntity, Upgradeable.UpgradeHolder upgradeHolder, Level world, BlockPos pos) {
		int awardedExperience = 0;
		if (getExperience() > 0) {
			double experienceModifier = upgradeHolder.getEffectiveValue(Upgradeable.UpgradeType.EXPERIENCE);
			float recipeExperienceBeforeMod = getExperience();
			awardedExperience = Support.getIntFromDecimalWithChance(recipeExperienceBeforeMod * experienceModifier, world.random);
			MultiblockCrafter.spawnExperience(world, pos.above(), awardedExperience);
		}
		
		// Run Advancement trigger
		grantPlayerSpiritInstillingAdvancementCriterion(spiritInstillerBlockEntity.getOwnerUUID(), resultStack, awardedExperience);
	}
	
	protected static void grantPlayerSpiritInstillingAdvancementCriterion(UUID playerUUID, ItemStack resultStack, int experience) {
		ServerPlayer serverPlayerEntity = (ServerPlayer) PlayerOwned.getPlayerEntityIfOnline(playerUUID);
		if (serverPlayerEntity != null) {
			SpectrumAdvancementCriteria.SPIRIT_INSTILLER_CRAFTING.trigger(serverPlayerEntity, resultStack, experience);
		}
	}
	
	public float getExperience() {
		return experience;
	}
	
	public int getCraftingTime() {
		return craftingTime;
	}
	
	public boolean areYieldAndEfficiencyUpgradesDisabled() {
		return noBenefitsFromYieldAndEfficiencyUpgrades;
	}
	
	@Override
	public ResourceLocation getRecipeTypeUnlockIdentifier() {
		return UNLOCK_IDENTIFIER;
	}
	
	@Override
	public boolean canPlayerCraft(Player playerEntity) {
		return AdvancementHelper.hasAdvancement(playerEntity, UNLOCK_IDENTIFIER) && AdvancementHelper.hasAdvancement(playerEntity, this.requiredAdvancementIdentifier);
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.SPIRIT_INSTILLING_ID;
	}
	
	public boolean canCraftWithStacks(Container inventory) {
		return true;
	}
	
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(SpectrumBlocks.SPIRIT_INSTILLER);
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.SPIRIT_INSTILLING;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 3;
	}
	
}
