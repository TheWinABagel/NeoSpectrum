package de.dafuqs.spectrum.items.trinkets;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.compat.claims.GenericClaimModsCompat;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class RadiancePinItem extends SpectrumTrinketItem {
	
	public static final int CHECK_EVERY_X_TICKS = 20;
	public static final int MAX_LIGHT_LEVEL = 7;
	public static final BlockState LIGHT_BLOCK_STATE = SpectrumBlocks.DECAYING_LIGHT_BLOCK.defaultBlockState().setValue(LightBlock.LEVEL, 15);
	public static final BlockState LIGHT_BLOCK_STATE_WATER = SpectrumBlocks.DECAYING_LIGHT_BLOCK.defaultBlockState().setValue(LightBlock.LEVEL, 15).setValue(LightBlock.WATERLOGGED, true);
	
	public RadiancePinItem(Properties settings) {
		super(settings, SpectrumCommon.locate("unlocks/trinkets/radiance_pin"));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.radiance_pin.tooltip").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		super.curioTick(slotContext, stack);
		LivingEntity entity = slotContext.entity();
		Level level = entity.level();
		if (!level.isClientSide && level.getGameTime() % CHECK_EVERY_X_TICKS == 0) {
			if (entity instanceof Player playerEntity && playerEntity.isSpectator()) {
				return;
			}
			BlockPos pos = entity.blockPosition();
			if (!GenericClaimModsCompat.canPlaceBlock(level, pos, entity)) {
				return;
			}

			if (!level.isOutsideBuildHeight(pos) && level.getMaxLocalRawBrightness(pos) <= MAX_LIGHT_LEVEL) {
				BlockState currentState = level.getBlockState(pos);
				boolean placed = false;
				if (currentState.isAir()) {
					level.setBlock(pos, LIGHT_BLOCK_STATE, 3);
					placed = true;
				} else if (currentState.equals(Blocks.WATER.defaultBlockState())) {
					level.setBlock(pos, LIGHT_BLOCK_STATE_WATER, 3);
					placed = true;
				} else if (currentState.is(SpectrumBlocks.DECAYING_LIGHT_BLOCK)) {
					if (currentState.getValue(LightBlock.WATERLOGGED)) {
						level.setBlock(pos, LIGHT_BLOCK_STATE_WATER, 3);
					} else {
						level.setBlock(pos, LIGHT_BLOCK_STATE, 3);
					}
					placed = true;
				}
				if (placed) {
					sendSmallLightCreatedParticle((ServerLevel) level, pos);
					level.playSound(null, entity.getX() + 0.5, entity.getY() + 0.5, entity.getZ() + 0.5, SpectrumSoundEvents.RADIANCE_STAFF_PLACE, SoundSource.PLAYERS, 0.08F, 0.9F + level.random.nextFloat() * 0.2F);
				}
			}
		}
	}

    public static void sendSmallLightCreatedParticle(ServerLevel world, BlockPos blockPos) {
        SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity(world, Vec3.atCenterOf(blockPos),
                SpectrumParticleTypes.SHIMMERSTONE_SPARKLE,
                4,
                Vec3.ZERO,
                new Vec3(0.1, 0.1, 0.1));
    }

}
