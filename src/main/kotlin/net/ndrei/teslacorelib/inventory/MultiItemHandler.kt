package net.ndrei.teslacorelib.inventory

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * Created by CF on 2017-06-28.
 */
open class MultiItemHandler(private val handlers: MutableList<IItemHandler> = mutableListOf())
    : IMultiItemHandler {

    fun addItemHandler(handler: IItemHandler) {
        this.handlers.add(handler)
    }

    override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
        var currentSlot = slot
        if (currentSlot < 0) {
            return false
        }
        for (i in this.handlers.indices) {
            val handler = this.handlers[i]
            if (handler.slots > currentSlot) {
                if (handler is IFilteredItemHandler) {
                    // filter the inputs
                    return handler.canInsertItem(currentSlot, stack)
                }
                return true
            }
            currentSlot -= handler.slots
        }
        return false
    }

    override fun canExtractItem(slot: Int): Boolean {
        var currentSlot = slot
        if (currentSlot < 0) {
            return false
        }
        for (i in this.handlers.indices) {
            val handler = this.handlers[i]
            if (handler.slots > currentSlot) {
                if (handler is IFilteredItemHandler) {
                    // filter the inputs
                    return handler.canExtractItem(currentSlot)
                }
                return true
            }
            currentSlot -= handler.slots
        }
        return false

    }

    override val inventories: Int
        get() = this.handlers.size

    override fun getInventory(inventory: Int): IItemHandler {
        return this.handlers[inventory]
    }

    override fun getFilteredInventory(inventory: Int): IFilteredItemHandler? {
        val handler = this.getInventory(inventory)
        if (handler is IFilteredItemHandler) {
            return handler
        }
        return null
    }

    override fun canInsertItem(inventory: Int, slot: Int, stack: ItemStack): Boolean {
        val handler = this.getFilteredInventory(inventory)
        if (handler != null) {
            return handler.canInsertItem(slot, stack)
        }
        return true
    }

    override fun canExtractItem(inventory: Int, slot: Int): Boolean {
        val handler = this.getFilteredInventory(inventory)
        if (handler != null) {
            return handler.canExtractItem(slot)
        }
        return true
    }

    override fun getSlots() =
        this.handlers.indices
            .map { this.handlers[it] }
            .sumBy { it.slots }

    override fun getStackInSlot(slot: Int): ItemStack {
        var currentSlot = slot
        if (currentSlot < 0) {
            return ItemStack.EMPTY
        }
        for (i in this.handlers.indices) {
            val handler = this.handlers[i]
            if (handler.slots > currentSlot) {
                return handler.getStackInSlot(currentSlot)
            }
            currentSlot -= handler.slots
        }
        return ItemStack.EMPTY
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        var currentSlot = slot
        if (currentSlot < 0) {
            return ItemStack.EMPTY
        }
        for (i in this.handlers.indices) {
            val handler = this.handlers[i]
            if (handler.slots > currentSlot) {
                return handler.insertItem(currentSlot, stack, simulate)
            }
            currentSlot -= handler.slots
        }
        return ItemStack.EMPTY
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        var currentSlot = slot
        if (currentSlot < 0) {
            return ItemStack.EMPTY
        }
        for (i in this.handlers.indices) {
            val handler = this.handlers[i]
            if (handler.slots > currentSlot) {
                return handler.extractItem(currentSlot, amount, simulate)
            }
            currentSlot -= handler.slots
        }
        return ItemStack.EMPTY
    }

    override fun getSlotLimit(slot: Int): Int {
        var currentSlot = slot
        if (currentSlot < 0) {
            return 0
        }
        for (i in this.handlers.indices) {
            val handler = this.handlers[i]
            if (handler.slots > currentSlot) {
                return handler.getSlotLimit(currentSlot)
            }
            currentSlot -= handler.slots
        }
        return 0
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        var currentSlot = slot
        if (currentSlot >= 0) {
            for (i in this.handlers.indices) {
                val handler = this.handlers[i]
                if (handler.slots > currentSlot) {
                    if (handler is IItemHandlerModifiable) {
                        handler.setStackInSlot(currentSlot, stack)
                    } else {
                        throw RuntimeException("Target inventory is not an IItemHandlerModifiable.")
                    }
                    return
                }
                currentSlot -= handler.slots
            }
        }
    }
}
