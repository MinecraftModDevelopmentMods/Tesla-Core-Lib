package net.ndrei.teslacorelib.inventory;

import com.google.common.collect.Lists;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.*;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by CF on 2016-12-29.
 */
public class FluidStorage implements IFluidHandler, INBTSerializable<NBTTagCompound> {
    private List<IFluidTank> tanks;

    public FluidStorage() {
        this.tanks = Lists.newArrayList();
    }

    //#region IFluidHandler Methods

    @Override
    public IFluidTankProperties[] getTankProperties() {
        List<IFluidTankProperties> list = Lists.newArrayList();

        for(IFluidTank tank : this.tanks) {
            boolean canDrain = true, canFill = true;
            if (tank instanceof  IFilteredFluidTank) {
                IFilteredFluidTank filtered = (IFilteredFluidTank)tank;
                canDrain = filtered.canDrain();
                canFill = filtered.canFill();
            }
            list.add(new FluidTankProperties(tank.getFluid(), tank.getCapacity(), canFill, canDrain));
        }

        return list.toArray(new IFluidTankProperties[0]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        int used = 0;
        resource = resource.copy();

        for(IFluidTank tank : this.tanks) {
            if (tank instanceof  IFilteredFluidTank) {
                IFilteredFluidTank filtered = (IFilteredFluidTank) tank;
                if (!filtered.canFill() || !filtered.acceptsFluid(resource)) {
                    continue;
                }
            }

            int amount = tank.fill(resource, doFill);
            used += Math.min(amount, resource.amount);
            if (resource.amount <= amount) {
                break;
            }
            resource.amount -= amount;
        }

        TeslaCoreLib.logger.info("Tank filled with " + used + " mb of " + resource.getFluid().getName());
        return used;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return this.drain(resource.amount, doDrain, resource);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return this.drain(maxDrain, doDrain, null);
    }

    private FluidStack drain(int maxDrain, boolean doDrain, FluidStack filter) {
        FluidStack fluid = (filter == null) ? null : filter.copy();
        if (fluid != null) {
            fluid.amount = 0;
        }

        for(IFluidTank tank : this.tanks) {
            if (tank instanceof IFilteredFluidTank) {
                IFilteredFluidTank filtered = (IFilteredFluidTank) tank;
                if (!filtered.canDrain()) {
                    continue;
                }
            }

            FluidStack contained = tank.getFluid();
            if (contained == null) {
                continue;
            }

            if ((filter != null) && !contained.isFluidEqual(filter)) {
                continue;
            }

            FluidStack drained = tank.drain(maxDrain, doDrain);
            if (drained == null) {
                continue;
            }

            if (fluid == null) {
                fluid = drained.copy();
                if (filter == null) {
                    // make sure we are not extracting different fluids from other tanks
                    filter = fluid;
                }
            } else {
                fluid.amount += drained.amount;
            }
        }

        return ((fluid != null) && (fluid.amount == 0)) ? null : fluid; // TODO: FluidStack.EMPTY ??
    }

    //#endregion

    public IFluidTank[] getTanks() {
        return this.tanks.toArray(new IFluidTank[0]);
    }

    public void addTank(IFluidTank tank) {
        this.tanks.add(tank);
    }

    public FilteredFluidTank addTank(Fluid acceptedFluid, int capacity) {
        FilteredFluidTank tank = new FilteredFluidTank(acceptedFluid, new FluidTank(capacity));
        this.addTank(tank);
        return tank;
    }

    public ColoredFluidHandler addTank(Fluid acceptedFluid, IFluidTank tank, EnumDyeColor color, String name, BoundingRectangle boundingBox) {
        ColoredFluidHandler colored = new ColoredFluidHandler(acceptedFluid, tank, color, name, boundingBox);
        this.addTank(colored);
        return colored;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        NBTTagList list = new NBTTagList();
        for(IFluidTank tank: this.tanks) {
            if (tank instanceof FilteredFluidTank) {
                tank = ((FilteredFluidTank)tank).getInnerTank();
            }
            NBTTagCompound tankNbt;
            if (tank instanceof ISerializableFluidTank) {
                tankNbt = ((ISerializableFluidTank) tank).serializeNBT();
            }
            else
            {
                tankNbt = new NBTTagCompound();
                FluidStack fluid = tank.getFluid();
                if (fluid != null) {
                    fluid.writeToNBT(tankNbt);
                } else {
                    tankNbt.setBoolean("_empty", true);
                }
            }
            list.appendTag(tankNbt);
        }
        nbt.setTag("tanks", list);

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("tanks", Constants.NBT.TAG_COMPOUND);
        if (list != null) {
            for(int index = 0; (index < list.tagCount()) && (index < this.tanks.size()); index++) {
                IFluidTank tank = this.tanks.get(index);
                if (tank instanceof FilteredFluidTank) {
                    tank = ((FilteredFluidTank)tank).getInnerTank();
                }
                NBTTagCompound tankNbt = list.getCompoundTagAt(index);
                if (tank instanceof ISerializableFluidTank) {
                    ((ISerializableFluidTank) tank).deserializeNBT(tankNbt);
                }
                else
                {
                    FluidStack fluid;
                    if (tankNbt.hasKey("_empty")) {
                        fluid = null;
                    } else {
                        fluid = FluidStack.loadFluidStackFromNBT(tankNbt);
                    }
                    if (tank.getFluidAmount() > 0) {
                        // drain the tank
                        tank.drain(tank.getFluidAmount(), true);
                    }
                    if (fluid != null) {
                        int filled = tank.fill(fluid, true);
                        if (filled != fluid.amount) {
                            TeslaCoreLib.logger.warn("FluidTank deserialized fluid wasn't totally put into tank: " + fluid.toString());
                        }
                    }
                }
            }
        }
    }

    public int tankCount() {
        return (this.tanks == null) ? 0 : this.tanks.size();
    }

    public boolean acceptsFluidFrom(ItemStack bucket) {
        if (!ItemStackUtil.isEmpty(bucket) && (bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))) {
            IFluidHandlerItem handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            FluidStack fluid = (handler != null) ? handler.drain(1000, false) : null;
            if ((fluid != null) && (fluid.amount > 0)) {
                return (1000 == this.fill(fluid, false));
            }
        }
        return false;
    }

    public ItemStack fillFluidFrom(ItemStack bucket) {
        if (!ItemStackUtil.isEmpty(bucket) && (bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))) {
            ItemStack clone = bucket.copy();
            IFluidHandlerItem handler = clone.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            FluidStack fluid = (handler != null) ? handler.drain(Fluid.BUCKET_VOLUME,false) : null;
            if ((fluid != null) && (fluid.amount == Fluid.BUCKET_VOLUME)) {
                int filled = this.fill(fluid, false);
                if (filled == Fluid.BUCKET_VOLUME) {
                    this.fill(fluid, true);
                    handler.drain(filled, true);
                    return handler.getContainer();
                }
            }
        }
        return bucket;
    }
}
