package net.ndrei.teslacorelib.inventory

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.IFluidTank

/**
 * Created by CF on 2017-06-28.
 */
open class FilteredFluidTank(private val filter: Fluid?, private val tank: IFluidTank)
    : IFilteredFluidTank, IFluidTankWrapper, ITypedFluidTank {

    constructor(tank: IFluidTank)
            : this(null, tank)

    override fun acceptsFluid(fluid: FluidStack): Boolean {
        return (this.filter == null) || (fluid.fluid === this.filter)
    }

    override fun canDrain() = true
    override fun canFill() = true

    override var tankType: FluidTankType = FluidTankType.BOTH

    override fun getFluid(): FluidStack? {
        var stack = this.tank.fluid
        if (stack == null && this.filter != null) {
            stack = FluidStack(this.filter, 0)
        }
        return stack
    }

    override fun getFluidAmount(): Int {
        return this.tank.fluidAmount
    }

    override fun getCapacity(): Int {
        return this.tank.capacity
    }

    override fun getInfo(): FluidTankInfo {
        return this.tank.info
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {
        if (!this.canFill() || !this.acceptsFluid(resource)) {
            return 0
        }

        return this.tank.fill(resource, doFill)
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (!this.canDrain()) {
            return null // TODO: FuildStack.EMPTY ??
        }

        return this.tank.drain(maxDrain, doDrain)
    }

    override val innerTank: IFluidTank
        get() = this.tank
}
