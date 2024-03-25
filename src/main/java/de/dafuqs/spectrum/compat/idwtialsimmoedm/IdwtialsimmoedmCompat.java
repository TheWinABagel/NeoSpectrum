package de.dafuqs.spectrum.compat.idwtialsimmoedm;

import de.dafuqs.spectrum.enchantments.SpectrumEnchantment;
import io.wispforest.idwtialsimmoedm.api.DefaultDescriptions;
import io.wispforest.idwtialsimmoedm.api.GatherDescriptionCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class IdwtialsimmoedmCompat {
    public static void register() {
        GatherDescriptionCallback.ENCHANTMENT.register(ench -> {
            Entity player = Minecraft.getInstance().player;
            Component original = DefaultDescriptions.forEnchantmentRaw(ench);
            if (original == null) return null;
            if (ench instanceof SpectrumEnchantment spectrumEnchantment && !spectrumEnchantment.canEntityUse(player)) {
                return GatherDescriptionCallback.wrapDescription(original.copy().withStyle(ChatFormatting.OBFUSCATED));
            }
            return null;
        });
    }
}
