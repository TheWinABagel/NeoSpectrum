package de.dafuqs.spectrum.items.armor;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.Preenchanted;
import de.dafuqs.spectrum.registries.client.SpectrumModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class BedrockArmorItem extends ArmorItem implements Preenchanted {
	@OnlyIn(Dist.CLIENT)
	private HumanoidModel<LivingEntity> model;
	
	public BedrockArmorItem(ArmorMaterial material, ArmorItem.Type type, Properties settings) {
		super(material, type, settings);
	}
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of();
	}
	
	@Override
	public ItemStack getDefaultInstance() {
		return getDefaultEnchantedStack(this);
	}
	
	@Override
	public boolean canBeDepleted() {
		return false;
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}
	
	@Override
	public boolean isValidRepairItem(ItemStack itemStack_1, ItemStack itemStack_2) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	protected HumanoidModel<LivingEntity> provideArmorModelForSlot(EquipmentSlot slot) {
		var models = Minecraft.getInstance().getEntityModels();
		var feet = models.bakeLayer(SpectrumModelLayers.FEET_BEDROCK_LAYER);
		var root = models.bakeLayer(SpectrumModelLayers.MAIN_BEDROCK_LAYER);
		if (slot == EquipmentSlot.FEET)
			return new FullArmorModel(feet, slot);
		else
			return new FullArmorModel(root, slot);
	}

	@OnlyIn(Dist.CLIENT)
	public HumanoidModel<LivingEntity> getArmorModel() {
		if (model == null) {
			model = provideArmorModelForSlot(getEquipmentSlot());
		}
		return model;
	}
	
	@NotNull
	public ResourceLocation getArmorTexture(ItemStack stack, EquipmentSlot slot) {
		if (slot == EquipmentSlot.FEET) {
			return SpectrumCommon.locate("textures/armor/bedrock_armor_feet.png");
		} else {
			return SpectrumCommon.locate("textures/armor/bedrock_armor_main.png");
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) { //todoforge bedrock armor rendering
		consumer.accept(new IClientItemExtensions() {

			@Override
			public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> contextModel) {

				BedrockArmorItem armor = (BedrockArmorItem) stack.getItem();
				HumanoidModel<LivingEntity> model = armor.getArmorModel();
				ResourceLocation texture = armor.getArmorTexture(stack, slot);
				((HumanoidModel<LivingEntity>) contextModel).copyPropertiesTo(model);

//				ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, texture);

				return IClientItemExtensions.super.getHumanoidArmorModel(livingEntity, stack, slot, contextModel);
			}
		});
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}
}
