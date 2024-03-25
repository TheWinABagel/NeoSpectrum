package de.dafuqs.spectrum.compat.patchouli.pages;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageStatusEffect extends PageWithText {
	
	String title;
	@SerializedName("status_effect_id")
	String statusEffectId;
	transient MobEffect statusEffect;
	transient TextureAtlasSprite statusEffectSprite;
	
	@Override
	public void build(Level world, BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(world, entry, builder, pageNum);
		statusEffect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(statusEffectId));
		statusEffectSprite = Minecraft.getInstance().getMobEffectTextures().get(statusEffect);
	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float pticks) {
		RenderSystem.enableBlend();
		drawContext.blit(49, 14, 0, 18, 18, statusEffectSprite);

		Component toDraw;
		if (title != null && !title.isEmpty()) {
			toDraw = i18nText(title);
		} else {
			toDraw = statusEffect.getDisplayName();
		}
		parent.drawCenteredStringNoShadow(drawContext, toDraw.getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		
		super.render(drawContext, mouseX, mouseY, pticks);
	}
	
	@Override
	public int getTextHeight() {
		return 40;
	}
	
}
