package net.ndrei.teslacorelib.utils

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * Created by CF on 2017-07-06.
 */
object BlockPosUtils {
    fun getCube(entityPos: BlockPos, facing: EnumFacing?, radius: Int, height: Int): BlockCube {
        val pos1: BlockPos
        var pos2: BlockPos

        if (facing != null) {
            if (facing == EnumFacing.UP) {
                pos1 = entityPos
                        .offset(EnumFacing.EAST, radius)
                        .offset(EnumFacing.SOUTH, radius)
                        .up(1)
                pos2 = entityPos
                        .offset(EnumFacing.WEST, radius)
                        .offset(EnumFacing.NORTH, radius)
                        .up(height)
            } else if (facing == EnumFacing.DOWN) {
                pos1 = entityPos
                        .offset(EnumFacing.EAST, radius)
                        .offset(EnumFacing.SOUTH, radius)
                        .down(1)
                pos2 = entityPos
                        .offset(EnumFacing.WEST, radius)
                        .offset(EnumFacing.NORTH, radius)
                        .down(height)
            } else {
                // assume horizontal facing
                val left = facing.rotateYCCW()
                val right = facing.rotateY()
                pos1 = entityPos
                        .offset(left, radius)
                        .offset(facing, 1)
                pos2 = entityPos
                        .offset(right, radius)
                        .offset(facing, radius * 2 + 1)
            }
        } else {
            pos1 = BlockPos(entityPos.x - radius, entityPos.y, entityPos.z - radius)
            pos2 = BlockPos(entityPos.x + radius, entityPos.y, entityPos.z + radius)
        }
        pos2 = pos2.offset(EnumFacing.UP, height - 1)

        return BlockCube(pos1, pos2)
    }
}