package net.ndrei.teslacorelib.inventory

import net.minecraft.util.EnumFacing
import net.minecraftforge.items.IItemHandler

/**
 * Created by CF on 2017-06-28.
 */
interface ISidedItemHandler : IItemHandler {
    fun getSlotsForFace(side: EnumFacing): IntArray
}
