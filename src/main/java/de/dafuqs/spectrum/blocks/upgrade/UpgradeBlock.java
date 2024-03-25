package de.dafuqs.spectrum.blocks.upgrade;

import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.particle.effect.ColoredTransmission;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UpgradeBlock extends BaseEntityBlock {

	protected static final VoxelShape SHAPE_UP = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 10.0D, 14.0D);
	private static final List<Block> upgradeBlocks = new ArrayList<>();
	// Positions to check on place / destroy to upgrade those blocks upgrade counts
	private final List<Vec3i> possibleUpgradeBlockOffsets = new ArrayList<>() {{
		// Pedestal
		add(new Vec3i(3, -2, 3));
		add(new Vec3i(-3, -2, 3));
		add(new Vec3i(3, -2, -3));
		add(new Vec3i(-3, -2, -3));

		// Fusion Shrine
		add(new Vec3i(2, 0, 2));
		add(new Vec3i(-2, 0, 2));
		add(new Vec3i(2, 0, -2));
		add(new Vec3i(-2, 0, -2));
		
		// Enchanter
		add(new Vec3i(3, 0, 3));
		add(new Vec3i(-3, 0, 3));
		add(new Vec3i(3, 0, -3));
		add(new Vec3i(-3, 0, -3));
		
		// Spirit Instiller
		add(new Vec3i(4, -1, 4));
		add(new Vec3i(-4, -1, 4));
		add(new Vec3i(4, -1, -4));
		add(new Vec3i(-4, -1, -4));
		
		// Cinderhearth
		add(new Vec3i(1, -1, 2));
		add(new Vec3i(-1, -1, 2));
		add(new Vec3i(1, -1, -2));
		add(new Vec3i(-1, -1, -2));
		add(new Vec3i(2, -1, 1));
		add(new Vec3i(-2, -1, 1));
		add(new Vec3i(2, -1, -1));
		add(new Vec3i(-2, -1, -1));
	}};
	// Like: The further the player progresses,
	// the higher are the chances for good mods?
	private final Upgradeable.UpgradeType upgradeType;
	private final int upgradeMod;
	private final DyeColor effectColor;

	public UpgradeBlock(Properties settings, Upgradeable.UpgradeType upgradeType, int upgradeMod, DyeColor effectColor) {
		super(settings);
		this.upgradeType = upgradeType;
		this.upgradeMod = upgradeMod;
		this.effectColor = effectColor;

		upgradeBlocks.add(this);
	}

	public static List<Block> getUpgradeBlocks() {
		return upgradeBlocks;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE_UP;
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onPlace(state, world, pos, oldState, notify);
		if (!world.isClientSide) {
			updateConnectedUpgradeBlock((ServerLevel) world, pos);
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
		super.onRemove(state, world, pos, newState, moved);
		if (!world.isClientSide) {
			updateConnectedUpgradeBlock((ServerLevel) world, pos);
		}
	}

	/**
	 * When placed or removed the upgrade block searches for a valid Upgradeable block
	 * and triggers it to update its upgrades
	 */
	private void updateConnectedUpgradeBlock(@NotNull ServerLevel world, @NotNull BlockPos pos) {
		for (Vec3i possibleUpgradeBlockOffset : possibleUpgradeBlockOffsets) {
			BlockPos currentPos = pos.offset(possibleUpgradeBlockOffset);
			BlockEntity blockEntity = world.getBlockEntity(currentPos);
			if (blockEntity instanceof Upgradeable upgradeable) {
				upgradeable.resetUpgrades();
				playConnectedParticles(world, pos, currentPos);
			}
		}
	}

	private void playConnectedParticles(@NotNull ServerLevel world, @NotNull BlockPos pos, BlockPos currentPos) {
		DyeColor particleColor = getEffectColor();
		world.playSound(null, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, SpectrumSoundEvents.ENCHANTER_DING, SoundSource.BLOCKS, 1.0F, 1.0F);
		SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity(
				world, Vec3.atCenterOf(pos),
				SpectrumParticleTypes.getSparkleRisingParticle(particleColor),
				10, new Vec3(0.5, 0.5, 0.5),
				new Vec3(0.1, 0.1, 0.1));
		SpectrumS2CPacketSender.playColorTransmissionParticle(
				world,
				new ColoredTransmission(
						new Vec3(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D),
						new BlockPositionSource(currentPos), 6,
						particleColor)
		);
	}

	private DyeColor getEffectColor() {
		return this.effectColor;
	}

	public Upgradeable.UpgradeType getUpgradeType() {
		return this.upgradeType;
	}

	public int getUpgradeMod() {
		return this.upgradeMod;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new UpgradeBlockEntity(pos, state);
	}
	
}
