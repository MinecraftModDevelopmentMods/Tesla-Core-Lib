package net.ndrei.teslacorelib.compatibility

import net.minecraft.item.ItemStack

/**
 * Created by CF on 2017-07-09.
 */
interface IItemColorDelegate {
    fun getColorFromItemStack(stack: ItemStack, tintIndex: Int): Int
}