package de.dafuqs.spectrum.blocks;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DeeperDownPortalBlock extends Block {

	private final static ResourceLocation CREATE_PORTAL_ADVANCEMENT_IDENTIFIER = SpectrumCommon.locate("midgame/open_deeper_down_portal");
	private final static String CREATE_PORTAL_ADVANCEMENT_CRITERION = "opened_deeper_down_portal";

	public static final BooleanProperty FACING_UP = BlockStateProperties.UP;

	protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4D, 16.0D);
	protected static final VoxelShape SHAPE_UP = Block.box(0.0D, 4D, 0.0D, 16.0D, 16.0D, 16.0D);

	public DeeperDownPortalBlock(Properties settings) {
		super(settings);
		this.registerDefaultState((this.stateDefinition.any()).setValue(FACING_UP, false));
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onPlace(state, world, pos, oldState, notify);

		if (!world.isClientSide) { // that should be a given, but in modded you never know
			SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) world, Vec3.atCenterOf(pos), SpectrumParticleTypes.VOID_FOG, 30, new Vec3(0.5, 0.0, 0.5), Vec3.ZERO);
			if (!hasNeighboringPortals(world, pos)) {
				world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SpectrumSoundEvents.DEEPER_DOWN_PORTAL_OPEN, SoundSource.BLOCKS, 0.75F, 0.75F);

				for (Player nearbyPlayer : world.getEntities(EntityType.PLAYER, AABB.ofSize(Vec3.atCenterOf(pos), 16D, 16D, 16D), LivingEntity::isAlive)) {
					Support.grantAdvancementCriterion((ServerPlayer) nearbyPlayer, CREATE_PORTAL_ADVANCEMENT_IDENTIFIER, CREATE_PORTAL_ADVANCEMENT_CRITERION);
				}
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack handStack = player.getItemInHand(hand);
		if (handStack.is(SpectrumItems.BEDROCK_DUST)) {
			if (world.isClientSide) {
				return InteractionResult.SUCCESS;
			} else {
				BlockState placedState = Blocks.BEDROCK.defaultBlockState();
				world.setBlockAndUpdate(pos, placedState);
				world.playSound(null, pos, placedState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
				return InteractionResult.CONSUME;
			}
		}

		return InteractionResult.PASS;
	}

	private boolean hasNeighboringPortals(Level world, BlockPos pos) {
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			if (world.getBlockState(pos.relative(direction)).is(this)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return state.getValue(FACING_UP) ? SHAPE_UP : SHAPE;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canBeReplaced(BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING_UP);
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (world instanceof ServerLevel
				&& !entity.isPassenger()
				&& !entity.isVehicle()
				&& entity.canChangeDimensions()) {

			ResourceKey<Level> currentWorldKey = world.dimension();
			if (currentWorldKey == Level.OVERWORLD) {
				if (!entity.isOnPortalCooldown()) {
					entity.setPortalCooldown();

					// => teleport to DD
					ServerLevel targetWorld = ((ServerLevel) world).getServer().getLevel(SpectrumDimensions.DIMENSION_KEY);
					if (targetWorld != null) {
						BlockPos portalPos = new BlockPos(pos.getX(), targetWorld.getMaxBuildHeight() - 1, pos.getZ());
						if (!targetWorld.getBlockState(portalPos).is(SpectrumBlocks.DEEPER_DOWN_PORTAL)) {
							targetWorld.setBlockAndUpdate(portalPos, SpectrumBlocks.DEEPER_DOWN_PORTAL.defaultBlockState().setValue(FACING_UP, true));
						}

						BlockPos targetPos = portalPos.below(3);
						if (entity instanceof Player) {
							makeRoomAround(targetWorld, targetPos, 2);
						}
						FabricDimensions.teleport(entity, targetWorld, new PortalInfo(Vec3.atCenterOf(targetPos), Vec3.ZERO, entity.getYRot(), entity.getXRot()));
						teleportToSafePosition(targetWorld, entity, targetPos.below(), 5);
					}
				}
			} else {
				if (!entity.isOnPortalCooldown()) {
					entity.setPortalCooldown();

					// => teleport to Overworld
					ServerLevel targetWorld = ((ServerLevel) world).getServer().getLevel(Level.OVERWORLD);
					if (targetWorld != null) {
						BlockPos portalPos = new BlockPos(pos.getX(), targetWorld.getMinBuildHeight(), pos.getZ());
						if (!targetWorld.getBlockState(portalPos).is(SpectrumBlocks.DEEPER_DOWN_PORTAL)) {
							targetWorld.setBlockAndUpdate(portalPos, SpectrumBlocks.DEEPER_DOWN_PORTAL.defaultBlockState().setValue(FACING_UP, false));
						}

						BlockPos targetPos = portalPos.above(2);
						makeRoomAround(targetWorld, targetPos, 2);
						FabricDimensions.teleport(entity, targetWorld, new PortalInfo(Vec3.atCenterOf(targetPos), Vec3.ZERO, entity.getYRot(), entity.getXRot()));
						teleportToSafePosition(targetWorld, entity, targetPos, 3);
					}
				}
			}
		}
	}

	public void makeRoomAround(Level world, BlockPos blockPos, int radius) {
		BlockState state = world.getBlockState(blockPos);
		if (state.getCollisionShape(world, blockPos).isEmpty() && state.getCollisionShape(world, blockPos.above()).isEmpty()) {
			return;
		}

		for (BlockPos pos : BlockPos.withinManhattan(blockPos, radius, radius, radius)) {
			if (world.getBlockEntity(pos) != null) {
				continue;
			}

			state = world.getBlockState(pos);

			if (state.is(Blocks.BEDROCK)) {
				if (pos.getX() == blockPos.getX() && pos.getZ() == blockPos.getZ()) {
					world.destroyBlock(pos, true, null);
				}
				continue;
			}

			if (!state.is(SpectrumBlockTags.BASE_STONE_DEEPER_DOWN)) {
				continue;
			}

			float hardness = state.getDestroySpeed(world, pos);
			if (hardness >= 0 && hardness < 30) {
				world.destroyBlock(pos, true, null);
			}
		}
	}

	public void teleportToSafePosition(Level world, Entity entity, BlockPos targetPos, int maxRadius) {
		for (BlockPos bp : BlockPos.withinManhattan(targetPos, maxRadius, maxRadius, maxRadius)) {
			entity.setPos(Vec3.atBottomCenterOf(bp));
			if (world.getBlockState(bp.below()).getCollisionShape(world, bp.below()) == Shapes.block()
					&& world.noCollision(entity)
					&& entity.getY() < (double) world.getMaxBuildHeight()
					&& entity.getY() > (double) world.getMinBuildHeight()) {

				entity.teleportToWithTicket(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5);
				return;
			}
		}

		world.removeBlock(targetPos.above(1), false);
		world.removeBlock(targetPos, false);
		world.setBlockAndUpdate(targetPos.below(1), Blocks.COBBLED_DEEPSLATE.defaultBlockState());
		entity.teleportToWithTicket(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		if (!state.getValue(DeeperDownPortalBlock.FACING_UP) || random.nextInt(8) == 0) {
			spawnVoidFogParticle(world, pos, random);
		}
	}

	private static void spawnVoidFogParticle(Level world, BlockPos pos, RandomSource random) {
		double d = (double) pos.getX() + random.nextDouble();
		double e = (double) pos.getY() + 0.3D;
		double f = (double) pos.getZ() + random.nextDouble();
		world.addParticle(SpectrumParticleTypes.VOID_FOG, d, e, f, 0.0D, 0.1D, 0.0D);
	}

}
