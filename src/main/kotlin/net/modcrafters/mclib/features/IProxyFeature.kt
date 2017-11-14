package net.modcrafters.mclib.features

import net.modcrafters.mclib.machines.IFeaturesMachine

interface IProxyFeature: IFeatureFactory {
    fun createClientFeature(machine: IFeaturesMachine): IFeature
    fun createServerFeature(machine: IFeaturesMachine): IFeature

    override fun createFeature(machine: IFeaturesMachine) =
        if (machine.machineWorld.isRemote) this.createClientFeature(machine)
        else this.createServerFeature(machine)
}
