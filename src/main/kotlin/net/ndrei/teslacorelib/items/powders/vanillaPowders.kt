@file:Suppress("unused")

package net.ndrei.teslacorelib.items.powders

import net.ndrei.teslacorelib.*
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-29.
 */
@AutoRegisterPowder
@AutoRegisterColoredThingy
object IronPowder : ColoredPowderItem(MATERIAL_IRON, MaterialColors.IRON.color, 0.7f, "ingotIron")

@AutoRegisterPowder
@AutoRegisterColoredThingy
object GoldPowder : ColoredPowderItem(MATERIAL_GOLD, MaterialColors.GOLD.color, 1.0f, "ingotGold")

//@AutoRegisterPowder
//@AutoRegisterColoredThingy
//object CoalPowder : ColoredPowderItem(MATERIAL_COAL, MaterialColors.COAL.color)

@AutoRegisterPowder
@AutoRegisterColoredThingy
object DiamondPowder : ColoredPowderItem(MATERIAL_DIAMOND, MaterialColors.DIAMOND.color, 0.0f, "gemDiamond")

@AutoRegisterPowder
@AutoRegisterColoredThingy
object EmeraldPowder : ColoredPowderItem(MATERIAL_EMERALD, MaterialColors.EMERALD.color, 0.0f, "gemEmerald")
