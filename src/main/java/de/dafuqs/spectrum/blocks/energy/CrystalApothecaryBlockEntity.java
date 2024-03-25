package de.dafuqs.spectrum.blocks.energy;

import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.api.block.PlayerOwnedWithName;
import de.dafuqs.spectrum.data_loaders.CrystalApothecarySimulationsDataLoader;
import de.dafuqs.spectrum.events.SpectrumGameEvents;
import de.dafuqs.spectrum.events.listeners.BlockPosEventQueue;
import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.inventories.GenericSpectrumContainerScreenHandler;
import de.dafuqs.spectrum.inventories.ScreenBackgroundVariant;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class CrystalApothecaryBlockEntity extends RandomizableContainerBlockEntity implements PlayerOwnedWithName, BlockPosEventQueue.Callback<BlockPosEventQueue.EventEntry> {
	
	private static final int RANGE = 12;
	private static final ItemStack HARVEST_ITEMSTACK = ItemStack.EMPTY;
	
	private final BlockPosEventQueue blockPosEventTransferListener;
	protected long compensationWorldTime;
	private NonNullList<ItemStack> inventory;
	private boolean listenerPaused;
	private UUID ownerUUID;
	private String ownerName;
	
	public CrystalApothecaryBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(SpectrumBlockEntities.CRYSTAL_APOTHECARY, blockPos, blockState);
		this.blockPosEventTransferListener = new BlockPosEventQueue(new BlockPositionSource(this.worldPosition), RANGE, this);
		this.inventory = NonNullList.withSize(27, ItemStack.EMPTY);
		this.listenerPaused = false;
		this.compensationWorldTime = -1;
	}
	
	public static void tick(Level world, BlockPos pos, BlockState state, CrystalApothecaryBlockEntity blockEntity) {
		if (!world.isClientSide) {
			blockEntity.blockPosEventTransferListener.tick(world);
			if (world.getGameTime() % 1000 == 0) {
				blockEntity.listenerPaused = false; // try to reset from time to time, to search for new clusters, even if full
			}
			if (blockEntity.compensationWorldTime > 0) {
				long compensationTicks = world.getGameTime() - blockEntity.compensationWorldTime;
				if (compensationTicks > 1200) { // only compensate if the time gap is at least 1 minute (lag)
					compensateGemstoneClusterDropsForUnloadedTicks(world, pos, blockEntity, compensationTicks);
				}
				blockEntity.compensationWorldTime = -1;
			}
		}
	}
	
	/**
	 * Scans the surrounding area for blocks in COMPENSATION_MAP
	 * and puts items into it's inventory, simulating it working for
	 * a specific amount of ticks, like when getting loaded after being
	 * unloaded after some time
	 * This function works with estimates, guessing how much time has passed and how many clusters would have grown
	 */
	private static void compensateGemstoneClusterDropsForUnloadedTicks(Level world, BlockPos blockPos, CrystalApothecaryBlockEntity blockEntity, long ticksToCompensate) {
		Map<Block, Integer> matches = new HashMap<>();
		
		// search for blocks in working range and sum them up
		Collection<Block> compensationBlocks = CrystalApothecarySimulationsDataLoader.COMPENSATIONS.keySet();
		for (BlockPos pos : BlockPos.withinManhattan(blockPos, RANGE, RANGE, RANGE)) {
			if (!blockPos.closerThan(pos, RANGE)) {
				continue;
			}
			
			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if (compensationBlocks.contains(block)) {
				int validBlocks = countValidGemstoneClusterBlocksAroundBlockPos(world, pos, CrystalApothecarySimulationsDataLoader.COMPENSATIONS.get(block).validNeighbors());
				if (matches.containsKey(block)) {
					matches.put(block, matches.get(block) + validBlocks);
				} else {
					matches.put(block, validBlocks);
				}
			}
		}
		
		// for each of those blocks generate some loot
		double gameRuleTickModifier = world.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).get() / 3.0;
		for (Map.Entry<Block, Integer> match : matches.entrySet()) {
			CrystalApothecarySimulationsDataLoader.SimulatedBlockGrowthEntry drop = CrystalApothecarySimulationsDataLoader.COMPENSATIONS.get(match.getKey());
			
			int compensatedItemCount = (int) (drop.compensatedStack().getCount() * match.getValue() * gameRuleTickModifier * ticksToCompensate) / drop.ticksForCompensationLootPerValidNeighbor();
			compensatedItemCount *= 0.8 + world.random.nextFloat() * 0.4;
			if (compensatedItemCount > 0) {
				ItemStack compensatedStack = drop.compensatedStack().copy();
				compensatedStack.setCount(compensatedItemCount);
				
				ItemStack remainingStack = InventoryHelper.smartAddToInventory(compensatedStack, blockEntity, null);
				if (!remainingStack.isEmpty()) {
					break; // overflow will be voided
				}
			}
		}
	}
	
	public static int countValidGemstoneClusterBlocksAroundBlockPos(Level world, BlockPos blockPos, Collection<Block> allowedBlocks) {
		int count = 0;
		for (Direction direction : Direction.values()) {
			BlockState offsetState = world.getBlockState(blockPos.relative(direction));
			if (offsetState.isAir() || offsetState.getBlock() == Blocks.WATER || allowedBlocks.contains(offsetState.getBlock())) {
				count++;
			}
		}
		return count;
	}
	
	@Override
	protected Component getDefaultName() {
		if (hasOwner()) {
			return Component.translatable("block.spectrum.crystal_apothecary.owner", this.ownerName);
		} else {
			return Component.translatable("block.spectrum.crystal_apothecary");
		}
	}
	
	public BlockPosEventQueue getEventListener() {
		return this.blockPosEventTransferListener;
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(nbt)) {
			ContainerHelper.loadAllItems(nbt, this.inventory);
		}
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
		if (nbt.contains("ListenerPaused")) {
			this.listenerPaused = nbt.getBoolean("ListenerPaused");
		}
		this.ownerName = PlayerOwned.readOwnerName(nbt);
		if (nbt.contains("LastWorldTime")) {
			this.compensationWorldTime = nbt.getLong("LastWorldTime");
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (!this.trySaveLootTable(nbt)) {
			ContainerHelper.saveAllItems(nbt, this.inventory);
		}
		nbt.putBoolean("ListenerPaused", this.listenerPaused);
		if (this.getLevel() != null) {
			nbt.putLong("LastWorldTime", this.getLevel().getGameTime());
		}
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
		PlayerOwned.writeOwnerName(nbt, this.ownerName);
	}
	
	@Override
	public int getContainerSize() {
		return 27;
	}
	
	@Override
	public void setItem(int slot, ItemStack stack) {
		if (stack.isEmpty() && this.listenerPaused) {
			this.listenerPaused = false;
			harvestExistingClusters();
		}
		super.setItem(slot, stack);
	}
	
	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.inventory;
	}
	
	@Override
	protected void setItems(NonNullList<ItemStack> list) {
		this.inventory = list;
	}
	
	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return GenericSpectrumContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this, ScreenBackgroundVariant.MIDGAME);
	}
	
	@Override
	public boolean canAcceptEvent(Level world, GameEventListener listener, GameEvent.ListenerInfo message, Vec3 sourcePos) {
		return message.gameEvent() == SpectrumGameEvents.BLOCK_CHANGED && !this.listenerPaused && message.context().affectedState().is(SpectrumBlockTags.CRYSTAL_APOTHECARY_HARVESTABLE);
	}
	
	@Override
	public void triggerEvent(Level world, GameEventListener listener, BlockPosEventQueue.EventEntry entry) {
		if (listener instanceof BlockPosEventQueue && this.getLevel() != null) {
			BlockPos eventPos = entry.eventSourceBlockPos;
			BlockState eventState = world.getBlockState(eventPos);
			if (eventState.is(SpectrumBlockTags.CRYSTAL_APOTHECARY_HARVESTABLE)) {
				// harvest
				LootParams.Builder builder = new LootParams.Builder((ServerLevel) world)
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(eventPos))
					.withParameter(LootContextParams.TOOL, HARVEST_ITEMSTACK)
					.withOptionalParameter(LootContextParams.THIS_ENTITY, getOwnerIfOnline())
					.withOptionalParameter(LootContextParams.BLOCK_ENTITY, eventState.hasBlockEntity() ? this.getLevel().getBlockEntity(eventPos) : null);

				List<ItemStack> drops = eventState.getDrops(builder);
				boolean anyDropsUsed = drops.size() == 0;
				for (ItemStack drop : drops) {
					if (hasOwner()) {
						Player owner = getOwnerIfOnline();
						if (owner instanceof ServerPlayer serverPlayerEntity) {
							SpectrumAdvancementCriteria.CRYSTAL_APOTHECARY_COLLECTING.trigger(serverPlayerEntity, drop);
						}
					}
					ItemStack remainingStack = InventoryHelper.smartAddToInventory(drop, this, null);
					if (remainingStack.isEmpty() || drop.getCount() != remainingStack.getCount()) {
						anyDropsUsed = true;
					}
					// remaining items are voided to not cause lag
				}
				
				if (anyDropsUsed) {
					world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, eventPos, Block.getId(eventState)); // block break particles & sound
					if (eventState.getBlock() instanceof SimpleWaterloggedBlock && eventState.getValue(BlockStateProperties.WATERLOGGED)) {
						world.setBlockAndUpdate(eventPos, Blocks.WATER.defaultBlockState());
					} else {
						world.setBlockAndUpdate(eventPos, Blocks.AIR.defaultBlockState());
					}
				} else {
					this.listenerPaused = true;
				}
			}
		}
	}
	
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public String getOwnerName() {
		return this.ownerName;
	}
	
	@Override
	public void setOwner(Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		this.ownerName = playerEntity.getName().getString();
		setChanged();
	}
	
	protected void harvestExistingClusters() {
		if (level instanceof ServerLevel serverWorld) {
			for (BlockPos currPos : BlockPos.withinManhattan(this.worldPosition, RANGE, RANGE, RANGE)) {
				if (!currPos.closerThan(worldPosition, RANGE)) {
					continue;
				}
				
				if (level.getBlockState(currPos).is(SpectrumBlockTags.CRYSTAL_APOTHECARY_HARVESTABLE)) {
					this.blockPosEventTransferListener.acceptEvent(serverWorld,
							new GameEvent.ListenerInfo(SpectrumGameEvents.BLOCK_CHANGED, Vec3.atCenterOf(currPos), GameEvent.Context.of(level.getBlockState(currPos)),
									this.blockPosEventTransferListener, Vec3.atCenterOf(this.worldPosition)), Vec3.atCenterOf(this.worldPosition));
				}
			}
		}
	}
}
