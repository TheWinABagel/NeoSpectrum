package de.dafuqs.spectrum.recipe.potion_workshop;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.api.energy.InkPoweredStatusEffectInstance;
import de.dafuqs.spectrum.api.item.InkPoweredPotionFillable;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumPotions;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionWorkshopBrewingRecipe extends PotionWorkshopRecipe {
	
	public static final int BASE_POTION_COUNT_ON_BREWING = 3;
	public static final int BASE_ARROW_COUNT_ON_BREWING = 12;
	
	/**
	 * When potionMod.potentDecreasingEffect is set each status effect is split into separate
	 * instances defined in this list. First value is the new effects new potency mod, second the duration
	 */
	protected static final List<Tuple<Float, Float>> SPLIT_EFFECT_POTENCY_AND_DURATION = new ArrayList<>() {{
		add(new Tuple<>(2.0F, 0.15F));
		add(new Tuple<>(0.75F, 0.5F));
		add(new Tuple<>(0.25F, 1.0F));
	}};
	
	public static final Map<MobEffect, MobEffect> negativeToPositiveEffect = new HashMap<>() {{
		put(MobEffects.BAD_OMEN, MobEffects.HERO_OF_THE_VILLAGE);
		put(MobEffects.HUNGER, MobEffects.SATURATION);
		put(MobEffects.HARM, MobEffects.HEAL);
		put(MobEffects.DIG_SLOWDOWN, MobEffects.DIG_SPEED);
		put(MobEffects.MOVEMENT_SLOWDOWN, MobEffects.MOVEMENT_SPEED);
		put(MobEffects.UNLUCK, MobEffects.LUCK);
		put(MobEffects.WEAKNESS, MobEffects.DAMAGE_BOOST);
		put(MobEffects.WITHER, MobEffects.REGENERATION);
		put(SpectrumStatusEffects.STIFFNESS, SpectrumStatusEffects.SWIFTNESS);
		put(SpectrumStatusEffects.DENSITY, SpectrumStatusEffects.LIGHTWEIGHT);
	}};
	
	public static @Nullable PotionWorkshopBrewingRecipe getPositiveRecipe(@NotNull MobEffect statusEffect) {
		if (statusEffect.getCategory() == MobEffectCategory.HARMFUL) {
			MobEffect positiveEffect = negativeToPositiveEffect.getOrDefault(statusEffect, null);
			if (positiveEffect == null) {
				return null;
			}
			for (PotionWorkshopBrewingRecipe positiveRecipe : positiveRecipes) {
				if (positiveRecipe.recipeData.statusEffect() == positiveEffect) {
					return positiveRecipe;
				}
			}
		}
		return null;
	}
	
	public static final List<PotionWorkshopBrewingRecipe> positiveRecipes = new ArrayList<>();
	public static final List<PotionWorkshopBrewingRecipe> negativeRecipes = new ArrayList<>();
	
	public final PotionRecipeEffect recipeData;
	
	protected ItemStack cachedOutput;
	
	public PotionWorkshopBrewingRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, int craftingTime,
									   IngredientStack ingredient1, IngredientStack ingredient2, IngredientStack ingredient3, PotionRecipeEffect recipeData) {
		
		super(id, group, secret, requiredAdvancementIdentifier, craftingTime, recipeData.statusEffect().getColor(), ingredient1, ingredient2, ingredient3);
		this.recipeData = recipeData;
		
		registerInToastManager(getType(), this);
		
		// remember one of each status effect recipe for quick lookup
		if (recipeData.statusEffect().getCategory() == MobEffectCategory.BENEFICIAL) {
			for (PotionWorkshopBrewingRecipe ae : positiveRecipes) {
				if (ae.recipeData.statusEffect() == recipeData.statusEffect()) {
					return;
				}
			}
			positiveRecipes.add(this);
		} else if (recipeData.statusEffect().getCategory() == MobEffectCategory.HARMFUL) {
			for (PotionWorkshopBrewingRecipe ae : negativeRecipes) {
				if (ae.recipeData.statusEffect() == recipeData.statusEffect()) {
					return;
				}
			}
			negativeRecipes.add(this);
		}
	}
	
	@Override
	public boolean isValidBaseIngredient(ItemStack itemStack) {
		return recipeData.applicableToPotions() && itemStack.is(Items.GLASS_BOTTLE)
				|| recipeData.applicableToTippedArrows() && itemStack.is(Items.ARROW)
				|| itemStack.getItem() instanceof InkPoweredPotionFillable fillable && ((fillable.isWeapon() && recipeData.applicableToWeapons()) || recipeData.applicableToPotionFillabes());
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.POTION_WORKSHOP_BREWING_RECIPE_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.POTION_WORKSHOP_BREWING;
	}
	
	@Override
	public boolean usesReagents() {
		return true;
	}
	
	@Override
	public int getMinOutputCount(ItemStack baseItemStack) {
		return baseItemStack.is(Items.GLASS_BOTTLE) ? BASE_POTION_COUNT_ON_BREWING : 1;
	}
	
	@Override
	public List<IngredientStack> getIngredientStacks() {
		NonNullList<IngredientStack> defaultedList = NonNullList.create();
		defaultedList.add(IngredientStack.ofStacks(SpectrumItems.MERMAIDS_GEM.getDefaultInstance()));
		defaultedList.add(IngredientStack.ofStacks(Items.GLASS_BOTTLE.getDefaultInstance()));
		addIngredientStacks(defaultedList);
		return defaultedList;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess registryManager) {
		if (this.cachedOutput == null) {
			this.cachedOutput = getPotion(Items.POTION.getDefaultInstance(), new PotionMod(), null, RandomSource.create());
		}
		return this.cachedOutput;
	}
	
	@Override
	public ItemStack assemble(Container inventory, RegistryAccess drm) {
		ItemStack stack = new ItemStack(Items.POTION);
		PotionUtils.setCustomEffects(stack, List.of(new MobEffectInstance(recipeData.statusEffect(), recipeData.baseDurationTicks())));
		return stack;
	}
	
	public ItemStack getPotion(ItemStack stack, PotionMod potionMod, PotionWorkshopBrewingRecipe lastRecipe, RandomSource random) {
		List<InkPoweredStatusEffectInstance> effects = generateEffects(stack, potionMod, lastRecipe, random);
		
		// potion type
		ItemStack itemStack;
		if (potionMod.makeSplashing) {
			itemStack = potionMod.makeLingering ? new ItemStack(Items.LINGERING_POTION) : new ItemStack(Items.SPLASH_POTION);
		} else {
			itemStack = new ItemStack(Items.POTION);
		}
		
		// apply to potion
		if (effects.isEmpty()) {
			// no effects: thick potion
			PotionUtils.setPotion(itemStack, Potions.THICK);
		} else {
			PotionUtils.setPotion(itemStack, SpectrumPotions.PIGMENT_POTION);
		}
		setCustomPotionEffects(itemStack, potionMod, effects);
		
		if (potionMod.additionalDrinkDurationTicks != 0) {
			CompoundTag compound = itemStack.getOrCreateTag();
			itemStack.setTag(compound);
		}
		
		return itemStack;
	}
	
	public ItemStack getTippedArrows(ItemStack stack, PotionMod potionMod, PotionWorkshopBrewingRecipe lastRecipe, int amount, RandomSource random) {
		List<InkPoweredStatusEffectInstance> effects = generateEffects(stack, potionMod, lastRecipe, random);
		
		ItemStack itemStack = new ItemStack(Items.TIPPED_ARROW, amount);
		if (effects.isEmpty()) {
			PotionUtils.setPotion(itemStack, Potions.THICK);
		} else {
			PotionUtils.setPotion(itemStack, SpectrumPotions.PIGMENT_POTION);
			setCustomPotionEffects(itemStack, potionMod, effects);
		}
		
		return itemStack;
	}
	
	public void fillPotionFillable(ItemStack stack, PotionMod potionMod, PotionWorkshopBrewingRecipe lastRecipe, RandomSource random) {
		if (stack.getItem() instanceof InkPoweredPotionFillable inkPoweredPotionFillable) {
			List<InkPoweredStatusEffectInstance> effects = generateEffects(stack, potionMod, lastRecipe, random);
			inkPoweredPotionFillable.addOrUpgradeEffects(stack, effects);
		}
	}
	
	private static void setCustomPotionEffects(ItemStack stack, PotionMod potionMod, List<InkPoweredStatusEffectInstance> effects) {
		List<MobEffectInstance> instances = new ArrayList<>();
		for (InkPoweredStatusEffectInstance e : effects) {
			instances.add(e.getStatusEffectInstance());
		}
		PotionUtils.setCustomEffects(stack, instances);
		for (InkPoweredStatusEffectInstance effect : effects) {
			if (effect.getColor() != -1) {
				CompoundTag nbtCompound = stack.getOrCreateTag();
				nbtCompound.putInt("CustomPotionColor", effect.getColor());
			}
		}
		CompoundTag nbtCompound = stack.getOrCreateTag();
		if (potionMod.unidentifiable) {
			nbtCompound.putBoolean("spectrum_unidentifiable", true); // used in PotionItemMixin
		}
	}
	
	private List<InkPoweredStatusEffectInstance> generateEffects(ItemStack baseIngredient, PotionMod potionMod, PotionWorkshopBrewingRecipe lastRecipe, RandomSource random) {
		List<InkPoweredStatusEffectInstance> effects = new ArrayList<>();
		
		addEffect(potionMod, random, effects); // main effect
		addLastEffect(baseIngredient, potionMod, lastRecipe, random, effects);
		addAdditionalEffects(baseIngredient, potionMod, random, effects);
		addRandomEffects(potionMod, random, effects);
		
		// split durations, if set
		if (potionMod.potentDecreasingEffect) {
			effects = applyPotentDecreasingEffect(effects, random);
		}
		
		return effects;
	}
	
	private static void addLastEffect(ItemStack baseIngredient, PotionMod potionMod, PotionWorkshopBrewingRecipe lastRecipe, RandomSource random, List<InkPoweredStatusEffectInstance> effects) {
		if (lastRecipe != null && (potionMod.chanceToAddLastEffect >= 1 || random.nextFloat() < potionMod.chanceToAddLastEffect) && lastRecipe.recipeData.isApplicableTo(baseIngredient, potionMod)) {
			PotionMod lastEffectMod = new PotionMod();
			lastEffectMod.potencyMultiplier = potionMod.lastEffectPotencyMultiplier;
			lastEffectMod.durationMultiplier = potionMod.lastEffectDurationMultiplier;
			lastEffectMod.modifyFrom(potionMod);
			lastRecipe.addEffect(lastEffectMod, random, effects);
		}
	}
	
	private static void addAdditionalEffects(ItemStack baseIngredient, PotionMod potionMod, RandomSource random, List<InkPoweredStatusEffectInstance> effects) {
		for (Tuple<PotionRecipeEffect, Float> entry : potionMod.additionalEffects) {
			if (random.nextFloat() < entry.getB() && entry.getA().isApplicableTo(baseIngredient, potionMod)) {
				InkPoweredStatusEffectInstance statusEffectInstance = entry.getA().getStatusEffectInstance(potionMod, random);
				if (statusEffectInstance != null) {
					effects.add(statusEffectInstance);
				}
			}
		}
	}
	
	private void addEffect(PotionMod potionMod, RandomSource random, List<InkPoweredStatusEffectInstance> effects) {
		if (potionMod.makeEffectsPositive) {
			PotionWorkshopBrewingRecipe positiveRecipe = getPositiveRecipe(recipeData.statusEffect());
			if (positiveRecipe != null) {
				effects.add(positiveRecipe.recipeData.getStatusEffectInstance(potionMod, random));
				return;
			}
		}
		
		InkPoweredStatusEffectInstance statusEffectInstance = recipeData.getStatusEffectInstance(potionMod, random);
		if (statusEffectInstance != null) {
			effects.add(statusEffectInstance);
		}
	}
	
	private void addRandomEffects(PotionMod potionMod, RandomSource random, List<InkPoweredStatusEffectInstance> effects) {
		// random positive ones
		if (!positiveRecipes.isEmpty()) {
			int additionalPositiveEffects = Support.getIntFromDecimalWithChance(potionMod.additionalRandomPositiveEffectCount, random);
			for (int i = 0; i < additionalPositiveEffects; i++) {
				int r;
				int tries = 0;
				PotionWorkshopBrewingRecipe selectedRecipe;
				do {
					r = random.nextInt(positiveRecipes.size());
					selectedRecipe = positiveRecipes.get(r);
					if (containsEffect(effects, selectedRecipe.recipeData.statusEffect())) {
						selectedRecipe = null;
						tries++;
					}
				} while (selectedRecipe == null && tries < 5);
				if (selectedRecipe != null) {
					InkPoweredStatusEffectInstance statusEffectInstance = selectedRecipe.recipeData.getStatusEffectInstance(potionMod, random);
					if (statusEffectInstance != null) {
						effects.add(statusEffectInstance);
					}
				}
			}
		}
		
		// random negative ones
		if (!negativeRecipes.isEmpty()) {
			int additionalNegativeEffects = Support.getIntFromDecimalWithChance(potionMod.additionalRandomNegativeEffectCount, random);
			for (int i = 0; i < additionalNegativeEffects; i++) {
				int r;
				int tries = 0;
				PotionWorkshopBrewingRecipe selectedRecipe;
				
				do {
					r = random.nextInt(negativeRecipes.size());
					selectedRecipe = negativeRecipes.get(r);
					
					if (potionMod.makeEffectsPositive) {
						selectedRecipe = this;
						PotionWorkshopBrewingRecipe positiveRecipe = getPositiveRecipe(recipeData.statusEffect());
						if (positiveRecipe != null) {
							selectedRecipe = positiveRecipe;
						}
					}
					if (containsEffect(effects, selectedRecipe.recipeData.statusEffect())) {
						selectedRecipe = null;
						tries++;
					}
				} while (selectedRecipe == null && tries < 5);
				if (selectedRecipe != null) {
					InkPoweredStatusEffectInstance statusEffectInstance = selectedRecipe.recipeData.getStatusEffectInstance(potionMod, random);
					if (statusEffectInstance != null) {
						effects.add(statusEffectInstance);
					}
				}
			}
		}
	}
	
	private boolean containsEffect(List<InkPoweredStatusEffectInstance> effects, MobEffect statusEffect) {
		for (InkPoweredStatusEffectInstance existingInstance : effects) {
			if (existingInstance.getStatusEffectInstance().getEffect() == statusEffect) {
				return true;
			}
		}
		return false;
	}
	
	private List<InkPoweredStatusEffectInstance> applyPotentDecreasingEffect(@NotNull List<InkPoweredStatusEffectInstance> statusEffectInstances, RandomSource random) {
		List<InkPoweredStatusEffectInstance> splitInstances = new ArrayList<>();
		
		for (InkPoweredStatusEffectInstance poweredInstance : statusEffectInstances) {
			MobEffectInstance instance = poweredInstance.getStatusEffectInstance();
			
			// instant effects, like harming do not get split (that would apply harming 3x
			if (instance.getEffect().isInstantenous()) {
				splitInstances.add(poweredInstance);
				continue;
			}
			
			for (Tuple<Float, Float> mods : SPLIT_EFFECT_POTENCY_AND_DURATION) {
				int newDuration = (int) (instance.getDuration() * mods.getB());
				int newAmplifier = Support.getIntFromDecimalWithChance(instance.getAmplifier() * mods.getA(), random);
				if (newAmplifier >= 0) {
					splitInstances.add(new InkPoweredStatusEffectInstance(new MobEffectInstance(instance.getEffect(), newDuration, newAmplifier, instance.isAmbient(), instance.isVisible()), poweredInstance.getInkCost(), poweredInstance.getColor(), poweredInstance.isUnidentifiable()));
				}
			}
		}
		
		return splitInstances;
	}
	
	public MobEffect getStatusEffect() {
		return this.recipeData.statusEffect();
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.POTION_WORKSHOP_BREWING_ID;
	}
	
}
