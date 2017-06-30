package net.ndrei.teslacorelib.capabilities.inventory

import net.minecraft.item.EnumDyeColor
import net.minecraft.util.EnumFacing
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandlerInfo

/**
 * Created by CF on 2017-06-28.
 */
interface ISidedItemHandlerConfig {
    val coloredInfo: List<ColoredItemHandlerInfo>
    fun addColoredInfo(name: String, color: EnumDyeColor, highlight: BoundingRectangle)
    fun addColoredInfo(name: String, color: EnumDyeColor, highlight: BoundingRectangle, index: Int)
    fun addColoredInfo(info: ColoredItemHandlerInfo)

    fun getSidesForColor(color: EnumDyeColor): List<EnumFacing>
    fun setSidesForColor(color: EnumDyeColor, sides: List<EnumFacing>)

    fun isSideSet(color: EnumDyeColor, side: EnumFacing): Boolean

    fun removeColoredInfo(color: EnumDyeColor)
}
