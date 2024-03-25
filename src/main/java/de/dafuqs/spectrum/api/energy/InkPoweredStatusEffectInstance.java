package de.dafuqs.spectrum.api.energy;

import com.google.common.collect.Lists;
import de.dafuqs.spectrum.helpers.Support;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InkPoweredStatusEffectInstance {
	
	public static final String NBT_KEY = "InkPoweredStatusEffects";
	public static final String UNIDENTIFIABLE_NBT_KEY = "Unidentifiable";
	public static final String CUSTOM_COLOR_NBT_KEY = "CustomColor";

	private final MobEffectInstance statusEffectInstance;
	private final InkCost cost;
	private final boolean unidentifiable;
	private final int customColor; // -1: use effect default
	
	public InkPoweredStatusEffectInstance(MobEffectInstance statusEffectInstance, InkCost cost, int customColor, boolean unidentifiable) {
		this.statusEffectInstance = statusEffectInstance;
		this.cost = cost;
		this.customColor = customColor;
		this.unidentifiable = unidentifiable;
	}
	
	public MobEffectInstance getStatusEffectInstance() {
		return statusEffectInstance;
	}
	
	public InkCost getInkCost() {
		return cost;
	}
	
	public CompoundTag toNbt() {
		CompoundTag nbt = new CompoundTag();
		this.statusEffectInstance.save(nbt);
		this.cost.writeNbt(nbt);
		if (customColor != -1) {
			nbt.putInt(CUSTOM_COLOR_NBT_KEY, this.customColor);
		}
		if (unidentifiable) {
			nbt.putBoolean(UNIDENTIFIABLE_NBT_KEY, true);
		}
		return nbt;
	}
	
	public static InkPoweredStatusEffectInstance fromNbt(CompoundTag nbt) {
		MobEffectInstance statusEffectInstance = MobEffectInstance.load(nbt);
		InkCost cost = InkCost.fromNbt(nbt);
		int customColor = -1;
		if (nbt.contains(CUSTOM_COLOR_NBT_KEY, Tag.TAG_ANY_NUMERIC)) {
			customColor = nbt.getInt(CUSTOM_COLOR_NBT_KEY);
		}
		boolean unidentifiable = false;
		if (nbt.contains(UNIDENTIFIABLE_NBT_KEY)) {
			unidentifiable = nbt.getBoolean(UNIDENTIFIABLE_NBT_KEY);
		}
		return new InkPoweredStatusEffectInstance(statusEffectInstance, cost, customColor, unidentifiable);
	}
	
	public static List<InkPoweredStatusEffectInstance> getEffects(ItemStack stack) {
		return getEffects(stack.getTag());
	}
	
	public static List<InkPoweredStatusEffectInstance> getEffects(@Nullable CompoundTag nbt) {
		List<InkPoweredStatusEffectInstance> list = new ArrayList<>();
		if (nbt != null && nbt.contains(NBT_KEY, Tag.TAG_LIST)) {
			ListTag nbtList = nbt.getList(NBT_KEY, Tag.TAG_COMPOUND);
			
			for (int i = 0; i < nbtList.size(); ++i) {
				CompoundTag nbtCompound = nbtList.getCompound(i);
				InkPoweredStatusEffectInstance instance = InkPoweredStatusEffectInstance.fromNbt(nbtCompound);
				list.add(instance);
			}
		}
		return list;
	}
	
	public static void setEffects(ItemStack stack, Collection<InkPoweredStatusEffectInstance> effects) {
		if (!effects.isEmpty()) {
			CompoundTag nbtCompound = stack.getOrCreateTag();
			ListTag nbtList = nbtCompound.getList(NBT_KEY, Tag.TAG_LIST);
			
			for (InkPoweredStatusEffectInstance effect : effects) {
				nbtList.add(effect.toNbt());
			}
			
			nbtCompound.put(NBT_KEY, nbtList);
		}
	}
	
	public static void buildTooltip(List<Component> tooltip, List<InkPoweredStatusEffectInstance> effects, MutableComponent attributeModifierText, boolean showDuration) {
		if (effects.size() > 0) {
			List<Tuple<Attribute, AttributeModifier>> attributeModifiers = Lists.newArrayList();
			for (InkPoweredStatusEffectInstance entry : effects) {
				if (entry.isUnidentifiable()) {
					tooltip.add(Component.translatable("item.spectrum.potion.tooltip.unidentifiable"));
					continue;
				}

				MobEffectInstance effect = entry.getStatusEffectInstance();
				InkCost cost = entry.getInkCost();
				
				MutableComponent mutableText = Component.translatable(effect.getDescriptionId());
				if (effect.getAmplifier() > 0) {
					mutableText = Component.translatable("potion.withAmplifier", mutableText, Component.translatable("potion.potency." + effect.getAmplifier()));
				}
				if (showDuration && effect.getDuration() > 20) {
					mutableText = Component.translatable("potion.withDuration", mutableText, MobEffectUtil.formatDuration(effect, 1.0F));
				}
				mutableText.withStyle(effect.getEffect().getCategory().getTooltipFormatting());
				mutableText.append(Component.translatable("spectrum.tooltip.ink_cost." + cost.getColor().toString().toLowerCase(Locale.ROOT), Support.getShortenedNumberString(cost.getCost())).withStyle(ChatFormatting.GRAY));
				tooltip.add(mutableText);
				
				Map<Attribute, AttributeModifier> map = effect.getEffect().getAttributeModifiers();
				for (Map.Entry<Attribute, AttributeModifier> entityAttributeEntityAttributeModifierEntry : map.entrySet()) {
					AttributeModifier entityAttributeModifier = entityAttributeEntityAttributeModifierEntry.getValue();
					AttributeModifier entityAttributeModifier2 = new AttributeModifier(entityAttributeModifier.getName(), effect.getEffect().getAttributeModifierValue(effect.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
					attributeModifiers.add(new Tuple<>(entityAttributeEntityAttributeModifierEntry.getKey(), entityAttributeModifier2));
				}
			}
			
			if (!attributeModifiers.isEmpty()) {
				tooltip.add(Component.empty());
				tooltip.add(attributeModifierText.withStyle(ChatFormatting.DARK_PURPLE));
				
				for (Tuple<Attribute, AttributeModifier> entityAttributeEntityAttributeModifierPair : attributeModifiers) {
					AttributeModifier mutableText = entityAttributeEntityAttributeModifierPair.getB();
					double statusEffect = mutableText.getAmount();
					double d;
					if (mutableText.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && mutableText.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
						d = mutableText.getAmount();
					} else {
						d = mutableText.getAmount() * 100.0D;
					}
					
					if (statusEffect > 0.0D) {
						tooltip.add((Component.translatable("attribute.modifier.plus." + mutableText.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d), Component.translatable((entityAttributeEntityAttributeModifierPair.getA()).getDescriptionId()))).withStyle(ChatFormatting.BLUE));
					} else if (statusEffect < 0.0D) {
						d *= -1.0D;
						tooltip.add((Component.translatable("attribute.modifier.take." + mutableText.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d), Component.translatable((entityAttributeEntityAttributeModifierPair.getA()).getDescriptionId()))).withStyle(ChatFormatting.RED));
					}
				}
			}
		}
	}
	
	public int getColor() {
		if (this.customColor == -1) {
			return statusEffectInstance.getEffect().getColor();
		}
		return this.customColor;
	}
	
	public boolean isUnidentifiable() {
		return this.unidentifiable;
	}

}