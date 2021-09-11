package de.dafuqs.spectrum.blocks.redstone;

import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RedstoneCalculatorBlock extends AbstractRedstoneGateBlock implements BlockEntityProvider {

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneCalculatorBlockEntity(pos, state);
    }

    public enum CalculationMode implements StringIdentifiable {
        ADDITION("addition"),
        SUBTRACTION("subtraction"),
        MULTIPLICATION("multiplication"),
        DIVISION("division"),
        MODULO("modulo");

        private final String name;

        CalculationMode(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String asString() {
            return this.name;
        }
    }
    public static EnumProperty<CalculationMode> CALCULATION_MODE = EnumProperty.of("calculation_mode", CalculationMode.class);

    public RedstoneCalculatorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false).with(CALCULATION_MODE, CalculationMode.ADDITION));
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, CALCULATION_MODE);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        } else {
            BlockState newModeState = state.cycle(CALCULATION_MODE);
            world.setBlockState(pos, newModeState, Block.NOTIFY_ALL);
            float pitch = 0.5F + state.get(CALCULATION_MODE).ordinal() * 0.05F;
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, pitch);
            this.updatePowered(world, pos, newModeState);
            return ActionResult.success(world.isClient);
        }
    }

    @Override
    public void updatePowered(World world, BlockPos pos, BlockState state) {
        int newSignal = Math.max(0, Math.min(15, this.calculateOutputSignal(world, pos, state)));
        BlockEntity blockEntity = world.getBlockEntity(pos);
        int lastSignal = 0;
        if (blockEntity instanceof RedstoneCalculatorBlockEntity) {
            RedstoneCalculatorBlockEntity redstoneCalculatorBlockEntity = (RedstoneCalculatorBlockEntity)blockEntity;
            lastSignal = redstoneCalculatorBlockEntity.getOutputSignal();
            redstoneCalculatorBlockEntity.setOutputSignal(newSignal);
        }

        if (lastSignal != newSignal) {
            boolean bl = this.hasPower(world, pos, state);
            boolean bl2 = state.get(POWERED);
            if (bl2 && !bl) {
                world.setBlockState(pos, state.with(POWERED, false), Block.NOTIFY_LISTENERS);
            } else if (!bl2 && bl) {
                world.setBlockState(pos, state.with(POWERED, true), Block.NOTIFY_LISTENERS);
            }

            this.updateTarget(world, pos, state);
        }
    }

    private int calculateOutputSignal(World world, BlockPos pos, BlockState state) {
        int power = this.getPower(world, pos, state);
        int powerSides = this.getMaxInputLevelSides(world, pos, state);

        switch (state.get(CALCULATION_MODE)) {
            case ADDITION -> {
                return power + powerSides;
            }
            case SUBTRACTION -> {
                return power - powerSides;
            }
            case MULTIPLICATION -> {
                return power * powerSides;
            }
            case DIVISION -> {
                if(powerSides == 0) {
                    return 0;
                } else {
                    return power / powerSides;
                }
            }
            default -> {
                if(powerSides == 0) {
                    return 0;
                } else {
                    return power % powerSides;
                }
            }
        }
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean bl = state.get(POWERED);
        boolean bl2 = this.hasPower(world, pos, state);
        if (bl && !bl2) {
            world.setBlockState(pos, state.with(POWERED, false), Block.NOTIFY_LISTENERS);
        } else if (!bl) {
            world.setBlockState(pos, state.with(POWERED, true), Block.NOTIFY_LISTENERS);
            if (!bl2) {
                world.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
            }
        }
    }

    /**
     * The block entity caches the output signal for performance
     */
    protected int getOutputLevel(@NotNull BlockView world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof RedstoneCalculatorBlockEntity ? ((RedstoneCalculatorBlockEntity)blockEntity).getOutputSignal() : 0;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = SpectrumBlocks.REDSTONE_CALCULATOR.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
        int signal = calculateOutputSignal(ctx.getWorld(), ctx.getBlockPos(), state);
        if(signal == 0) {
            return state;
        } else {
            return state.with(POWERED, true);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        updatePowered(world, pos, state);
    }

}
