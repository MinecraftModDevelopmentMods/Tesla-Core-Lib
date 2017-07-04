package net.ndrei.teslacorelib.inventory

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.ndrei.teslacorelib.compatibility.ItemStackUtil

/**
 * Created by CF on 2017-06-28.
 */
open class FilteredItemHandler protected constructor(val innerHandler: IItemHandler)
    : IFilteredItemHandler {

    override fun canInsertItem(slot: Int, stack: ItemStack)
            = if (this.innerHandler is IFilteredItemHandler) this.innerHandler.canInsertItem(slot, stack) else true

    override fun canExtractItem(slot: Int)
            = if (this.innerHandler is IFilteredItemHandler) this.innerHandler.canExtractItem(slot) else true

    override fun getSlots() = this.innerHandler.slots

    override fun getStackInSlot(slot: Int) = this.innerHandler.getStackInSlot(slot)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (!this.canInsertItem(slot, stack)) {
            return stack
        }
        return this.innerHandler.insertItem(slot, stack, simulate)
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (!this.canExtractItem(slot)) {
            return ItemStackUtil.emptyStack
        }
        return this.innerHandler.extractItem(slot, amount, simulate)
    }

    override fun getSlotLimit(slot: Int) = this.innerHandler.getSlotLimit(slot)

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        if (this.innerHandler is IItemHandlerModifiable) {
            this.innerHandler.setStackInSlot(slot, stack)
        } else {
            throw RuntimeException("Inner item handler is not modifiable.")
        }
    }
}
