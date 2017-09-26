@file:Suppress("unused", "MemberVisibilityCanPrivate")
package net.ndrei.teslacorelib.blocks.multipart

import net.minecraft.util.math.AxisAlignedBB

open class BlockPartHitBox(override val aabb: AxisAlignedBB) : IBlockPartHitBox {
    companion object {
        fun big(size: Double, x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double) =
            BlockPartHitBox(AxisAlignedBB(x1 / size, y1 / size, z1 / size, x2 / size, y2 / size, z2 / size))

        fun big16(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double) =
            big(16.0, x1, y1, z1, x2, y2, z2)
        fun big16Sized(x1: Double, y1: Double, z1: Double, width: Double, height: Double, depth: Double) =
            big16(x1, y1, z1, x1 + width, y1 + height, z1 + depth)

        fun big32(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double) =
            big(32.0, x1, y1, z1, x2, y2, z2)
        fun big32Sized(x1: Double, y1: Double, z1: Double, width: Double, height: Double, depth: Double) =
            big32(x1, y1, z1, x1 + width, y1 + height, z1 + depth)
    }
}