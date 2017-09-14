package net.ndrei.teslacorelib.inventory

import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.items.ItemStackHandler

open class SyncItemHandler : ItemStackHandler, ISyncProvider {
    constructor(stacks: NonNullList<ItemStack>) : super(stacks)
    constructor(size: Int) : super(size)

    private var syncTarget: ISyncTarget? = null
    private var syncKey: String? = null

    override fun setSyncTarget(target: ISyncTarget, key: String?) {
        this.syncTarget = target
        this.syncKey = key
    }

    override fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)

        this.syncTarget?.also {
            if (this.syncKey.isNullOrBlank()) it.forceSync() else it.partialSync(this.syncKey!!)
        }
    }
}
