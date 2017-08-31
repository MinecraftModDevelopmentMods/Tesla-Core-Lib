package net.ndrei.teslacorelib.items.gears

import net.ndrei.teslacorelib.MATERIAL_LAPIS
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-29.
 */
@AutoRegisterGear
@AutoRegisterColoredThingy
@Suppress("unused")
object GearLapisItem : ColoredGearItem(MATERIAL_LAPIS, MaterialColors.LAPIS.color, "gearStone", "gemLapis")
