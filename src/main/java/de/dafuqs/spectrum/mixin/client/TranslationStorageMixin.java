package de.dafuqs.spectrum.mixin.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientLanguage.class)
public abstract class TranslationStorageMixin {

    @Mutable
    @Shadow
    @Final
    private Map<String, String> storage;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addTranslations(Map<String, String> translations, boolean rightToLeft, CallbackInfo ci) {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) != Calendar.APRIL || calendar.get(Calendar.DAY_OF_MONTH) != 1) return;

        Map<String, String> builder = new HashMap<>(translations);
        builder.put("block.spectrum.crystallarieum", getCrystallarieuaeuieueum());
        builder.put("item.spectrum.ring_of_pursuit", "Ring of Fursuit");

        this.storage = builder;
    }
    
    @Unique
    private static String getCrystallarieuaeuieueum() {
        List<String> possibilities = new ArrayList<>() {{
            add("Crystallarieum");
            add("Crystallareium");
            add("Crystallerium");
            add("Crystallarium");
            add("Crystallium");
            add("Crystalleium");
            add("Crystallum");
            add("Crystallarieium");
            add("Christalerium");
        }};
        char c = Minecraft.getInstance().getUser().getName().toCharArray()[0];
        return possibilities.get((int) c % possibilities.size());
    }

}