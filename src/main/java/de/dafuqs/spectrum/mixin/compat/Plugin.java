package de.dafuqs.spectrum.mixin.compat;

import net.minecraftforge.fml.ModList;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.*;

import java.util.*;

public final class Plugin implements IMixinConfigPlugin {
	
	@Override
	public void onLoad(String mixinPackage) {
	}
	
	@Override
	public String getRefMapperConfig() {
		return null;
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		String COMPAT_PACKAGE_ROOT = Plugin.class.getPackageName(); // Shorthand getting the plugin package to ensure not making trouble with other mixins
		String COMPAT_PRESENT_KEY = "present";

		ModList LOADER = ModList.get();
		
		if (!mixinClassName.startsWith(COMPAT_PACKAGE_ROOT)) {
			return true; // We do not meddle with the others' work
		}
		String[] compatRoot = COMPAT_PACKAGE_ROOT.split("\\.");
		String[] mixinPath = mixinClassName.split("\\.");
		// The id of the mod the mixin depends on
		String compatModId = mixinPath[compatRoot.length];
		// Whether the mixin is for when the mod is loaded or not
		boolean isPresentMixin = mixinPath[compatRoot.length + 1].equals(COMPAT_PRESENT_KEY);

		if (isPresentMixin) {
			// We only load the mixin if the mod we want to be present is found
			return LOADER.isLoaded(compatModId);
		}
		return !LOADER.isLoaded(compatModId);
	}
	
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}
	
	@Override
	public List<String> getMixins() {
		return List.of();
	}
	
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
	
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
	
}