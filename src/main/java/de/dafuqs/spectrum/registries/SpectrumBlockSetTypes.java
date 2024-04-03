package de.dafuqs.spectrum.registries;

import cpw.mods.util.Lazy;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.function.Supplier;

import static de.dafuqs.spectrum.SpectrumCommon.locate;

public class SpectrumBlockSetTypes {
    public static final BlockSetType POLISHED_BASALT = BlockSetType.register(new BlockSetType(locate("polished_basalt").toString(),
        true,
        SoundType.BASALT,
        SoundEvents.IRON_DOOR_CLOSE,
        SoundEvents.IRON_DOOR_OPEN,
        SoundEvents.IRON_TRAPDOOR_CLOSE,
        SoundEvents.IRON_TRAPDOOR_OPEN,
        SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
        SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
        SoundEvents.STONE_BUTTON_CLICK_OFF,
        SoundEvents.STONE_BUTTON_CLICK_ON));

    public static final BlockSetType POLISHED_CALCITE = BlockSetType.register(new BlockSetType(locate("polished_calcite").toString(),
        true,
        SoundType.CALCITE,
        SoundEvents.IRON_DOOR_CLOSE,
        SoundEvents.IRON_DOOR_OPEN,
        SoundEvents.IRON_TRAPDOOR_CLOSE,
        SoundEvents.IRON_TRAPDOOR_OPEN,
        SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
        SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
        SoundEvents.STONE_BUTTON_CLICK_OFF,
        SoundEvents.STONE_BUTTON_CLICK_ON));

    public static final BlockSetType POLISHED_BLACKSLAG = BlockSetType.register(new BlockSetType(locate("polished_blackslag").toString(),
        true,
        SoundType.DEEPSLATE,
        SoundEvents.IRON_DOOR_CLOSE,
        SoundEvents.IRON_DOOR_OPEN,
        SoundEvents.IRON_TRAPDOOR_CLOSE,
        SoundEvents.IRON_TRAPDOOR_OPEN,
        SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
        SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
        SoundEvents.STONE_BUTTON_CLICK_OFF,
        SoundEvents.STONE_BUTTON_CLICK_ON));

    // TODO - Custom wood sounds? Maybe?
    public static final BlockSetType NOXWOOD = copy(BlockSetType.ACACIA, locate("noxwood").toString());
    
    public static final BlockSetType COLORED_WOOD = copy(BlockSetType.ACACIA, locate("colored_wood").toString());

    private static BlockSetType copy(BlockSetType old, String name) {
        return new BlockSetType(name,
                old.canOpenByHand(),
                old.soundType(),
                old.doorClose(),
                old.doorOpen(),
                old.trapdoorClose(),
                old.trapdoorOpen(),
                old.pressurePlateClickOff(),
                old.pressurePlateClickOn(),
                old.buttonClickOff(),
                old.buttonClickOn());
    }
}
