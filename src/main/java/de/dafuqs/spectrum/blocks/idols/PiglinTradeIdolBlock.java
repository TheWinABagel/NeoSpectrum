package de.dafuqs.spectrum.blocks.idols;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PiglinTradeIdolBlock extends IdolBlock {
	
	public PiglinTradeIdolBlock(Properties settings, ParticleOptions particleEffect) {
		super(settings, particleEffect);
	}
	
	private static List<ItemStack> getBarteredStacks(@NotNull ServerLevel world, BlockPos blockPos) {
		Piglin piglin = new Piglin(EntityType.PIGLIN, world);
		piglin.setPosRaw(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		
		LootTable lootTable = world.getServer().getLootData().getLootTable(BuiltInLootTables.PIGLIN_BARTERING);
		List<ItemStack> loot = lootTable.getRandomItems(new LootParams.Builder(world).withParameter(LootContextParams.THIS_ENTITY, piglin).create(LootContextParamSets.PIGLIN_BARTER));
		
		piglin.discard();
		
		return loot;
	}
	
	@Override
	public boolean trigger(ServerLevel world, BlockPos blockPos, BlockState state, @Nullable Entity entity, Direction side) {
		if (entity instanceof ItemEntity itemEntity) {
			ItemStack stack = itemEntity.getItem();
			if (stack.is(PiglinAi.BARTERING_ITEM)) {
				int newAmount = stack.getCount() - 1;
				if (newAmount <= 0) {
					itemEntity.discard();
				} else {
					stack.shrink(1);
				}
				
				outputLoot(world, blockPos, side);
				return true;
			}
		} else if (entity instanceof Player player) {
			for (ItemStack handStack : player.getHandSlots()) {
				if (handStack.is(PiglinAi.BARTERING_ITEM)) {
					handStack.shrink(1);
					
					outputLoot(world, blockPos, side);
					return true;
				}
			}
		}
		return false;
	}
	
	private void outputLoot(ServerLevel world, BlockPos blockPos, Direction side) {
		Position outputLocation = getOutputLocation(new BlockSourceImpl(world, blockPos), side);
		for (ItemStack barteredStack : getBarteredStacks(world, blockPos)) {
			ItemEntity itemEntity = new ItemEntity(world, outputLocation.x(), outputLocation.y(), outputLocation.z(), barteredStack);
			itemEntity.push(side.getStepX() * 0.25, side.getStepY() * 0.25 + 0.03, side.getStepZ() * 0.25);
			world.addFreshEntity(itemEntity);
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("block.spectrum.piglin_trade_idol.tooltip"));
	}
	
}
