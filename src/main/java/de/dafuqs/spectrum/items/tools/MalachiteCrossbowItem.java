package de.dafuqs.spectrum.items.tools;

//import de.dafuqs.arrowhead.api.ArrowheadCrossbow;
import de.dafuqs.spectrum.api.item.Preenchanted;
import de.dafuqs.spectrum.registries.SpectrumItemTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;
import java.util.function.Predicate;

public class MalachiteCrossbowItem extends CrossbowItem implements Preenchanted/*, ArrowheadCrossbow*/ { //todoforge arrowhead
	
	public static final Predicate<ItemStack> PROJECTILES = (stack) -> stack.is(ItemTags.ARROWS) || stack.is(SpectrumItemTags.GLASS_ARROWS);
	
	public MalachiteCrossbowItem(Properties settings) {
        super(settings);
    }
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of(Enchantments.PIERCING, 5);
	}
	
	@Override
	public ItemStack getDefaultInstance() {
		return getDefaultEnchantedStack(this);
	}
	
	public static ItemStack getFirstProjectile(ItemStack crossbow) {
		CompoundTag nbtCompound = crossbow.getTag();
		if (nbtCompound != null && nbtCompound.contains("ChargedProjectiles", 9)) {
			ListTag nbtList = nbtCompound.getList("ChargedProjectiles", 10);
			if (nbtList != null && nbtList.size() > 0) {
				CompoundTag nbtCompound2 = nbtList.getCompound(0);
				return ItemStack.of(nbtCompound2);
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return PROJECTILES;
	}

//	@Override
//	public float getProjectileVelocityModifier(ItemStack stack) {
//		return 1.25F;
//	}
//
//	@Override
//	public float getPullTimeModifier(ItemStack stack) {
//		return 1.0F;
//	}
//
//	@Override
//	public float getDivergenceMod(ItemStack stack) {
//		return 0.75F;
//	}
	
}
