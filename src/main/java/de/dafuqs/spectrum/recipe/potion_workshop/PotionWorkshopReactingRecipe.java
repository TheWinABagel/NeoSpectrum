package de.dafuqs.spectrum.recipe.potion_workshop;

import de.dafuqs.spectrum.api.recipe.DescriptiveGatedRecipe;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class PotionWorkshopReactingRecipe extends GatedSpectrumRecipe implements DescriptiveGatedRecipe {
	
	protected static final HashMap<Item, List<PotionMod>> reagents = new HashMap<>();
	
	protected final Item item;
	protected final List<PotionMod> modifiers;
	
	public PotionWorkshopReactingRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Item item, List<PotionMod> modifiers) {
		super(id, group, secret, requiredAdvancementIdentifier);
		this.item = item;
		this.modifiers = modifiers;
		
		reagents.put(item, modifiers);
		
		registerInToastManager(getType(), this);
	}
	
	@Override
	public boolean matches(@NotNull Container inv, Level world) {
		return false;
	}
	
	@Override
	public ItemStack assemble(Container inventory, RegistryAccess registryManager) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess manager) {
		return item.getDefaultInstance();
	}
	
	@Override
	public ItemStack getToastSymbol() {
		return SpectrumBlocks.POTION_WORKSHOP.asItem().getDefaultInstance();
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.POTION_WORKSHOP_REACTING_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.POTION_WORKSHOP_REACTING;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> defaultedList = NonNullList.create();
		defaultedList.add(Ingredient.of(this.item));
		return defaultedList;
	}
	
	@Override
	public ResourceLocation getRecipeTypeUnlockIdentifier() {
		return PotionWorkshopRecipe.UNLOCK_IDENTIFIER;
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.POTION_WORKSHOP_REACTING_ID;
	}
	
	@Override
	public Component getDescription() {
		ResourceLocation identifier = BuiltInRegistries.ITEM.getKey(this.item);
		return Component.translatable("spectrum.rei.potion_workshop_reacting." + identifier.getNamespace() + "." + identifier.getPath());
	}
	
	@Override
	public Item getItem() {
		return this.item;
	}
	
	public static boolean isReagent(Item item) {
		return reagents.containsKey(item);
	}
	
	public static PotionMod combine(PotionMod potionMod, ItemStack reagentStack, RandomSource random) {
		Item reagent = reagentStack.getItem();
		List<PotionMod> reagentMods = reagents.getOrDefault(reagent, null);
		if (reagentMods != null) {
			potionMod.modifyFrom(reagentMods.get(random.nextInt(reagentMods.size())));
		}
		return potionMod;
	}
	
}
