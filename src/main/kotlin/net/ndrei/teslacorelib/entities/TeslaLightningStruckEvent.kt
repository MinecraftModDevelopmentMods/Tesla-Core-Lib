package net.ndrei.teslacorelib.entities

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.Event

/**
 * Created by CF on 2017-07-11.
 */
class TeslaLightningStruckEvent(val world: World, val entity: TeslaLightningBolt, val pos: BlockPos): Event()