package net.ndrei.teslacorelib.utils

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil

/**
 * Created by CF on 2017-06-30.
 */
object FluidUtils {
    fun canFillFrom(tank: IFluidTank, bucket: ItemStack): Boolean {
        if (!ItemStackUtil.isEmpty(bucket) && bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            val handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
            val fluid = handler?.drain(1000, false)
            if (fluid != null && fluid.amount > 0) {
                return 1000 == tank.fill(fluid, false)
            }
        }
        return false
    }

    fun fillFluidFrom(tank: IFluidTank, bucket: ItemStack): ItemStack {
        if (!ItemStackUtil.isEmpty(bucket) && bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            val clone = bucket.copy()
            val handler = clone.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
            val fluid = handler?.drain(Fluid.BUCKET_VOLUME, false)
            if (fluid != null && fluid.amount == Fluid.BUCKET_VOLUME) {
                val filled = tank.fill(fluid, false)
                if (filled == Fluid.BUCKET_VOLUME) {
                    tank.fill(fluid, true)
                    handler.drain(filled, true)
                    return handler.container
                }
            }
        }
        return bucket
    }
}