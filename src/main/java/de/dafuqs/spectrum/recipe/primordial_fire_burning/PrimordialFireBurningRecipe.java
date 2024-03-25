package de.dafuqs.spectrum.recipe.primordial_fire_burning;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.entity.FireproofItemEntity;
import de.dafuqs.spectrum.inventories.AutoCraftingInventory;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class PrimordialFireBurningRecipe extends GatedSpectrumRecipe {
	
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("lategame/collect_doombloom_seed");
	
	private static AutoCraftingInventory AUTO_INVENTORY = new AutoCraftingInventory(1, 1);
	
	protected final Ingredient input;
	protected final ItemStack output;
	
	public PrimordialFireBurningRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Ingredient input, ItemStack output) {
		super(id, group, secret, requiredAdvancementIdentifier);
		
		this.input = input;
		this.output = output;
		
		registerInToastManager(getType(), this);
	}
	
	@Override
	public boolean matches(Container inv, Level world) {
		return this.input.test(inv.getItem(0));
	}
	
	@Override
	public ItemStack assemble(Container inv, RegistryAccess drm) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess registryManager) {
		return output;
	}
	
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(SpectrumBlocks.DOOMBLOOM);
	}
	
	@Override
	public ResourceLocation getRecipeTypeUnlockIdentifier() {
		return UNLOCK_IDENTIFIER;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.PRIMORDIAL_FIRE_BURNING_RECIPE_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.PRIMORDIAL_FIRE_BURNING;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> defaultedList = NonNullList.create();
		defaultedList.add(this.input);
		return defaultedList;
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.PRIMORDIAL_FIRE_BURNING_ID;
	}
	
	public static PrimordialFireBurningRecipe getRecipeFor(@NotNull Level world, ItemStack stack) {
		AUTO_INVENTORY.setInputInventory(Collections.singletonList(stack));
		return world.getRecipeManager().getRecipeFor(SpectrumRecipeTypes.PRIMORDIAL_FIRE_BURNING, AUTO_INVENTORY, world).orElse(null);
	}
	
	public static boolean processBlock(Level world, BlockPos pos, BlockState state) {
		Item item = state.getBlock().asItem();
		if(item == Items.AIR) {
			return false;
		}
		
		PrimordialFireBurningRecipe recipe = PrimordialFireBurningRecipe.getRecipeFor(world, item.getDefaultInstance());
		if (recipe == null) {
			return false;
		}
		
		AUTO_INVENTORY.setInputInventory(Collections.singletonList(state.getBlock().asItem().getDefaultInstance()));
		ItemStack output = recipe.assemble(AUTO_INVENTORY, world.registryAccess()).copy();
		
		world.playSound(null, pos, SpectrumSoundEvents.PRIMORDIAL_FIRE_CRACKLE, SoundSource.BLOCKS, 0.7F, 1.0F);
		if(output.getItem() instanceof BlockItem blockItem) {
			world.setBlockAndUpdate(pos, blockItem.getBlock().defaultBlockState());
		} else {
			FireproofItemEntity.scatter(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, output);
		}
		
		return true;
	}
	
	public static boolean processItemEntity(Level world, ItemEntity itemEntity) {
		Vec3 pos = itemEntity.position();
		
		ItemStack inputStack = itemEntity.getItem();
		PrimordialFireBurningRecipe recipe = PrimordialFireBurningRecipe.getRecipeFor(world, inputStack);
		if (recipe == null) {
			return false;
		}
		
		int inputCount = inputStack.getCount();
		AUTO_INVENTORY.setInputInventory(Collections.singletonList(inputStack));
		ItemStack outputStack = recipe.assemble(AUTO_INVENTORY, world.registryAccess()).copy();
		outputStack.setCount(outputStack.getCount() * inputCount);
		
		inputStack.setCount(0);
		itemEntity.discard();
		
		FireproofItemEntity.scatter(world, pos.x(), pos.y(), pos.z(), outputStack);
		world.playSound(null, itemEntity.blockPosition(), SpectrumSoundEvents.PRIMORDIAL_FIRE_CRACKLE, SoundSource.BLOCKS, 0.7F, 1.0F);
		
		return true;
	}
}
