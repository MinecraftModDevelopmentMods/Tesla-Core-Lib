package net.modcrafters.mclib.machines

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.modcrafters.mclib.features.IFeaturesHolder

interface IFeaturesMachine: IFeaturesHolder {
    val machineWorld: World
    val machinePos: BlockPos
}
