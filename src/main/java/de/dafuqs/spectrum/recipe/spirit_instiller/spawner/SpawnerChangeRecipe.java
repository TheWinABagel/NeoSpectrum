package de.dafuqs.spectrum.recipe.spirit_instiller.spawner;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.matchbooks.recipe.matchbook.Matchbook;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.item_bowl.ItemBowlBlockEntity;
import de.dafuqs.spectrum.blocks.spirit_instiller.SpiritInstillerBlockEntity;
import de.dafuqs.spectrum.recipe.spirit_instiller.SpiritInstillerRecipe;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class SpawnerChangeRecipe extends SpiritInstillerRecipe {
	
	public SpawnerChangeRecipe(ResourceLocation identifier, IngredientStack ingredient) {
		super(identifier, "spawner_manipulation", false, SpectrumCommon.locate("milestones/unlock_spawner_manipulation"),
				IngredientStack.of(Ingredient.of(Items.SPAWNER)), ingredient, IngredientStack.of(Ingredient.of(SpectrumItems.VEGETAL), Matchbook.empty(), null, 4),
				Items.SPAWNER.getDefaultInstance(), 200, 0, true);
	}
	
	@Override
	public ItemStack assemble(Container inv, RegistryAccess drm) {
		ItemStack resultStack = ItemStack.EMPTY;
		
		if (inv instanceof SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
			BlockEntity leftBowlBlockEntity = spiritInstillerBlockEntity.getLevel().getBlockEntity(SpiritInstillerBlockEntity.getItemBowlPos(spiritInstillerBlockEntity, false));
			BlockEntity rightBowlBlockEntity = spiritInstillerBlockEntity.getLevel().getBlockEntity(SpiritInstillerBlockEntity.getItemBowlPos(spiritInstillerBlockEntity, true));
			if (leftBowlBlockEntity instanceof ItemBowlBlockEntity leftBowl && rightBowlBlockEntity instanceof ItemBowlBlockEntity rightBowl) {
				Level world = spiritInstillerBlockEntity.getLevel();
				BlockPos pos = spiritInstillerBlockEntity.getBlockPos();
				
				ItemStack firstBowlStack = leftBowl.getItem(0);
				ItemStack secondBowlStack = rightBowl.getItem(0);
				ItemStack spawnerStack = spiritInstillerBlockEntity.getItem(0);
				
				CompoundTag spawnerNbt = spawnerStack.getOrCreateTag();
				CompoundTag blockEntityTag;
				if (spawnerNbt.contains("BlockEntityTag")) {
					blockEntityTag = spawnerNbt.getCompound("BlockEntityTag").copy();
				} else {
					blockEntityTag = new CompoundTag();
				}
				
				blockEntityTag = getSpawnerResultNbt(blockEntityTag, firstBowlStack, secondBowlStack);
				
				resultStack = spawnerStack.copy();
				resultStack.setCount(1);
				resultStack.addTagElement("BlockEntityTag", blockEntityTag);
				
				spawnXPAndGrantAdvancements(resultStack, spiritInstillerBlockEntity, spiritInstillerBlockEntity.getUpgradeHolder(), world, pos);
			}
		}
		
		return resultStack;
	}
	
	@Override
	public boolean canCraftWithStacks(Container inventory) {
		CompoundTag blockEntityTag = inventory.getItem(0).getTagElement("BlockEntityTag");
		if (blockEntityTag == null) {
			return true;
		}
		return canCraftWithBlockEntityTag(blockEntityTag, inventory.getItem(1), inventory.getItem(2));
	}
	
	// Overwrite these
	@Override
	public abstract RecipeSerializer<?> getSerializer();
	
	public abstract boolean canCraftWithBlockEntityTag(CompoundTag spawnerBlockEntityNbt, ItemStack leftBowlStack, ItemStack rightBowlStack);
	
	public abstract CompoundTag getSpawnerResultNbt(CompoundTag spawnerBlockEntityNbt, ItemStack secondBowlStack, ItemStack centerStack);
	
	public abstract Component getOutputLoreText();
	
}
