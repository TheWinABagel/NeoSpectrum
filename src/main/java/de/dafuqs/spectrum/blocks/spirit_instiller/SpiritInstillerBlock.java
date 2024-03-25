package de.dafuqs.spectrum.blocks.spirit_instiller;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.InWorldInteractionBlock;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

public class SpiritInstillerBlock extends InWorldInteractionBlock {
	
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("midgame/build_spirit_instiller_structure");
	protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
	
	public SpiritInstillerBlock(Properties settings) {
		super(settings);
	}
	
	public static void clearCurrentlyRenderedMultiBlock(Level world) {
		if (world.isClientSide) {
			IMultiblock currentlyRenderedMultiBlock = PatchouliAPI.get().getCurrentMultiblock();
			if (currentlyRenderedMultiBlock != null && currentlyRenderedMultiBlock.getID().equals(SpectrumMultiblocks.SPIRIT_INSTILLER_IDENTIFIER)) {
				PatchouliAPI.get().clearMultiblock();
			}
		}
	}
	
	public static boolean verifyStructure(Level world, @NotNull BlockPos blockPos, @Nullable ServerPlayer serverPlayerEntity, @NotNull SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
		IMultiblock multiblock = SpectrumMultiblocks.MULTIBLOCKS.get(SpectrumMultiblocks.SPIRIT_INSTILLER_IDENTIFIER);
		
		Rotation lastBlockRotation = spiritInstillerBlockEntity.getMultiblockRotation();
		boolean valid = false;
		
		// try all 4 rotations
		int offset = -4;
		Rotation checkRotation = lastBlockRotation;
		for (int i = 0; i < Rotation.values().length; i++) {
			valid = multiblock.validate(world, blockPos.below(1).relative(Support.directionFromRotation(checkRotation), offset), checkRotation);
			if (valid) {
				if (i != 0) {
					spiritInstillerBlockEntity.setMultiblockRotation(checkRotation);
				}
				break;
			} else {
				checkRotation = Rotation.values()[(checkRotation.ordinal() + 1) % Rotation.values().length];
			}
		}
		
		if (valid) {
			if (serverPlayerEntity != null) {
				SpectrumAdvancementCriteria.COMPLETED_MULTIBLOCK.trigger(serverPlayerEntity, multiblock);
			}
		} else {
			if (world.isClientSide) {
				IMultiblock currentMultiBlock = PatchouliAPI.get().getCurrentMultiblock();
				if (currentMultiBlock == multiblock) {
					lastBlockRotation = Rotation.values()[(lastBlockRotation.ordinal() + 1) % Rotation.values().length]; // cycle rotation
					spiritInstillerBlockEntity.setMultiblockRotation(lastBlockRotation);
				}
				PatchouliAPI.get().showMultiblock(multiblock, Component.translatable("multiblock.spectrum.spirit_instiller.structure"), blockPos.below(2).relative(Support.directionFromRotation(lastBlockRotation), offset), lastBlockRotation);
			} else {
				scatterContents(world, blockPos);
			}
		}
		
		return valid;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SpiritInstillerBlockEntity(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		if (world.isClientSide) {
			return createTickerHelper(type, SpectrumBlockEntities.SPIRIT_INSTILLER, SpiritInstillerBlockEntity::clientTick);
		} else {
			return createTickerHelper(type, SpectrumBlockEntities.SPIRIT_INSTILLER, SpiritInstillerBlockEntity::serverTick);
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
		if (world.isClientSide()) {
			clearCurrentlyRenderedMultiBlock((Level) world);
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (world.isClientSide) {
			if (blockEntity instanceof SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
				verifyStructure(world, pos, null, spiritInstillerBlockEntity);
			}
			return InteractionResult.SUCCESS;
		} else {
			if (blockEntity instanceof SpiritInstillerBlockEntity spiritInstillerBlockEntity) {
				if (verifyStructure(world, pos, (ServerPlayer) player, spiritInstillerBlockEntity)) {
					ItemStack handStack = player.getItemInHand(hand);
					if (exchangeStack(world, pos, player, hand, handStack, spiritInstillerBlockEntity)) {
						spiritInstillerBlockEntity.setOwner(player);
						spiritInstillerBlockEntity.inventoryChanged();
					}
				}
			}
			return InteractionResult.CONSUME;
		}
	}
	
}
