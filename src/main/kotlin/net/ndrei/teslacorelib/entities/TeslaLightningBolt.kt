package net.ndrei.teslacorelib.entities

import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge

/**
 * Created by CF on 2017-07-11.
 */
class TeslaLightningBolt(world: World, pos: BlockPos)
    : EntityLightningBolt(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), false) {

    private var state: Int = 0

    override fun onEntityUpdate() {
        super.onEntityUpdate()

        if (this.state == 1) {
            if (!this.world.isRemote) {
                MinecraftForge.EVENT_BUS.post(TeslaLightningStruckEvent(this.world, this, this.position))
            }
        }
        this.state ++
    }
}
