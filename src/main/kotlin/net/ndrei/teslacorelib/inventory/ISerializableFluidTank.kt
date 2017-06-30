package net.ndrei.teslacorelib.inventory

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fluids.IFluidTank

/**
 * Created by CF on 2017-06-28.
 */
interface ISerializableFluidTank : IFluidTank, INBTSerializable<NBTTagCompound>
