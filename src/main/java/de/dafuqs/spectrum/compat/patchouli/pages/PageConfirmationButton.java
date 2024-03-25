package de.dafuqs.spectrum.compat.patchouli.pages;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.networking.SpectrumC2SPacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageConfirmationButton extends PageWithText {
	
	IVariable checked_advancement;
	IVariable button_text;
	IVariable button_text_confirmed;
	IVariable confirmation;
	
	ResourceLocation checkedAdvancementIdentifier;
	Component buttonText;
	Component buttonTextConfirmed;
	String confirmationString;
	
	String title;
	
	@Override
	public void build(Level world, BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(world, entry, builder, pageNum);
		
		this.checkedAdvancementIdentifier = ResourceLocation.tryParse(checked_advancement.asString());
		this.buttonText = button_text.as(Component.class);
		this.buttonTextConfirmed = button_text_confirmed.as(Component.class);
		this.confirmationString = confirmation.asString();
	}
	
	public boolean isConfirmed() {
		Minecraft client = Minecraft.getInstance();
		return AdvancementHelper.hasAdvancement(client.player, checkedAdvancementIdentifier);
	}
	
	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);
		
		boolean completed = isConfirmed();
		
		Component buttonText = completed ? buttonTextConfirmed : this.buttonText;
		Button button = Button.builder(buttonText, this::confirmationButtonClicked)
				.size(GuiBook.PAGE_WIDTH - 12, Button.DEFAULT_HEIGHT)
				.pos(6, 120)
				.build();
		button.active = !completed;
		
		addButton(button);
	}
	
	@Override
	public int getTextHeight() {
		return 22;
	}
	
	protected void confirmationButtonClicked(Button button) {
		SpectrumC2SPacketSender.sendConfirmationButtonPressedPaket(confirmationString);
		button.setMessage(buttonTextConfirmed);
		entry.markReadStateDirty();
	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float pticks) {
		super.render(drawContext, mouseX, mouseY, pticks);
		
		parent.drawCenteredStringNoShadow(drawContext, title == null || title.isEmpty() ? I18n.get("patchouli.gui.lexicon.objective") : i18n(title), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		GuiBook.drawSeparator(drawContext, book, 0, 12);
		GuiBook.drawSeparator(drawContext, book, 0, GuiBook.PAGE_HEIGHT - 44);
	}
	
}
