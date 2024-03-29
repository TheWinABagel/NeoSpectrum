package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.networking.SpectrumC2SPacketSender;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class PaintbrushScreen extends QuickNavigationGridScreen<PaintbrushScreenHandler> {
	
	public static final QuickNavigationGridScreen.Grid MAGENTA_GRID = new QuickNavigationGridScreen.Grid(
			GridEntry.colored(InkColors.MAGENTA.getColor(), "spectrum.ink.color.magenta", (screen) -> chooseColor(InkColors.MAGENTA)),
			GridEntry.colored(InkColors.RED.getColor(), "spectrum.ink.color.red", (screen) -> chooseColor(InkColors.RED)),
			GridEntry.colored(InkColors.PINK.getColor(), "spectrum.ink.color.pink", (screen) -> chooseColor(InkColors.PINK)),
			GridEntry.colored(InkColors.BROWN.getColor(), "spectrum.ink.color.brown", (screen) -> chooseColor(InkColors.BROWN)),
			GridEntry.BACK
	);
	
	public static final QuickNavigationGridScreen.Grid CYAN_GRID = new QuickNavigationGridScreen.Grid(
			GridEntry.colored(InkColors.CYAN.getColor(), "spectrum.ink.color.cyan", (screen) -> chooseColor(InkColors.CYAN)),
			GridEntry.BACK,
			GridEntry.colored(InkColors.LIGHT_BLUE.getColor(), "spectrum.ink.color.light_blue", (screen) -> chooseColor(InkColors.LIGHT_BLUE)),
			GridEntry.colored(InkColors.BLUE.getColor(), "spectrum.ink.color.blue", (screen) -> chooseColor(InkColors.BLUE)),
			GridEntry.colored(InkColors.PURPLE.getColor(), "spectrum.ink.color.purple", (screen) -> chooseColor(InkColors.PURPLE))
	);
	
	public static final QuickNavigationGridScreen.Grid YELLOW_GRID = new QuickNavigationGridScreen.Grid(
			GridEntry.colored(InkColors.YELLOW.getColor(), "spectrum.ink.color.yellow", (screen) -> chooseColor(InkColors.YELLOW)),
			GridEntry.colored(InkColors.ORANGE.getColor(), "spectrum.ink.color.orange", (screen) -> chooseColor(InkColors.ORANGE)),
			GridEntry.BACK,
			GridEntry.colored(InkColors.LIME.getColor(), "spectrum.ink.color.lime", (screen) -> chooseColor(InkColors.LIME)),
			GridEntry.colored(InkColors.GREEN.getColor(), "spectrum.ink.color.green", (screen) -> chooseColor(InkColors.GREEN))
	);
	
	public static final QuickNavigationGridScreen.Grid BLACK_GRID = new QuickNavigationGridScreen.Grid(
			GridEntry.colored(InkColors.BLACK.getColor(), "spectrum.ink.color.black", (screen) -> chooseColor(InkColors.BLACK)),
			GridEntry.colored(InkColors.WHITE.getColor(), "spectrum.ink.color.white", (screen) -> chooseColor(InkColors.WHITE)),
			GridEntry.colored(InkColors.LIGHT_GRAY.getColor(), "spectrum.ink.color.light_gray", (screen) -> chooseColor(InkColors.LIGHT_GRAY)),
			GridEntry.BACK,
			GridEntry.colored(InkColors.GRAY.getColor(), "spectrum.ink.color.gray", (screen) -> chooseColor(InkColors.GRAY))
	);
	
	public PaintbrushScreen(PaintbrushScreenHandler handler, Inventory playerInventory, Component title) {
		super(handler, playerInventory, title);
		gridStack.push(new QuickNavigationGridScreen.Grid(
				GridEntry.CLOSE,
				handler.hasAccessToWhites() ? GridEntry.colored(InkColors.BLACK.getColor(), "spectrum.ink.color.group_blacks", (screen) -> selectGrid(BLACK_GRID)) : GridEntry.colored(InkColors.BLACK.getColor(), "spectrum.ink.color.black", (screen) -> chooseColor(InkColors.BLACK)),
				GridEntry.colored(InkColors.MAGENTA.getColor(), "spectrum.ink.color.group_magentas", (screen) -> selectGrid(MAGENTA_GRID)),
				GridEntry.colored(InkColors.CYAN.getColor(), "spectrum.ink.color.group_cyans", (screen) -> selectGrid(CYAN_GRID)),
				GridEntry.colored(InkColors.YELLOW.getColor(), "spectrum.ink.color.group_yellows", (screen) -> selectGrid(YELLOW_GRID))
		));
	}
	
	protected static void chooseColor(@Nullable InkColor inkColor) {
		SpectrumC2SPacketSender.sendInkColorSelectedInGUI(inkColor);
		Minecraft client = Minecraft.getInstance();
		client.level.playSound(null, client.player.blockPosition(), SpectrumSoundEvents.PAINTBRUSH_PAINT, SoundSource.NEUTRAL, 0.6F, 1.0F);
		client.player.closeContainer();
	}
	
}