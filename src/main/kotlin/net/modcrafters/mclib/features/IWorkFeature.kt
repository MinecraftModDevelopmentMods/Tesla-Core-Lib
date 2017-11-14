package net.modcrafters.mclib.features

import net.modcrafters.mclib.machines.IFeaturesMachine

interface IWorkFeature : IFeature {
    fun canStart(machine: IFeaturesMachine)
    fun start(machine: IFeaturesMachine)
}
