package net.modcrafters.mclib.features.implementations

import net.minecraft.nbt.NBTTagCompound
import net.modcrafters.mclib.features.IFeature
import net.modcrafters.mclib.features.IFeatureFactory
import net.modcrafters.mclib.features.IFeaturesHolder
import net.modcrafters.mclib.machines.IFeaturesMachine

class FeatureWrapper(private val machine: IFeaturesMachine, inner: IFeature) : IFeature {
    val originalFeature: IFeature = inner
    val feature: IFeature by lazy {
        when (this.originalFeature) {
            is IFeatureFactory -> this.originalFeature.createFeature(this.machine)
            else -> this.originalFeature
        }
    }

    override val key: String get() = this.feature.key
    override fun canBeAddedTo(machine: IFeaturesHolder) = this.feature.canBeAddedTo(machine)
    override fun added(holder: IFeaturesHolder) { this.feature.added(holder) }
    override fun removed(holder: IFeaturesHolder) { this.feature.removed(holder)}

    override fun serializeNBT(): NBTTagCompound = this.feature.serializeNBT()
    override fun deserializeNBT(nbt: NBTTagCompound?) { this.feature.deserializeNBT(nbt) }
}