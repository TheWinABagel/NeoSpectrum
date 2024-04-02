package de.dafuqs.spectrum.compat.idwtialsimmoedm;

import de.dafuqs.spectrum.enchantments.SpectrumEnchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

@OnlyIn(Dist.CLIENT)
public class IdwtialsimmoedmCompat { //todoforge idwtialsimmoedm
    public static void register() {
//        GatherDescriptionCallback.ENCHANTMENT.register(ench -> {
//            Entity player = Minecraft.getInstance().player;
//            Component original = DefaultDescriptions.forEnchantmentRaw(ench);
//            if (original == null) return null;
//            if (ench instanceof SpectrumEnchantment spectrumEnchantment && !spectrumEnchantment.canEntityUse(player)) {
//                return GatherDescriptionCallback.wrapDescription(original.copy().withStyle(ChatFormatting.OBFUSCATED));
//            }
//            return null;
//        });
    }
}
