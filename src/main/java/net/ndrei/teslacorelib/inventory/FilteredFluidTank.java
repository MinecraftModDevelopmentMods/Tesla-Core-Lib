package net.ndrei.teslacorelib.inventory;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by CF on 2016-12-29.
 */
public class FilteredFluidTank implements IFilteredFluidTank, IFluidTankWrapper {
    private IFluidTank tank;
    private Fluid filter = null;

    public FilteredFluidTank(IFluidTank tank) {
        this(null, tank);
    }

    public FilteredFluidTank(Fluid filter, IFluidTank tank) {
        this.filter = filter;
        this.tank = tank;
    }

    @Override
    public boolean acceptsFluid(@Nonnull FluidStack fluid) {
        return (this.filter == null) || (fluid.getFluid() == this.filter);
    }

    @Override
    public boolean canDrain() {
        return true;
    }

    @Override
    public boolean canFill() {
        return true;
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        FluidStack stack = this.tank.getFluid();
        if ((stack == null) && (this.filter != null)) {
            stack = new FluidStack(this.filter, 0);
        }
        return stack;
    }

    @Override
    public int getFluidAmount() {
        return this.tank.getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return this.tank.getCapacity();
    }

    @Override
    public FluidTankInfo getInfo() {
        return this.tank.getInfo();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (!this.canFill() || !this.acceptsFluid(resource)) {
            return 0;
        }

        return this.tank.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (!this.canDrain()) {
            return null; // TODO: FuildStack.EMPTY ??
        }

        return this.tank.drain(maxDrain, doDrain);
    }

    @Override
    public IFluidTank getInnerTank() {
        return this.tank;
    }
}
