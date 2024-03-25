package de.dafuqs.spectrum.items;

import de.dafuqs.spectrum.api.item.LoomPatternProvider;
import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.inventories.CraftingTabletScreenHandler;
import de.dafuqs.spectrum.items.tooltip.CraftingTabletTooltipData;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipe;
import de.dafuqs.spectrum.registries.SpectrumBannerPatterns;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.List;
import java.util.Optional;

public class CraftingTabletItem extends Item implements LoomPatternProvider {
	
	private static final Component TITLE = Component.translatable("item.spectrum.crafting_tablet");
	
	public CraftingTabletItem(Properties settings) {
		super(settings);
	}
	
	public static void setStoredRecipe(ItemStack craftingTabletItemStack, Recipe<?> recipe) {
		CompoundTag nbtCompound = craftingTabletItemStack.getOrCreateTag();
		nbtCompound.putString("recipe", recipe.getId().toString());
		craftingTabletItemStack.setTag(nbtCompound);
	}
	
	public static void clearStoredRecipe(ItemStack craftingTabletItemStack) {
		CompoundTag nbtCompound = craftingTabletItemStack.getOrCreateTag();
		if (nbtCompound.contains("recipe")) {
			nbtCompound.remove("recipe");
			craftingTabletItemStack.setTag(nbtCompound);
		}
	}
	
	public static Recipe<?> getStoredRecipe(Level world, ItemStack itemStack) {
		if (world != null) {
			CompoundTag nbtCompound = itemStack.getTag();
			
			if (nbtCompound != null && nbtCompound.contains("recipe")) {
				String recipeString = nbtCompound.getString("recipe");
				ResourceLocation recipeIdentifier = new ResourceLocation(recipeString);
				
				Optional<? extends Recipe<?>> optional = world.getRecipeManager().byKey(recipeIdentifier);
				if (optional.isPresent()) {
					return optional.get();
				}
			}
		}
		return null;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
		
		if (!world.isClientSide) {
			Recipe<?> storedRecipe = getStoredRecipe(world, itemStack);
			if (storedRecipe == null || user.isShiftKeyDown()) {
				user.openMenu(createScreenHandlerFactory(world, (ServerPlayer) user, itemStack));
			} else {
				if (storedRecipe instanceof PedestalRecipe) {
					return InteractionResultHolder.pass(user.getItemInHand(hand));
				} else {
					tryCraftRecipe((ServerPlayer) user, storedRecipe);
				}
			}
			return InteractionResultHolder.success(user.getItemInHand(hand));
		} else {
			return InteractionResultHolder.consume(user.getItemInHand(hand));
		}
	}
	
	public MenuProvider createScreenHandlerFactory(Level world, ServerPlayer serverPlayerEntity, ItemStack itemStack) {
		return new SimpleMenuProvider((syncId, inventory, player) -> new CraftingTabletScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, serverPlayerEntity.blockPosition()), itemStack), TITLE);
	}
	
	public static void tryCraftRecipe(ServerPlayer serverPlayerEntity, Recipe<?> recipe) {
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		
		Container playerInventory = serverPlayerEntity.getInventory();
		if (InventoryHelper.hasInInventory(ingredients, playerInventory)) {
			List<ItemStack> remainders = InventoryHelper.removeFromInventoryWithRemainders(ingredients, playerInventory);
			
			ItemStack craftingResult = recipe.getResultItem(serverPlayerEntity.level().registryAccess()).copy();
			serverPlayerEntity.getInventory().placeItemBackInInventory(craftingResult);
			
			for (ItemStack remainder : remainders) {
				serverPlayerEntity.getInventory().placeItemBackInInventory(remainder);
			}
		}
	}
	
	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, tooltip, tooltipContext);
		Recipe<?> recipe = getStoredRecipe(world, itemStack);
		if (recipe == null) {
			tooltip.add(Component.translatable("item.spectrum.crafting_tablet.tooltip.no_recipe").withStyle(ChatFormatting.GRAY));
		} else {
			if (recipe instanceof PedestalRecipe) {
				tooltip.add(Component.translatable("item.spectrum.crafting_tablet.tooltip.pedestal_recipe").withStyle(ChatFormatting.GRAY));
			} else {
				tooltip.add(Component.translatable("item.spectrum.crafting_tablet.tooltip.crafting_recipe").withStyle(ChatFormatting.GRAY));
			}
			tooltip.add(Component.translatable("item.spectrum.crafting_tablet.tooltip.shift_to_view_gui").withStyle(ChatFormatting.GRAY));
		}
		
		addBannerPatternProviderTooltip(tooltip);
	}
	
	@Environment(EnvType.CLIENT)
	@Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		Minecraft client = Minecraft.getInstance();
		Recipe<?> storedRecipe = CraftingTabletItem.getStoredRecipe(client.level, stack);
		if (storedRecipe != null) {
			return Optional.of(new CraftingTabletTooltipData(storedRecipe));
		} else {
			return Optional.empty();
		}
	}
	
	@Override
	public Holder<BannerPattern> getPattern() {
		return SpectrumBannerPatterns.CRAFTING_TABLET;
	}
	
}
