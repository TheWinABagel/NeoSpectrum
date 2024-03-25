package de.dafuqs.spectrum.blocks.jade_vines;

import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class NephriteBlossomLeavesBlock extends LeavesBlock implements BonemealableBlock {
    
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    public static final int MAX_AGE = BlockStateProperties.MAX_AGE_2;
    
    public NephriteBlossomLeavesBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(AGE, 0));
    }
    
    @Override
	@SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(AGE) == MAX_AGE) {
			ItemStack handStack = player.getItemInHand(hand);
			int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, handStack) / 2;
			int count = 1 + world.getRandom().nextInt(fortuneLevel + 1);
			player.getInventory().placeItemBackInInventory(new ItemStack(SpectrumItems.GLASS_PEACH, count));
	
			world.setBlockAndUpdate(pos, state.setValue(AGE, 0));
			player.playNotifySound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1, 1 + player.getRandom().nextFloat() * 0.25F);
			return InteractionResult.sidedSuccess(world.isClientSide());
		}
	
		return super.use(state, world, pos, player, hand, hit);
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return SpectrumItems.NEPHRITE_BLOSSOM_BULB.getDefaultInstance();
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		int age = state.getValue(AGE);
		int leafSum = 0;
		
		if (state.getValue(PERSISTENT)) {
			super.randomTick(state, world, pos, random);
			return;
		}
    
        for (BlockPos iPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            var leafState = world.getBlockState(iPos);
            if (leafState.is(this)) {
                leafSum += (leafState.getValue(AGE).byteValue() + 1) * 3;
            }
        }

        leafSum = Math.max(leafSum, 0) + 1;

        if (random.nextInt(leafSum + 1) != 0) {
            super.randomTick(state, world, pos, random);
            return;
        }

        if (age == 2) {
            BlockPos.MutableBlockPos dropPos = pos.mutable();
            while (world.getBlockState(dropPos).is(this) && pos.getY() - dropPos.getY() < 32) {
                dropPos.move(0, -1, 0);
            }
            ItemStack drop = new ItemStack(SpectrumItems.GLASS_PEACH);
            world.addFreshEntity(new ItemEntity(world, dropPos.getX() + 0.5, dropPos.getY() + 0.15, dropPos.getZ() + 0.5, drop));
            BlockState newState = state.setValue(AGE, 0);
            world.setBlockAndUpdate(pos, newState);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
        }
        else {
            world.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
        }

        super.randomTick(state, world, pos, random);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) != MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
	public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
		return state.getValue(AGE) != 2;
	}

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        var age = state.getValue(AGE);
        if (age == MAX_AGE)
            return;
    
        world.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
    }
}
