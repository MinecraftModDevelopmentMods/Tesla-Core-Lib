package net.ndrei.teslacorelib.capabilities.wrench

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslacorelib.items.TeslaWrench

/**
 * Created by CF on 2017-06-28.
 */
interface ITeslaWrenchHandler {
    fun onWrenchUse(wrench: TeslaWrench,
                    player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand,
                    facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult
}