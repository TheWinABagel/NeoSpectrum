package de.dafuqs.spectrum.progression.toast;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.RenderHelper;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class UnlockedRecipeToast implements Toast {
	
	private final ResourceLocation TEXTURE = SpectrumCommon.locate("textures/gui/toasts.png");
	private final Component title;
	private final Component text;
	private final List<ItemStack> itemStacks;
	private final SoundEvent soundEvent = SpectrumSoundEvents.NEW_RECIPE;
	private boolean soundPlayed;
	
	public UnlockedRecipeToast(Component title, Component text, List<ItemStack> itemStacks) {
		this.title = title;
		this.text = text;
		this.itemStacks = itemStacks;
		this.soundPlayed = false;
	}
	
	public static void showRecipeToast(@NotNull Minecraft client, ItemStack itemStack, Component title) {
		Component text = getTextForItemStack(itemStack);
		client.getToasts().addToast(new UnlockedRecipeToast(title, text, new ArrayList<>() {{
			add(itemStack);
		}}));
	}
	
	public static void showRecipeGroupToast(@NotNull Minecraft client, String groupName, List<ItemStack> itemStacks, Component title) {
		Component text = Component.translatable("recipeGroup.spectrum." + groupName);
		client.getToasts().addToast(new UnlockedRecipeToast(title, text, itemStacks));
	}
	
	public static void showLotsOfRecipesToast(@NotNull Minecraft client, List<ItemStack> itemStacks) {
		client.getToasts().addToast(new UnlockedRecipeToast(
				Component.translatable("spectrum.toast.lots_of_recipes_unlocked.title"),
				Component.translatable("spectrum.toast.lots_of_recipes_unlocked.description", itemStacks.size()),
				itemStacks));
	}
	
	public static Component getTextForItemStack(@NotNull ItemStack itemStack) {
		if (itemStack.is(Items.ENCHANTED_BOOK)) {
			// special handling for enchanted books
			// Instead of the text "enchanted book" the toast will
			// read the first stored enchantment in the book
			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
			if (enchantments.size() > 0) {
				Map.Entry<Enchantment, Integer> firstEnchantment = enchantments.entrySet().iterator().next();
				return Component.translatable(firstEnchantment.getKey().getDescriptionId());
			}
		} else if (itemStack.is(Items.POTION)) {
			// special handling for potions
			// use the name of the first custom potion effect
			List<MobEffectInstance> effects = PotionUtils.getCustomEffects(itemStack);
			if (effects.size() > 0) {
				return Component.translatable(effects.get(0).getDescriptionId()).append(" ").append(Component.translatable("item.minecraft.potion"));
			}
		}
		return itemStack.getHoverName();
	}
	
	@Override
	public Toast.Visibility render(GuiGraphics drawContext, @NotNull ToastComponent manager, long startTime) {
		drawContext.blit(TEXTURE, 0, 0, 0, 32, this.width(), this.height());
		
		Minecraft client = manager.getMinecraft();
		Font textRenderer = client.font;
		drawContext.drawString(textRenderer, title, 30, 7, RenderHelper.GREEN_COLOR, false);
		drawContext.drawString(textRenderer, text, 30, 18, 0, false);
		
		long toastTimeMilliseconds = SpectrumCommon.CONFIG.ToastTimeMilliseconds;
		if (!this.soundPlayed && startTime > 0L) {
			this.soundPlayed = true;
			if (this.soundEvent != null) {
				manager.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(this.soundEvent, 1.0F, 1.0F));
			}
		}
		
		int itemStackIndex = (int) (startTime / Math.max(1, toastTimeMilliseconds / this.itemStacks.size()) % this.itemStacks.size());
		drawContext.renderItem(itemStacks.get(itemStackIndex), 8, 8);

		return startTime >= toastTimeMilliseconds ? Visibility.HIDE : Visibility.SHOW;
	}
	
}
