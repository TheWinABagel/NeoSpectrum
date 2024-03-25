package de.dafuqs.spectrum.blocks.jade_vines;

import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class NephriteBlossomStemBlock extends BushBlock {

	public static final EnumProperty<StemComponent> STEM_PART = StemComponent.PROPERTY;

	public NephriteBlossomStemBlock(Properties settings) {
		super(settings);
		registerDefaultState(defaultBlockState().setValue(STEM_PART, StemComponent.BASE));
	}
	
	public static BlockState getStemVariant(boolean top) {
		return SpectrumBlocks.NEPHRITE_BLOSSOM_STEM.defaultBlockState().setValue(STEM_PART, top ? StemComponent.STEMALT : StemComponent.STEM);
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return SpectrumItems.NEPHRITE_BLOSSOM_BULB.getDefaultInstance();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		var handStack = player.getItemInHand(hand);
		
		if (handStack.is(ConventionalItemTags.SHEARS) && state.getValue(STEM_PART) == StemComponent.BASE) {
			BlockState newState = state.setValue(STEM_PART, StemComponent.STEM);
			world.setBlockAndUpdate(pos, newState);
			player.playNotifySound(SoundEvents.MOOSHROOM_SHEAR, SoundSource.BLOCKS, 1, 0.9F + player.getRandom().nextFloat() * 0.2F);
			handStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
			world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
    
            return InteractionResult.sidedSuccess(world.isClientSide());
        }

        return super.use(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var world = ctx.getLevel();
        var pos = ctx.getClickedPos();
        var floor = world.getBlockState(pos.below());

        var state = super.getStateForPlacement(ctx);

        if (state == null)
            return null;

        if (floor.is(this) ) {
            if (floor.getValue(STEM_PART) != StemComponent.STEMALT) {
                state = state.setValue(STEM_PART, StemComponent.STEMALT);
            }
            else if (floor.getValue(STEM_PART) != StemComponent.STEM) {
                state = state.setValue(STEM_PART, StemComponent.STEM);
            }
        }

        return state;
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.is(this) || super.mayPlaceOn(floor, world, pos);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		if (!state.canSurvive(world, pos)) {
			world.scheduleTick(pos, this, 1);
		}

		return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
	}

    @Override
	@SuppressWarnings("deprecation")
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(state, world, pos, random);
		if (!state.canSurvive(world, pos)) {
			world.destroyBlock(pos, true);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(STEM_PART);
	}

}
