package de.dafuqs.spectrum.mixin.compat.enchdesc.present;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.dafuqs.spectrum.enchantments.SpectrumEnchantment;
import net.darkhax.enchdesc.DescriptionManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(DescriptionManager.class)
public abstract class DescriptionManagerMixin {
    @ModifyReturnValue(method = "getDescription", at = @At("RETURN"), remap = false)
    private static MutableComponent spectrum$obfuscateDescription(MutableComponent original, Enchantment ench) {
        Entity player = Minecraft.getInstance().player; // that feels kinda risky, since the class is not annotated as EnvType.Client
        if (ench instanceof SpectrumEnchantment spectrumEnchantment && !spectrumEnchantment.canEntityUse(player)) {
            return original.copy().withStyle(ChatFormatting.OBFUSCATED);
        }
        return original;
    }
}
