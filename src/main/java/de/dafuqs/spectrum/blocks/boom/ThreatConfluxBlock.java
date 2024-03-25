package de.dafuqs.spectrum.blocks.boom;

import de.dafuqs.spectrum.blocks.FluidLogging;
import de.dafuqs.spectrum.blocks.PlacedItemBlock;
import de.dafuqs.spectrum.blocks.PlacedItemBlockEntity;
import de.dafuqs.spectrum.explosion.ModularExplosionDefinition;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThreatConfluxBlock extends PlacedItemBlock implements FluidLogging.SpectrumFluidLoggable {
	
	public enum ArmedState implements StringRepresentable {
		NOT_ARMED("not_armed", false),
		ARMED("armed", true),
		FUSED("fused", true);
		
		private final String name;
		private final boolean explodesWhenBroken;
		
		ArmedState(String name, boolean explodesWhenBroken) {
			this.name = name;
			this.explodesWhenBroken = explodesWhenBroken;
		}
		
		@Override
		public String getSerializedName() {
			return this.name;
		}
		
		public boolean explodesWhenBroken() {
			return this.explodesWhenBroken;
		}
	}
	
	private static final int TICKS_TO_ARM = 50;
	private static final int TICKS_TO_DETONATE = 20;
	
	public static final VoxelShape UNARMED_SHAPE = Block.box(0, 0, 0, 16, 3, 16);
	public static final VoxelShape ARMED_SHAPE = Block.box(0, 0, 0, 16, 0.125, 16);
	
	public static final EnumProperty<ArmedState> ARMED = EnumProperty.create("armed", ArmedState.class);
	public static final EnumProperty<FluidLogging.State> LOGGED = FluidLogging.ANY_INCLUDING_NONE;
	
	public ThreatConfluxBlock(Properties settings) {
		super(settings);
		registerDefaultState(defaultBlockState().setValue(ARMED, ArmedState.NOT_ARMED).setValue(LOGGED, FluidLogging.State.NOT_LOGGED));
	}
	
	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		if (!world.isClientSide && state.getValue(ARMED).explodesWhenBroken()) {
			explode((ServerLevel) world, pos);
		}
		super.playerWillDestroy(world, pos, state, player);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		var handStack = player.getItemInHand(hand);
		if (state.getValue(ARMED).explodesWhenBroken() && handStack.is(SpectrumItems.MIDNIGHT_CHIP)) {
			world.setBlockAndUpdate(pos, state.setValue(ARMED, ArmedState.NOT_ARMED));
			world.playSound(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, SpectrumSoundEvents.BLOCK_THREAT_CONFLUX_DISARM, SoundSource.BLOCKS, 1.0F, 1.0F);
			
			if (!world.isClientSide) {
				ServerLevel serverWorld = ((ServerLevel) world);
				for (int i = 0; i < 5; ++i) {
					serverWorld.sendParticles(ParticleTypes.SMOKE,
							pos.getX() + serverWorld.random.nextDouble(), pos.getY() + serverWorld.random.nextDouble(), pos.getZ() + serverWorld.random.nextDouble(),
							5, 0.0, 0.0, 0.0, 0.05);
				}
			}
			
			if (!player.isCreative()) {
				handStack.shrink(1);
			}
			
			return InteractionResult.sidedSuccess(world.isClientSide());
		}
		
		return super.use(state, world, pos, player, hand, hit);
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.setPlacedBy(world, pos, state, placer, itemStack);
		
		if (!world.isClientSide) {
			world.scheduleTick(pos, this, TICKS_TO_ARM);
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (state.getValue(ARMED) == ArmedState.ARMED) {
			world.playSound(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, SpectrumSoundEvents.BLOCK_THREAT_CONFLUX_PRIME, SoundSource.BLOCKS, 1, 2F);
			world.setBlockAndUpdate(pos, state.setValue(ARMED, ArmedState.FUSED));
			world.scheduleTick(pos, this, TICKS_TO_DETONATE);
		}
		
		super.entityInside(state, world, pos, entity);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return state.getValue(ARMED).explodesWhenBroken() ? ARMED_SHAPE : UNARMED_SHAPE;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(state, world, pos, random);
		
		ArmedState s = state.getValue(ARMED);
		if (s == ArmedState.NOT_ARMED) {
			world.setBlockAndUpdate(pos, state.setValue(ARMED, ArmedState.ARMED));
			world.playSound(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, SpectrumSoundEvents.BLOCK_THREAT_CONFLUX_ARM, SoundSource.BLOCKS, 2F, 0.1F + world.getRandom().nextFloat() * 0.3F);
		} else if (s == ArmedState.FUSED) {
			explode(world, pos);
		}
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ARMED, LOGGED);
	}
	
	public void explode(@NotNull ServerLevel world, BlockPos pos) {
		if (!(world.getBlockEntity(pos) instanceof PlacedItemBlockEntity blockEntity)) {
			return;
		}
		ItemStack stack = blockEntity.getStack();
		Player owner = blockEntity.getOwnerIfOnline();
		
		world.removeBlock(pos, false);
		
		ModularExplosionDefinition.explode(world, pos, owner, stack);
	}
	
}
