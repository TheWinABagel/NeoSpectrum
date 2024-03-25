package de.dafuqs.spectrum.enchantments;

import de.dafuqs.spectrum.registries.SpectrumAttributeTags;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class InexorableEnchantment extends SpectrumEnchantment {
    
    public InexorableEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
        super(weight, EnchantmentCategory.ARMOR_CHEST, slotTypes, unlockAdvancementIdentifier);
    }
    
    public static void checkAndRemoveSlowdownModifiers(LivingEntity entity) {
		var armorInexorable = isArmorActive(entity);
		var toolInexorable = EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.INEXORABLE, entity.getItemInHand(entity.getUsedItemHand())) > 0;

		var armorAttributes = BuiltInRegistries.ATTRIBUTE.getTag(SpectrumAttributeTags.INEXORABLE_ARMOR_EFFECTIVE);
		var toolAttributes = BuiltInRegistries.ATTRIBUTE.getTag(SpectrumAttributeTags.INEXORABLE_HANDHELD_EFFECTIVE);

		if (armorInexorable && armorAttributes.isPresent()) {
			for (Holder<Attribute> attributeRegistryEntry : armorAttributes.get()) {

				var attributeInstance = entity.getAttribute(attributeRegistryEntry.value());

				if (attributeInstance == null)
					continue;

				var badMods = attributeInstance.getModifiers()
                        .stream()
                        .filter(modifier -> modifier.getAmount() < 0)
                        .toList();
                
                badMods.forEach(modifier -> attributeInstance.removeModifier(modifier.getId()));
            }
        }
        
        if (toolInexorable && toolAttributes.isPresent()) {
            for (Holder<Attribute> attributeRegistryEntry : toolAttributes.get()) {
                
                var attributeInstance = entity.getAttribute(attributeRegistryEntry.value());
                
                if (attributeInstance == null)
                    continue;
                
                var badMods = attributeInstance.getModifiers()
                        .stream()
                        .filter(modifier -> modifier.getAmount() < 0)
                        .toList();
                
                badMods.forEach(modifier -> attributeInstance.removeModifier(modifier.getId()));
            }
        }
    }
    
    @Override
    public int getMinCost(int level) {
        return 50;
    }
    
    @Override
    public int getMaxCost(int level) {
        return 100;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        var item = stack.getItem();

        if (item instanceof ArmorItem armor)
            return armor.getEquipmentSlot() == EquipmentSlot.CHEST;

        return item instanceof TieredItem || item instanceof TridentItem;
    }

    public static boolean isArmorActive(LivingEntity entity) {
        return EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.INEXORABLE, entity.getItemBySlot(EquipmentSlot.CHEST)) > 0;
    }
}
