package net.ndrei.teslacorelib.inventory

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by CF on 2017-06-30.
 */
@Suppress("unused")
open class LockableItemHandler : ItemStackHandler {
    constructor(stacks: NonNullList<ItemStack>) : super(stacks)
    constructor(size: Int) : super(size)

    var locked: Boolean = false

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return super.extractItem(slot,
                if (this.locked) Math.min(amount,
                        if (this.getStackInSlot(slot).isEmpty)
                            0
                        else
                            this.getStackInSlot(slot).count - 1)
                else
                    amount
                , simulate)
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        this.locked = if ((nbt != null) && nbt.hasKey("_locked")) nbt.getBoolean("_locked") else false
        super.deserializeNBT(nbt)
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = super.serializeNBT()
        nbt.setBoolean("_locked", this.locked)
        return nbt
    }
}