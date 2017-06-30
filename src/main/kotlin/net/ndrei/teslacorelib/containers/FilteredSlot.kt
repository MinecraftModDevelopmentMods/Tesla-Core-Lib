package net.ndrei.teslacorelib.containers

import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler
import net.ndrei.teslacorelib.inventory.IFilteredItemHandler

/**
 * Created by CF on 2017-06-28.
 */
class FilteredSlot(private val handler: IFilteredItemHandler, index: Int, xPosition: Int, yPosition: Int)
    : SlotItemHandler(handler, index, xPosition, yPosition) {

    override fun isItemValid(stack: ItemStack): Boolean {
        return this.handler.canInsertItem(this.slotIndex, stack)
    }
}
