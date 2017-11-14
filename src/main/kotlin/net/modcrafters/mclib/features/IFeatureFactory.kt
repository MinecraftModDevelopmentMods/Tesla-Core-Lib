package net.modcrafters.mclib.features

import net.modcrafters.mclib.machines.IFeaturesMachine

interface IFeatureFactory {
    fun createFeature(machine: IFeaturesMachine): IFeature
}