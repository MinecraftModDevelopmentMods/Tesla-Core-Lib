package net.ndrei.teslacorelib.inventory

import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.IItemHandler

/**
 * Created by CF on 2017-06-28.
 */
class SidedItemHandlerWrapper(private val handler: SidedItemHandler, private val side: EnumFacing) : IItemHandler {
    private val slots: IntArray
            = this.handler.getSlotsForFace(this.side)

    override fun getSlots()
            = this.slots.size

    override fun getStackInSlot(slot: Int)
            = this.handler.getStackInSlot(this.slots[slot])

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean)
            = this.handler.insertItem(this.slots[slot], stack, simulate)

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean)
            = this.handler!!.extractItem(this.slots[slot], amount, simulate)

    override fun getSlotLimit(slot: Int)
            = this.handler!!.getSlotLimit(this.slots[slot])
}
