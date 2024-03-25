package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.compat.REI.GatedSpectrumDisplay;
import de.dafuqs.spectrum.compat.REI.SpectrumPlugins;
import de.dafuqs.spectrum.recipe.crystallarieum.CrystallarieumCatalyst;
import de.dafuqs.spectrum.recipe.crystallarieum.CrystallarieumRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CrystallarieumDisplay extends GatedSpectrumDisplay {
	
	protected final List<EntryIngredient> growthStages;
	protected final List<CrystallarieumCatalyst> catalysts;
	protected final InkColor inkColor;
	protected final boolean growsWithoutCatalyst;
	protected final int secondsPerStage;
	
	public CrystallarieumDisplay(@NotNull CrystallarieumRecipe recipe) {
		super(recipe, inputs(recipe), outputs(recipe));
		
		this.growthStages = new ArrayList<>();
		for (BlockState state : recipe.getGrowthStages()) {
			growthStages.add(EntryIngredients.of(state.getBlock().asItem()));
		}
		this.catalysts = recipe.getCatalysts();
		this.inkColor = recipe.getInkColor();
		this.growsWithoutCatalyst = recipe.growsWithoutCatalyst();
		this.secondsPerStage = recipe.getSecondsPerGrowthStage();
	}
	
	public static List<EntryIngredient> inputs(CrystallarieumRecipe recipe) {
		List<EntryIngredient> inputs = new ArrayList<>();
		inputs.add(EntryIngredients.ofIngredient(recipe.getIngredientStack()));
		
		Item firstBlockStateItem = recipe.getGrowthStages().get(0).getBlock().asItem();
		if (firstBlockStateItem != Items.AIR) {
			inputs.add(EntryIngredients.of(firstBlockStateItem));
		}
		return inputs;
	}
	
	public static List<EntryIngredient> outputs(CrystallarieumRecipe recipe) {
		List<EntryIngredient> outputs = new ArrayList<>();
		outputs.add(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess())));
		for (ItemStack additionalOutput : recipe.getAdditionalOutputs(BasicDisplay.registryAccess())) {
			outputs.add(EntryIngredients.of(additionalOutput));
		}
		
		for (BlockState growthStageState : recipe.getGrowthStages()) {
			Item blockStateItem = growthStageState.getBlock().asItem();
			if (blockStateItem != Items.AIR) {
				outputs.add(EntryIngredients.of(blockStateItem));
			}
		}
		return outputs;
	}
	
	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return SpectrumPlugins.CRYSTALLARIEUM;
	}
	
	@Override
    public boolean isUnlocked() {
		Minecraft client = Minecraft.getInstance();
		return AdvancementHelper.hasAdvancement(client.player, CrystallarieumRecipe.UNLOCK_IDENTIFIER) && super.isUnlocked();
	}
	
}
