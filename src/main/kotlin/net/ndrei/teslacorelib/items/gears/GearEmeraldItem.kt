package net.ndrei.teslacorelib.items.gears

import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-29.
 */
@AutoRegisterGear
@AutoRegisterColoredThingy
object GearEmeraldItem : ColoredGearItem("emerald", MaterialColors.EMERALD.color, "gearIron", "gemEmerald")