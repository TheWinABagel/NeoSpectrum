package de.dafuqs.spectrum.blocks.memory;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.spirit_instiller.SpiritInstillerRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MemoryItem extends BlockItem {
	
	// There are a few entities in vanilla that do not have a corresponding spawn egg
	// therefore to make it nicer we specify custom colors for them here
	private static final HashMap<EntityType<?>, Tuple<Integer, Integer>> customColors = new HashMap<>() {{
		put(EntityType.BAT, new Tuple<>(0x463d2b, 0x191307));
		put(EntityType.SNOW_GOLEM, new Tuple<>(0xc9cbcf, 0xa26e28));
		put(EntityType.WITHER, new Tuple<>(0x101211, 0x3e4140));
		put(EntityType.ILLUSIONER, new Tuple<>(0x29578d, 0x4b4e4f));
		put(EntityType.ENDER_DRAGON, new Tuple<>(0x111111, 0x856c8f));
		put(EntityType.IRON_GOLEM, new Tuple<>(0x9a9a9a, 0x8b7464));
	}};
	
	public MemoryItem(Block block, Properties settings) {
		super(block, settings);
	}
	
	public static ItemStack getMemoryForEntity(LivingEntity entity) {
		CompoundTag tag = new CompoundTag();
		entity.saveAsPassenger(tag);
		tag.remove("Pos"); // yeet everything that we don't need and could interfere when spawning
		tag.remove("OnGround");
		tag.remove("Rotation");
		tag.remove("Motion");
		tag.remove("FallDistance");
		tag.remove("InLove");
		tag.remove("UUID");
		tag.remove("Health");
		tag.remove("Fire");
		tag.remove("HurtByTimestamp");
		tag.remove("DeathTime");
		tag.remove("AbsorptionAmount");
		tag.remove("Air");
		tag.remove("FallFlying");
		tag.remove("PortalCooldown");
		tag.remove("HurtTime");
		
		ItemStack stack = SpectrumBlocks.MEMORY.asItem().getDefaultInstance();
		CompoundTag stackNbt = stack.getOrCreateTag();
		stackNbt.put("EntityTag", tag);
		stack.setTag(stackNbt);
		
		return stack;
	}
	
	public static ItemStack getForEntityType(EntityType<?> entityType, int ticksToManifest) {
		ItemStack stack = SpectrumBlocks.MEMORY.asItem().getDefaultInstance();
		
		CompoundTag stackNbt = stack.getOrCreateTag();
		stackNbt.putInt("TicksToManifest", ticksToManifest);
		
		CompoundTag entityCompound = new CompoundTag();
		entityCompound.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
		stackNbt.put("EntityTag", entityCompound);

		return stack;
	}
	
	public static Optional<EntityType<?>> getEntityType(@Nullable CompoundTag nbt) {
		if (nbt != null && nbt.contains("EntityTag", Tag.TAG_COMPOUND)) {
			CompoundTag nbtCompound = nbt.getCompound("EntityTag");
			if (nbtCompound.contains("id", Tag.TAG_STRING)) {
				return EntityType.byString(nbtCompound.getString("id"));
			}
		}
		return Optional.empty();
	}
	
	public static @Nullable Component getMemoryEntityCustomName(@Nullable CompoundTag nbt) {
		if (nbt != null && nbt.contains("EntityTag", Tag.TAG_COMPOUND)) {
			CompoundTag nbtCompound = nbt.getCompound("EntityTag");
			if (nbtCompound.contains("CustomName", Tag.TAG_STRING)) {
				return Component.Serializer.fromJson(nbtCompound.getString("CustomName"));
			}
		}
		return null;
	}
	
	public static boolean isBrokenPromise(@Nullable CompoundTag nbt) {
		return nbt != null && nbt.getBoolean("BrokenPromise");
	}
	
	// Same nbt format as SpawnEggs
	// That way we can reuse entityType.spawnFromItemStack()
	public static int getTicksToManifest(@Nullable CompoundTag nbtCompound) {
		if (nbtCompound != null && nbtCompound.contains("TicksToManifest", Tag.TAG_ANY_NUMERIC)) {
			return nbtCompound.getInt("TicksToManifest");
		}
		return -1;
	}
	
	public static void setTicksToManifest(@NotNull ItemStack itemStack, int newTicksToManifest) {
		CompoundTag nbtCompound = itemStack.getOrCreateTag();
		nbtCompound.putInt("TicksToManifest", newTicksToManifest);
		itemStack.setTag(nbtCompound);
	}
	
	public static void setSpawnAsAdult(@NotNull ItemStack itemStack, boolean spawnAsAdult) {
		CompoundTag nbtCompound = itemStack.getOrCreateTag();
		if (spawnAsAdult) {
			nbtCompound.putBoolean("SpawnAsAdult", true);
		} else {
			nbtCompound.remove("SpawnAsAdult");
		}
		itemStack.setTag(nbtCompound);
	}
	
	public static void markAsBrokenPromise(ItemStack itemStack, boolean isBrokenPromise) {
		CompoundTag nbtCompound = itemStack.getOrCreateTag();
		if (isBrokenPromise) {
			nbtCompound.putBoolean("BrokenPromise", true);
		} else {
			nbtCompound.remove("BrokenPromise");
		}
		itemStack.setTag(nbtCompound);
	}
	
	public static int getEggColor(CompoundTag nbtCompound, int tintIndex) {
		if (nbtCompound == null || isEntityTypeUnrecognizable(nbtCompound)) {
			if (tintIndex == 0) {
				return 0x222222;
			} else {
				return 0xDDDDDD;
			}
		}
		
		Optional<EntityType<?>> entityType = MemoryItem.getEntityType(nbtCompound);
		if (entityType.isPresent()) {
			EntityType<?> type = entityType.get();
			if (customColors.containsKey(type)) {
				// statically defined: fetch from map
				return tintIndex == 0 ? customColors.get(type).getA() : customColors.get(type).getB();
			} else {
				// dynamically defined: fetch from spawn egg
				SpawnEggItem spawnEggItem = SpawnEggItem.byId(entityType.get());
				if (spawnEggItem != null) {
					return spawnEggItem.getColor(tintIndex);
				}
			}
		}
		
		if (tintIndex == 0) {
			return 0x222222;
		} else {
			return 0xDDDDDD;
		}
	}
	
	public static boolean isEntityTypeUnrecognizable(@Nullable CompoundTag nbtCompound) {
		if (nbtCompound != null && nbtCompound.contains("Unrecognizable")) {
			return nbtCompound.getBoolean("Unrecognizable");
		}
		return false;
	}
	
	public static void makeUnrecognizable(@NotNull ItemStack itemStack) {
		CompoundTag nbtCompound = itemStack.getOrCreateTag();
		nbtCompound.putBoolean("Unrecognizable", true);
		itemStack.setTag(nbtCompound);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		
		CompoundTag nbt = stack.getTag();
		Optional<EntityType<?>> entityType = getEntityType(nbt);
		int ticksToHatch = getTicksToManifest(nbt);
		
		if (entityType.isPresent()) {
			if (isEntityTypeUnrecognizable(nbt)) {
				tooltip.add(Component.translatable("item.spectrum.memory.tooltip.unrecognizable_entity_type").withStyle(ChatFormatting.GRAY));
			} else {
				boolean isBrokenPromise = isBrokenPromise(nbt);
				Component customName = getMemoryEntityCustomName(nbt);
				if (isBrokenPromise) {
					if (customName == null) {
						tooltip.add(Component.translatable("item.spectrum.memory.tooltip.entity_type_broken_promise", entityType.get().getDescription()));
					} else {
						tooltip.add(Component.translatable("item.spectrum.memory.tooltip.named_broken_promise").append(customName).withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC));
					}
				} else {
					if (customName == null) {
						tooltip.add(Component.translatable("item.spectrum.memory.tooltip.entity_type", entityType.get().getDescription()));
					} else {
						tooltip.add(Component.translatable("item.spectrum.memory.tooltip.named").append(customName).withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC));
					}
				}
			}
		} else {
			tooltip.add(Component.translatable("item.spectrum.memory.tooltip.unset_entity_type").withStyle(ChatFormatting.GRAY));
			return;
		}
		
		if (ticksToHatch <= 0) {
			tooltip.add(Component.translatable("item.spectrum.memory.tooltip.does_not_manifest").withStyle(ChatFormatting.GRAY));
		} else if (ticksToHatch > 100) {
			tooltip.add(Component.translatable("item.spectrum.memory.tooltip.extra_long_time_to_manifest").withStyle(ChatFormatting.GRAY));
		} else if (ticksToHatch > 20) {
			tooltip.add(Component.translatable("item.spectrum.memory.tooltip.long_time_to_manifest").withStyle(ChatFormatting.GRAY));
		} else if (ticksToHatch > 5) {
			tooltip.add(Component.translatable("item.spectrum.memory.tooltip.medium_time_to_manifest").withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.add(Component.translatable("item.spectrum.memory.tooltip.short_time_to_manifest").withStyle(ChatFormatting.GRAY));
		}
	}
	
	public static void appendEntries(Output entries) {
		// adding all memories that have spirit instiller recipes
		Set<CompoundTag> encountered = new HashSet<>();
		if (SpectrumCommon.minecraftServer != null) {
			Item memoryItem = SpectrumBlocks.MEMORY.asItem();
			for (SpiritInstillerRecipe recipe : SpectrumCommon.minecraftServer.getRecipeManager().getAllRecipesFor(SpectrumRecipeTypes.SPIRIT_INSTILLING)) {
				ItemStack output = recipe.getResultItem(SpectrumCommon.minecraftServer.registryAccess());
				if (output.is(memoryItem) && !encountered.contains(output.getTag())) {
					entries.accept(output);
					encountered.add(output.getTag());
				}
			}
		}
	}
	
}