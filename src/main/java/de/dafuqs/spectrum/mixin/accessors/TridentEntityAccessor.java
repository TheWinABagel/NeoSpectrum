package de.dafuqs.spectrum.mixin.accessors;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThrownTrident.class)
public interface TridentEntityAccessor {

    @Accessor("LOYALTY")
    static EntityDataAccessor<Byte> spectrum$getLoyalty() {
        return null;
    }

    @Accessor("ENCHANTED")
    static EntityDataAccessor<Boolean> spectrum$getEnchanted() {
        return null;
    }

    @Accessor("tridentStack")
    ItemStack spectrum$getTridentStack();

    @Accessor("tridentStack")
    void spectrum$setTridentStack(ItemStack stack);

    @Accessor("dealtDamage")
    boolean spectrum$hasDealtDamage();

    @Accessor("dealtDamage")
    void spectrum$setDealtDamage(boolean dealtDamage);

}