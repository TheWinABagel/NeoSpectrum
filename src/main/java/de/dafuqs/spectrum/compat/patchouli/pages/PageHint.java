package de.dafuqs.spectrum.compat.patchouli.pages;

import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.networking.SpectrumC2SPacketSender;
import de.dafuqs.spectrum.sound.HintRevelationSoundInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.book.Book;

import java.util.List;

public class PageHint extends BookPage {
	
	public static class PaymentButtonWidget extends Button {
		
		final PageHint pageHint;
		
		public PaymentButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress, PageHint pageHint) {
			super(x, y, width, height, message, onPress, Button.DEFAULT_NARRATION);
			this.pageHint = pageHint;
			setMessage(Component.translatable("spectrum.gui.lexicon.reveal_hint_button.text"));
		}
		
		@Override
		public void renderWidget(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
			if (pageHint.revealProgress < 0) {
				super.renderWidget(drawContext, mouseX, mouseY, delta);
			}
		}
		
	}
	
	IVariable cost;
	IVariable text;
	transient BookTextRenderer textRender;
	transient Ingredient ingredient;
	
	// this once was a single property. But because the world sometimes got backdated we have to go this
	// a tad more complicated approach, comparing the current tick with the last reveled tick every time
	transient long lastRevealTick; // advance revealProgress each time this does not match the previous tick
	transient long revealProgress; // -1: not revealed, 0: fully revealed; 1+: currently revealing (+1 every tick)
	
	Component rawText;
	Component displayedText;
	
	String title;
	
	public int getTextHeight() {
		return 22;
	}
	
	@Override
	public void build(Level world, BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(world, entry, builder, pageNum);
		ingredient = cost.as(Ingredient.class);
	}
	
	public boolean isQuestDone(Book book) {
		return PersistentData.data.getBookData(book).completedManualQuests.contains(getEntryId());
	}
	
	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);
		rawText = text.as(Component.class);
		
		boolean isDone = isQuestDone(parent.book);
		if (!isDone) {
			revealProgress = -1;
			displayedText = calculateTextToRender(rawText);
			
			PaymentButtonWidget paymentButtonWidget = new PaymentButtonWidget(GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 35, 100, 20, Component.empty(), this::paymentButtonClicked, this);
			addButton(paymentButtonWidget);
		} else {
			displayedText = rawText;
			revealProgress = 0;
		}
		textRender = new BookTextRenderer(parent, displayedText, 0, getTextHeight());
	}
	
	private Component calculateTextToRender(Component text) {
		Minecraft client = Minecraft.getInstance();
		if (revealProgress == 0) {
			return text;
		} else if (revealProgress < 0) {
			return Component.literal("$(obf)" + text.getString());
		}
		
		// Show a new letter each tick
		Component calculatedText = Component.literal(text.getString().substring(0, (int) revealProgress) + "$(obf)" + text.getString().substring((int) revealProgress));
		
		long currentTime = client.level.getGameTime();
		if (currentTime != lastRevealTick) {
			lastRevealTick = currentTime;
			
			revealProgress++;
			revealProgress = Math.min(text.getString().length(), revealProgress);
			if (text.getString().length() < revealProgress) {
				revealProgress = 0;
				return text;
			}
		}
		
		return calculatedText;
	}
	
	protected ResourceLocation getEntryId() {
		return new ResourceLocation(entry.getId().getNamespace(), entry.getId().getPath() + "_" + this.pageNum);
	}
	
	@Environment(EnvType.CLIENT)
	protected void paymentButtonClicked(Button button) {
		Minecraft client = Minecraft.getInstance();
		if (revealProgress > -1) {
			// has already been paid
			return;
		}
		if (client.player.isCreative() || InventoryHelper.hasInInventory(List.of(ingredient), client.player.getInventory())) {
			// mark as complete in book data
			PersistentData.BookData data = PersistentData.data.getBookData(parent.book);
			data.completedManualQuests.add(getEntryId());
			PersistentData.save();
			entry.markReadStateDirty();
			
			Minecraft.getInstance().getSoundManager().play(new HintRevelationSoundInstance(mc.player, rawText.getString().length()));
			
			SpectrumC2SPacketSender.sendGuidebookHintBoughtPaket(ingredient);
			revealProgress = 1;
			lastRevealTick = client.level.getGameTime();
			client.player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
		}
	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float pticks) {
		super.render(drawContext, mouseX, mouseY, pticks);
		
		if (revealProgress >= 0) {
			textRender = new BookTextRenderer(parent, calculateTextToRender(rawText), 0, getTextHeight());
		}
		textRender.render(drawContext, mouseX, mouseY);
		if (revealProgress == -1) {
			parent.renderIngredient(drawContext, GuiBook.PAGE_WIDTH / 2 + 23, GuiBook.PAGE_HEIGHT - 34, mouseX, mouseY, ingredient);
		}
		
		parent.drawCenteredStringNoShadow(drawContext, title == null || title.isEmpty() ? I18n.get("patchouli.gui.lexicon.objective") : i18n(title), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		GuiBook.drawSeparator(drawContext, book, 0, 12);
	}
	
}
