package net.ndrei.teslacorelib.inventory

import net.minecraft.util.EnumFacing
import net.minecraftforge.items.IItemHandler
import net.ndrei.teslacorelib.capabilities.inventory.SidedItemHandlerConfig
import java.util.*

/**
 * Created by CF on 2017-06-28.
 */
class SidedItemHandler(handlers: MutableList<IItemHandler>?, private val sidedConfig: SidedItemHandlerConfig)
    : MultiItemHandler(handlers ?: mutableListOf()), ISidedItemHandler {
    constructor(sidedConfig: SidedItemHandlerConfig) : this(null, sidedConfig) {}

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        var result = IntArray(0)

        var index = 0
        for (i in 0..this.inventories - 1) {
            val handler = this.getInventory(i)
            val size = handler.slots
            if (handler is ColoredItemHandler) {
                val color = handler.color
                if (this.sidedConfig!!.isSideSet(color, side)) {
                    val startIndex = result.size
                    result = Arrays.copyOf(result, result.size + size)
                    for (x in 0..size - 1) {
                        result[startIndex + x] = index + x
                    }
                }
            }
            index += size
        }

        return result
    }

    fun getSideWrapper(side: EnumFacing): SidedItemHandlerWrapper {
        // TODO: cache them!
        return SidedItemHandlerWrapper(this, side)
    }
}
