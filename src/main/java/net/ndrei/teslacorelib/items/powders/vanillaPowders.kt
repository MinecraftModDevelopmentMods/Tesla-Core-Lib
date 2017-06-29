@file:Suppress("unused")

package net.ndrei.teslacorelib.items.powders

import net.ndrei.teslacorelib.*
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-29.
 */
@AutoRegisterPowder
@AutoRegisterColoredThingy
object IronPowder : ColoredPowderItem(MATERIAL_IRON, COLOR_IRON, 0.7f, "ingotIron")

@AutoRegisterPowder
@AutoRegisterColoredThingy
object GoldPowder : ColoredPowderItem(MATERIAL_GOLD, COLOR_GOLD, 1.0f, "ingotGold")

@AutoRegisterPowder
@AutoRegisterColoredThingy
object CoalPowder : ColoredPowderItem(MATERIAL_COAL, COLOR_COAL)

@AutoRegisterPowder
@AutoRegisterColoredThingy
object DiamondPowder : ColoredPowderItem(MATERIAL_DIAMOND, COLOR_DIAMOND)

@AutoRegisterPowder
@AutoRegisterColoredThingy
object EmeraldPowder : ColoredPowderItem(MATERIAL_DIAMOND, COLOR_EMERALD)
