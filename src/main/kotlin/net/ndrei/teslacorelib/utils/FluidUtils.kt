@file:Suppress("unused")

package net.ndrei.teslacorelib.utils

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties

/**
 * Created by CF on 2017-06-30.
 */
object FluidUtils {
    fun canFillFrom(target: IFluidTank, source: IFluidTankProperties): Boolean {
        val fluid = source.contents ?: return false
        if ((fluid.amount > 0) && source.canDrainFluidType(fluid)) {
            if (0 < target.fill(fluid, false)) {
                return true
            }
        }
        return false
    }

    fun canFillFrom(tank: IFluidTank, bucket: ItemStack): Boolean {
        if (!bucket.isEmpty && bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            val handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) ?: return false
            return handler.tankProperties.any {
                this.canFillFrom(tank, it)
            }
        }
        return false
    }

    fun fillFluidFrom(tank: IFluidTank, bucket: ItemStack): ItemStack {
        if (!bucket.isEmpty && bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            val clone = bucket.copy()
            val handler = clone.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) ?: return bucket
            val result = handler.tankProperties.firstOrNull { this.canFillFrom(tank, it) } ?: return bucket
            if ((result.contents != null)) {
                val amount = Math.min(Fluid.BUCKET_VOLUME, result.contents!!.amount)
                // see how much we can drain
                var drained = handler.drain(FluidStack(result.contents!!, amount), false) ?: return bucket
                if (drained.amount > 0) {
                    // see how much we can fill
                    val filled = tank.fill(drained, false)
                    if (filled > 0) {
                        if (filled != drained.amount) {
                            // see if we can drain the amount we can fill
                            drained = handler.drain(FluidStack(result.contents!!, filled), false) ?: return bucket
                        }

                        if (drained.amount == filled) {
                            handler.drain(FluidStack(result.contents!!, filled), true)
                            tank.fill(FluidStack(result.contents!!, filled), true)
                            return handler.container
                        }
                    }
                }
            }
        }
        return bucket
    }
}