package net.ndrei.teslacorelib.inventory;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nonnull;

/**
 * Created by CF on 2016-12-29.
 */
public interface IFilteredFluidTank extends IFluidTank {
    boolean acceptsFluid(@Nonnull FluidStack fluid);
    boolean canDrain();
    boolean canFill();
}
