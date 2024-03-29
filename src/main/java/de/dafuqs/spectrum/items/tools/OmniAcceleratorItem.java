package de.dafuqs.spectrum.items.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import de.dafuqs.spectrum.api.energy.InkCost;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.api.interaction.OmniAcceleratorProjectile;
import de.dafuqs.spectrum.api.render.DynamicItemRenderer;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class OmniAcceleratorItem extends BundleItem implements InkPowered {

	protected static final InkCost COST = new InkCost(InkColors.YELLOW, 20);
	
	public OmniAcceleratorItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		return ItemUtils.startUsingInstantly(world, user, hand);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 20;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
		if (!(user instanceof ServerPlayer player)) {
			return stack;
		}

		Optional<ItemStack> shootStackOptional = getFirstStack(stack);
		if (shootStackOptional.isEmpty()) {
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
			return stack;
		}

		if (!InkPowered.tryDrainEnergy(player, COST)) {
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SpectrumSoundEvents.USE_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
			return stack;
		}

		ItemStack shootStack = shootStackOptional.get();
		OmniAcceleratorProjectile projectile = OmniAcceleratorProjectile.get(shootStack);
		if (projectile.createProjectile(shootStack, user, world) != null) {
			world.playSound(null, user.getX(), user.getY(), user.getZ(), projectile.getSoundEffect(), SoundSource.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
			if (!player.isCreative()) {
				decrementFirstItem(stack);
			}
		}
		
		return stack;
	}
	
	public static void decrementFirstItem(ItemStack acceleratorStack) {
		CompoundTag nbtCompound = acceleratorStack.getOrCreateTag();
		if (nbtCompound.contains("Items")) {
			ListTag itemsList = nbtCompound.getList("Items", Tag.TAG_COMPOUND);
			if (!itemsList.isEmpty()) {
				CompoundTag stackNbt = itemsList.getCompound(0);
				int count = stackNbt.getByte("Count");
				if (count > 1) {
					stackNbt.putByte("Count", (byte) (count - 1));
				} else {
					itemsList.remove(0);
					if (itemsList.isEmpty()) {
						acceleratorStack.removeTagKey("Items");
					}
				}
			}
		}
	}
	
	public static Optional<ItemStack> getFirstStack(ItemStack stack) {
		CompoundTag nbtCompound = stack.getOrCreateTag();
		if (!nbtCompound.contains("Items")) {
			return Optional.empty();
		} else {
			ListTag itemsList = nbtCompound.getList("Items", Tag.TAG_COMPOUND);
			if (itemsList.isEmpty()) {
				return Optional.empty();
			} else {
				CompoundTag stackNbt = itemsList.getCompound(0);
				ItemStack itemStack = ItemStack.of(stackNbt);
				return Optional.of(itemStack);
			}
		}
	}

	@Override
	public List<InkColor> getUsedColors() {
		return List.of(COST.getColor());
	}

	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		addInkPoweredTooltip(tooltip);
	}

	@Environment(EnvType.CLIENT)
	public static class Renderer implements DynamicItemRenderer {
		public Renderer() {}
		@Override
		public void render(ItemRenderer renderer, ItemStack stack, ItemDisplayContext mode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel model) {
			renderer.render(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, model);
			if(mode != ItemDisplayContext.GUI) return;
			
			Optional<ItemStack> optionalStack = getFirstStack(stack);
			if(optionalStack.isEmpty()) {
				return;
			}
			ItemStack bundledStack = optionalStack.get();
			
			Minecraft client = Minecraft.getInstance();
			BakedModel bundledModel = renderer.getModel(bundledStack, client.level, client.player, 0);
			
			matrices.pushPose();
			matrices.scale(0.5F, 0.5F, 0.5F);
			matrices.translate(0.5F, 0.5F, 0.5F);
			renderer.render(bundledStack, mode, leftHanded, matrices, vertexConsumers, light, overlay, bundledModel);
			matrices.popPose();
		}
	}

}
