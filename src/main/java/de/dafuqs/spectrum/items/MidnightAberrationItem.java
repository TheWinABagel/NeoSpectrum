package de.dafuqs.spectrum.items;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.items.conditional.CloakedItem;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MidnightAberrationItem extends CloakedItem {
	
	private static final ResourceLocation MIDNIGHT_ABERRATION_CRUMBLING_ADVANCEMENT_ID = SpectrumCommon.locate("midgame/crumble_midnight_aberration");
	private static final String MIDNIGHT_ABERRATION_CRUMBLING_ADVANCEMENT_CRITERION = "have_midnight_aberration_crumble";
	
	public MidnightAberrationItem(Properties settings, ResourceLocation cloakAdvancementIdentifier, Item cloakItem) {
		super(settings, cloakAdvancementIdentifier, cloakItem);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		
		if (!world.isClientSide && world.getGameTime() % 20 == 0 && entity instanceof ServerPlayer player) {
			CompoundTag compound = stack.getTag();
			if (compound != null && compound.getBoolean("Stable")) {
				return;
			}
			
			// check if it's a real stack in the player's inventory or just a proxy item (like a Bottomless Bundle)
			if (world.random.nextFloat() < 0.2F) {
				stack.shrink(1);
				player.getInventory().placeItemBackInInventory(Items.GUNPOWDER.getDefaultInstance());
				world.playSound(null, player, SpectrumSoundEvents.MIDNIGHT_ABERRATION_CRUMBLING, SoundSource.PLAYERS, 0.5F, 1.0F);
				
				Support.grantAdvancementCriterion(player, MIDNIGHT_ABERRATION_CRUMBLING_ADVANCEMENT_ID, MIDNIGHT_ABERRATION_CRUMBLING_ADVANCEMENT_CRITERION);
			}
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		
		CompoundTag compound = stack.getTag();
		if (compound != null && compound.getBoolean("Stable")) {
			tooltip.add(Component.translatable("item.spectrum.midnight_aberration.tooltip.stable"));
		}
	}
	
	public ItemStack getStableStack() {
		ItemStack stack = getDefaultInstance();
		CompoundTag compound = stack.getOrCreateTag();
		compound.putBoolean("Stable", true);
		stack.setTag(compound);
		return stack;
	}
	
	@Override
	public @Nullable Tuple<Item, MutableComponent> getCloakedItemTranslation() {
		return new Tuple<>(this, Component.translatable("item.spectrum.midnight_aberration.cloaked"));
	}
	
}
