package de.dafuqs.spectrum.recipe.anvil_crushing;

import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class AnvilCrushingRecipe extends GatedSpectrumRecipe {
	
	protected final Ingredient inputIngredient;
	protected final ItemStack outputItemStack;
	protected final float crushedItemsPerPointOfDamage;
	protected final float experience;
	protected final ResourceLocation particleEffectIdentifier;
	protected final int particleCount;
	protected final ResourceLocation soundEvent;
	
	public AnvilCrushingRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier,
	                           Ingredient inputIngredient, ItemStack outputItemStack, float crushedItemsPerPointOfDamage,
	                           float experience, ResourceLocation particleEffectIdentifier, int particleCount, ResourceLocation soundEventIdentifier) {
		
		super(id, group, secret, requiredAdvancementIdentifier);
		
		this.inputIngredient = inputIngredient;
		this.outputItemStack = outputItemStack;
		this.crushedItemsPerPointOfDamage = crushedItemsPerPointOfDamage;
		this.experience = experience;
		this.particleEffectIdentifier = particleEffectIdentifier;
		this.particleCount = particleCount;
		this.soundEvent = soundEventIdentifier;
		
		if (requiredAdvancementIdentifier != null) {
			registerInToastManager(getType(), this);
		}
	}
	
	@Override
	public boolean matches(Container inv, Level world) {
		return this.inputIngredient.test(inv.getItem(0));
	}
	
	@Override
	public ItemStack assemble(Container inv, RegistryAccess drm) {
		return outputItemStack.copy();
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess registryManager) {
		return outputItemStack.copy();
	}
	
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(Blocks.ANVIL);
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.ANVIL_CRUSHING_RECIPE_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.ANVIL_CRUSHING;
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.ANVIL_CRUSHING_ID;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> defaultedList = NonNullList.create();
		defaultedList.add(this.inputIngredient);
		return defaultedList;
	}

	public float getCrushedItemsPerPointOfDamage() {
		return crushedItemsPerPointOfDamage;
	}

	public SoundEvent getSoundEvent() {
		return BuiltInRegistries.SOUND_EVENT.get(soundEvent);
	}

	public ParticleOptions getParticleEffect() {
		return (ParticleOptions) BuiltInRegistries.PARTICLE_TYPE.get(particleEffectIdentifier);
	}

	public int getParticleCount() {
		return particleCount;
	}

	public float getExperience() {
		return experience;
	}
	
	@Override
	public ResourceLocation getRecipeTypeUnlockIdentifier() {
		return null;
	}
	
}
