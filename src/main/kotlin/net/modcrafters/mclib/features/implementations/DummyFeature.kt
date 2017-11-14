package net.modcrafters.mclib.features.implementations

import net.minecraft.nbt.NBTTagCompound
import net.modcrafters.mclib.features.IFeature
import net.modcrafters.mclib.features.IFeaturesHolder

object DummyFeature: IFeature {
    override val key get() = "DUMMY"

    override fun canBeAddedTo(machine: IFeaturesHolder) = true

    override fun added(holder: IFeaturesHolder) { }

    override fun removed(holder: IFeaturesHolder) { }

    override fun deserializeNBT(nbt: NBTTagCompound) { }

    override fun serializeNBT() = null
}
