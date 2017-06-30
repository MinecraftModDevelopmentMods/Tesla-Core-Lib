@file:Suppress("unused")

package net.ndrei.teslacorelib.items.sheets

import net.ndrei.teslacorelib.*
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-29.
 */
@AutoRegisterSheet
@AutoRegisterColoredThingy
object IronSheet : ColoredSheetItem(MATERIAL_IRON, COLOR_IRON)

@AutoRegisterSheet
@AutoRegisterColoredThingy
object GoldSheet : ColoredSheetItem(MATERIAL_GOLD, COLOR_GOLD)

@AutoRegisterSheet
@AutoRegisterColoredThingy
object DiamondSheet : ColoredSheetItem(MATERIAL_DIAMOND, COLOR_DIAMOND)

@AutoRegisterSheet
@AutoRegisterColoredThingy
object EmeraldSheet : ColoredSheetItem(MATERIAL_EMERALD, COLOR_EMERALD)