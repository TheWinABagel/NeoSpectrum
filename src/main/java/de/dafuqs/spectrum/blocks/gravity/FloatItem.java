package de.dafuqs.spectrum.blocks.gravity;

import de.dafuqs.spectrum.api.item.GravitableItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FloatItem extends Item implements GravitableItem {
	
	private final float gravityMod;
	
	public FloatItem(Properties settings, float gravityMod) {
		super(settings);
		this.gravityMod = gravityMod;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		applyGravity(stack, world, entity);
	}
	
	@Override
	public float getGravityMod() {
		return gravityMod;
	}
	
}
