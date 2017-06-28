package net.ndrei.teslacorelib.inventory

import net.minecraftforge.fluids.IFluidTank

/**
 * Created by CF on 2017-06-28.
 */
interface IFluidTankWrapper {
    val innerTank: IFluidTank
}
