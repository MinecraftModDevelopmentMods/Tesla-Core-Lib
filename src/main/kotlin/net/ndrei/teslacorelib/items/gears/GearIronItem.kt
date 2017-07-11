package net.ndrei.teslacorelib.items.gears

import net.ndrei.teslacorelib.MATERIAL_IRON
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-22.
 */
@AutoRegisterGear
@AutoRegisterColoredThingy
object GearIronItem : ColoredGearItem(MATERIAL_IRON, MaterialColors.IRON.color, "gearStone", "ingotIron")