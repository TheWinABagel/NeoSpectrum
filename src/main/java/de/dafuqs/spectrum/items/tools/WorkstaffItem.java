package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.api.energy.InkCost;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.api.item.AoEBreakingTool;
import de.dafuqs.spectrum.api.item.Preenchanted;
import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.inventories.WorkstaffScreenHandler;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class WorkstaffItem extends MultiToolItem implements AoEBreakingTool, Preenchanted {
	
	protected static final InkCost BASE_COST_PER_AOE_MINING_RANGE_INCREMENT = new InkCost(InkColors.WHITE, 3); // TODO: make pricier once ink networking is in
	
	public enum GUIToggle {
		SELECT_SILK_TOUCH("item.spectrum.workstaff.message.silk_touch"),
		SELECT_FORTUNE("item.spectrum.workstaff.message.fortune"),
		SELECT_RESONANCE("item.spectrum.workstaff.message.resonance"),
		SELECT_1x1("item.spectrum.workstaff.message.1x1"),
		SELECT_3x3("item.spectrum.workstaff.message.3x3"),
		SELECT_5x5("item.spectrum.workstaff.message.5x5"),
		ENABLE_RIGHT_CLICK_ACTIONS("item.spectrum.workstaff.message.enabled_right_click_actions"),
		DISABLE_RIGHT_CLICK_ACTIONS("item.spectrum.workstaff.message.disabled_right_click_actions"),
		ENABLE_PROJECTILES("item.spectrum.workstaff.message.enabled_projectiles"),
		DISABLE_PROJECTILES("item.spectrum.workstaff.message.disabled_projectiles");

		private final String triggerText;

		GUIToggle(String triggerText) {
			this.triggerText = triggerText;
		}

		public Component getTriggerText() {
			return Component.translatable(triggerText);
		}
		
	}
	
	public static final String RANGE_NBT_STRING = "Range";
	public static final String RIGHT_CLICK_DISABLED_NBT_STRING = "RightClickDisabled";
	public static final String PROJECTILES_DISABLED_NBT_STRING = "ProjectilesDisabled";

    public WorkstaffItem(Tier material, int attackDamage, float attackSpeed, Properties settings) {
        super(material, attackDamage, attackSpeed, settings);
    }
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		if (user.isShiftKeyDown()) {
			if (user instanceof ServerPlayer serverPlayerEntity) {
				serverPlayerEntity.openMenu(createScreenHandlerFactory(user.getItemInHand(hand)));
			}
			return InteractionResultHolder.consume(user.getItemInHand(hand));
		}
		return super.use(world, user, hand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		int range = getAoERange(stack);
		if(range > 0) {
			int displayedRange = 1 + range + range;
			tooltip.add(Component.translatable("item.spectrum.workstaff.tooltip.mining_range", displayedRange, displayedRange).withStyle(ChatFormatting.GRAY));
		}
	}
	
	@Override
	public boolean canTill(ItemStack stack) {
		CompoundTag nbt = stack.getTag();
		return nbt == null || !nbt.getBoolean(RIGHT_CLICK_DISABLED_NBT_STRING);
	}
	
	public MenuProvider createScreenHandlerFactory(ItemStack itemStack) {
		return new SimpleMenuProvider((syncId, inventory, player) ->
				new WorkstaffScreenHandler(syncId, inventory, itemStack),
				Component.translatable("item.spectrum.workstaff")
		);
	}
	
	@Override
	public int getAoERange(ItemStack stack) {
		CompoundTag nbt = stack.getTag();
		if (nbt == null || !nbt.contains(RANGE_NBT_STRING, Tag.TAG_ANY_NUMERIC)) {
			return 0;
		}
		return nbt.getInt(RANGE_NBT_STRING);
	}
	
	@Override
	public boolean canUseAoE(Player player, ItemStack stack) {
		int range = getAoERange(stack);
		if (range <= 0) {
			return true;
		}
		
		int costForRange = (int) Math.pow(BASE_COST_PER_AOE_MINING_RANGE_INCREMENT.getCost(), range);
		return InkPowered.tryDrainEnergy(player, BASE_COST_PER_AOE_MINING_RANGE_INCREMENT.getColor(), costForRange);
	}
	
	public static void applyToggle(Player player, ItemStack stack, GUIToggle toggle) {
		CompoundTag nbt = stack.getOrCreateTag();
		switch (toggle) {
			case SELECT_1x1 -> {
				nbt.remove(RANGE_NBT_STRING);
				player.displayClientMessage(toggle.getTriggerText(), true);
			}
			case SELECT_3x3 -> {
				nbt.putInt(RANGE_NBT_STRING, 1);
				player.displayClientMessage(toggle.getTriggerText(), true);
			}
			case SELECT_5x5 -> {
				nbt.putInt(RANGE_NBT_STRING, 2);
				player.displayClientMessage(toggle.getTriggerText(), true);
			}
			// switching to another enchantment
			// fortune handling is a bit special. Its level is preserved in NBT,
			// to restore the original enchant level when switching back
			case SELECT_FORTUNE -> {
				enchantAndRemoveOthers(player, stack, toggle.getTriggerText(), Enchantments.BLOCK_FORTUNE);
			}
			case SELECT_SILK_TOUCH -> {
				enchantAndRemoveOthers(player, stack, toggle.getTriggerText(), Enchantments.SILK_TOUCH);
			}
			case SELECT_RESONANCE -> {
				enchantAndRemoveOthers(player, stack, toggle.getTriggerText(), SpectrumEnchantments.RESONANCE);
			}
			case ENABLE_RIGHT_CLICK_ACTIONS -> {
				nbt.remove(RIGHT_CLICK_DISABLED_NBT_STRING);
				player.displayClientMessage(toggle.getTriggerText(), true);
			}
			case DISABLE_RIGHT_CLICK_ACTIONS -> {
				nbt.putBoolean(RIGHT_CLICK_DISABLED_NBT_STRING, true);
				player.displayClientMessage(toggle.getTriggerText(), true);
			}
			case ENABLE_PROJECTILES -> {
				nbt.remove(PROJECTILES_DISABLED_NBT_STRING);
				player.displayClientMessage(toggle.getTriggerText(), true);
			}
			case DISABLE_PROJECTILES -> {
				nbt.putBoolean(PROJECTILES_DISABLED_NBT_STRING, true);
				player.displayClientMessage(toggle.getTriggerText(), true);
			}
		}
		stack.setTag(nbt);
	}
	
	private static void enchantAndRemoveOthers(Player player, ItemStack stack, Component message, Enchantment enchantment) {
		int existingLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
		if (existingLevel > 0) {
			return;
		}
		
		int level = 1;
		
		CompoundTag nbt = stack.getOrCreateTag();
		if (enchantment == Enchantments.BLOCK_FORTUNE) {
			if (nbt.contains("FortuneLevel", Tag.TAG_ANY_NUMERIC)) {
				level = nbt.getInt("FortuneLevel");
				nbt.remove("FortuneLevel");
			}
		} else {
			int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack);
			if (fortuneLevel > 0) {
				nbt.putInt("FortuneLevel", fortuneLevel);
			}
		}
		
		if (SpectrumEnchantmentHelper.removeEnchantments(stack, Enchantments.SILK_TOUCH, SpectrumEnchantments.RESONANCE, Enchantments.BLOCK_FORTUNE).getB() > 0) {
			SpectrumEnchantmentHelper.addOrUpgradeEnchantment(stack, enchantment, level, true, true);
			player.displayClientMessage(message, true);
		} else if (player instanceof ServerPlayer serverPlayerEntity) {
			triggerUnenchantedWorkstaffAdvancement(serverPlayerEntity);
		}
	}
	
	private static void triggerUnenchantedWorkstaffAdvancement(ServerPlayer player) {
		player.playNotifySound(SpectrumSoundEvents.USE_FAIL, SoundSource.PLAYERS, 0.75F, 1.0F);
		Support.grantAdvancementCriterion(player, "lategame/trigger_unenchanted_workstaff", "code_triggered");
	}
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of(Enchantments.BLOCK_FORTUNE, 4);
	}

	@Override
	public ItemStack getDefaultInstance() {
		return getDefaultEnchantedStack(this);
	}

}
