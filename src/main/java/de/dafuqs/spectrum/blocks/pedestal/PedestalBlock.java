package de.dafuqs.spectrum.blocks.pedestal;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.PaintbrushTriggered;
import de.dafuqs.spectrum.api.block.PedestalVariant;
import de.dafuqs.spectrum.api.block.RedstonePoweredBlock;
import de.dafuqs.spectrum.api.recipe.GatedRecipe;
import de.dafuqs.spectrum.blocks.InWorldInteractionBlock;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipeTier;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumMultiblocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

public class PedestalBlock extends BaseEntityBlock implements RedstonePoweredBlock, PaintbrushTriggered {
	
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("place_pedestal");
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");
	private static final VoxelShape SHAPE;
	private final PedestalVariant variant;
	
	public PedestalBlock(Properties settings, PedestalVariant variant) {
		super(settings);
		this.variant = variant;
		registerDefaultState(getStateDefinition().any().setValue(POWERED, false));
	}
	
	/**
	 * Sets pedestal to a new tier
	 * while keeping the inventory and all other data
	 */
	public static void upgradeToVariant(@NotNull Level world, BlockPos blockPos, PedestalVariant newPedestalVariant) {
		world.setBlockAndUpdate(blockPos, newPedestalVariant.getPedestalBlock().getStateForPlacement(new DirectionalPlaceContext(world, blockPos, Direction.DOWN, null, Direction.UP)));
	}
	
	public static void clearCurrentlyRenderedMultiBlock(Level world) {
		if (world.isClientSide) {
			IMultiblock currentlyRenderedMultiBlock = PatchouliAPI.get().getCurrentMultiblock();
			if (currentlyRenderedMultiBlock != null
					&& (currentlyRenderedMultiBlock.getID().equals(SpectrumMultiblocks.PEDESTAL_SIMPLE_STRUCTURE_IDENTIFIER_CHECK)
					|| currentlyRenderedMultiBlock.getID().equals(SpectrumMultiblocks.PEDESTAL_ADVANCED_STRUCTURE_IDENTIFIER_CHECK)
					|| currentlyRenderedMultiBlock.getID().equals(SpectrumMultiblocks.PEDESTAL_COMPLEX_STRUCTURE_WITHOUT_MOONSTONE_IDENTIFIER_CHECK)
					|| currentlyRenderedMultiBlock.getID().equals(SpectrumMultiblocks.PEDESTAL_COMPLEX_STRUCTURE_IDENTIFIER_CHECK))) {
				
				PatchouliAPI.get().clearMultiblock();
			}
		}
	}
	
	/**
	 * Called when a pedestal is upgraded to a new tier
	 * (like amethyst to the cmy variant). Spawns lots of matching particles.
	 *
	 * @param newPedestalRecipeTier The tier the pedestal has been upgraded to
	 */
	@Environment(EnvType.CLIENT)
    public static void spawnUpgradeParticleEffectsForTier(BlockPos blockPos, @NotNull PedestalRecipeTier newPedestalRecipeTier) {
		Minecraft client = Minecraft.getInstance();
		Level world = client.level;
		RandomSource random = world.getRandom();
		
		switch (newPedestalRecipeTier) {
			case COMPLEX -> {
				ParticleOptions particleEffect = SpectrumParticleTypes.getCraftingParticle(DyeColor.WHITE);
				for (int i = 0; i < 25; i++) {
					float randomZ = random.nextFloat() * 1.2F;
					world.addParticle(particleEffect, blockPos.getX() + 1.1, blockPos.getY(), blockPos.getZ() + randomZ, 0.0D, 0.03D, 0.0D);
				}
				for (int i = 0; i < 25; i++) {
					float randomZ = random.nextFloat() * 1.2F;
					world.addParticle(particleEffect, blockPos.getX() - 0.1, blockPos.getY(), blockPos.getZ() + randomZ, 0.0D, 0.03D, 0.0D);
				}
				for (int i = 0; i < 25; i++) {
					float randomX = random.nextFloat() * 1.2F;
					world.addParticle(particleEffect, blockPos.getX() + randomX, blockPos.getY(), blockPos.getZ() + 1.1, 0.0D, 0.03D, 0.0D);
				}
				for (int i = 0; i < 25; i++) {
					float randomX = random.nextFloat() * 1.2F;
					world.addParticle(particleEffect, blockPos.getX() + randomX, blockPos.getY(), blockPos.getZ() - 0.1, 0.0D, 0.03D, 0.0D);
				}
			}
			case ADVANCED -> {
				ParticleOptions particleEffect = SpectrumParticleTypes.getCraftingParticle(DyeColor.BLACK);
				for (int i = 0; i < 25; i++) {
					float randomZ = random.nextFloat() * 1.2F;
					world.addParticle(particleEffect, blockPos.getX() + 1.1, blockPos.getY(), blockPos.getZ() + randomZ, 0.0D, 0.03D, 0.0D);
				}
				for (int i = 0; i < 25; i++) {
					float randomZ = random.nextFloat() * 1.2F;
					world.addParticle(particleEffect, blockPos.getX() - 0.1, blockPos.getY(), blockPos.getZ() + randomZ, 0.0D, 0.03D, 0.0D);
				}
				for (int i = 0; i < 25; i++) {
					float randomX = random.nextFloat() * 1.2F;
					world.addParticle(particleEffect, blockPos.getX() + randomX, blockPos.getY(), blockPos.getZ() + 1.1, 0.0D, 0.03D, 0.0D);
				}
				for (int i = 0; i < 25; i++) {
					float randomX = random.nextFloat() * 1.2F;
					world.addParticle(particleEffect, blockPos.getX() + randomX, blockPos.getY(), blockPos.getZ() - 0.1, 0.0D, 0.03D, 0.0D);
				}
			}
			case SIMPLE -> {
				ParticleOptions particleEffectC = SpectrumParticleTypes.getCraftingParticle(DyeColor.CYAN);
				ParticleOptions particleEffectM = SpectrumParticleTypes.getCraftingParticle(DyeColor.MAGENTA);
				ParticleOptions particleEffectY = SpectrumParticleTypes.getCraftingParticle(DyeColor.YELLOW);
				for (int i = 0; i < 25; i++) {
					float randomZ = random.nextFloat() * 1.2F;
					world.addParticle(particleEffectY, blockPos.getX() + 1.1, blockPos.getY() + 0.1, blockPos.getZ() + randomZ, 0.0D, 0.05D, 0.0D);
				}
				for (int i = 0; i < 25; i++) {
					float randomZ = random.nextFloat() * 1.2F;
					world.addParticle(particleEffectC, blockPos.getX() - 0.1, blockPos.getY() + 0.1, blockPos.getZ() + randomZ, 0.0D, 0.05D, 0.0D);
				}
				for (int i = 0; i < 25; i++) {
					float randomX = random.nextFloat() * 1.2F;
					world.addParticle(particleEffectM, blockPos.getX() + randomX, blockPos.getY() + 0.1, blockPos.getZ() + 1.1, 0.0D, 0.05D, 0.0D);
				}
				for (int i = 0; i < 25; i++) {
					float randomX = random.nextFloat() * 1.2F;
					world.addParticle(particleEffectM, blockPos.getX() + randomX, blockPos.getY() + 0.1, blockPos.getZ() - 0.1, 0.0D, 0.05D, 0.0D);
				}
			}
			case BASIC -> {
			}
		}
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		if (placer instanceof ServerPlayer) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof PedestalBlockEntity pedestalBlockEntity) {
				pedestalBlockEntity.setOwner((ServerPlayer) placer);
				if (itemStack.hasCustomHoverName()) {
					pedestalBlockEntity.setCustomName(itemStack.getHoverName());
				}
				blockEntity.setChanged();
			}
		}
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
		stateManager.add(POWERED);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		InteractionResult actionResult = checkAndDoPaintbrushTrigger(state, world, pos, player, hand, hit);
		if (actionResult.consumesAction()) {
			return actionResult;
		}
		
		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			this.openScreen(world, pos, player);
			return InteractionResult.CONSUME;
		}
	}
	
	protected void openScreen(Level world, BlockPos pos, Player player) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PedestalBlockEntity pedestalBlockEntity) {
			pedestalBlockEntity.setOwner(player);
			player.openMenu((MenuProvider) blockEntity);
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
		if (newState.getBlock() instanceof PedestalBlock newStateBlock) {
			if (!state.is(newStateBlock)) {
				// pedestal is getting upgraded. Keep the blockEntity with its contents
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof PedestalBlockEntity pedestalBlockEntity) {
					if (state.getBlock().equals(newStateBlock)) {
						PedestalVariant newVariant = newStateBlock.getVariant();
						pedestalBlockEntity.setVariant(newVariant);
					}
				}
			}
		} else {
			InWorldInteractionBlock.scatterContents(world, pos);
			super.onRemove(state, world, pos, newState, moved);
		}
	}
	
	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PedestalBlockEntity(pos, state);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, @NotNull Level world, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level world, BlockState state, BlockEntityType<T> type) {
		if (world.isClientSide) {
			return createTickerHelper(type, SpectrumBlockEntities.PEDESTAL, PedestalBlockEntity::clientTick);
		} else {
			return createTickerHelper(type, SpectrumBlockEntities.PEDESTAL, PedestalBlockEntity::serverTick);
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, @NotNull Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClientSide) {
			if (this.checkGettingPowered(world, pos)) {
				this.power(world, pos);
			} else {
				this.unPower(world, pos);
			}
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(@NotNull BlockState state, Level world, BlockPos pos, RandomSource random) {
		if (state.getValue(PedestalBlock.POWERED)) {
			Vector3f color = new Vector3f(0.5F, 0.5F, 0.5F);
			float xOffset = random.nextFloat();
			float zOffset = random.nextFloat();
			world.addParticle(new DustParticleOptions(color, 1.0F), pos.getX() + xOffset, pos.getY() + 1, pos.getZ() + zOffset, 0.0D, 0.0D, 0.0D);
		}
	}
	
	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
		if (world.isClientSide()) {
			clearCurrentlyRenderedMultiBlock((Level) world);
		}
	}
	
	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext ctx) {
		BlockState placementState = this.defaultBlockState();
		
		if (ctx.getLevel().getBestNeighborSignal(ctx.getClickedPos()) > 0) {
			placementState = placementState.setValue(POWERED, true);
		}
		
		return placementState;
	}
	
	public PedestalVariant getVariant() {
		return this.variant;
	}
	
	static {
		var foot = Block.box(3, 0, 3, 13, 3, 13);
		var neck = Block.box(5, 3, 5, 11, 12, 11);
		var head = Block.box(0, 12, 0, 16, 16, 16);
		foot = Shapes.or(foot, neck);
		SHAPE = Shapes.or(foot, head);
	}
	
	@Override
	public InteractionResult onPaintBrushTrigger(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PedestalBlockEntity pedestalBlockEntity) {
			if (pedestalBlockEntity.craftingTime > 0) {
				return InteractionResult.FAIL;
			}
			if (pedestalBlockEntity.currentRecipe == null) {
				return InteractionResult.FAIL;
			}
			if (pedestalBlockEntity.currentRecipe instanceof GatedRecipe gatedRecipe && !gatedRecipe.canPlayerCraft(player)) {
				return InteractionResult.FAIL;
			}
			
			if (!world.isClientSide) {
				pedestalBlockEntity.shouldCraft = true;
				SpectrumS2CPacketSender.spawnPedestalStartCraftingParticles(pedestalBlockEntity);
			}
			
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		return InteractionResult.FAIL;
	}
	
}
