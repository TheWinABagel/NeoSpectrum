package de.dafuqs.spectrum.blocks.present;

import de.dafuqs.spectrum.api.item.PresentUnpackBehavior;
import de.dafuqs.spectrum.helpers.ColorHelper;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.effect.DynamicParticleEffect;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.world.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.loot.context.*;
import net.minecraft.network.chat.Component;
import net.minecraft.particle.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.world.*;
import net.minecraft.sound.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.state.*;
import net.minecraft.state.property.*;
import net.minecraft.text.*;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PresentBlock extends BaseEntityBlock {
	
	protected static Map<Item, PresentUnpackBehavior> BEHAVIORS = new Object2ObjectOpenHashMap<>();
	
	public @Nullable PresentUnpackBehavior getBehaviorFor(ItemStack stack) {
		return BEHAVIORS.getOrDefault(stack.getItem(), null);
	}
	
	public static void registerBehavior(ItemLike provider, PresentUnpackBehavior behavior) {
		BEHAVIORS.put(provider.asItem(), behavior);
	}
	
	public enum WrappingPaper implements StringRepresentable {
		RED,
		BLUE,
		CYAN,
		GREEN,
		PURPLE,
		CAKE,
		STRIPED,
		STARRY,
		WINTER,
		PRIDE;
		
		@Override
		public String getSerializedName() {
			return this.toString().toLowerCase(Locale.ROOT);
		}
	}
	
	public static final int TICKS_PER_OPENING_STEP = 20;
	public static final int OPENING_STEPS = 6;
	
	public static final BooleanProperty OPENING = BooleanProperty.create("opening");
	private static final EnumProperty<WrappingPaper> VARIANT = EnumProperty.create("variant", WrappingPaper.class);
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 10.0D, 14.0D);
	
	public PresentBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(OPENING, false).setValue(VARIANT, WrappingPaper.RED));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		builder.add(OPENING, VARIANT);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	@Override
	public boolean canSurvive(@NotNull BlockState state, LevelReader world, BlockPos pos) {
		BlockState downState = world.getBlockState(pos.below());
		return downState.isFaceSturdy(world, pos, Direction.UP);
	}
	
	@Override
	public void setPlacedBy(@NotNull Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		world.setBlockAndUpdate(pos, state.setValue(PresentBlock.VARIANT, PresentItem.getVariant(itemStack.getTag())));
		if (blockEntity instanceof PresentBlockEntity presentBlockEntity) {
			presentBlockEntity.setDataFromPresentStack(itemStack);
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!player.getAbilities().mayBuild) {
			return InteractionResult.PASS;
		} else {
			if (world.isClientSide) {
				return InteractionResult.SUCCESS;
			} else {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof PresentBlockEntity presentBlockEntity) {
					if (player.isShiftKeyDown()) {
						presentBlockEntity.setOpenerUUID(player);
						state = state.setValue(OPENING, true);
						world.setBlock(pos, state, 3);
						world.scheduleTick(pos, state.getBlock(), TICKS_PER_OPENING_STEP);
					} else {
						if (presentBlockEntity.getOwnerName() != null) {
							player.displayClientMessage(Component.translatable("block.spectrum.present.tooltip.wrapped_placed.giver", presentBlockEntity.getOwnerName()), true);
						} else {
							player.displayClientMessage(Component.translatable("block.spectrum.present.tooltip.wrapped_placed"), true);
						}
						
					}
				}
				return InteractionResult.CONSUME;
			}
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		BlockEntity blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof PresentBlockEntity presentBlockEntity) {
			return List.of(presentBlockEntity.retrievePresent(state.getValue(VARIANT)));
		} else {
			return super.getDrops(state, builder);
		}
	}
	
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (state.getValue(OPENING)) {
			if (!world.isClientSide) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof PresentBlockEntity presentBlockEntity) {
					int openingTick = presentBlockEntity.openingTick();
					Vec3 posVec = new Vec3(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
					if (openingTick >= OPENING_STEPS) {
						spawnParticles(world, pos, presentBlockEntity.colors);
						presentBlockEntity.triggerAdvancement();
						if (presentBlockEntity.isEmpty()) {
							world.playSound(null, posVec.x, posVec.y, posVec.z, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 0.8F);
							SpectrumS2CPacketSender.playParticleWithExactVelocity(world, posVec, ParticleTypes.SMOKE, 5, Vec3.ZERO);
						} else {
							world.playSound(null, posVec.x, posVec.y, posVec.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5F, 4.0F);
							SpectrumS2CPacketSender.playParticleWithExactVelocity(world, posVec, ParticleTypes.EXPLOSION, 1, Vec3.ZERO);
							processInteractions(presentBlockEntity.stacks, presentBlockEntity, world, pos, random);
							Containers.dropContents(world, pos, presentBlockEntity.stacks);
						}
						world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
					} else {
						world.playSound(null, posVec.x, posVec.y, posVec.z, SoundEvents.SAND_PLACE, SoundSource.BLOCKS, 0.8F + openingTick * 0.1F, 1.0F);
						spawnParticles(world, pos, presentBlockEntity.colors);
					}
				}
				world.scheduleTick(pos, state.getBlock(), TICKS_PER_OPENING_STEP);
			}
		}
	}

	public void processInteractions(List<ItemStack> stacks, PresentBlockEntity present, ServerLevel world, BlockPos pos, RandomSource random) {
		for (int i = 0; i < stacks.size(); i++) {
			ItemStack stack = stacks.get(i);
			@Nullable PresentUnpackBehavior behavior = getBehaviorFor(stack);
			if (behavior != null) {
				stacks.set(i, behavior.onPresentUnpack(stack, present, world, pos, random));
			}
		}
	}

	public static void spawnParticles(ServerLevel world, BlockPos pos, Map<DyeColor, Integer> colors) {
		SpectrumS2CPacketSender.playPresentOpeningParticles(world, pos, colors);
	}
	
	public static void spawnParticles(ClientLevel world, BlockPos pos, Map<DyeColor, Integer> colors) {
		if (colors.isEmpty()) {
			DyeColor randomColor = DyeColor.byId(world.random.nextInt(DyeColor.values().length));
			spawnParticles(world, pos, randomColor, 15);
		} else {
			for (Map.Entry<DyeColor, Integer> color : colors.entrySet()) {
				spawnParticles(world, pos, color.getKey(), color.getValue() * 10);
			}
		}
	}
	
	private static void spawnParticles(ClientLevel world, BlockPos pos, DyeColor color, int amount) {
		double posX = pos.getX() + 0.5;
		double posY = pos.getY() + 0.25;
		double posZ = pos.getZ() + 0.5;
		RandomSource random = world.random;
		Vector3f colorVec = ColorHelper.getRGBVec(color);
		for (int i = 0; i < amount; i++) {
			double randX = 0.35 - random.nextFloat() * 0.7;
			double randY = random.nextFloat() * 0.7;
			double randZ = 0.35 - random.nextFloat() * 0.7;
			float randomScale = 0.5F + random.nextFloat();
			int randomLifetime = 20 + random.nextInt(20);
			
			ParticleOptions particleEffect = new DynamicParticleEffect(0.98F, colorVec, randomScale, randomLifetime, true, false);
			world.addParticle(particleEffect, posX, posY, posZ, randX, randY, randZ);
		}
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PresentBlockEntity(pos, state);
	}
	
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	
}
