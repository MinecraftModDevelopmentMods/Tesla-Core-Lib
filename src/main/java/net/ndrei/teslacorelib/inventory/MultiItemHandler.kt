package net.ndrei.teslacorelib.inventory

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.ndrei.teslacorelib.compatibility.ItemStackUtil

/**
 * Created by CF on 2017-06-28.
 */
open class MultiItemHandler(private val handlers: MutableList<IItemHandler> = mutableListOf())
    : IMultiItemHandler {

    fun addItemHandler(handler: IItemHandler) {
        this.handlers.add(handler)
    }

    override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
        var slot = slot
        if (slot < 0) {
            return false
        }
        for (i in this.handlers!!.indices) {
            val handler = this.handlers!![i]
            if (handler.slots > slot) {
                if (handler is IFilteredItemHandler) {
                    // filter the inputs
                    return handler.canInsertItem(slot, stack)
                }
                return true
            }
            slot -= handler.slots
        }
        return false
    }

    override fun canExtractItem(slot: Int): Boolean {
        var slot = slot
        if (slot < 0) {
            return false
        }
        for (i in this.handlers!!.indices) {
            val handler = this.handlers!![i]
            if (handler.slots > slot) {
                if (handler is IFilteredItemHandler) {
                    // filter the inputs
                    return handler.canExtractItem(slot)
                }
                return true
            }
            slot -= handler.slots
        }
        return false

    }

    override val inventories: Int
        get() = this.handlers!!.size

    override fun getInventory(inventory: Int): IItemHandler {
        return this.handlers!![inventory]
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

    override fun getSlots(): Int {
        var slots = 0
        for (i in this.handlers!!.indices) {
            val handler = this.handlers!![i]
            slots += handler.slots
        }
        return slots
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        var slot = slot
        if (slot < 0) {
            return ItemStackUtil.emptyStack
        }
        for (i in this.handlers!!.indices) {
            val handler = this.handlers!![i]
            if (handler.slots > slot) {
                return handler.getStackInSlot(slot)
            }
            slot -= handler.slots
        }
        return ItemStackUtil.emptyStack
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        var slot = slot
        if (slot < 0) {
            return ItemStackUtil.emptyStack
        }
        for (i in this.handlers!!.indices) {
            val handler = this.handlers!![i]
            if (handler.slots > slot) {
                return handler.insertItem(slot, stack, simulate)
            }
            slot -= handler.slots
        }
        return ItemStackUtil.emptyStack
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        var slot = slot
        if (slot < 0) {
            return ItemStackUtil.emptyStack
        }
        for (i in this.handlers!!.indices) {
            val handler = this.handlers!![i]
            if (handler.slots > slot) {
                return handler.extractItem(slot, amount, simulate)
            }
            slot -= handler.slots
        }
        return ItemStackUtil.emptyStack
    }

    override fun getSlotLimit(slot: Int): Int {
        var slot = slot
        if (slot < 0) {
            return 0
        }
        for (i in this.handlers!!.indices) {
            val handler = this.handlers!![i]
            if (handler.slots > slot) {
                return handler.getSlotLimit(slot)
            }
            slot -= handler.slots
        }
        return 0
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        var slot = slot
        if (slot >= 0) {
            for (i in this.handlers!!.indices) {
                val handler = this.handlers!![i]
                if (handler.slots > slot) {
                    if (handler is IItemHandlerModifiable) {
                        handler.setStackInSlot(slot, stack)
                    } else {
                        throw RuntimeException("Target inventory is not an IItemHandlerModifiable.")
                    }
                    return
                }
                slot -= handler.slots
            }
        }
    }
}
