package de.dafuqs.spectrum.explosion;

import de.dafuqs.spectrum.api.item.ModularExplosionProvider;
import de.dafuqs.spectrum.registries.SpectrumRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Set of ExplosionModifiers
 * - serializable / deserializable via nbt
 * - implements the actual explosion logic
 */
public class ModularExplosionDefinition {
	
	protected ExplosionArchetype archetype = ExplosionArchetype.COSMETIC;
	protected List<ExplosionModifier> modifiers;
	
	public ModularExplosionDefinition() {
		this.modifiers = new ArrayList<>();
	}
	
	public ModularExplosionDefinition(ArrayList<ExplosionModifier> modifiers) {
		this.modifiers = modifiers;
	}
	
	public void addModifier(ExplosionModifier modifier) {
		this.modifiers.add(modifier);
	}
	
	public void addModifiers(List<ExplosionModifier> modifiers) {
		this.modifiers.addAll(modifiers);
	}
	
	public void setArchetype(ExplosionArchetype archetype) {
		this.archetype = archetype;
	}
	
	public ExplosionArchetype getArchetype() {
		return archetype;
	}
	
	public boolean isValid(ModularExplosionProvider provider) {
		if (this.modifiers.size() > provider.getMaxExplosionModifiers()) {
			return false;
		}
		
		Map<ExplosionModifierType, Integer> occurrences = new HashMap<>();
		for (ExplosionModifier modifier : modifiers) {
			if (!modifier.type.acceptsArchetype(archetype)) {
				return false;
			}
			ExplosionModifierType type = modifier.getType();
			int typeCount = occurrences.getOrDefault(type, 0);
			if (typeCount > type.getMaxModifiersForType()) {
				return false;
			}
			occurrences.put(type, typeCount + 1);
		}
		
		return true;
	}
	
	public int getModifierCount() {
		return this.modifiers.size();
	}
	
	protected static String NBT_ROOT_KEY = "explosion_data";
	protected static String NBT_ARCHETYPE_KEY = "archetype";
	protected static String NBT_MODIFIER_LIST_KEY = "mods";
	
	// Serialization
	public CompoundTag toNbt() {
		CompoundTag nbt = new CompoundTag();
		
		nbt.putString(NBT_ARCHETYPE_KEY, this.archetype.toString());
		ListTag modifierList = new ListTag();
		for (ExplosionModifier modifier : this.modifiers) {
			modifierList.add(StringTag.valueOf(modifier.getId().toString()));
		}
		nbt.put(NBT_MODIFIER_LIST_KEY, modifierList);
		
		return nbt;
	}
	
	public static ModularExplosionDefinition fromNbt(CompoundTag nbt) {
		ModularExplosionDefinition set = new ModularExplosionDefinition();
		if (nbt == null) {
			return set;
		}
		
		if (nbt.contains(NBT_ARCHETYPE_KEY, Tag.TAG_STRING)) {
			set.archetype = ExplosionArchetype.tryParse(nbt.getString(NBT_ARCHETYPE_KEY));
		}
		ListTag modifierList = nbt.getList(NBT_MODIFIER_LIST_KEY, Tag.TAG_STRING);
		for (Tag e : modifierList) {
			ExplosionModifier mod = SpectrumRegistries.EXPLOSION_MODIFIERS.get(ResourceLocation.tryParse(e.getAsString()));
			if (mod != null) {
				set.modifiers.add(mod);
			}
		}
		
		return set;
	}
	
	public static ModularExplosionDefinition getFromStack(ItemStack stack) {
		CompoundTag nbt = stack.getTag();
		if (nbt != null && nbt.contains(NBT_ROOT_KEY, Tag.TAG_COMPOUND)) {
			return fromNbt(nbt.getCompound(NBT_ROOT_KEY));
		}
		return new ModularExplosionDefinition();
	}
	
	public void attachToStack(ItemStack stack) {
		stack.addTagElement(NBT_ROOT_KEY, toNbt());
	}
	
	public static void removeFromStack(ItemStack stack) {
		stack.removeTagKey(NBT_ROOT_KEY);
	}
	
	// Tooltips
	public void appendTooltip(List<Component> tooltip, ModularExplosionProvider provider) {
		int modifierCount = this.modifiers.size();
		int maxModifierCount = provider.getMaxExplosionModifiers();
		
		tooltip.add(archetype.getName());
		tooltip.add(Component.translatable("item.spectrum.tooltip.explosives.remaining_slots", modifierCount, maxModifierCount).withStyle(ChatFormatting.GRAY));
		
		if (modifierCount == 0) {
			tooltip.add(Component.translatable("item.spectrum.tooltip.explosives.modifiers").withStyle(ChatFormatting.GRAY));
		} else {
			for (ExplosionModifier explosionModifier : modifiers) {
				tooltip.add(explosionModifier.getName());
			}
		}
	}
	
	// Calls the explosion logic
	public void explode(@NotNull ServerLevel world, BlockPos pos, @Nullable Player owner, double baseBlastRadius, float baseDamage) {
		ModularExplosion.explode(world, pos, owner, baseBlastRadius, baseDamage, this.archetype, this.modifiers);
	}
	
	// Calls the explosion logic
	public static void explode(@NotNull ServerLevel world, BlockPos pos, @Nullable Player owner, ItemStack stack) {
		if (stack.getItem() instanceof ModularExplosionProvider provider) {
			ModularExplosionDefinition definition = getFromStack(stack);
			ModularExplosion.explode(world, pos, owner, provider.getBaseExplosionBlastRadius(), provider.getBaseExplosionDamage(), definition.archetype, definition.modifiers);
		}
	}
	
	public static void explode(@NotNull ServerLevel world, BlockPos pos, Direction direction, @Nullable Player owner, ItemStack stack) {
		if (stack.getItem() instanceof ModularExplosionProvider provider) {
			ModularExplosionDefinition definition = getFromStack(stack);
			BlockPos finalPos = pos.relative(direction, (int) provider.getBaseExplosionBlastRadius() - 2); // TODO: Add distance added via blast range modification
			ModularExplosion.explode(world, finalPos, owner, provider.getBaseExplosionBlastRadius(), provider.getBaseExplosionDamage(), definition.archetype, definition.modifiers);
		}
	}
	
}
