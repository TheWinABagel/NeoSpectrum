package de.dafuqs.spectrum.blocks.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;

public class RedstoneTimerBlock extends DiodeBlock {
	
	public static final EnumProperty<TimingStep> ACTIVE_TIME = EnumProperty.create("active_time", TimingStep.class);
	public static final EnumProperty<TimingStep> INACTIVE_TIME = EnumProperty.create("inactive_time", TimingStep.class);
	
	public RedstoneTimerBlock(BlockBehaviour.Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(ACTIVE_TIME, TimingStep.OneSecond).setValue(INACTIVE_TIME, TimingStep.OneSecond));
	}
	
	@Override
	protected int getDelay(BlockState state) {
		if (state.getValue(POWERED)) {
			return state.getValue(ACTIVE_TIME).ticks;
		} else {
			return state.getValue(INACTIVE_TIME).ticks;
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, @NotNull Level world, BlockPos pos, @NotNull Player player, InteractionHand hand, BlockHitResult hit) {
		if (!player.getAbilities().mayBuild) {
			return InteractionResult.PASS;
		} else {
			if (world.isClientSide) {
				return InteractionResult.SUCCESS;
			} else {
				stepTiming((ServerLevel) world, pos, (ServerPlayer) player);
				return InteractionResult.CONSUME;
			}
		}
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, ACTIVE_TIME, INACTIVE_TIME);
	}
	
	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onPlace(state, world, pos, oldState, notify);
		if (world instanceof ServerLevel serverWorld) {
			// remove currently scheduled ticks at the blocks position
			// and schedule new ticks
			serverWorld.getBlockTicks().clearArea(new BoundingBox(pos));
			serverWorld.scheduleTick(pos, state.getBlock(), getDelay(state));
		}
	}
	
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		BlockState newState = state.setValue(POWERED, !state.getValue(POWERED));
		world.setBlock(pos, newState, 3);
		world.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, 1.0F);
		world.scheduleTick(pos, this, this.getDelay(state), TickPriority.NORMAL);
	}
	
	@Override
	protected int getInputSignal(Level world, BlockPos pos, BlockState state) {
		return world.getBlockState(pos).getValue(POWERED) ? 15 : 0;
	}
	
	@Override
	protected void checkTickOnNeighbor(Level world, BlockPos pos, BlockState state) {
		boolean bl = state.getValue(POWERED);
		if (!world.getBlockTicks().willTickThisTick(pos, this)) {
			TickPriority tickPriority = TickPriority.HIGH;
			if (this.shouldPrioritize(world, pos, state)) {
				tickPriority = TickPriority.EXTREMELY_HIGH;
			} else if (bl) {
				tickPriority = TickPriority.VERY_HIGH;
			}
			world.scheduleTick(pos, this, this.getDelay(state), tickPriority);
		}
	}
	
	public void stepTiming(ServerLevel world, BlockPos pos, ServerPlayer serverPlayerEntity) {
		if (serverPlayerEntity != null) {
			BlockState blockState = world.getBlockState(pos);
			if (serverPlayerEntity.isShiftKeyDown()) {
				// toggle inactive time
				TimingStep newStep = blockState.getValue(INACTIVE_TIME).next();
				serverPlayerEntity.displayClientMessage(Component.translatable("block.spectrum.redstone_timer.setting.inactive").append(Component.translatable(newStep.localizationString)), true);
				float pitch = 0.5F + newStep.ordinal() * 0.05F;
				world.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, pitch);
				world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(INACTIVE_TIME, newStep));
			} else {
				// toggle active time
				TimingStep newStep = blockState.getValue(ACTIVE_TIME).next();
				serverPlayerEntity.displayClientMessage(Component.translatable("block.spectrum.redstone_timer.setting.active").append(Component.translatable(newStep.localizationString)), true);
				float pitch = 0.5F + newStep.ordinal() * 0.05F;
				world.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, pitch);
				world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(ACTIVE_TIME, newStep));
			}
			
			world.getBlockTicks().clearArea(new BoundingBox(pos)); // remove currently scheduled ticks at the blocks position
			BlockState state = world.getBlockState(pos);
			checkTickOnNeighbor(world, pos, state);
		}
	}
	
	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		if (state.getValue(POWERED)) {
			Direction direction = state.getValue(FACING);
			double x = (double) pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
			double y = (double) pos.getY() + 0.4D + (random.nextDouble() - 0.5D) * 0.2D;
			double z = (double) pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
			float g = -0.3F;
			double xOffset = (g * (float) direction.getStepX());
			double zOffset = (g * (float) direction.getStepZ());
			world.addParticle(DustParticleOptions.REDSTONE, x + xOffset, y, z + zOffset, 0.0D, 0.0D, 0.0D);
		}
	}
	
	public enum TimingStep implements StringRepresentable {
		FourTicks("four_ticks", 4, "block.spectrum.redstone_timer.setting.four_ticks"),
		OneSecond("one_second", 20, "block.spectrum.redstone_timer.setting.one_second"),
		TenSeconds("ten_seconds", 10 * 20, "block.spectrum.redstone_timer.setting.ten_seconds"),
		OneMinute("one_minute", 60 * 20, "block.spectrum.redstone_timer.setting.one_minute"),
		TenMinutes("ten_minutes", 60 * 20 * 10, "block.spectrum.redstone_timer.setting.ten_minutes");
		
		public final int ticks;
		public final String localizationString;
		private final String name;
		
		TimingStep(String name, int ticks, String localizationString) {
			this.name = name;
			this.ticks = ticks;
			this.localizationString = localizationString;
		}
		
		public TimingStep next() {
			return values()[(this.ordinal() + 1) % values().length];
		}
		
		@Override
		public String getSerializedName() {
			return name;
		}
	}
	
}
