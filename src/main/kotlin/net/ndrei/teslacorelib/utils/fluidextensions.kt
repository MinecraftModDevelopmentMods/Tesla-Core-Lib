package net.ndrei.teslacorelib.utils

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
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

fun Iterable<IFluidTank>.processInputInventory(inventory: IItemHandlerModifiable): Boolean {
    val stack = inventory.getStackInSlot(0)
    if (!stack.isEmpty && this.canFillFrom(stack)) {
        val result = this.fillFrom(stack)
        if (!ItemStack.areItemStacksEqual(stack, result)) {
            inventory.setStackInSlot(0, result)
            if (!this.canFillFrom(result)) {
                inventory.discardUsedItem()
            }
            return true
        }
    }
    else if (!stack.isEmpty) {
        inventory.discardUsedItem()
    }
    return false
}

fun IItemHandlerModifiable.discardUsedItem()
    = this.setStackInSlot(0, this.insertItem(1, this.getStackInSlot(0), false))

fun ItemStack.canFillFrom(tank: IFluidTank) = FluidUtils.canDrainInto(tank, this)

fun ItemStack.fillFrom(tank: IFluidTank) = FluidUtils.drainInto(tank, this)

/**
 * This will return only @see(Fluid.BUCKET_VOLUME) amount of the first fluid.
 */
fun ItemStack.getContainedFluid(): FluidStack? {
    if (!this.isEmpty && this.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
        val handler = this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) ?: return null
        return handler.drain(Fluid.BUCKET_VOLUME, false)
    }
    return null
}