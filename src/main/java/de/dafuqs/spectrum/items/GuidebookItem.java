package de.dafuqs.spectrum.items;

import de.dafuqs.revelationary.advancement_criteria.AdvancementGottenCriterion;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.LoomPatternProvider;
import de.dafuqs.spectrum.registries.SpectrumBannerPatterns;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;
import java.util.Map;

public class GuidebookItem extends Item implements LoomPatternProvider {
	
	public static final ResourceLocation GUIDEBOOK_ID = SpectrumCommon.locate("guidebook");
	
	
	public GuidebookItem(Properties settings) {
		super(settings);
	}
	
	public static void reprocessAdvancementUnlocks(ServerPlayer serverPlayerEntity) {
		if (serverPlayerEntity.getServer() == null) {
			return;
		}
		
		PlayerAdvancements tracker = serverPlayerEntity.getAdvancements();
		
		// "has advancement" criteria with nonexistent advancements
		for (Advancement advancement : serverPlayerEntity.getServer().getAdvancements().getAllAdvancements()) {
			if (advancement.getId().getNamespace().startsWith(SpectrumCommon.MOD_ID)) {
				AdvancementProgress hasAdvancement = tracker.getOrStartProgress(advancement);
				if (!hasAdvancement.isDone()) {
					for (Map.Entry<String, Criterion> criterionEntry : advancement.getCriteria().entrySet()) {
						CriterionTriggerInstance conditions = criterionEntry.getValue().getTrigger();
						if (conditions != null && conditions.getCriterion().equals(AdvancementGottenCriterion.ID) && conditions instanceof AdvancementGottenCriterion.Conditions hasAdvancementConditions) {
							Advancement advancementCriterionAdvancement = SpectrumCommon.minecraftServer.getAdvancements().getAdvancement(hasAdvancementConditions.getAdvancementIdentifier());
							if (advancementCriterionAdvancement != null) {
								AdvancementProgress hasAdvancementCriterionAdvancement = tracker.getOrStartProgress(advancementCriterionAdvancement);
								if (hasAdvancementCriterionAdvancement.isDone()) {
									tracker.award(advancement, criterionEntry.getKey());
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		if (!world.isClientSide && user instanceof ServerPlayer serverPlayerEntity) {
			
			// Workaround for new advancement unlocks getting added after spectrum has been installed
			reprocessAdvancementUnlocks(serverPlayerEntity);
			
			// if the player has never opened the book before
			// automatically open the introduction page
			if (isNewPlayer(serverPlayerEntity)) {
				openGuidebook(serverPlayerEntity, SpectrumCommon.locate("general/intro"), 0);
			} else {
				openGuidebook(serverPlayerEntity);
			}
			
			user.awardStat(Stats.ITEM_USED.get(this));
			
			return InteractionResultHolder.success(user.getItemInHand(hand));
		} else {
			return InteractionResultHolder.consume(user.getItemInHand(hand));
		}
	}
	
	private boolean isNewPlayer(ServerPlayer serverPlayerEntity) {
		return serverPlayerEntity.getStats().getValue(Stats.ITEM_USED, this) == 0;
	}
	
	private void openGuidebook(ServerPlayer serverPlayerEntity) {
		PatchouliAPI.get().openBookGUI(serverPlayerEntity, GUIDEBOOK_ID);
	}
	
	private void openGuidebook(ServerPlayer serverPlayerEntity, ResourceLocation entry, int page) {
		PatchouliAPI.get().openBookEntry(serverPlayerEntity, GUIDEBOOK_ID, entry, page);
	}
	
	@Override
	public Holder<BannerPattern> getPattern() {
		return SpectrumBannerPatterns.GUIDEBOOK;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		addBannerPatternProviderTooltip(tooltip);
	}
	
}