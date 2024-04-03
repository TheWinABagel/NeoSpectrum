package de.dafuqs.spectrum.registries;

import com.mojang.serialization.DataResult;
import de.dafuqs.spectrum.compat.biome_makeover.BiomeMakeoverCompat;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Optional;

public class SpectrumTooltips {

	@SubscribeEvent
	public static void register(ItemTooltipEvent e) {
		ItemStack stack = e.getItemStack();
		CompoundTag nbt = stack.getTag();
		List<Component> lines = e.getToolTip();
		if (nbt != null) {

			if (stack.is(Blocks.SCULK_SHRIEKER.asItem())) {
				addSculkShriekerTooltips(lines, nbt);
			} else if (stack.is(ItemTags.SIGNS)) {
				addSignTooltips(lines, nbt);
			} else if (stack.is(Items.SPAWNER)) {
				addSpawnerTooltips(lines, nbt);
			}

			if (nbt.getBoolean(BiomeMakeoverCompat.CURSED_TAG)) {
				lines.add(Component.translatable("spectrum.tooltip.biomemakeover_cursed").withStyle(ChatFormatting.GRAY));
			}
		}
	}
	
	private static void addSculkShriekerTooltips(List<Component> lines, CompoundTag nbt) {
		if (!nbt.contains("BlockStateTag", Tag.TAG_COMPOUND)) {
			return;
		}
		CompoundTag blockStateTag = nbt.getCompound("BlockStateTag");
		if (Boolean.parseBoolean(blockStateTag.getString("can_summon"))) {
			lines.add(Component.translatable("spectrum.tooltip.able_to_summon_warden").withStyle(ChatFormatting.GRAY));
		}
	}
	
	private static void addSignTooltips(List<Component> lines, CompoundTag nbt) {
		if (!nbt.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
			return;
		}
		CompoundTag blockEntityTag = nbt.getCompound("BlockEntityTag");
		addSignText(lines, SignText.DIRECT_CODEC.parse(NbtOps.INSTANCE, blockEntityTag.getCompound("front_text")));
		addSignText(lines, SignText.DIRECT_CODEC.parse(NbtOps.INSTANCE, blockEntityTag.getCompound("back_text")));
	}

	private static void addSignText(List<Component> lines, DataResult<SignText> signText) {
		if(signText.result().isPresent()) {
			SignText st = signText.result().get();
			Style style = Style.EMPTY.withColor(st.getColor().getTextColor());
			for (Component text : st.getMessages(false)) {
				lines.addAll(text.toFlatList(style));
			}
		}
	}

	public static void addSpawnerTooltips(List<Component> lines, CompoundTag nbt) {
		if (!nbt.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
			return;
		}
		
		Optional<EntityType<?>> entityType = Optional.empty();
		CompoundTag blockEntityTag = nbt.getCompound("BlockEntityTag");
		
		if (blockEntityTag.contains("SpawnData", Tag.TAG_COMPOUND)
				&& blockEntityTag.getCompound("SpawnData").contains("entity", Tag.TAG_COMPOUND)
				&& blockEntityTag.getCompound("SpawnData").getCompound("entity").contains("id", Tag.TAG_STRING)) {
			String spawningEntityType = blockEntityTag.getCompound("SpawnData").getCompound("entity").getString("id");
			entityType = EntityType.byString(spawningEntityType);
		}
		
		try {
			short spawnCount = blockEntityTag.getShort("SpawnCount");
			short minSpawnDelay = blockEntityTag.getShort("MinSpawnDelay");
			short maxSpawnDelay = blockEntityTag.getShort("MaxSpawnDelay");
			short spawnRange = blockEntityTag.getShort("SpawnRange");
			short requiredPlayerRange = blockEntityTag.getShort("RequiredPlayerRange");
			
			if (entityType.isPresent()) {
				lines.add(Component.translatable(entityType.get().getDescriptionId()));
			} else {
				lines.add(Component.translatable("item.spectrum.spawner.tooltip.unknown_mob"));
			}
			if (spawnCount > 0) {
				lines.add(Component.translatable("item.spectrum.spawner.tooltip.spawn_count", spawnCount).withStyle(ChatFormatting.GRAY));
			}
			if (minSpawnDelay > 0) {
				lines.add(Component.translatable("item.spectrum.spawner.tooltip.min_spawn_delay", minSpawnDelay).withStyle(ChatFormatting.GRAY));
			}
			if (maxSpawnDelay > 0) {
				lines.add(Component.translatable("item.spectrum.spawner.tooltip.max_spawn_delay", maxSpawnDelay).withStyle(ChatFormatting.GRAY));
			}
			if (spawnRange > 0) {
				lines.add(Component.translatable("item.spectrum.spawner.tooltip.spawn_range", spawnRange).withStyle(ChatFormatting.GRAY));
			}
			if (requiredPlayerRange > 0) {
				lines.add(Component.translatable("item.spectrum.spawner.tooltip.required_player_range", requiredPlayerRange).withStyle(ChatFormatting.GRAY));
			}
		} catch (Exception e) {
			lines.add(Component.translatable("item.spectrum.spawner.tooltip.unknown_mob"));
		}
	}
	
	
}
