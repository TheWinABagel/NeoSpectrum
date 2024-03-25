package de.dafuqs.spectrum.recipe.primordial_fire_burning.dynamic;

import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import de.dafuqs.spectrum.recipe.primordial_fire_burning.PrimordialFireBurningRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class EnchantedBookUnsoulingRecipe extends PrimordialFireBurningRecipe {
	
	public static final RecipeSerializer<EnchantedBookUnsoulingRecipe> SERIALIZER = new EmptyRecipeSerializer<>(EnchantedBookUnsoulingRecipe::new);
	
	public EnchantedBookUnsoulingRecipe(ResourceLocation identifier) {
		super(identifier, "", false, UNLOCK_IDENTIFIER,
				Ingredient.of(SpectrumEnchantmentHelper.addOrUpgradeEnchantment(Items.ENCHANTED_BOOK.getDefaultInstance(), Enchantments.SOUL_SPEED, 1, false, false).getB()),
				SpectrumEnchantmentHelper.addOrUpgradeEnchantment(Items.ENCHANTED_BOOK.getDefaultInstance(), Enchantments.SWIFT_SNEAK, 1, false, false).getB());
	}
	
	@Override
	public boolean matches(Container inv, Level world) {
		ItemStack stack = inv.getItem(0);
		return EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.SOUL_SPEED);
	}
	
	@Override
	public ItemStack assemble(Container inv, RegistryAccess drm) {
		ItemStack stack = inv.getItem(0);
		
		int level = EnchantmentHelper.getEnchantments(stack).getOrDefault(Enchantments.SOUL_SPEED, 0);
		if(level > 0) {
			stack = SpectrumEnchantmentHelper.removeEnchantments(stack, Enchantments.SOUL_SPEED).getA();
			stack = SpectrumEnchantmentHelper.addOrUpgradeEnchantment(stack, Enchantments.SWIFT_SNEAK, level, false, false).getB();
		}
		return stack;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
}
