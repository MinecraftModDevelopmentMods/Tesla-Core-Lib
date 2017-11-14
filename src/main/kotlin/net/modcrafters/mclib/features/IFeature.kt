package net.modcrafters.mclib.features

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

interface IFeature: INBTSerializable<NBTTagCompound> {
    val key: String

    fun canBeAddedTo(machine: IFeaturesHolder): Boolean
    fun added(holder: IFeaturesHolder)
    fun removed(holder: IFeaturesHolder)

    fun writeSyncNBT() = this.serializeNBT()
    fun readSyncNBT(compound: NBTTagCompound) = this.deserializeNBT(compound)
}
