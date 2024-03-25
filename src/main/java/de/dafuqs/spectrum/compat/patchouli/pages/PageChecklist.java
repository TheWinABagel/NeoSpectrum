package de.dafuqs.spectrum.compat.patchouli.pages;

import com.google.gson.annotations.SerializedName;
import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

import java.util.HashMap;
import java.util.Map;

public class PageChecklist extends BookPage {
	
	protected String title;
	transient Component titleText;
	
	protected IVariable text;
	transient BookTextRenderer textRender;
	
	@SerializedName("checklist")
	final
	Map<String, String> checklist = new HashMap<>();
	
	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);
		
		StringBuilder stringBuilder = new StringBuilder();
		if (text == null) {
			text = IVariable.wrap("");
		} else {
			stringBuilder.append(text.asString());
		}
		
		for (Map.Entry<String, String> entry : checklist.entrySet()) {
			String value = entry.getValue();
			if (AdvancementHelper.hasAdvancementClient(ResourceLocation.tryParse(entry.getKey()))) {
				stringBuilder.append("$(li)");
				stringBuilder.append("$(m)");
				stringBuilder.append(value);
				stringBuilder.append("$()");
				stringBuilder.append(" ✔");
			} else {
				stringBuilder.append("$(li)");
				stringBuilder.append(value);
				stringBuilder.append("$()");
			}
		}
		
		textRender = new BookTextRenderer(parent, IVariable.wrap(stringBuilder.toString()).as(Component.class), 0, getTextHeight());
		
		if (title == null) {
			title = "";
			titleText = Component.literal("");
		} else {
			titleText = i18nText(title);
		}
	}
	
	public int getTextHeight() {
		return title == null ? -4 : title.isEmpty() ? -4 : 14;
	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float pticks) {
		super.render(drawContext, mouseX, mouseY, pticks);
		
		textRender.render(drawContext, mouseX, mouseY);
		parent.drawCenteredStringNoShadow(drawContext, getTitle().getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
	}
	
	protected Component getTitle() {
		return titleText;
	}
	
}