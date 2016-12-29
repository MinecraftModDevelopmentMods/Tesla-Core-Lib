package net.ndrei.teslacorelib.inventory;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by CF on 2016-12-29.
 */
public class FluidTank extends net.minecraftforge.fluids.FluidTank implements ISerializableFluidTank {
    public FluidTank(int capacity) {
        super(capacity);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        return super.writeToNBT(nbt);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }
}
