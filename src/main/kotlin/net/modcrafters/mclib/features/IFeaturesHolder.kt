package net.modcrafters.mclib.features

import net.minecraft.nbt.NBTTagCompound
import net.modcrafters.mclib.checkEmpty
import net.modcrafters.mclib.getNullOrCompound
import net.modcrafters.mclib.setNonNullTag

interface IFeaturesHolder {
    fun featureChanged(feature: IFeature, makeDirty: Boolean)

    val features: List<IFeature>
    fun addFeature(feature: IFeature)
    fun removeFeature(key: String)

    fun deserializeFeaturesNBT(nbt: NBTTagCompound?) {
        this.features.forEach { feature ->
            feature.deserializeNBT(nbt?.getNullOrCompound(feature.key))
        }
    }

    fun serializeFeaturesNBT() = this.features.mapNotNull { feature ->
        val featureNBT = feature.serializeNBT()
        if (featureNBT == null) null else (feature.key to featureNBT)
    }.fold(NBTTagCompound()) { nbt, pair->
        nbt.setNonNullTag(pair.first, pair.second.checkEmpty())
    }.checkEmpty()
}
