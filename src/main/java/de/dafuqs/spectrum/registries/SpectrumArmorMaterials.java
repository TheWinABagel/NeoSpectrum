package de.dafuqs.spectrum.registries;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;
import java.util.function.Supplier;

public enum SpectrumArmorMaterials implements ArmorMaterial {
	
	GEMSTONE("gemstone", 9, SpectrumCommon.CONFIG.GemstoneArmorBootsProtection, SpectrumCommon.CONFIG.GemstoneArmorLeggingsProtection, SpectrumCommon.CONFIG.GemstoneArmorChestplateProtection, SpectrumCommon.CONFIG.GemstoneArmorHelmetProtection,
			15, SoundEvents.AMETHYST_BLOCK_CHIME, SpectrumCommon.CONFIG.GemstoneArmorToughness, SpectrumCommon.CONFIG.GemstoneArmorKnockbackResistance, () -> Ingredient.of(SpectrumItemTags.GEMSTONE_SHARDS)),
	BEDROCK("bedrock", 70, SpectrumCommon.CONFIG.BedrockArmorBootsProtection, SpectrumCommon.CONFIG.BedrockArmorLeggingsProtection, SpectrumCommon.CONFIG.BedrockArmorChestplateProtection, SpectrumCommon.CONFIG.BedrockArmorHelmetProtection,
			5, SoundEvents.ARMOR_EQUIP_NETHERITE, SpectrumCommon.CONFIG.BedrockArmorToughness, SpectrumCommon.CONFIG.BedrockArmorKnockbackResistance, () -> Ingredient.of(SpectrumItems.BEDROCK_DUST));
	
	private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
	private final String name;
	private final int durabilityMultiplier;
	private final Map<ArmorItem.Type, Integer> protectionAmounts;
	private final int enchantability;
	private final SoundEvent equipSound;
	private final float toughness;
	private final float knockbackResistance;
	private final Supplier<Ingredient> repairIngredientSupplier;
	
	SpectrumArmorMaterials(String name, int durabilityMultiplier, int bootsProtection, int leggingsProtection, int chestplateProtection, int helmetProtection, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredientSupplier) {
		this.name = name;
		this.durabilityMultiplier = durabilityMultiplier;
		
		this.protectionAmounts = Maps.newEnumMap(ArmorItem.Type.class);
		this.protectionAmounts.put(ArmorItem.Type.BOOTS, bootsProtection);
		this.protectionAmounts.put(ArmorItem.Type.LEGGINGS, leggingsProtection);
		this.protectionAmounts.put(ArmorItem.Type.CHESTPLATE, chestplateProtection);
		this.protectionAmounts.put(ArmorItem.Type.HELMET, helmetProtection);
		
		this.enchantability = enchantability;
		this.equipSound = equipSound;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.repairIngredientSupplier = Suppliers.memoize(repairIngredientSupplier::get);
	}
	
	@Override
	public int getDurabilityForType(ArmorItem.Type type) {
		return BASE_DURABILITY[type.getSlot().getIndex()] * this.durabilityMultiplier;
	}
	
	@Override
	public int getDefenseForType(ArmorItem.Type type) {
		return protectionAmounts.get(type);
	}
	
	@Override
	public int getEnchantmentValue() {
		return this.enchantability;
	}
	
	@Override
	public SoundEvent getEquipSound() {
		return this.equipSound;
	}
	
	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredientSupplier.get();
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public float getToughness() {
		return this.toughness;
	}
	
	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
	
}
