package net.ndrei.teslacorelib.inventory

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

/**
 * Created by CF on 2017-06-28.
 */
interface IMultiItemHandler : IFilteredItemHandler {
    val inventories: Int
    fun getInventory(inventory: Int): IItemHandler
    fun getFilteredInventory(inventory: Int): IFilteredItemHandler?

    fun canInsertItem(inventory: Int, slot: Int, stack: ItemStack): Boolean
    fun canExtractItem(inventory: Int, slot: Int): Boolean
}
