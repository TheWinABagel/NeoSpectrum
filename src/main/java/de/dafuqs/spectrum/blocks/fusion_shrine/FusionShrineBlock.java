package de.dafuqs.spectrum.blocks.fusion_shrine;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.InWorldInteractionBlock;
import de.dafuqs.spectrum.inventories.storage.DroppedItemStorage;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumMultiblocks;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

@SuppressWarnings("UnstableApiUsage")
public class FusionShrineBlock extends InWorldInteractionBlock {
	
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("collect_all_basic_pigments_besides_brown");
	public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 15);
	protected static final VoxelShape SHAPE;
	
	public FusionShrineBlock(Properties settings) {
		super(settings);
		registerDefaultState(getStateDefinition().any().setValue(LIGHT_LEVEL, 0));
	}
	
	public static void clearCurrentlyRenderedMultiBlock(Level world) {
		if (world.isClientSide) {
			IMultiblock currentlyRenderedMultiBlock = PatchouliAPI.get().getCurrentMultiblock();
			if (currentlyRenderedMultiBlock != null && currentlyRenderedMultiBlock.getID().equals(SpectrumMultiblocks.FUSION_SHRINE_IDENTIFIER)) {
				PatchouliAPI.get().clearMultiblock();
			}
		}
	}
	
	public static boolean verifySkyAccess(ServerLevel world, BlockPos blockPos) {
		if (!world.getBlockState(blockPos.above()).isAir()) {
			world.playSound(null, blockPos, SpectrumSoundEvents.USE_FAIL, SoundSource.NEUTRAL, 1.0F, 1.0F);
			return false;
		}
		if (!world.canSeeSky(blockPos)) {
			SpectrumS2CPacketSender.playParticleWithExactVelocity(world, new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5), SpectrumParticleTypes.RED_SPARKLE_RISING, 1, new Vec3(0, 0.5, 0));
			world.playSound(null, blockPos, SpectrumSoundEvents.USE_FAIL, SoundSource.NEUTRAL, 1.0F, 1.0F);
			return false;
		}
		return true;
	}
	
	public static boolean verifyStructure(Level world, BlockPos blockPos, @Nullable ServerPlayer serverPlayerEntity) {
		IMultiblock multiblock = SpectrumMultiblocks.MULTIBLOCKS.get(SpectrumMultiblocks.FUSION_SHRINE_IDENTIFIER);
		boolean valid = multiblock.validate(world, blockPos.below(), Rotation.NONE);
		
		if (valid) {
			if (serverPlayerEntity != null) {
				SpectrumAdvancementCriteria.COMPLETED_MULTIBLOCK.trigger(serverPlayerEntity, multiblock);
			}
		} else {
			if (world.isClientSide) {
				IMultiblock currentMultiBlock = PatchouliAPI.get().getCurrentMultiblock();
				if (currentMultiBlock == multiblock) {
					PatchouliAPI.get().clearMultiblock();
				} else {
					PatchouliAPI.get().showMultiblock(multiblock, Component.translatable("multiblock.spectrum.fusion_shrine.structure"), blockPos.below(2), Rotation.NONE);
				}
			} else if (world.getBlockEntity(blockPos) instanceof FusionShrineBlockEntity fusionShrineBlockEntity) {
				fusionShrineBlockEntity.scatterContents(world);
			}
		}
		
		return valid;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LIGHT_LEVEL);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FusionShrineBlockEntity(pos, state);
	}
	
	@Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		if(world.getBlockEntity(pos) instanceof FusionShrineBlockEntity blockEntity) {
			NonNullList<ItemStack> inventory = blockEntity.getItems();
			
			int i = 0;
	        float f = 0.0f;
	        for (int j = 0; j < inventory.size(); ++j) {
	            ItemStack itemStack = blockEntity.getItem(j);
	            if (itemStack.isEmpty()) continue;
	            f += (float)itemStack.getCount() / (float)Math.min(blockEntity.getMaxStackSize(), itemStack.getMaxStackSize());
	            ++i;
	        }
			
			if (blockEntity.fluidStorage.getFluidAmount() > 0) {
				f += (float)blockEntity.fluidStorage.getFluidAmount() / (float)blockEntity.fluidStorage.getCapacity();
				++i;
			}
			
	        return Mth.floor(f / ((float) inventory.size() + 1) * 14.0f) + (i > 0 ? 1 : 0);
		}
		
		return 0;
    }
	
	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
		if (world.isClientSide()) {
			clearCurrentlyRenderedMultiBlock((Level) world);
		}
	}
	
	@Override
	public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if(!world.isClientSide) {
			// Specially handle fluid items
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if(entity instanceof ItemEntity itemEntity && blockEntity instanceof FusionShrineBlockEntity fusionShrineBlockEntity) {
				FluidTank storage = fusionShrineBlockEntity.fluidStorage;
				ItemStack itemStack = itemEntity.getItem();

				// We're not considering stacked fluid storages for the time being
				if(itemStack.getCount() == 1) {
					Item item = itemStack.getItem();
//					SingleSlotStorage<ItemVariant> slot = new DroppedItemStorage(item, itemStack.getTag());
					ItemStackHandler slot = new DroppedItemStorage(item, itemStack.getTag());
					//todoforge fluid item handler maybe? NEEDS TO BE REIMPLEMENTED
//					SingleSlotContainerItemContext ctx = new SingleSlotContainerItemContext(slot);
//					Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(itemStack, ctx);
//
//					if(fluidStorage != null) {
//						boolean anyInserted = false;
//						for(StorageView<FluidVariant> view : fluidStorage) {
//							try(Transaction transaction = Transaction.openOuter()) {
//								FluidVariant variant = view.getResource();
//								long inserted = variant.isBlank() ? 0 : storage.insert(variant, view.getAmount(), transaction);
//								long extracted = fluidStorage.extract(variant, inserted, transaction);
//								if(inserted == extracted && inserted != 0) {
//									anyInserted = true;
//									transaction.commit();
//								}
//							}
//						}
//
//						if(!anyInserted && !storage.getResource().isBlank()) {
//							try(Transaction transaction = Transaction.openOuter()) {
//								long inserted = fluidStorage.insert(storage.getResource(), storage.getAmount(), transaction);
//								long extracted = storage.extract(storage.getResource(), inserted, transaction);
//								if(inserted == extracted && inserted != 0) {
//									transaction.commit();
//								}
//							}
//						}
//
//						itemEntity.setItem(slot.getResource().toStack(itemStack.getCount()));
//						return;
//					}
				}
			}

			// do not pick up items that were results of crafting
			if(entity.position().x % 0.5 != 0 && entity.position().z % 0.5 != 0) {
				super.fallOn(world, state, pos, entity, fallDistance);
			}
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			verifyStructure(world, pos, null);
			return InteractionResult.SUCCESS;
		} else {
			verifySkyAccess((ServerLevel) world, pos);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			// if the structure is valid the player can put / retrieve items and fluids into the shrine
			if (blockEntity instanceof FusionShrineBlockEntity fusionShrineBlockEntity && verifyStructure(world, pos, (ServerPlayer) player)) {
				fusionShrineBlockEntity.setOwner(player);

				ItemStack handStack = player.getItemInHand(hand);
				if (FluidUtil.interactWithFluidHandler(player, hand, fusionShrineBlockEntity.fluidStorage)
				|| (player.isShiftKeyDown() || handStack.isEmpty()) && retrieveLastStack(world, pos, player, hand, handStack, fusionShrineBlockEntity)
				|| !handStack.isEmpty() && inputHandStack(world, player, hand, handStack, fusionShrineBlockEntity)) {
					fusionShrineBlockEntity.updateInClientWorld();
				}
			}

			return InteractionResult.CONSUME;
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		if (world.isClientSide) {
			return createTickerHelper(type, SpectrumBlockEntities.FUSION_SHRINE, FusionShrineBlockEntity::clientTick);
		} else {
			return createTickerHelper(type, SpectrumBlockEntities.FUSION_SHRINE, FusionShrineBlockEntity::serverTick);
		}
	}
	
	static {
		VoxelShape neck = Block.box(2, 0, 2, 14, 12, 14);
		VoxelShape head = Block.box(1, 12, 1, 15, 15, 15);
		VoxelShape crystal = Block.box(6.5, 13, 6.5, 9.5, 23, 9.5);
		neck = Shapes.or(neck, head);
		SHAPE = Shapes.or(neck, crystal);
	}
}
