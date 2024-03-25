package de.dafuqs.spectrum.blocks.conditional;

import de.dafuqs.revelationary.api.revelations.RevelationAware;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Hashtable;
import java.util.Map;

public class StuckStormStoneBlock extends Block implements RevelationAware {
	
	protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 11.0D, 2.0D, 11.0D);
	
	public StuckStormStoneBlock(Properties settings) {
		super(settings);
		RevelationAware.register(this);
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return world.getBlockState(pos.below()).isRedstoneConductor(world, pos);
	}
	
	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}
	
	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return 1.0F;
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
		return true;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
	}
	
	@Override
	public void wasExploded(Level world, BlockPos pos, Explosion explosion) {
		super.wasExploded(world, pos, explosion);
		
		if (world.canSeeSky(pos)) {
			LightningBolt lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
			if (lightningEntity != null) {
				lightningEntity.moveTo(Vec3.atBottomCenterOf(pos));
				world.addFreshEntity(lightningEntity);
			}
		}
		
		int power = 2;
		Biome biomeAtPos = world.getBiome(pos).value();
		if (!biomeAtPos.hasPrecipitation() && !biomeAtPos.coldEnoughToSnow(pos)) {
			// there is no rain in deserts or snow
			power = world.isThundering() ? 4 : world.isRaining() ? 3 : 2;
		}
		world.explode(null, pos.getX(), pos.getY(), pos.getZ(), power, Level.ExplosionInteraction.BLOCK);
	}
	
	@Override
	public ResourceLocation getCloakAdvancementIdentifier() {
		return SpectrumCommon.locate("milestones/reveal_storm_stones");
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (this.isVisibleTo(context)) {
			return SHAPE;
		}
		return Shapes.block();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext entityShapeContext) {
			Entity var4 = entityShapeContext.getEntity();
			if (var4 instanceof Player player) {
				return this.isVisibleTo(player) ? SHAPE : Shapes.empty();
			}
		}
		return Shapes.block();
	}
	
	@Override
	public Map<BlockState, BlockState> getBlockStateCloaks() {
		Map<BlockState, BlockState> map = new Hashtable<>();
		map.put(this.defaultBlockState(), Blocks.AIR.defaultBlockState());
		return map;
	}
	
	@Override
	public Tuple<Item, Item> getItemCloak() {
		return null;
	}
	
	/**
	 * If it gets ticked there is a chance to vanish
	 */
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (random.nextFloat() < 0.1) {
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		}
	}
	
}
