package de.dafuqs.spectrum.recipe.crystallarieum;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrystallarieumRecipe extends GatedSpectrumRecipe {

	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("unlocks/blocks/crystallarieum");

	protected final Ingredient inputIngredient;
	protected final List<BlockState> growthStages;
	protected final int secondsPerGrowthStage;
	protected final InkColor inkColor;
	protected final int inkPerSecond;
	protected final boolean growsWithoutCatalyst;
	protected final List<CrystallarieumCatalyst> catalysts;
	protected final List<ItemStack> additionalOutputs; // these aren't actual outputs, but recipe managers will treat it as such, showing this recipe as a way to get them. Use for drops of the growth blocks, for example

	protected final static Map<Ingredient, CrystallarieumRecipe> ingredientMap = new HashMap<>();
	protected final static Map<BlockState, CrystallarieumRecipe> stateMap = new HashMap<>();

	public CrystallarieumRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Ingredient inputIngredient, List<BlockState> growthStages, int secondsPerGrowthStage, InkColor inkColor, int inkPerSecond, boolean growsWithoutCatalyst, List<CrystallarieumCatalyst> catalysts, List<ItemStack> additionalOutputs) {
		super(id, group, secret, requiredAdvancementIdentifier);

		this.inputIngredient = inputIngredient;
		this.growthStages = growthStages;
		this.secondsPerGrowthStage = secondsPerGrowthStage;
		this.inkColor = inkColor;
		this.inkPerSecond = inkPerSecond;
		this.growsWithoutCatalyst = growsWithoutCatalyst;
		this.catalysts = catalysts;
		this.additionalOutputs = additionalOutputs;
		
		ingredientMap.put(inputIngredient, this);
		for (BlockState growthStage : growthStages) {
			stateMap.put(growthStage, this);
		}
		
		registerInToastManager(getType(), this);
	}
	
	@Nullable
	public static CrystallarieumRecipe getRecipeForStack(ItemStack itemStack) {
		for (Map.Entry<Ingredient, CrystallarieumRecipe> entry : ingredientMap.entrySet()) {
			if (entry.getKey().test(itemStack)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	@Nullable
	public static CrystallarieumRecipe getRecipeForState(BlockState state) {
		return stateMap.getOrDefault(state, null);
	}
	
	public static void clearCache() {
		ingredientMap.clear();
		stateMap.clear();
	}
	
	@Override
	public boolean matches(Container inv, Level world) {
		return this.inputIngredient.test(inv.getItem(0));
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
		List<BlockState> states = getGrowthStages();
		return states.get(states.size() - 1).getBlock().asItem().getDefaultInstance();
	}
	
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(SpectrumBlocks.CRYSTALLARIEUM);
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.CRYSTALLARIEUM_RECIPE_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.CRYSTALLARIEUM;
	}
	
	@Override
	public ResourceLocation getRecipeTypeUnlockIdentifier() {
		return UNLOCK_IDENTIFIER;
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.CRYSTALLARIEUM_ID;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> defaultedList = NonNullList.create();
		defaultedList.add(this.inputIngredient);
		return defaultedList;
	}
	
	public Ingredient getIngredientStack() {
		return this.inputIngredient;
	}
	
	public CrystallarieumCatalyst getCatalyst(ItemStack itemStack) {
		for (CrystallarieumCatalyst catalyst : this.catalysts) {
			if (catalyst.ingredient.test(itemStack)) {
				return catalyst;
			}
		}
		return CrystallarieumCatalyst.EMPTY;
	}
	
	public List<BlockState> getGrowthStages() {
		return growthStages;
	}
	
	public int getSecondsPerGrowthStage() {
		return secondsPerGrowthStage;
	}
	
	public InkColor getInkColor() {
		return inkColor;
	}
	
	public int getInkPerSecond() {
		return inkPerSecond;
	}

	public boolean growsWithoutCatalyst() {
		return growsWithoutCatalyst;
	}

	public List<CrystallarieumCatalyst> getCatalysts() {
		return this.catalysts;
	}
	
	public List<ItemStack> getAdditionalOutputs(RegistryAccess registryManager) {
		return additionalOutputs;
	}

}
