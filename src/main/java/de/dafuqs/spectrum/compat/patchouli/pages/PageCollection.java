package de.dafuqs.spectrum.compat.patchouli.pages;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

import java.util.ArrayList;
import java.util.List;

public class PageCollection extends PageWithText {
	
	private static final int ENTRIES_PER_ROW = 6;
	
	String title;
	IVariable items;
	
	transient List<ItemStack> stacks;
	
	@Override
	public void build(Level world, BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(world, entry, builder, pageNum);
		
		stacks = new ArrayList<>();
		for (IVariable item : items.asList()) {
			String stackString = item.asString();
			ItemStack stack;
			try {
				stack = new ItemArgument(CommandBuildContext.configurable(world.registryAccess(), world.enabledFeatures())).parse(new StringReader(stackString)).createItemStack(1, false);
			} catch (CommandSyntaxException e) {
				PatchouliAPI.LOGGER.warn("Unable to parse stack {} in collection page", stackString);
				continue;
			}
			stacks.add(stack);
			entry.addRelevantStack(builder, stack, pageNum);
		}
	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float pticks) {
		super.render(drawContext, mouseX, mouseY, pticks);
		
		boolean hasTitle = title != null && !title.isEmpty();
		if (hasTitle) {
			parent.drawCenteredStringNoShadow(drawContext, i18n(title), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
			GuiBook.drawSeparator(drawContext, book, 0, 12);
		}
		
		int startY = hasTitle ? 18 : 0;
		int row = 0;
		int column = -1;
		int firstNonFullRowIndex = (stacks.size()) / ENTRIES_PER_ROW;
		int unusedEntriesInLastRow = ENTRIES_PER_ROW - (stacks.size() % ENTRIES_PER_ROW);
		for (ItemStack stack : stacks) {
			column++;
			if (column == ENTRIES_PER_ROW) {
				column = 0;
				row++;
			}
			int startX = 5 + column * 18;
			if (row == firstNonFullRowIndex) {
				startX += unusedEntriesInLastRow * 9;
			}
			parent.renderItemStack(drawContext, startX, startY + row * 18, mouseX, mouseY, stack);
		}
		
		if (!text.asString().isEmpty()) {
			GuiBook.drawSeparator(drawContext, book, 0, startY + 20 + row * 18);
		}
		
		super.render(drawContext, mouseX, mouseY, pticks);
	}
	
	@Override
	public int getTextHeight() {
		boolean hasTitle = title != null && !title.isEmpty();
		return 8 + (hasTitle ? 18 : 0) + (int) Math.ceil(stacks.size() / (float) ENTRIES_PER_ROW) * 18;
	}
	
}