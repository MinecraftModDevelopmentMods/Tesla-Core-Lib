package net.ndrei.teslacorelib.inventory

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank

/**
 * Created by CF on 2017-06-28.
 */
interface IFilteredFluidTank : IFluidTank {
    fun acceptsFluid(fluid: FluidStack): Boolean
    fun canDrain(): Boolean
    fun canFill(): Boolean
}
