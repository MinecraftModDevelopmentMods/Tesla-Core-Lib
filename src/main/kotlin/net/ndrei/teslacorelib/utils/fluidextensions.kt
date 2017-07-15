package net.ndrei.teslacorelib.utils

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * Created by CF on 2017-07-15.
 */
fun IFluidTank.canFillFrom(bucket: ItemStack) = FluidUtils.canFillFrom(this, bucket)
fun IFluidTank.fillFrom(bucket: ItemStack) = FluidUtils.fillFluidFrom(this, bucket)

fun Iterable<IFluidTank>.canFillFrom(bucket: ItemStack)
    = this.any { it.canFillFrom(bucket) }

fun Iterable<IFluidTank>.fillFrom(bucket: ItemStack): ItemStack
    = this.fold(bucket.copy()) { b, t -> t.fillFrom(b) }

fun Iterable<IFluidTank>.processInputInventory(inventory: IItemHandlerModifiable) {
    val stack = inventory.getStackInSlot(0)
    if (!stack.isEmpty && this.canFillFrom(stack)) {
        val result = this.fillFrom(stack)
        if (!ItemStack.areItemStacksEqual(stack, result)) {
            inventory.setStackInSlot(0, result)
            if (!this.canFillFrom(result)) {
                inventory.discardUsedItem()
            }
        }
    }
    else if (!stack.isEmpty) {
        inventory.discardUsedItem()
    }
}

fun IItemHandlerModifiable.discardUsedItem()
    = this.setStackInSlot(0, this.insertItem(1, this.getStackInSlot(0), false))