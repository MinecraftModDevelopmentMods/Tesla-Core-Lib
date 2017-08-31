@file:Suppress("unused")

package net.ndrei.teslacorelib.items.sheets

import net.ndrei.teslacorelib.*
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-29.
 */
@AutoRegisterSheet
@AutoRegisterColoredThingy
object IronSheet : ColoredSheetItem(MATERIAL_IRON, MaterialColors.IRON.color)

@AutoRegisterSheet
@AutoRegisterColoredThingy
object GoldSheet : ColoredSheetItem(MATERIAL_GOLD, MaterialColors.GOLD.color)

@AutoRegisterSheet
@AutoRegisterColoredThingy
object DiamondSheet : ColoredSheetItem(MATERIAL_DIAMOND, MaterialColors.DIAMOND.color)

@AutoRegisterSheet
@AutoRegisterColoredThingy
object EmeraldSheet : ColoredSheetItem(MATERIAL_EMERALD, MaterialColors.EMERALD.color)

@AutoRegisterSheet
@AutoRegisterColoredThingy
object LapisSheet : ColoredSheetItem(MATERIAL_LAPIS, MaterialColors.LAPIS.color)

@AutoRegisterSheet
@AutoRegisterColoredThingy
object RedstoneSheet : ColoredSheetItem(MATERIAL_REDSTONE, MaterialColors.REDSTONE.color)
