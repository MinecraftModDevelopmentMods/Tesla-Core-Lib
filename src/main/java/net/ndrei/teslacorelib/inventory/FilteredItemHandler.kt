package net.ndrei.teslacorelib.inventory

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.ndrei.teslacorelib.compatibility.ItemStackUtil

/**
 * Created by CF on 2017-06-28.
 */
open class FilteredItemHandler protected constructor(protected val handler: IItemHandler)
    : IFilteredItemHandler {

    override fun canInsertItem(slot: Int, stack: ItemStack) = true

    override fun canExtractItem(slot: Int) = true

    override fun getSlots() = this.handler.slots

    override fun getStackInSlot(slot: Int) = this.handler.getStackInSlot(slot)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (!this.canInsertItem(slot, stack)) {
            return stack
        }
        return this.handler.insertItem(slot, stack, simulate)
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (!this.canExtractItem(slot)) {
            return ItemStackUtil.emptyStack
        }
        return this.handler.extractItem(slot, amount, simulate)
    }

    override fun getSlotLimit(slot: Int) = this.handler.getSlotLimit(slot)

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        if (this.handler is IItemHandlerModifiable) {
            this.handler.setStackInSlot(slot, stack)
        } else {
            throw RuntimeException("Inner item handler is not modifiable.")
        }
    }
}
