@file:Suppress("unused")
package net.ndrei.teslacorelib.utils

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil

/**
 * Created by CF on 2017-07-14.
 */
fun ItemStack.copyWithSize(size: Int): ItemStack = ItemStackUtil.copyWithSize(this, size)

fun ItemStack.equalsIgnoreSize(b: ItemStack) = ItemStackUtil.areEqualIgnoreSize(this, b)

fun ItemStack.equalsIgnoreSizeAndNBT(b: ItemStack) = ItemStackUtil.areEqualIgnoreSizeAndNBT(this, b)

fun IItemHandler.getCombinedInventory() = ItemStackUtil.getCombinedInventory(this)

fun IItemHandler.extractFromCombinedInventory(stack: ItemStack, amount: Int, simulate: Boolean = false) =
    ItemStackUtil.extractFromCombinedInventory(this, stack, amount, simulate)

fun IItemHandler.insertInExistingStacks(stack: ItemStack, simulate: Boolean) = ItemStackUtil.insertItemInExistingStacks(this, stack, simulate)

fun IItemHandler.insertItems(stack: ItemStack, simulate: Boolean) = ItemStackUtil.insertItems(this, stack, simulate)

fun Item?.stack(size: Int = 1, meta: Int = 0): ItemStack = if (this != null) ItemStack(this, size, meta) else ItemStack.EMPTY