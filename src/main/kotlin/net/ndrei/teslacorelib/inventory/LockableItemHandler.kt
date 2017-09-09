package net.ndrei.teslacorelib.inventory

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraftforge.common.util.Constants
import net.ndrei.teslacorelib.compatibility.ItemStackUtil

/**
 * Created by CF on 2017-06-30.
 */
@Suppress("unused")
open class LockableItemHandler : SyncItemHandler, IFilteredItemHandler {
    constructor(stacks: NonNullList<ItemStack>) : super(stacks)
    constructor(size: Int) : super(size)

    var filter: Array<ItemStack>? = null
    private var _locked: Boolean = false

    var locked
        get() = this._locked
        set(value) {
            this._locked = value
            if (this._locked) {
                this.filter = (0 until this.slots)
                        .map { if (this.getStackInSlot(it).isEmpty) ItemStack.EMPTY else ItemStackUtil.copyWithSize(this.getStackInSlot(it), 1) }
                        .toTypedArray()
            }
            this.onContentsChanged(-1)
        }

//    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
//        return super.extractItem(slot,
//                if (this.locked) Math.min(amount,
//                        if (this.getStackInSlot(slot).isEmpty)
//                            0
//                        else
//                            this.getStackInSlot(slot).count - 1)
//                else
//                    amount
//                , simulate)
//    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        this.locked = if ((nbt != null) && nbt.hasKey("_locked")) nbt.getBoolean("_locked") else false

        val filter = mutableListOf<ItemStack>()
        if (this.locked && (nbt != null) && nbt.hasKey("_filterSize", Constants.NBT.TAG_INT)) {
            val size = nbt.getInteger("_filterSize")
            for(x in 0..size-1) {
                val key = "_filter_$x"
                filter.add(x, if (nbt.hasKey(key, Constants.NBT.TAG_COMPOUND))
                    ItemStack(nbt.getCompoundTag(key))
                else
                    ItemStack.EMPTY)
            }
        }
        this.filter = filter.toTypedArray()

        super.deserializeNBT(nbt)
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = super.serializeNBT()
        nbt.setBoolean("_locked", this.locked)
        if (this.locked && (this.filter != null)) {
            nbt.setInteger("_filterSize", this.filter!!.size)
            for(x in 0..this.filter!!.size - 1) {
                val stack = this.filter!![x]
                if (!stack.isEmpty) {
                    nbt.setTag("_filter_$x", this.filter!![x].serializeNBT())
                }
            }
        }
        return nbt
    }

    override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
        if (this.locked && (this.filter != null) && (slot in 0..this.filter!!.size-1)) {
            val f = this.filter!![slot]
            if (!f.isEmpty) {
                return ItemStackUtil.areEqualIgnoreSize(f, stack)
            }
        }

        return !this.locked
    }

    override fun canExtractItem(slot: Int) = true

    fun getFilterStack(slot: Int): ItemStack =
            if (this.locked && (this.filter != null) && (slot in 0..this.filter!!.size))
                this.filter!![slot]
            else
                ItemStack.EMPTY
}
