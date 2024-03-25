package de.dafuqs.spectrum.recipe.crafting;

import de.dafuqs.spectrum.blocks.present.PresentBlock;
import de.dafuqs.spectrum.blocks.present.PresentItem;
import de.dafuqs.spectrum.items.PigmentItem;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WrapPresentRecipe extends CustomRecipe {
	public static final RecipeSerializer<WrapPresentRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(WrapPresentRecipe::new);
	
	public WrapPresentRecipe(ResourceLocation identifier, CraftingBookCategory category) {
		super(identifier, CraftingBookCategory.MISC);
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess registryManager) {
		return SpectrumBlocks.PRESENT.asItem().getDefaultInstance();
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.withSize(1, Ingredient.EMPTY);
		ItemStack stack = SpectrumBlocks.PRESENT.asItem().getDefaultInstance();
		PresentItem.wrap(stack, PresentBlock.WrappingPaper.RED, Map.of());
		list.set(0, Ingredient.of(stack));
		return list;
	}
	
	@Override
	public boolean matches(@NotNull CraftingContainer craftingInventory, Level world) {
		boolean presentItemFound = false;
		boolean wrappingItemFound = false;
		
		for (int j = 0; j < craftingInventory.getContainerSize(); ++j) {
			ItemStack itemStack = craftingInventory.getItem(j);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() instanceof PresentItem) {
					if (presentItemFound || PresentItem.isWrapped(itemStack)) {
						return false;
					}
					presentItemFound = true;
				} else if (!wrappingItemFound && getPresentVariantForStack(itemStack) != null) {
					wrappingItemFound = true;
				} else if (!(itemStack.getItem() instanceof PigmentItem)) {
					return false;
				}
			}
		}
		
		return presentItemFound;
	}
	
	@Override
	public ItemStack craft(@NotNull CraftingContainer craftingInventory, RegistryAccess drm) {
		ItemStack presentStack = ItemStack.EMPTY;
		PresentBlock.WrappingPaper wrappingPaper = PresentBlock.WrappingPaper.RED;
		Map<DyeColor, Integer> colors = new HashMap<>();
		
		for (int j = 0; j < craftingInventory.getContainerSize(); ++j) {
			ItemStack stack = craftingInventory.getItem(j);
			if (stack.getItem() instanceof PresentItem) {
				presentStack = stack.copy();
			} else if (stack.getItem() instanceof PigmentItem pigmentItem) {
				DyeColor color = pigmentItem.getColor();
				if (colors.containsKey(color)) {
					colors.put(color, colors.get(color) + 1);
				} else {
					colors.put(color, 1);
				}
			}
			PresentBlock.WrappingPaper stackWrappingPaper = getPresentVariantForStack(stack);
			if (stackWrappingPaper != null) {
				wrappingPaper = stackWrappingPaper;
			}
		}
		
		if (!presentStack.isEmpty()) {
			PresentItem.wrap(presentStack, wrappingPaper, colors);
		}
		return presentStack;
	}
	
	public @Nullable PresentBlock.WrappingPaper getPresentVariantForStack(@NotNull ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.RED_DYE) {
			return PresentBlock.WrappingPaper.RED;
		} else if (item == Items.BLUE_DYE) {
			return PresentBlock.WrappingPaper.BLUE;
		} else if (item == Items.CYAN_DYE) {
			return PresentBlock.WrappingPaper.CYAN;
		} else if (item == Items.GREEN_DYE) {
			return PresentBlock.WrappingPaper.GREEN;
		} else if (item == Items.PURPLE_DYE) {
			return PresentBlock.WrappingPaper.PURPLE;
		} else if (item == Items.CAKE) {
			return PresentBlock.WrappingPaper.CAKE;
		} else if (stack.is(ItemTags.FLOWERS)) {
			return PresentBlock.WrappingPaper.STRIPED;
		} else if (item == Items.FIREWORK_STAR) {
			return PresentBlock.WrappingPaper.STARRY;
		} else if (item == Items.SNOWBALL) {
			return PresentBlock.WrappingPaper.WINTER;
		} else if (item == Items.SPORE_BLOSSOM) {
			return PresentBlock.WrappingPaper.PRIDE;
		}
		return null;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 1;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
}
