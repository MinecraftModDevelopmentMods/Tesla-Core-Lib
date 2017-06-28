package net.ndrei.teslacorelib.inventory

import net.minecraft.nbt.NBTTagCompound

/**
 * Created by CF on 2017-06-28.
 */
open class FluidTank(capacity: Int)
    : net.minecraftforge.fluids.FluidTank(capacity), ISerializableFluidTank {

    override fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()
        return super.writeToNBT(nbt)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
    }
}
