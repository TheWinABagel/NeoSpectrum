package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.spectrum.cca.everpromise_ribbon.DefaultEverpromiseRibbonCapability;
import de.dafuqs.spectrum.compat.claims.GenericClaimModsCompat;
import de.dafuqs.spectrum.helpers.EntityHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class EverpromiseRibbonItem extends Item {
	
	public EverpromiseRibbonItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
		Level world = user.level();
		if (!GenericClaimModsCompat.canInteract(world, entity, user)) {
			return InteractionResult.FAIL;
		}
		
		if (stack.hasCustomHoverName() && !(entity instanceof Player)) {
			if (entity.isAlive()) {
				if (world.isClientSide) {
					Level entityWorld = entity.level();
					RandomSource random = entityWorld.random;
					for (int i = 0; i < 7; ++i) {
						world.addParticle(ParticleTypes.HEART, entity.getRandomX(1.0), entity.getRandomY() + 0.5, entity.getRandomZ(1.0),
								random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02);
					}
				} else {
					DefaultEverpromiseRibbonCapability.attachRibbon(entity);
					
					Component newName = stack.getHoverName();
					if (newName instanceof MutableComponent mutableText) {
						newName = Component.literal(mutableText.getString() + " ❣").setStyle(mutableText.getStyle());
					}
					entity.setCustomName(newName);
					if (entity instanceof Mob mobEntity) {
						mobEntity.setPersistenceRequired();
						EntityHelper.addPlayerTrust(mobEntity, user);
					}
				}
				
				stack.shrink(1);
			}
			
			return InteractionResult.sidedSuccess(world.isClientSide);
		} else {
			return InteractionResult.PASS;
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.everpromise_ribbon.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.everpromise_ribbon.tooltip2").withStyle(ChatFormatting.GRAY));
	}
	
	
}
