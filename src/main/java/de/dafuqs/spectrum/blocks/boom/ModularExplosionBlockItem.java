package de.dafuqs.spectrum.blocks.boom;

import de.dafuqs.spectrum.api.item.ModularExplosionProvider;
import de.dafuqs.spectrum.explosion.ModularExplosionDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModularExplosionBlockItem extends BlockItem implements ModularExplosionProvider {
	
	private final int maxModifierCount;
	private final double baseBlastRadius;
	private final float baseDamage;
	
	public ModularExplosionBlockItem(Block block, double baseBlastRadius, float baseDamage, int maxModifierCount, Properties settings) {
		super(block, settings);
		this.maxModifierCount = maxModifierCount;
		this.baseBlastRadius = baseBlastRadius;
		this.baseDamage = baseDamage;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		ModularExplosionDefinition.getFromStack(stack).appendTooltip(tooltip, this);
	}
	
	@Override
	public double getBaseExplosionBlastRadius() {
		return baseBlastRadius;
	}
	
	@Override
	public float getBaseExplosionDamage() {
		return baseDamage;
	}
	
	@Override
	public int getMaxExplosionModifiers() {
		return maxModifierCount;
	}
	
}
