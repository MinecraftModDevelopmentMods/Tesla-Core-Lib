package net.ndrei.teslacorelib.compatibility

import com.google.common.collect.Lists
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.ndrei.teslacorelib.utils.copyWithSize
import net.ndrei.teslacorelib.utils.equalsIgnoreSize

/**
 * Created by CF on 2017-06-28.
 */
@Suppress("unused")
object ItemStackUtil {
    @Deprecated("This was for the stupid 1.10 / 1.11 compatibility. Stop using it!", ReplaceWith("ItemStack::isEmpty"))
    fun isEmpty(stack: ItemStack?): Boolean = (stack == null) || stack.isEmpty || (stack.count == 0)

    @Deprecated("This was for the stupid 1.10 / 1.11 compatibility. Stop using it!", ReplaceWith("ItemStack::grow"))
    fun grow(stack: ItemStack, amount: Int) = stack.grow(amount)

    @Deprecated("This was for the stupid 1.10 / 1.11 compatibility. Stop using it!", ReplaceWith("ItemStack::shrink"))
    fun shrink(stack: ItemStack, amount: Int) = stack.shrink(amount)

    @Deprecated("This was for the stupid 1.10 / 1.11 compatibility. Stop using it!", ReplaceWith("ItemStack::count"))
    fun getSize(stack: ItemStack): Int = if (ItemStackUtil.isEmpty(stack)) 0 else stack.count

    @Deprecated("This was for the stupid 1.10 / 1.11 compatibility. Stop using it!", ReplaceWith("ItemStack::count"))
    fun setSize(stack: ItemStack, size: Int) { stack.count = size; }

    fun copyWithSize(stack: ItemStack, size: Int): ItemStack {
        val result = stack.copy()
        result.count = size
        return result
    }

    @Deprecated("This was for the stupid 1.10 / 1.11 compatibility. Stop using it!", ReplaceWith("ItemStack.EMPTY"))
    val emptyStack: ItemStack
        get() = ItemStack.EMPTY

    fun getCombinedInventory(handler: IItemHandler): List<ItemStack> {
        val list = Lists.newArrayList<ItemStack>()
        for (i in 0 until handler.slots) {
            val stack = handler.getStackInSlot(i)
            if (stack.isEmpty) {
                continue
            }

            val match: ItemStack? = list.firstOrNull { it.equalsIgnoreSize(stack) }
            if (match == null) {
                list.add(stack.copy())
            } else {
                match.count = match.count + stack.count
            }
        }
        return list
    }

    fun extractFromCombinedInventory(handler: IItemHandler, stack: ItemStack, amount: Int, simulate: Boolean = false): Int {
        if (stack.isEmpty) {
            return 0
        }

        val result = stack.copyWithSize(amount)
        var taken = 0
        for (i in 0 until handler.slots) {
            val temp = handler.getStackInSlot(i)
            if (temp.isEmpty || !temp.equalsIgnoreSize(stack)) {
                continue
            }

            val takenStack = handler.extractItem(i, Math.min(result.count, temp.count), simulate)
            taken += takenStack.count
            result.shrink(takenStack.count)
            if (result.isEmpty) {
                break
            }
        }
        return taken
    }

    fun insertItemInExistingStacks(dest: IItemHandler?, stack: ItemStack, simulate: Boolean): ItemStack {
        var remaining = stack
        if ((dest == null) || remaining.isEmpty)
            return ItemStack.EMPTY

        for (i in 0 until dest.slots) {
            if (dest.getStackInSlot(i).isEmpty) {
                continue
            }

            remaining = dest.insertItem(i, remaining, simulate)
            if (remaining.isEmpty) {
                return ItemStack.EMPTY
            }
        }

        return remaining
    }

    fun insertItems(dest: IItemHandler, stack: ItemStack, simulate: Boolean): ItemStack {
        val remaining = ItemStackUtil.insertItemInExistingStacks(dest, stack, simulate)
        return if (remaining.isEmpty) ItemStack.EMPTY
        else ItemHandlerHelper.insertItem(dest, remaining, simulate)
    }

    fun areEqualIgnoreSize(a: ItemStack, b: ItemStack) =
        if (a.isEmpty && b.isEmpty)
            true
        else if (a.isEmpty != b.isEmpty)
            false
        else {
            val x = ItemStackUtil.copyWithSize(a, b.count)
            ItemStack.areItemStacksEqualUsingNBTShareTag(x, b)
        }

    fun areEqualIgnoreSizeAndNBT(a: ItemStack, b: ItemStack) =
            if (a.isEmpty && b.isEmpty)
                true
            else if (a.isEmpty != b.isEmpty)
                false
            else {
                val x = ItemStackUtil.copyWithSize(a, b.count)
                ItemStack.areItemStacksEqual(x, b)
            }
}
