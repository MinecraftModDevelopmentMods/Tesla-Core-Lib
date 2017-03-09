package net.ndrei.teslacorelib.inventory;

import net.minecraftforge.fluids.IFluidTank;

/**
 * Created by CF on 2017-03-08.
 */
public interface IFluidTankWrapper {
    IFluidTank getInnerTank();
}
