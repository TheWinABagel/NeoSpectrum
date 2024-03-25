package de.dafuqs.spectrum.items;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;

public class CookbookItem extends Item {
	
	public String guidebookPageToOpen;
	
	public CookbookItem(Properties settings, String guidebookPageToOpen) {
		super(settings);
		this.guidebookPageToOpen = guidebookPageToOpen;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		if (!world.isClientSide && user instanceof ServerPlayer serverPlayerEntity) {
			openGuidebookPage(serverPlayerEntity, SpectrumCommon.locate(guidebookPageToOpen), 0);
			user.awardStat(Stats.ITEM_USED.get(this));
			
			return InteractionResultHolder.success(user.getItemInHand(hand));
		}
		
		return InteractionResultHolder.consume(user.getItemInHand(hand));
	}
	
	private void openGuidebookPage(ServerPlayer serverPlayerEntity, ResourceLocation entry, int page) {
		PatchouliAPI.get().openBookEntry(serverPlayerEntity, GuidebookItem.GUIDEBOOK_ID, entry, page);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
	}
	
}