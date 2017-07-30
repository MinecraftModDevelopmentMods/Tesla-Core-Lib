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
            if (handler.tankProperties.any { this.canFillFrom(tank, it) })
                return true
            else {
                // MAYBE RETARDED ITEMS THAT DON'T KNOW HOW TO IMPLEMENT AN "OPTIONAL" INTERFACE GOT IN HERE
                val drained = handler.drain(Fluid.BUCKET_VOLUME, false) ?: return false
                return ((drained.amount > 0) && (tank.fill(drained, false) > 0))
            }
        }
        return false
    }

    fun fillFluidFrom(tank: IFluidTank, bucket: ItemStack): ItemStack {
        val clone = bucket.copy()
        if (!clone.isEmpty && clone.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            val handler = clone.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) ?: return bucket
            val result = handler.tankProperties.firstOrNull { this.canFillFrom(tank, it) }
            if ((result != null) && (result.contents != null)) {
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
            } else {
                // MAYBE RETARDED ITEMS THAT DON'T KNOW HOW TO IMPLEMENT AN "OPTIONAL" INTERFACE GOT IN HERE
                val drained = handler.drain(Fluid.BUCKET_VOLUME, false) ?: return bucket
                if (drained.amount > 0) {
                    val filled = tank.fill(drained, false)
                    if (filled > 0) {
                        val toMove = if (filled != drained.amount)
                            (handler.drain(filled, false) ?: return bucket).amount
                        else filled

                        if (toMove > 0) {
                            tank.fill(handler.drain(toMove, true), true)
                            return handler.container
                        }
                    }
                }
            }
        }
        return bucket
    }

    fun canDrainInto(tank: IFluidTank, bucket: ItemStack): Boolean {
        if (!bucket.isEmpty && (bucket.count == 1)) {
            /*if (bucket.item == Items.BUCKET) {
                return (tank.fluidAmount >= Fluid.BUCKET_VOLUME) && FluidRegistry.getBucketFluids().contains(tank.fluid?.fluid)
            } else*/ if (bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                val handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) ?: return false
                val filled = handler.fill(tank.fluid ?: return false, false)
                if (filled > 0) {
                    val drained = tank.drain(filled, false)
                    return (drained != null) && (drained.amount == filled)
                }
            }
        }
        return false
    }

    fun drainInto(tank: IFluidTank, bucket: ItemStack): ItemStack {
        if (bucket.isEmpty || (bucket.count > 1)) {
            return bucket
        }

        /*if (bucket.item == Items.BUCKET) {
            if (FluidRegistry.getBucketFluids().contains(tank.fluid?.fluid) && (tank.fluidAmount >= Fluid.BUCKET_VOLUME)) {
            }
        }
        else */if (bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            val handler = bucket.copy().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) ?: return bucket
            val fluid = (tank.fluid ?: return bucket).let { FluidStack(it.fluid, Math.min(it.amount, Fluid.BUCKET_VOLUME)) }
            val filled = handler.fill(fluid, false)
            if (filled > 0) {
                val drained = tank.drain(filled, false)
                if (filled == (drained?.amount ?: 0)) {
                    handler.fill(fluid, true)
                    tank.drain(filled, true)
                    return handler.container
                }
            }
        }

        return bucket
    }
}
