package net.ndrei.teslacorelib.compatibility

import com.google.common.collect.Lists
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper

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

    fun ItemStack.copyWithCount(size: Int): ItemStack =  ItemStackUtil.copyWithSize(this, size)

    @Deprecated("This was for the stupid 1.10 / 1.11 compatibility. Stop using it!", ReplaceWith("ItemStack.EMPTY"))
    val emptyStack: ItemStack
        get() = ItemStack.EMPTY

    fun getCombinedInventory(handler: IItemHandler): List<ItemStack> {
        val list = Lists.newArrayList<ItemStack>()
        for (i in 0..handler.slots - 1) {
            val stack = handler.getStackInSlot(i)
            if (ItemStackUtil.isEmpty(stack)) {
                continue
            }

            var match: ItemStack? = null
            for (existing in list) {
                if (existing.item === stack.item) {
                    match = existing
                    break
                }
            }
            if (match == null) {
                list.add(stack.copy())
            } else {
                match.count = match.count + stack.count
            }
        }
        return list
    }

    fun extractFromCombinedInventory(handler: IItemHandler, stack: ItemStack, amount: Int): Int {
        var amount = amount
        if (ItemStackUtil.isEmpty(stack)) {
            return 0
        }

        var taken = 0
        for (i in 0..handler.slots - 1) {
            val temp = handler.getStackInSlot(i)
            if (temp == null || temp.isEmpty || temp.item !== stack.item) {
                continue
            }

            val takenStack = handler.extractItem(i, Math.min(amount, temp.count), false)
            taken += takenStack.count
            amount -= takenStack.count
            if (amount <= 0) {
                break
            }
        }
        return taken
    }

    fun insertItemInExistingStacks(dest: IItemHandler?, stack: ItemStack, simulate: Boolean): ItemStack {
        var stack = stack
        if (dest == null || stack.isEmpty)
            return ItemStack.EMPTY

        for (i in 0..dest.slots - 1) {
            if (ItemStackUtil.isEmpty(dest.getStackInSlot(i))) {
                continue
            }

            stack = dest.insertItem(i, stack, simulate)
            if (stack.isEmpty) {
                return ItemStack.EMPTY
            }
        }

        return stack
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
    fun ItemStack.equalsIgnoreSize(b: ItemStack) = ItemStackUtil.areEqualIgnoreSize(this, b)

    fun areEqualIgnoreSizeAndNBT(a: ItemStack, b: ItemStack) =
            if (a.isEmpty && b.isEmpty)
                true
            else if (a.isEmpty != b.isEmpty)
                false
            else {
                val x = ItemStackUtil.copyWithSize(a, b.count)
                ItemStack.areItemStacksEqual(x, b)
            }
    fun ItemStack.equalsIgnoreSizeAndNBT(b: ItemStack) = ItemStackUtil.areEqualIgnoreSizeAndNBT(this, b)
}
