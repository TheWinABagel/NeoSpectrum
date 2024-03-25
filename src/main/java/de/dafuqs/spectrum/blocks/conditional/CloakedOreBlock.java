package de.dafuqs.spectrum.blocks.conditional;

import de.dafuqs.revelationary.api.revelations.RevelationAware;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;
import java.util.Map;

public class CloakedOreBlock extends DropExperienceBlock implements RevelationAware {
	
	protected static boolean dropXP;
	protected final ResourceLocation cloakAdvancementIdentifier;
	protected final BlockState cloakBlockState;
	
	public CloakedOreBlock(Properties settings, UniformInt uniformIntProvider, ResourceLocation cloakAdvancementIdentifier, BlockState cloakBlockState) {
		super(settings, uniformIntProvider);
		this.cloakAdvancementIdentifier = cloakAdvancementIdentifier;
		this.cloakBlockState = cloakBlockState;
		RevelationAware.register(this);
	}
	
	@Override
	public Map<BlockState, BlockState> getBlockStateCloaks() {
		return Map.of(this.defaultBlockState(), cloakBlockState);
	}
	
	@Override
	public ResourceLocation getCloakAdvancementIdentifier() {
		return cloakAdvancementIdentifier;
	}
	
	@Override
	public Tuple<Item, Item> getItemCloak() {
		return new Tuple<>(this.asItem(), cloakBlockState.getBlock().asItem());
	}

	@Override
	@SuppressWarnings("deprecation")
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		// workaround: since onStacksDropped() has no way of checking if it was
		// triggered by a player we have to cache that information here
		Player lootPlayerEntity = RevelationAware.getLootPlayerEntity(builder);
		dropXP = lootPlayerEntity != null && isVisibleTo(lootPlayerEntity);
		
		return super.getDrops(state, builder);
	}


	
	@Override
	public void spawnAfterBreak(BlockState state, ServerLevel world, BlockPos pos, ItemStack stack, boolean dropExperience) {
		super.spawnAfterBreak(state, world, pos, stack, dropExperience && dropXP);
	}
	
}
