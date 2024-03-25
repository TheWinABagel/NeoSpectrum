package de.dafuqs.spectrum.recipe.crafting;

import de.dafuqs.spectrum.helpers.ColorHelper;
import de.dafuqs.spectrum.items.PigmentItem;
import de.dafuqs.spectrum.items.magic_items.EverpromiseRibbonItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

public class ColorEverpromiseRibbonRecipe extends CustomRecipe {
	
	public static final RecipeSerializer<ColorEverpromiseRibbonRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(ColorEverpromiseRibbonRecipe::new);
	
	public ColorEverpromiseRibbonRecipe(ResourceLocation identifier, CraftingBookCategory category) {
		super(identifier, category);
	}
	
	@Override
	public boolean matches(CraftingContainer craftingInventory, Level world) {
		boolean ribbonFound = false;
		boolean pigmentFound = false;
		
		for (int i = 0; i < craftingInventory.getContainerSize(); ++i) {
			ItemStack itemStack = craftingInventory.getItem(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() instanceof EverpromiseRibbonItem) {
					if (!itemStack.hasCustomHoverName()) {
						return false;
					}
					if (ribbonFound) {
						return false;
					} else {
						ribbonFound = true;
					}
				} else if (itemStack.getItem() instanceof PigmentItem) {
					if (pigmentFound) {
						return false;
					} else {
						pigmentFound = true;
					}
				} else {
					return false;
				}
			}
		}
		
		return ribbonFound && pigmentFound;
	}
	
	@Override
	public ItemStack craft(CraftingContainer craftingInventory, RegistryAccess drm) {
		ItemStack ribbon = null;
		PigmentItem pigment = null;
		
		
		for (int i = 0; i < craftingInventory.getContainerSize(); ++i) {
			ItemStack stack = craftingInventory.getItem(i);
			if (stack.getItem() instanceof EverpromiseRibbonItem) {
				ribbon = stack;
			}
			if (stack.getItem() instanceof PigmentItem pigmentItem) {
				pigment = pigmentItem;
			}
		}
		
		if (ribbon == null || pigment == null) {
			return ItemStack.EMPTY;
		}
		
		ribbon = ribbon.copy();
		ribbon.setCount(1);
		
		Component text = ribbon.getHoverName();
		if (text instanceof MutableComponent mutableText) {
			TextColor newColor = TextColor.fromRgb(ColorHelper.getInt(pigment.getColor()));
			Component newName = mutableText.setStyle(mutableText.getStyle().withColor(newColor));
			ribbon.setHoverName(newName);
		}
		
		return ribbon;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
}
