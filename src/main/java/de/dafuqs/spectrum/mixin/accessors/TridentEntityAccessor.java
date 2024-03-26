package de.dafuqs.spectrum.mixin.accessors;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThrownTrident.class)
public interface TridentEntityAccessor {

    @Accessor("ID_LOYALTY")
    static EntityDataAccessor<Byte> spectrum$getLoyalty() {
        return null;
    }

    @Accessor("ID_FOIL")
    static EntityDataAccessor<Boolean> spectrum$getEnchanted() {
        return null;
    }

    @Accessor("tridentItem")
    ItemStack spectrum$getTridentStack();

    @Accessor("tridentItem")
    void spectrum$setTridentStack(ItemStack stack);

    @Accessor("dealtDamage")
    boolean spectrum$hasDealtDamage();

    @Accessor("dealtDamage")
    void spectrum$setDealtDamage(boolean dealtDamage);

}