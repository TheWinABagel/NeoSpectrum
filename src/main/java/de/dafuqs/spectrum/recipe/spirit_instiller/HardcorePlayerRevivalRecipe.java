package de.dafuqs.spectrum.recipe.spirit_instiller;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.spirit_instiller.SpiritInstillerBlockEntity;
import de.dafuqs.spectrum.cca.HardcoreDeathCapability;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class HardcorePlayerRevivalRecipe extends SpiritInstillerRecipe {
	
	public static final RecipeSerializer<HardcorePlayerRevivalRecipe> SERIALIZER = new EmptyRecipeSerializer<>(HardcorePlayerRevivalRecipe::new);
	
	public HardcorePlayerRevivalRecipe(ResourceLocation identifier) {
		super(identifier, "", false, null,
				IngredientStack.of(Ingredient.of(Blocks.PLAYER_HEAD.asItem())), IngredientStack.of(Ingredient.of(Items.TOTEM_OF_UNDYING)), IngredientStack.of(Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE)),
				ItemStack.EMPTY, 1200, 100, true);
	}
	
	@Override
	public ItemStack assemble(Container inv, RegistryAccess drm) {
		if (inv instanceof SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
			GameProfile gameProfile = getSkullOwner(inv.getItem(SpiritInstillerRecipe.CENTER_INGREDIENT));
			if (gameProfile != null) {
				ServerPlayer revivedPlayer = SpectrumCommon.minecraftServer.getPlayerList().getPlayerByName(gameProfile.getName());
				if (revivedPlayer != null) {
					HardcoreDeathCapability.removeHardcoreDeath(gameProfile);
					revivedPlayer.setGameMode(SpectrumCommon.minecraftServer.getDefaultGameType());
					
					Rotation blockRotation = spiritInstillerBlockEntity.getMultiblockRotation();
					float yaw = 0.0F;
					switch (blockRotation) {
						case NONE -> yaw = -90.0F;
						case CLOCKWISE_90 -> yaw = 0.0F;
						case CLOCKWISE_180 -> yaw = 900.0F;
						case COUNTERCLOCKWISE_90 -> yaw = 180.0F;
					}
					
					FabricDimensions.teleport(revivedPlayer, (ServerLevel) spiritInstillerBlockEntity.getLevel(), new PortalInfo(Vec3.atCenterOf(spiritInstillerBlockEntity.getBlockPos().above()), new Vec3(0, 0, 0), yaw, revivedPlayer.getXRot()));
				}
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canCraftWithStacks(Container inventory) {
		ItemStack instillerStack = inventory.getItem(0);
		if (instillerStack.is(Blocks.PLAYER_HEAD.asItem())) {
			GameProfile gameProfile = getSkullOwner(instillerStack);
			if (gameProfile == null) {
				return false;
			}
			
			PlayerList playerManager = SpectrumCommon.minecraftServer.getPlayerList();
			ServerPlayer playerToRevive = gameProfile.getId() == null ? playerManager.getPlayerByName(gameProfile.getName()) : playerManager.getPlayer(gameProfile.getId());
			return playerToRevive != null && HardcoreDeathCapability.hasHardcoreDeath(gameProfile);
		}
		return false;
	}
	
	@Override
	public boolean canPlayerCraft(Player playerEntity) {
		return true;
	}
	
	@Nullable
	private GameProfile getSkullOwner(ItemStack instillerStack) {
		GameProfile gameProfile = null;
		CompoundTag nbtCompound = instillerStack.getTag();
		if (nbtCompound != null) {
			if (nbtCompound.contains("SkullOwner", Tag.TAG_COMPOUND)) {
				gameProfile = NbtUtils.readGameProfile(nbtCompound.getCompound("SkullOwner"));
			} else if (nbtCompound.contains("SkullOwner", Tag.TAG_STRING) && !StringUtils.isBlank(nbtCompound.getString("SkullOwner"))) {
				gameProfile = new GameProfile(null, nbtCompound.getString("SkullOwner"));
			}
		}
		return gameProfile;
	}
	
	public static class Serializer implements RecipeSerializer<HardcorePlayerRevivalRecipe> {
		
		@Override
		public HardcorePlayerRevivalRecipe fromJson(ResourceLocation id, JsonObject json) {
			return null;
		}
		
		@Override
		public HardcorePlayerRevivalRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			return null;
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf buf, HardcorePlayerRevivalRecipe recipe) {
		
		}
	}
	
}
