package net.modcrafters.mclib.features

import net.modcrafters.mclib.features.implementations.FeatureWrapper
import net.modcrafters.mclib.machines.IFeaturesMachine

inline fun <reified T: IFeature> IFeaturesHolder.findFeature(): T? =
    this.features.filterIsInstance<T>().firstOrNull()

inline fun <reified T: IFeature> IFeaturesHolder.findFeatures(): List<T> =
    this.features.filterIsInstance<T>()

fun IFeature.wrap(machine: IFeaturesMachine) = FeatureWrapper(machine, this)