package net.ndrei.teslacorelib.inventory

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * Created by CF on 2017-06-28.
 */
interface IFilteredItemHandler : IItemHandlerModifiable {
    fun canInsertItem(slot: Int, stack: ItemStack): Boolean
    fun canExtractItem(slot: Int): Boolean
}
