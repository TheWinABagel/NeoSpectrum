package de.dafuqs.spectrum.blocks.spirit_sallow;

import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OminousSaplingBlock extends BushBlock implements EntityBlock {
	
	public OminousSaplingBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!world.isClientSide()) {
			OminousSaplingBlockEntity ominousSaplingBlockEntity = getBlockEntity(world, pos);
			if (ominousSaplingBlockEntity != null) {
				player.displayClientMessage(Component.nullToEmpty("Sapling owner UUID: " + ominousSaplingBlockEntity.getOwnerUUID()), true);
			} else {
				player.displayClientMessage(Component.nullToEmpty("Sapling block entity putt :("), true);
			}
		}
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (world.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(2) == 0) {
			this.generateOminousTree(world, pos, state, random);
		}
	}
	
	private void generateOminousTree(ServerLevel world, BlockPos pos, BlockState state, RandomSource random) {
		OminousSaplingBlockEntity ominousSaplingBlockEntity = getBlockEntity(world, pos);
		if (ominousSaplingBlockEntity != null) {
			UUID ownerUUID = ominousSaplingBlockEntity.getOwnerUUID();
			Player playerEntity = PlayerOwned.getPlayerEntityIfOnline(ownerUUID);
			if (playerEntity instanceof ServerPlayer serverPlayerEntity) {
				Support.grantAdvancementCriterion(serverPlayerEntity, "endgame/grow_ominous_sapling", "grow");
			}
		}
		
		// TODO: grow!
	}
	
	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OminousSaplingBlockEntity(SpectrumBlockEntities.OMINOUS_SAPLING, pos, state);
	}
	
	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return null;
	}
	
	private OminousSaplingBlockEntity getBlockEntity(Level world, BlockPos blockPos) {
		BlockEntity saplingBlockEntity = world.getBlockEntity(blockPos);
		if (saplingBlockEntity instanceof OminousSaplingBlockEntity) {
			return (OminousSaplingBlockEntity) saplingBlockEntity;
		} else {
			return null;
		}
	}
	
}
