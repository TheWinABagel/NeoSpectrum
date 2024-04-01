package de.dafuqs.spectrum.blocks.dd_deco;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.jade_vines.JadeVinePlantBlock;
import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolActions;

public class SawbladeHollyBushBlock extends BushBlock implements BonemealableBlock {
    
    public static final int MAX_TINY_AGE = 0;
    public static final int MAX_SMALL_AGE = 2;
    public static final int MAX_AGE = BlockStateProperties.MAX_AGE_7;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    private static final VoxelShape SMALL_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
    private static final VoxelShape LARGE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    
    public static final ResourceLocation HARVESTING_LOOT_TABLE_ID = SpectrumCommon.locate("gameplay/sawblade_holly_harvesting");
    public static final ResourceLocation SHEARING_LOOT_TABLE_ID = SpectrumCommon.locate("gameplay/sawblade_holly_shearing");
    
    public SawbladeHollyBushBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
    
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }
    
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int i = state.getValue(AGE);
        if (i < MAX_AGE && random.nextInt(5) == 0 && world.getRawBrightness(pos.above(), 0) >= 9) {
            BlockState blockState = state.setValue(AGE, i + 1);
            world.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
        }
    }
    
    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(SpectrumItems.SAWBLADE_HOLLY_BERRY);
    }
    
    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.is(SpectrumBlockTags.SAWBLADE_HOLLY_PLANTABLE);
    }
    
    @Override
	@SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(AGE) <= MAX_TINY_AGE) {
            return SMALL_SHAPE;
        } else {
            return state.getValue(AGE) <= MAX_SMALL_AGE ? LARGE_SHAPE : super.getShape(state, world, pos, context);
        }
    }
	
	@Override
	@SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int age = state.getValue(AGE);
        
        ItemStack handStack = player.getItemInHand(hand);
        if (canBeSheared(age) && handStack.canPerformAction(ToolActions.SHEARS_HARVEST)) {
            if (!world.isClientSide) {
                for (ItemStack stack : JadeVinePlantBlock.getHarvestedStacks(state, (ServerLevel) world, pos, world.getBlockEntity(pos), player, player.getMainHandItem(), SHEARING_LOOT_TABLE_ID)) {
                    popResource(world, pos, stack);
                }
                handStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }
            
            BlockState newState = state.setValue(AGE, state.getValue(AGE) - 1);
            world.setBlock(pos, newState, Block.UPDATE_CLIENTS);
            world.gameEvent(GameEvent.SHEAR, pos, GameEvent.Context.of(player, newState));
            world.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (age == MAX_AGE) {
            if (!world.isClientSide) {
                for (ItemStack stack : JadeVinePlantBlock.getHarvestedStacks(state, (ServerLevel) world, pos, world.getBlockEntity(pos), player, player.getMainHandItem(), HARVESTING_LOOT_TABLE_ID)) {
                    popResource(world, pos, stack);
                }
            }
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            
            BlockState newState = state.setValue(AGE, 4);
            world.setBlock(pos, newState, Block.UPDATE_CLIENTS);
            world.gameEvent(GameEvent.SHEAR, pos, GameEvent.Context.of(player, newState));
            
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return super.use(state, world, pos, player, hand, hit);
        }
    }
    
    public static boolean canBeSheared(int age) {
        return age > MAX_SMALL_AGE;
    }
    
    @Override
	public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
		return state.getValue(AGE) < MAX_AGE;
	}
    
    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }
    
    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        int newAge = Math.min(MAX_AGE, state.getValue(AGE) + (random.nextBoolean() ? 1 : 2));
        world.setBlock(pos, state.setValue(AGE, newAge), Block.UPDATE_CLIENTS);
    }
    
}
