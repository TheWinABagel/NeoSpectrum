package de.dafuqs.spectrum.items;

import com.mojang.datafixers.util.Pair;
import de.dafuqs.spectrum.registries.SpectrumStructureTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StructureCompassItem extends CompassItem {
	
	protected final TagKey<Structure> locatedStructures;
	
	public StructureCompassItem(Properties settings, TagKey<Structure> locatedStructures) {
		super(settings);
		this.locatedStructures = locatedStructures;
	}
	
	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, Entity entity, int slot, boolean selected) {
		if (!world.isClientSide && world.getGameTime() % 200 == 0) {
			locateStructure(stack, world, entity);
		}
	}

	protected void locateStructure(@NotNull ItemStack stack, @NotNull Level world, Entity entity) {
		Pair<BlockPos, Holder<Structure>> foundStructure = locateStructure((ServerLevel) world, entity.blockPosition());
		if (foundStructure != null) {
			saveStructurePos(stack, world.dimension(), foundStructure.getFirst());
		} else {
			removeStructurePos(stack);
		}
	}

	public @Nullable Pair<BlockPos, Holder<Structure>> locateStructure(@NotNull ServerLevel world, @NotNull BlockPos pos) {
		Optional<HolderSet.Named<Structure>> registryEntryList = SpectrumStructureTags.entriesOf(world, locatedStructures);
		if (registryEntryList.isPresent()) {
			return world.getChunkSource().getGenerator().findNearestMapStructure(world, registryEntryList.get(), pos, 100, false);
		} else {
			return null;
		}
	}
	
	public static boolean hasStructure(@NotNull ItemStack stack) {
		CompoundTag nbtCompound = stack.getTag();
		return nbtCompound != null && (nbtCompound.contains("StructureDimension") && nbtCompound.contains("StructurePos"));
	}
	
	public static @Nullable GlobalPos getStructurePos(ItemStack stack) {
		CompoundTag nbt = stack.getTag();
		if (nbt == null) {
			return null;
		}
		boolean bl = nbt.contains("StructurePos");
		boolean bl2 = nbt.contains("StructureDimension");
		if (bl && bl2) {
			Optional<ResourceKey<Level>> optional = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, nbt.get("StructureDimension")).result();
			if (optional.isPresent()) {
				BlockPos blockPos = NbtUtils.readBlockPos(nbt.getCompound("StructurePos"));
				return GlobalPos.of(optional.get(), blockPos);
			}
		}
		return null;
	}
	
	protected void saveStructurePos(ItemStack stack, @NotNull ResourceKey<Level> worldKey, @NotNull BlockPos pos) {
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.put("StructurePos", NbtUtils.writeBlockPos(pos));
		nbt.putString("StructureDimension", worldKey.location().toString());
	}
	
	protected void removeStructurePos(@NotNull ItemStack stack) {
		stack.removeTagKey("StructurePos");
		stack.removeTagKey("StructureDimension");
	}
	
}
