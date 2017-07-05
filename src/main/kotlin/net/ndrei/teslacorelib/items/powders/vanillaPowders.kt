@file:Suppress("unused")

package net.ndrei.teslacorelib.items.powders

import net.ndrei.teslacorelib.MATERIAL_COAL
import net.ndrei.teslacorelib.MATERIAL_DIAMOND
import net.ndrei.teslacorelib.MATERIAL_EMERALD
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-29.
 */
//@AutoRegisterPowder
//@AutoRegisterColoredThingy
//object IronPowder : ColoredPowderItem(MATERIAL_IRON, COLOR_IRON, 0.7f, "ingotIron")
//
//@AutoRegisterPowder
//@AutoRegisterColoredThingy
//object GoldPowder : ColoredPowderItem(MATERIAL_GOLD, COLOR_GOLD, 1.0f, "ingotGold")

@AutoRegisterPowder
@AutoRegisterColoredThingy
object CoalPowder : ColoredPowderItem(MATERIAL_COAL, MaterialColors.COAL.color)

@AutoRegisterPowder
@AutoRegisterColoredThingy
object DiamondPowder : ColoredPowderItem(MATERIAL_DIAMOND, MaterialColors.DIAMOND.color)

@AutoRegisterPowder
@AutoRegisterColoredThingy
object EmeraldPowder : ColoredPowderItem(MATERIAL_EMERALD, MaterialColors.EMERALD.color)
