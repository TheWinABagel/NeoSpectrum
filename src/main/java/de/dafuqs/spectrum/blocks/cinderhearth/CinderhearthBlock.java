package de.dafuqs.spectrum.blocks.cinderhearth;

import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

public class CinderhearthBlock extends BaseEntityBlock {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public CinderhearthBlock(Properties settings) {
		super(settings);
		this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.EAST));
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CinderhearthBlockEntity(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		if (world.isClientSide) {
			return null;
		} else {
			return createTickerHelper(type, SpectrumBlockEntities.CINDERHEARTH, CinderhearthBlockEntity::serverTick);
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			verifyStructure(world, pos, null);
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CinderhearthBlockEntity cinderhearthBlockEntity) {
				cinderhearthBlockEntity.setOwner(player);
				if (verifyStructure(world, pos, (ServerPlayer) player) != CinderhearthBlockEntity.CinderHearthStructureType.NONE) {
					player.openMenu(cinderhearthBlockEntity);
				}
			}
			return InteractionResult.CONSUME;
		}
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof CinderhearthBlockEntity cinderhearthBlockEntity) {
			if (placer instanceof Player player) {
				cinderhearthBlockEntity.setOwner(player);
			}
			if (itemStack.hasCustomHoverName()) {
				cinderhearthBlockEntity.setCustomName(itemStack.getHoverName());
			}
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CinderhearthBlockEntity cinderhearthBlockEntity) {
				if (world instanceof ServerLevel) {
					Containers.dropContents(world, pos, cinderhearthBlockEntity);
				}
				world.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, world, pos, newState, moved);
		}
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof CinderhearthBlockEntity cinderhearthBlockEntity) {
			Direction direction = state.getValue(FACING);
			Direction.Axis axis = direction.getAxis();
			double d = (double) pos.getX() + 0.5D;
			double e = pos.getY() + 0.4;
			double f = (double) pos.getZ() + 0.5D;
			
			Recipe<?> recipe = cinderhearthBlockEntity.getCurrentRecipe();
			if (recipe != null) {
				if (random.nextDouble() < 0.1D) {
					world.playLocalSound(d, e, f, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 0.8F, false);
				}
				
				double g = 0.35D;
				double h = random.nextDouble() * 0.4D - 0.2D;
				double i = axis == Direction.Axis.X ? (double) direction.getStepX() * g : h;
				double j = random.nextDouble() * 4.0D / 16.0D;
				double k = axis == Direction.Axis.Z ? (double) direction.getStepZ() * g : h;
				world.addParticle(ParticleTypes.FLAME, d + i, e + j, f + k, 0.0D, 0.0D, 0.0D);
				world.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0D, 0.0D, 0.0D);
				
				if (random.nextBoolean()) {
					double g2 = -3D / 16D;
					double h2 = 4D / 16D;
					double i2 = axis == Direction.Axis.X ? (double) direction.getStepX() * g2 : h2;
					double k2 = axis == Direction.Axis.Z ? (double) direction.getStepZ() * g2 : h2;
					world.addParticle(ParticleTypes.CLOUD, d + i2, pos.getY() + 1.1, f + k2, 0.0D, 0.06D, 0.0D);
				}
			}
			if (cinderhearthBlockEntity.structure == CinderhearthBlockEntity.CinderHearthStructureType.WITH_LAVA) {
				for (int v = 0; v < 2; v++) {
					double g3 = 1.5 - random.nextDouble() * 2.0;
					double h3 = 1.5 - random.nextDouble() * 3.0;
					double i3 = axis == Direction.Axis.X ? (double) direction.getStepX() * g3 : h3;
					double k3 = axis == Direction.Axis.Z ? (double) direction.getStepZ() * g3 : h3;
					world.addParticle(SpectrumParticleTypes.ORANGE_SPARKLE_RISING, d + i3, pos.getY() - 1.2, f + k3, 0.0D, 0.1D, 0.0D);
				}
			}
		}
	}
	
	public static CinderhearthBlockEntity.CinderHearthStructureType verifyStructure(Level world, @NotNull BlockPos blockPos, @Nullable ServerPlayer serverPlayerEntity) {
		Rotation rotation = Support.rotationFromDirection(world.getBlockState(blockPos).getValue(FACING).getOpposite());
		
		IMultiblock multiblockWithLava = SpectrumMultiblocks.MULTIBLOCKS.get(SpectrumMultiblocks.CINDERHEARTH_IDENTIFIER);
		IMultiblock multiblockWithoutLava = SpectrumMultiblocks.MULTIBLOCKS.get(SpectrumMultiblocks.CINDERHEARTH_WITHOUT_LAVA_IDENTIFIER);
		if (world.isClientSide) {
			if (multiblockWithoutLava.validate(world, blockPos.below(3), rotation)) {
				return CinderhearthBlockEntity.CinderHearthStructureType.WITH_LAVA;
			} else {
				PatchouliAPI.get().showMultiblock(multiblockWithLava, Component.translatable("multiblock.spectrum.cinderhearth.structure"), blockPos.below(4), rotation);
				return CinderhearthBlockEntity.CinderHearthStructureType.NONE;
			}
		} else {
			if (multiblockWithLava.validate(world, blockPos.below(3), rotation)) {
				if (serverPlayerEntity != null) {
					SpectrumAdvancementCriteria.COMPLETED_MULTIBLOCK.trigger(serverPlayerEntity, multiblockWithLava);
				}
				return CinderhearthBlockEntity.CinderHearthStructureType.WITH_LAVA;
			} else {
				if (multiblockWithoutLava.validate(world, blockPos.below(3), rotation)) {
					if (serverPlayerEntity != null) {
						SpectrumAdvancementCriteria.COMPLETED_MULTIBLOCK.trigger(serverPlayerEntity, multiblockWithoutLava);
					}
					return CinderhearthBlockEntity.CinderHearthStructureType.WITHOUT_LAVA;
				}
				return CinderhearthBlockEntity.CinderHearthStructureType.NONE;
			}
		}
	}
	
	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
		if (world.isClientSide()) {
			clearCurrentlyRenderedMultiBlock();
		}
	}
	
	public static void clearCurrentlyRenderedMultiBlock() {
		IMultiblock currentlyRenderedMultiBlock = PatchouliAPI.get().getCurrentMultiblock();
		if (currentlyRenderedMultiBlock != null && currentlyRenderedMultiBlock.getID().equals(SpectrumMultiblocks.CINDERHEARTH_IDENTIFIER)) {
			PatchouliAPI.get().clearMultiblock();
		}
	}
	
}
