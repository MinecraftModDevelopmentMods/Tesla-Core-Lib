package net.ndrei.teslacorelib.inventory;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.IFluidTank;

/**
 * Created by CF on 2016-12-29.
 */
public interface ISerializableFluidTank extends IFluidTank, INBTSerializable<NBTTagCompound> {
}
