package de.dafuqs.spectrum.registries;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

import static net.minecraft.world.item.Tiers.DIAMOND;
import static net.minecraft.world.item.Tiers.IRON;

public class SpectrumToolMaterials {
	
	public enum ToolMaterial implements net.minecraft.world.item.Tier {
		LOW_HEALTH(IRON.getLevel(), 16, 4.0F, 2.0F, 10, Ingredient::of),
		VOIDING(DIAMOND.getLevel(), 1143, 20.0F, 1.0F, 5, Ingredient::of),
		
		BEDROCK(4, 0, 15.0F, 8.0F, 3, () -> Ingredient.of(SpectrumItems.BEDROCK_DUST)),
		DRACONIC(5, 10000, 9.0F, 7.0F, 2, () -> Ingredient.of(SpectrumItems.REFINED_BLOODSTONE)),
		MALACHITE(5, 1536, 9.0F, 5.0F, 20, () -> Ingredient.of(SpectrumItems.REFINED_MALACHITE)),
		GLASS_CREST(5, 1536 * 4, 18.0F, 10.0F, 5, () -> Ingredient.of(SpectrumItems.REFINED_MALACHITE)),
		
		DREAMFLAYER(IRON.getLevel(), 650, 5.0F, 2.0F, 20, () -> Ingredient.of(SpectrumItems.BISMUTH_CRYSTAL)),
		NIGHTFALL(IRON.getLevel(), 650, 2.0F, 1.0F, 0, () -> Ingredient.of(SpectrumItems.MIDNIGHT_CHIP));
		
		private final int miningLevel;
		private final int itemDurability;
		private final float miningSpeed;
		private final float attackDamage;
		private final int enchantability;
		private final Supplier<Ingredient> repairIngredient;
		
		ToolMaterial(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
			this.miningLevel = miningLevel;
			this.itemDurability = itemDurability;
			this.miningSpeed = miningSpeed;
			this.attackDamage = attackDamage;
			this.enchantability = enchantability;
			this.repairIngredient = Suppliers.memoize(repairIngredient::get);
		}
		
		@Override
		public int getUses() {
			return this.itemDurability;
		}
		
		@Override
		public float getSpeed() {
			return this.miningSpeed;
		}
		
		@Override
		public float getAttackDamageBonus() {
			return this.attackDamage;
		}
		
		@Override
		public int getLevel() {
			return this.miningLevel;
		}
		
		@Override
		public int getEnchantmentValue() {
			return this.enchantability;
		}
		
		@Override
		public Ingredient getRepairIngredient() {
			return this.repairIngredient.get();
		}
	}
	
}
