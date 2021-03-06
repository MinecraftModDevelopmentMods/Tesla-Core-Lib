package net.ndrei.teslacorelib.inventory

import net.minecraft.item.EnumDyeColor

@Suppress("unused")
/**
 * Created by CF on 2017-06-28.
 */
class ColoredItemHandlerInfo(val name: String, val color: EnumDyeColor, val highlight: BoundingRectangle, var index: Int = 0)