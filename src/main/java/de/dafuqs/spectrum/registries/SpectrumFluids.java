package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.color.ItemColors;
import de.dafuqs.spectrum.blocks.fluid.DragonrotFluid;
import de.dafuqs.spectrum.blocks.fluid.LiquidCrystalFluid;
import de.dafuqs.spectrum.blocks.fluid.MidnightSolutionFluid;
import de.dafuqs.spectrum.blocks.fluid.MudFluid;
import de.dafuqs.spectrum.helpers.ColorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.joml.Vector3f;

public class SpectrumFluids {
	
	// LIQUID CRYSTAL
	public static final FlowingFluid LIQUID_CRYSTAL = new LiquidCrystalFluid.Still();
	public static final FlowingFluid FLOWING_LIQUID_CRYSTAL = new LiquidCrystalFluid.Flowing();
	public static final int LIQUID_CRYSTAL_TINT = 0xcbbbcb;
	public static final Vector3f LIQUID_CRYSTAL_COLOR_VEC = ColorHelper.colorIntToVec(LIQUID_CRYSTAL_TINT);
	public static final ResourceLocation LIQUID_CRYSTAL_OVERLAY_TEXTURE = SpectrumCommon.locate("textures/misc/liquid_crystal_overlay.png");
	public static final float LIQUID_CRYSTAL_OVERLAY_ALPHA = 0.6F;
	
	// MUD
	public static final FlowingFluid MUD = new MudFluid.StillMud();
	public static final FlowingFluid FLOWING_MUD = new MudFluid.FlowingMud();
	public static final int MUD_TINT = 0x4e2e0a;
	public static final Vector3f MUD_COLOR_VEC = ColorHelper.colorIntToVec(MUD_TINT);
	public static final ResourceLocation MUD_OVERLAY_TEXTURE = SpectrumCommon.locate("textures/misc/mud_overlay.png");
	public static final float MUD_OVERLAY_ALPHA = 0.995F;
	
	// MIDNIGHT SOLUTION
	public static final FlowingFluid MIDNIGHT_SOLUTION = new MidnightSolutionFluid.Still();
	public static final FlowingFluid FLOWING_MIDNIGHT_SOLUTION = new MidnightSolutionFluid.Flowing();
	public static final int MIDNIGHT_SOLUTION_TINT = 0x11183b;
	public static final Vector3f MIDNIGHT_SOLUTION_COLOR_VEC = ColorHelper.colorIntToVec(MIDNIGHT_SOLUTION_TINT);
	public static final ResourceLocation MIDNIGHT_SOLUTION_OVERLAY_TEXTURE = SpectrumCommon.locate("textures/misc/midnight_solution_overlay.png");
	public static final float MIDNIGHT_SOLUTION_OVERLAY_ALPHA = 0.995F;
	
	// DRAGONROT
	public static final FlowingFluid DRAGONROT = new DragonrotFluid.Still();
	public static final FlowingFluid FLOWING_DRAGONROT = new DragonrotFluid.Flowing();
	public static final int DRAGONROT_TINT = 0xe3772f;
	public static final Vector3f DRAGONROT_COLOR_VEC = ColorHelper.colorIntToVec(DRAGONROT_TINT);
	public static final ResourceLocation DRAGONROT_OVERLAY_TEXTURE = SpectrumCommon.locate("textures/misc/dragonrot_overlay.png");
	public static final float DRAGONROT_OVERLAY_ALPHA = 0.98F;
	
	public static void register() {
		registerFluid("liquid_crystal", LIQUID_CRYSTAL, FLOWING_LIQUID_CRYSTAL, DyeColor.LIGHT_GRAY);
		registerFluid("mud", MUD, FLOWING_MUD, DyeColor.BROWN);
		registerFluid("midnight_solution", MIDNIGHT_SOLUTION, FLOWING_MIDNIGHT_SOLUTION, DyeColor.GRAY);
		registerFluid("dragonrot", DRAGONROT, FLOWING_DRAGONROT, DyeColor.GRAY);
	}

	private static void registerFluid(String name, Fluid stillFluid, Fluid flowingFluid, DyeColor dyeColor) {
		Registry.register(BuiltInRegistries.FLUID, SpectrumCommon.locate(name), stillFluid);
		Registry.register(BuiltInRegistries.FLUID, SpectrumCommon.locate("flowing_" + name), flowingFluid);
		ItemColors.FLUID_COLORS.registerColorMapping(stillFluid, dyeColor);
		ItemColors.FLUID_COLORS.registerColorMapping(flowingFluid, dyeColor);
	}
	
	@Environment(EnvType.CLIENT)
	public static void registerClient() {
		setupFluidRendering(LIQUID_CRYSTAL, FLOWING_LIQUID_CRYSTAL, "liquid_crystal", LIQUID_CRYSTAL_TINT);
		setupFluidRendering(MUD, FLOWING_MUD, "mud", MUD_TINT);
		setupFluidRendering(MIDNIGHT_SOLUTION, FLOWING_MIDNIGHT_SOLUTION, "midnight_solution", MIDNIGHT_SOLUTION_TINT);
		setupFluidRendering(DRAGONROT, FLOWING_DRAGONROT, "dragonrot", DRAGONROT_TINT);
	}

	@Environment(EnvType.CLIENT)
	private static void setupFluidRendering(final Fluid stillFluid, final Fluid flowingFluid, final String name, int tint) {
		FluidRenderHandlerRegistry.INSTANCE.register(stillFluid, flowingFluid, new SimpleFluidRenderHandler(
				SpectrumCommon.locate("block/" + name + "_still"),
				SpectrumCommon.locate("block/" + name + "_flow"),
				tint
		));

		BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), stillFluid, flowingFluid);
	}
	
}
